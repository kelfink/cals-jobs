package gov.ca.cwds.jobs;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedRelationshipsDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.EsRelationship;
import gov.ca.cwds.data.persistence.cms.ReplicatedRelationships;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.inject.LastRunFile;
import gov.ca.cwds.jobs.util.JobLogUtils;
import gov.ca.cwds.jobs.util.jdbc.JobResultSetAware;
import gov.ca.cwds.jobs.util.jdbc.NeutronDB2Utils;
import gov.ca.cwds.jobs.util.transform.EntityNormalizer;

/**
 * Job to load familial relationships from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class RelationshipIndexerJob
    extends BasePersonIndexerJob<ReplicatedRelationships, EsRelationship>
    implements JobResultSetAware<EsRelationship> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RelationshipIndexerJob.class);

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
  protected void prepHibernatePull(Session session, Transaction txn, final Date lastRunTime)
      throws SQLException {
    final Work work = new Work() {
      @Override
      public void execute(Connection con) throws SQLException {
        con.setSchema(getDBSchemaName());
        con.setAutoCommit(false);
        NeutronDB2Utils.enableParallelism(con);

        final StringBuilder buf = new StringBuilder();
        buf.append("TIMESTAMP('")
            .append(new SimpleDateFormat(LEGACY_TIMESTAMP_FORMAT).format(lastRunTime)).append("')");

        final String sql = INSERT_CLIENT_LAST_CHG.replaceAll("#SCHEMA#", getDBSchemaName())
            .replaceAll("##TIMESTAMP##", buf.toString());
        LOGGER.info("Prep SQL: {}", sql);

        try (final Statement stmt =
            con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
          LOGGER.info("Find referrals new/changed since {}", lastRunTime);
          final int cntInsClientReferral = stmt.executeUpdate(sql);
          LOGGER.info("Total relationships new/changed: {}", cntInsClientReferral);
        } finally {
          // The statement closes automatically.
        }

      }
    };
    session.doWork(work);
  }

  @Override
  public EsRelationship extract(ResultSet rs) throws SQLException {
    return EsRelationship.mapRow(rs);
  }

  @Override
  protected Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
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
    StringBuilder buf = new StringBuilder();
    buf.append("SELECT x.* FROM ");
    buf.append(dbSchemaName);
    buf.append(".");
    buf.append(getInitialLoadViewName());
    buf.append(" x ");

    if (!getOpts().isLoadSealedAndSensitive()) {
      buf.append(" WHERE x.THIS_SENSITIVITY_IND = 'N' AND x.RELATED_SENSITIVITY_IND = 'N' ");
    }

    buf.append(getJdbcOrderBy()).append(" FOR READ ONLY WITH UR ");
    return buf.toString();
  }

  @Override
  protected UpdateRequest prepareUpsertRequest(ElasticSearchPerson esp, ReplicatedRelationships p)
      throws IOException {

    // If at first you don't succeed, cheat. :-)
    final StringBuilder buf = new StringBuilder();
    buf.append("{\"relationships\":[");

    if (!p.getRelations().isEmpty()) {
      try {
        buf.append(p.getRelations().stream().map(this::jsonify).sorted(String::compareTo)
            .collect(Collectors.joining(",")));
      } catch (Exception e) {
        JobLogUtils.raiseError(LOGGER, e, "ERROR SERIALIZING RELATIONSHIPS! {}", e.getMessage());
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
  protected ReplicatedRelationships normalizeSingle(List<EsRelationship> recs) {
    return !recs.isEmpty() ? normalize(recs).get(0) : null;
  }

  @Override
  protected List<ReplicatedRelationships> normalize(List<EsRelationship> recs) {
    return EntityNormalizer.<ReplicatedRelationships, EsRelationship>normalizeList(recs);
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    runStandalone(RelationshipIndexerJob.class, args);
  }

}
