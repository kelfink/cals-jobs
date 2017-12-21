package gov.ca.cwds.generic.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.generic.dao.cms.ReplicatedRelationshipsDao;
import gov.ca.cwds.generic.data.persistence.cms.EsRelationship;
import gov.ca.cwds.generic.data.persistence.cms.ReplicatedRelationships;
import gov.ca.cwds.generic.jobs.inject.JobRunner;
import gov.ca.cwds.generic.jobs.inject.LastRunFile;
import gov.ca.cwds.generic.jobs.util.JobLogs;
import gov.ca.cwds.generic.jobs.util.jdbc.JobResultSetAware;
import gov.ca.cwds.generic.jobs.util.transform.ElasticTransformer;
import gov.ca.cwds.generic.jobs.util.transform.EntityNormalizer;
import gov.ca.cwds.inject.CmsSessionFactory;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Job to load family relationships from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class RelationshipIndexerJob
    extends BasePersonIndexerJob<ReplicatedRelationships, EsRelationship>
    implements JobResultSetAware<EsRelationship> {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(
      RelationshipIndexerJob.class);

  private static final String INSERT_CLIENT_LAST_CHG =
      "INSERT INTO #SCHEMA#.GT_ID (IDENTIFIER)\n" + "SELECT clnr.IDENTIFIER\n"
          + "FROM #SCHEMA#.CLN_RELT CLNR\n" + "WHERE CLNR.IBMSNAP_LOGMARKER > ##TIMESTAMP##\n"
          + "UNION ALL\n" + "SELECT clnr.IDENTIFIER\n" + "FROM #SCHEMA#.CLN_RELT CLNR\n"
          + "JOIN #SCHEMA#.CLIENT_T CLNS ON CLNR.FKCLIENT_T = CLNS.IDENTIFIER\n"
          + "WHERE CLNS.IBMSNAP_LOGMARKER > ##TIMESTAMP##\n" + "UNION ALL\n"
          + "SELECT clnr.IDENTIFIER\n" + "FROM #SCHEMA#.CLN_RELT CLNR\n"
          + "JOIN #SCHEMA#.CLIENT_T CLNP ON CLNR.FKCLIENT_0 = CLNP.IDENTIFIER\n"
          + "WHERE CLNP.IBMSNAP_LOGMARKER > ##TIMESTAMP##";

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param dao Relationship View DAO
   * @param esDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public RelationshipIndexerJob(final ReplicatedRelationshipsDao dao, final ElasticsearchDao esDao,
      @LastRunFile final String lastJobRunTimeFilename, final ObjectMapper mapper,
      @CmsSessionFactory SessionFactory sessionFactory) {
    super(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  @Override
  public String getPrepLastChangeSQL() {
    return INSERT_CLIENT_LAST_CHG;
  }

  @Override
  public EsRelationship extract(ResultSet rs) throws SQLException {
    return EsRelationship.mapRow(rs);
  }

  @Override
  public Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    return EsRelationship.class;
  }

  @Override
  public String getInitialLoadViewName() {
    return "VW_MQT_BI_DIR_RELATION";
  }

  @Override
  public String getJdbcOrderBy() {
    return " ORDER BY THIS_LEGACY_ID, RELATED_LEGACY_ID ";
  }

  @Override
  public String getInitialLoadQuery(String dbSchemaName) {
    final StringBuilder buf = new StringBuilder();
    buf.append("SELECT x.* FROM ").append(dbSchemaName).append(".").append(getInitialLoadViewName())
        .append(" x ");

    if (!getOpts().isLoadSealedAndSensitive()) {
      buf.append(" WHERE x.THIS_SENSITIVITY_IND = 'N' AND x.RELATED_SENSITIVITY_IND = 'N' ");
    }

    buf.append(getJdbcOrderBy()).append(" FOR READ ONLY WITH UR ");
    return buf.toString();
  }

  @Override
  protected UpdateRequest prepareUpsertRequest(ElasticSearchPerson esp, ReplicatedRelationships p)
      throws IOException {
    final StringBuilder buf = new StringBuilder();
    buf.append("{\"relationships\":[");

    if (!p.getRelations().isEmpty()) {
      try {
        buf.append(p.getRelations().stream().map(ElasticTransformer::jsonify)
            .sorted(String::compareTo).collect(Collectors.joining(",")));
      } catch (Exception e) {
        JobLogs.raiseError(LOGGER, e, "ERROR SERIALIZING RELATIONSHIPS! {}", e.getMessage());
      }
    }

    buf.append("]}");

    final String insertJson = mapper.writeValueAsString(esp);
    final String updateJson = buf.toString();

    final String alias = esDao.getConfig().getElasticsearchAlias();
    final String docType = esDao.getConfig().getElasticsearchDocType();

    return new UpdateRequest(alias, docType, esp.getId()).doc(updateJson)
        .upsert(new IndexRequest(alias, docType, esp.getId()).source(insertJson));
  }

  @Override
  public ReplicatedRelationships normalizeSingle(List<EsRelationship> recs) {
    return !recs.isEmpty() ? normalize(recs).get(0) : null;
  }

  @Override
  public List<ReplicatedRelationships> normalize(List<EsRelationship> recs) {
    return EntityNormalizer.<ReplicatedRelationships, EsRelationship>normalizeList(recs);
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    JobRunner.runStandalone(RelationshipIndexerJob.class, args);
  }

}
