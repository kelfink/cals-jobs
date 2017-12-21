package gov.ca.cwds.generic.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.generic.dao.cms.ReplicatedAkaDao;
import gov.ca.cwds.generic.dao.cms.ReplicatedOtherClientNameDao;
import gov.ca.cwds.generic.data.persistence.cms.ReplicatedAkas;
import gov.ca.cwds.generic.data.persistence.cms.rep.ReplicatedOtherClientName;
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
import javax.persistence.Table;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Job to load Other Client Name from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class OtherClientNameIndexerJob
    extends BasePersonIndexerJob<ReplicatedAkas, ReplicatedOtherClientName>
    implements JobResultSetAware<ReplicatedOtherClientName> {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(
      OtherClientNameIndexerJob.class);

  private static final String INSERT_CLIENT_LAST_CHG = "INSERT INTO #SCHEMA#.GT_ID (IDENTIFIER)\n"
      + "SELECT CLT.IDENTIFIER AS CLIENT_ID\n" + "FROM #SCHEMA#.OCL_NM_T ONM\n"
      + "JOIN #SCHEMA#.CLIENT_T CLT ON CLT.IDENTIFIER = ONM.FKCLIENT_T\n"
      + "WHERE ONM.IBMSNAP_LOGMARKER > ##TIMESTAMP##\n" + "UNION ALL\n" + "SELECT CLT.IDENTIFIER\n"
      + "FROM #SCHEMA#.CLIENT_T CLT WHERE CLT.IBMSNAP_LOGMARKER > ##TIMESTAMP##";

  private transient ReplicatedOtherClientNameDao denormDao;

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param dao Relationship View DAO
   * @param denormDao de-normalized DAO
   * @param esDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public OtherClientNameIndexerJob(final ReplicatedAkaDao dao,
      final ReplicatedOtherClientNameDao denormDao, final ElasticsearchDao esDao,
      @LastRunFile final String lastJobRunTimeFilename, final ObjectMapper mapper,
      @CmsSessionFactory SessionFactory sessionFactory) {
    super(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
    this.denormDao = denormDao;
  }

  @Override
  public String getPrepLastChangeSQL() {
    return INSERT_CLIENT_LAST_CHG;
  }

  @Override
  public ReplicatedOtherClientName extract(ResultSet rs) throws SQLException {
    return ReplicatedOtherClientName.mapRowToBean(rs);
  }

  @Override
  public Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    return ReplicatedOtherClientName.class;
  }

  @Override
  public ReplicatedAkas normalizeSingle(final List<ReplicatedOtherClientName> recs) {
    return recs != null && !recs.isEmpty() ? normalize(recs).get(0) : null;
  }

  @Override
  public List<ReplicatedAkas> normalize(final List<ReplicatedOtherClientName> recs) {
    return EntityNormalizer.<ReplicatedAkas, ReplicatedOtherClientName>normalizeList(recs);
  }

  @Override
  public String getDriverTable() {
    String ret = null;
    final Table tbl = this.denormDao.getEntityClass().getDeclaredAnnotation(Table.class);
    if (tbl != null) {
      ret = tbl.name();
    }

    return ret;
  }

  @Override
  protected UpdateRequest prepareUpsertRequest(ElasticSearchPerson esp, ReplicatedAkas p)
      throws IOException {

    // If at first you don't succeed, cheat. :-)
    StringBuilder buf = new StringBuilder();
    buf.append("{\"akas\":[");

    if (!p.getAkas().isEmpty()) {
      try {
        buf.append(p.getAkas().stream().map(ElasticTransformer::jsonify).sorted(String::compareTo)
            .collect(Collectors.joining(",")));
      } catch (Exception e) {
        JobLogs.raiseError(LOGGER, e, "ERROR SERIALIZING OTHER CLIENT NAMES! {}", e.getMessage());
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
  public String getInitialLoadViewName() {
    return "MQT_OTHER_CLIENT_NAME";
  }

  /**
   * @deprecated delete after old legacy_source_table attribute is removed from ES
   */
  @Override
  @Deprecated
  public String getLegacySourceTable() {
    return "OCL_NM_T";
  }

  /**
   * Optional method to customize JDBC ORDER BY clause on initial load.
   * 
   * @return custom ORDER BY clause for JDBC query
   */
  @Override
  public String getJdbcOrderBy() {
    return " ORDER BY x.FKCLIENT_T ";
  }

  @Override
  public String getInitialLoadQuery(String dbSchemaName) {
    StringBuilder buf = new StringBuilder();
    buf.append("SELECT x.* FROM ");
    buf.append(dbSchemaName);
    buf.append(".");
    buf.append(getInitialLoadViewName());
    buf.append(" x ");

    if (!getOpts().isLoadSealedAndSensitive()) {
      buf.append(" WHERE x.CLIENT_SENSITIVITY_IND = 'N' ");
    }

    buf.append(getJdbcOrderBy()).append(" FOR READ ONLY WITH UR ");
    return buf.toString();
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    JobRunner.runStandalone(OtherClientNameIndexerJob.class, args);
  }

}
