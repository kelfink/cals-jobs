package gov.ca.cwds.jobs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Table;

import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedAkaDao;
import gov.ca.cwds.dao.cms.ReplicatedOtherClientNameDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.ReplicatedAkas;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherClientName;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.FlightRecorder;
import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.jobs.util.JobLogs;
import gov.ca.cwds.jobs.util.jdbc.NeutronJdbcUtils;
import gov.ca.cwds.jobs.util.jdbc.NeutronRowMapper;
import gov.ca.cwds.neutron.util.transform.ElasticTransformer;
import gov.ca.cwds.neutron.util.transform.EntityNormalizer;

/**
 * Job to load Other Client Name from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class OtherClientNameIndexerJob
    extends BasePersonIndexerJob<ReplicatedAkas, ReplicatedOtherClientName>
    implements NeutronRowMapper<ReplicatedOtherClientName> {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(OtherClientNameIndexerJob.class);

  private static final String INSERT_CLIENT_LAST_CHG =
      "INSERT INTO GT_ID (IDENTIFIER)\n" + "SELECT CLT.IDENTIFIER AS CLIENT_ID\n"
          + "FROM OCL_NM_T ONM\n" + "JOIN CLIENT_T CLT ON CLT.IDENTIFIER = ONM.FKCLIENT_T\n"
          + "WHERE ONM.IBMSNAP_LOGMARKER > ?\n" + "UNION ALL\n" + "SELECT CLT.IDENTIFIER\n"
          + "FROM CLIENT_T CLT WHERE CLT.IBMSNAP_LOGMARKER > ?";

  private transient ReplicatedOtherClientNameDao denormDao;

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param dao Relationship View DAO
   * @param denormDao de-normalized DAO
   * @param esDao ElasticSearch DAO
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   * @param jobHistory job history
   * @param opts command line options
   */
  @Inject
  public OtherClientNameIndexerJob(final ReplicatedAkaDao dao,
      final ReplicatedOtherClientNameDao denormDao, final ElasticsearchDao esDao,
      final ObjectMapper mapper, @CmsSessionFactory SessionFactory sessionFactory,
      FlightRecorder jobHistory, FlightPlan opts) {
    super(dao, esDao, opts.getLastRunLoc(), mapper, sessionFactory, jobHistory, opts);
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
      throws NeutronException {

    final StringBuilder buf = new StringBuilder();
    buf.append("{\"akas\":[");

    if (!p.getAkas().isEmpty()) {
      try {
        buf.append(p.getAkas().stream().map(ElasticTransformer::jsonify).sorted(String::compareTo)
            .collect(Collectors.joining(",")));
      } catch (Exception e) {
        throw JobLogs.runtime(LOGGER, e, "ERROR SERIALIZING CLIENT ALIASES! {}", e.getMessage());
      }
    }

    buf.append("]}");

    String insertJson;
    try {
      insertJson = mapper.writeValueAsString(esp);
    } catch (JsonProcessingException e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO WRITE OBJECT TO JSON! {}", e.getMessage());
    }
    final String updateJson = buf.toString();

    final String alias = esDao.getConfig().getElasticsearchAlias();
    final String docType = esDao.getConfig().getElasticsearchDocType();

    return new UpdateRequest(alias, docType, esp.getId()).doc(updateJson, XContentType.JSON).upsert(
        new IndexRequest(alias, docType, esp.getId()).source(insertJson, XContentType.JSON));
  }

  @Override
  public String getInitialLoadViewName() {
    return "MQT_OTHER_CLIENT_NAME";
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
  public boolean isInitialLoadJdbc() {
    return true;
  }

  @Override
  protected List<Pair<String, String>> getPartitionRanges() {
    return NeutronJdbcUtils.getCommonPartitionRanges16(this);
  }

  @Override
  public String getInitialLoadQuery(String dbSchemaName) {
    final StringBuilder buf = new StringBuilder();
    buf.append("SELECT x.* FROM ").append(dbSchemaName).append('.').append(getInitialLoadViewName())
        .append(" x ");

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
    LaunchCommand.runStandalone(OtherClientNameIndexerJob.class, args);
  }

}
