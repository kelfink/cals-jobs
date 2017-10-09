package gov.ca.cwds.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedPersonCasesDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPersonCase;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.EsPersonCase;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonCases;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.inject.LastRunFile;
import gov.ca.cwds.jobs.util.JobLogs;
import gov.ca.cwds.jobs.util.jdbc.JobResultSetAware;
import gov.ca.cwds.jobs.util.transform.ElasticTransformer;
import gov.ca.cwds.jobs.util.transform.EntityNormalizer;

/**
 * Job to load case history from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public abstract class CaseHistoryIndexerJob
    extends BasePersonIndexerJob<ReplicatedPersonCases, EsPersonCase>
    implements JobResultSetAware<EsPersonCase> {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(CaseHistoryIndexerJob.class);

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param clientDao Case history view DAO
   * @param elasticsearchDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public CaseHistoryIndexerJob(final ReplicatedPersonCasesDao clientDao,
      final ElasticsearchDao elasticsearchDao, @LastRunFile final String lastJobRunTimeFilename,
      final ObjectMapper mapper, @CmsSessionFactory SessionFactory sessionFactory) {
    super(clientDao, elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  @Override
  public String getPrepLastChangeSQL() {
    return "INSERT INTO GT_ID (IDENTIFIER)\nSELECT CAS.IDENTIFIER\nFROM CASE_T CAS "
        + "\nWHERE CAS.IBMSNAP_LOGMARKER > ? \nUNION \nSELECT CAS.IDENTIFIER "
        + "\nFROM CASE_T CAS\nLEFT JOIN CHLD_CLT CCL ON CCL.FKCLIENT_T = CAS.FKCHLD_CLT "
        + "\nLEFT JOIN CLIENT_T CLC ON CLC.IDENTIFIER = CCL.FKCLIENT_T "
        + "\nWHERE CCL.IBMSNAP_LOGMARKER > ? \nUNION\nSELECT CAS.IDENTIFIER \nFROM CASE_T CAS "
        + "\nLEFT JOIN CHLD_CLT CCL ON CCL.FKCLIENT_T = CAS.FKCHLD_CLT "
        + "\nLEFT JOIN CLIENT_T CLC ON CLC.IDENTIFIER = CCL.FKCLIENT_T "
        + "\nWHERE CLC.IBMSNAP_LOGMARKER > ? \nUNION \nSELECT CAS.IDENTIFIER "
        + "\nFROM CASE_T CAS " + "\nLEFT JOIN CHLD_CLT CCL ON CCL.FKCLIENT_T = CAS.FKCHLD_CLT "
        + "\nLEFT JOIN CLIENT_T CLC ON CLC.IDENTIFIER = CCL.FKCLIENT_T "
        + "\nJOIN CLN_RELT CLR ON CLR.FKCLIENT_T = CCL.FKCLIENT_T AND ((CLR.CLNTRELC BETWEEN 187 and 214) OR "
        + "\n(CLR.CLNTRELC BETWEEN 245 and 254) OR (CLR.CLNTRELC BETWEEN 282 and 294) OR (CLR.CLNTRELC IN (272, 273, 5620, 6360, 6361))) "
        + "\nWHERE CLR.IBMSNAP_LOGMARKER > ? \nUNION \nSELECT CAS.IDENTIFIER "
        + "\nFROM CASE_T CAS " + "\nLEFT JOIN CHLD_CLT CCL ON CCL.FKCLIENT_T = CAS.FKCHLD_CLT "
        + "\nLEFT JOIN CLIENT_T CLC ON CLC.IDENTIFIER = CCL.FKCLIENT_T "
        + "\nJOIN CLN_RELT CLR ON CLR.FKCLIENT_T = CCL.FKCLIENT_T AND ((CLR.CLNTRELC BETWEEN 187 and 214) OR "
        + "\n(CLR.CLNTRELC BETWEEN 245 and 254) OR (CLR.CLNTRELC BETWEEN 282 and 294) OR (CLR.CLNTRELC IN (272, 273, 5620, 6360, 6361))) "
        + "\nJOIN CLIENT_T CLP ON CLP.IDENTIFIER = CLR.FKCLIENT_0 "
        + "\nWHERE CLP.IBMSNAP_LOGMARKER > ? ";
  }

  @Override
  public String getInitialLoadQuery(String dbSchemaName) {
    final StringBuilder buf = new StringBuilder();

    buf.append("SELECT x.* FROM ").append(dbSchemaName).append(".").append(getInitialLoadViewName())
        .append(" x ");

    if (!getOpts().isLoadSealedAndSensitive()) {
      buf.append(" WHERE x.LIMITED_ACCESS_CODE = 'N' ");
    }

    buf.append(getJdbcOrderBy()).append(" FOR READ ONLY WITH UR");
    return buf.toString();
  }

  /**
   * If sealed or sensitive data must NOT be loaded then any records indexed with sealed or
   * sensitive flag must be deleted.
   */
  @Override
  public boolean mustDeleteLimitedAccessRecords() {
    return !getOpts().isLoadSealedAndSensitive();
  }

  @Override
  protected UpdateRequest prepareUpsertRequest(ElasticSearchPerson esp, ReplicatedPersonCases cases)
      throws IOException {

    final StringBuilder buf = new StringBuilder();
    buf.append("{\"cases\":[");

    List<ElasticSearchPersonCase> esPersonCases = cases.getCases();
    esp.setCases(esPersonCases);

    if (!esPersonCases.isEmpty()) {
      try {
        buf.append(esPersonCases.stream().map(ElasticTransformer::jsonify).sorted(String::compareTo)
            .collect(Collectors.joining(",")));
      } catch (Exception e) {
        JobLogs.raiseError(LOGGER, e, "ERROR SERIALIZING CASES! {}", e.getMessage());
      }
    }

    buf.append("]}");

    final String insertJson = mapper.writeValueAsString(esp);
    final String updateJson = buf.toString();
    LOGGER.trace("updateJson: {}", updateJson);

    final String alias = esDao.getConfig().getElasticsearchAlias();
    final String docType = esDao.getConfig().getElasticsearchDocType();

    return new UpdateRequest(alias, docType, esp.getId()).doc(updateJson, XContentType.JSON).upsert(
        new IndexRequest(alias, docType, esp.getId()).source(insertJson, XContentType.JSON));
  }

  @Override
  public ReplicatedPersonCases normalizeSingle(final List<EsPersonCase> recs) {
    return recs != null && !recs.isEmpty() ? normalize(recs).get(0) : null;
  }

  @Override
  public List<ReplicatedPersonCases> normalize(final List<EsPersonCase> recs) {
    return EntityNormalizer.<ReplicatedPersonCases, EsPersonCase>normalizeList(recs);
  }

  @Override
  public boolean providesInitialKeyRanges() {
    return true;
  }

  @Override
  protected List<Pair<String, String>> getPartitionRanges() {
    List<Pair<String, String>> ret = new ArrayList<>();

    final boolean isMainframe = isDB2OnZOS();
    if (isMainframe && (getDBSchemaName().toUpperCase().endsWith("RSQ")
        || getDBSchemaName().toUpperCase().endsWith("REP"))) {
      // ----------------------------
      // z/OS, large data set:
      // ORDER: a,z,A,Z,0,9
      // ----------------------------
      ret.add(Pair.of("aaaaaaaaaa", "ACWv4wR7XG"));
      ret.add(Pair.of("ACWv4wR7XG", "A6mPxR5Ilb"));
      ret.add(Pair.of("A6mPxR5Ilb", "By4e5CkE1Y"));
      ret.add(Pair.of("By4e5CkE1Y", "B2VVjwvIzg"));
      ret.add(Pair.of("B2VVjwvIzg", "Cvu2NaFJQh"));
      ret.add(Pair.of("Cvu2NaFJQh", "CY2KjEP1Jz"));
      ret.add(Pair.of("CY2KjEP1Jz", "DsBBvmOC7k"));
      ret.add(Pair.of("DsBBvmOC7k", "DWD2Aww6vg"));
      ret.add(Pair.of("DWD2Aww6vg", "EpHUhIeJPL"));
      ret.add(Pair.of("EpHUhIeJPL", "ESz8bHjH02"));
      ret.add(Pair.of("ESz8bHjH02", "FmgkniGBg1"));
      ret.add(Pair.of("FmgkniGBg1", "FND4ccFFJR"));
      ret.add(Pair.of("FND4ccFFJR", "GiD6FT6BD0"));
      ret.add(Pair.of("GiD6FT6BD0", "GMnU7C06IC"));
      ret.add(Pair.of("GMnU7C06IC", "HeXctUnJ0m"));
      ret.add(Pair.of("HeXctUnJ0m", "HIQcSR7CId"));
      ret.add(Pair.of("HIQcSR7CId", "Ibs7YSPCYV"));
      ret.add(Pair.of("Ibs7YSPCYV", "IEm27VXGHu"));
      ret.add(Pair.of("IEm27VXGHu", "I6I3TkGFuB"));
      ret.add(Pair.of("I6I3TkGFuB", "JBHNzeB8N6"));
      ret.add(Pair.of("JBHNzeB8N6", "J5wi6rRCus"));
      ret.add(Pair.of("J5wi6rRCus", "KylCQWnBml"));
      ret.add(Pair.of("KylCQWnBml", "K10gdgFAfo"));
      ret.add(Pair.of("K10gdgFAfo", "LuI9JvsAAr"));
      ret.add(Pair.of("LuI9JvsAAr", "LX3HLPv5cT"));
      ret.add(Pair.of("LX3HLPv5cT", "MrjMMnw7nF"));
      ret.add(Pair.of("MrjMMnw7nF", "MUVQnW6D02"));
      ret.add(Pair.of("MUVQnW6D02", "NobPqq48bS"));
      ret.add(Pair.of("NobPqq48bS", "NR6ilQBL7M"));
      ret.add(Pair.of("NR6ilQBL7M", "Oj5e5GDJJ2"));
      ret.add(Pair.of("Oj5e5GDJJ2", "ONVolXj8bU"));
      ret.add(Pair.of("ONVolXj8bU", "PgLWwgB0KH"));
      ret.add(Pair.of("PgLWwgB0KH", "PJwxpOiEIK"));
      ret.add(Pair.of("PJwxpOiEIK", "Qerlpwt3rK"));
      ret.add(Pair.of("Qerlpwt3rK", "QG098CcAVo"));
      ret.add(Pair.of("QG098CcAVo", "RaGhRfW42v"));
      ret.add(Pair.of("RaGhRfW42v", "REA1TMKO8F"));
      ret.add(Pair.of("REA1TMKO8F", "R7hkXmY3Q9"));
      ret.add(Pair.of("R7hkXmY3Q9", "SzQ7uFaEby"));
      ret.add(Pair.of("SzQ7uFaEby", "S2qE3aO3cV"));
      ret.add(Pair.of("S2qE3aO3cV", "TxprGutIqm"));
      ret.add(Pair.of("TxprGutIqm", "TZ93nQ9BYF"));
      ret.add(Pair.of("TZ93nQ9BYF", "0fNIQDT7WA"));
      ret.add(Pair.of("0fNIQDT7WA", "0Ju1gmGIim"));
      ret.add(Pair.of("0Ju1gmGIim", "1bdKe9tNJX"));
      ret.add(Pair.of("1bdKe9tNJX", "1E5UydmLZ4"));
      ret.add(Pair.of("1E5UydmLZ4", "17OD3f1MhW"));
      ret.add(Pair.of("17OD3f1MhW", "2Ay7CEHAKJ"));
      ret.add(Pair.of("2Ay7CEHAKJ", "224GZzPDRI"));
      ret.add(Pair.of("224GZzPDRI", "3x5JWBfFec"));
      ret.add(Pair.of("3x5JWBfFec", "31yJ4q3LXy"));
      ret.add(Pair.of("31yJ4q3LXy", "4uC8unkBDb"));
      ret.add(Pair.of("4uC8unkBDb", "4YsUNr59us"));
      ret.add(Pair.of("4YsUNr59us", "5rkUIyGD0v"));
      ret.add(Pair.of("5rkUIyGD0v", "5TOieGe3T6"));
      ret.add(Pair.of("5TOieGe3T6", "6nCZQnQCta"));
      ret.add(Pair.of("6nCZQnQCta", "6Rppbk16oI"));
      ret.add(Pair.of("6Rppbk16oI", "7jUdFIE7IS"));
      ret.add(Pair.of("7jUdFIE7IS", "7NMj9cuAWz"));
      ret.add(Pair.of("7NMj9cuAWz", "8gnsaW4EQf"));
      ret.add(Pair.of("8gnsaW4EQf", "8JeC4AvNtB"));
      ret.add(Pair.of("8JeC4AvNtB", "9cA1GT74tY"));
      ret.add(Pair.of("9cA1GT74tY", "9GnY4XWCG7"));
      ret.add(Pair.of("9GnY4XWCG7", "9999999999"));

      ret = limitRange(ret); // command line range restriction
    } else if (isMainframe) {
      // ----------------------------
      // z/OS, small data set:
      // ORDER: a,z,A,Z,0,9
      // ----------------------------
      ret.add(Pair.of("aaaaaaaaaa", "9999999999"));
    } else {
      // ----------------------------
      // Linux or small data set:
      // ORDER: 0,9,a,A,z,Z
      // ----------------------------
      ret.add(Pair.of("0000000000", "ZZZZZZZZZZ"));
    }

    return ret;
  }

}
