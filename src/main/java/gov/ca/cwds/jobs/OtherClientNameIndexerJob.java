package gov.ca.cwds.jobs;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
import gov.ca.cwds.jobs.inject.JobRunner;
import gov.ca.cwds.jobs.inject.LastRunFile;
import gov.ca.cwds.jobs.util.JobLogs;
import gov.ca.cwds.jobs.util.jdbc.JobResultSetAware;
import gov.ca.cwds.jobs.util.transform.ElasticTransformer;
import gov.ca.cwds.jobs.util.transform.EntityNormalizer;

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
    final StringBuilder buf = new StringBuilder();
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

    return new UpdateRequest(alias, docType, esp.getId()).doc(updateJson, XContentType.JSON).upsert(
        new IndexRequest(alias, docType, esp.getId()).source(insertJson, XContentType.JSON));
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
  public boolean providesInitialKeyRanges() {
    return true;
  }

  @Override
  protected List<Pair<String, String>> getPartitionRanges() {
    List<Pair<String, String>> ret = new ArrayList<>(16);

    final boolean isMainframe = isDB2OnZOS();
    if (isMainframe && (getDBSchemaName().toUpperCase().endsWith("RSQ")
        || getDBSchemaName().toUpperCase().endsWith("REP"))) {
      // ----------------------------
      // z/OS, large data set:
      // ORDER: a,z,A,Z,0,9
      // ----------------------------
      ret.add(Pair.of("aaaaaaaaaa", "B3bMRWu8NV"));
      ret.add(Pair.of("B3bMRWu8NV", "DW5GzxJ30A"));
      ret.add(Pair.of("DW5GzxJ30A", "FNOBbaG6qq"));
      ret.add(Pair.of("FNOBbaG6qq", "HJf1EJe25X"));
      ret.add(Pair.of("HJf1EJe25X", "JCoyq0Iz36"));
      ret.add(Pair.of("JCoyq0Iz36", "LvijYcj01S"));
      ret.add(Pair.of("LvijYcj01S", "Npf4LcB3Lr"));
      ret.add(Pair.of("Npf4LcB3Lr", "PiJ6a0H49S"));
      ret.add(Pair.of("PiJ6a0H49S", "RbL4aAL34A"));
      ret.add(Pair.of("RbL4aAL34A", "S3qiIdg0BN"));
      ret.add(Pair.of("S3qiIdg0BN", "0Ltok9y5Co"));
      ret.add(Pair.of("0Ltok9y5Co", "2CFeyJd49S"));
      ret.add(Pair.of("2CFeyJd49S", "4w3QDw136B"));
      ret.add(Pair.of("4w3QDw136B", "6p9XaHC10S"));
      ret.add(Pair.of("6p9XaHC10S", "8jw5J580MQ"));
      ret.add(Pair.of("8jw5J580MQ", "9999999999"));

      ret = limitRange(ret); // command line range restriction
    } else if (isMainframe) {
      // ----------------------------
      // z/OS, small data set:
      // ORDER: a,z,A,Z,0,9
      // ----------------------------
      ret.add(Pair.of("aaaaaaaaaa", "9999999999"));
    } else {
      // ----------------------------
      // Linux:
      // ORDER: 0,9,a,A,z,Z
      // ----------------------------
      ret.add(Pair.of("0000000000", "ZZZZZZZZZZ"));
    }

    return ret;
  }

  @Override
  public String getInitialLoadQuery(String dbSchemaName) {
    final StringBuilder buf = new StringBuilder();
    buf.append("SELECT x.* FROM ").append(dbSchemaName).append(".").append(getInitialLoadViewName())
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
    JobRunner.runStandalone(OtherClientNameIndexerJob.class, args);
  }

}
