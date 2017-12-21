package gov.ca.cwds.generic.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.generic.dao.cms.ReplicatedCollateralIndividualDao;
import gov.ca.cwds.generic.data.persistence.cms.rep.ReplicatedCollateralIndividual;
import gov.ca.cwds.generic.jobs.inject.JobRunner;
import gov.ca.cwds.generic.jobs.inject.LastRunFile;
import gov.ca.cwds.inject.CmsSessionFactory;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.SessionFactory;

/**
 * Job to load collateral individuals from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public final class CollateralIndividualIndexerJob
    extends BasePersonIndexerJob<ReplicatedCollateralIndividual, ReplicatedCollateralIndividual> {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param mainDao collateral individual DAO
   * @param elasticsearchDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public CollateralIndividualIndexerJob(final ReplicatedCollateralIndividualDao mainDao,
      final ElasticsearchDao elasticsearchDao, @LastRunFile final String lastJobRunTimeFilename,
      final ObjectMapper mapper, @CmsSessionFactory SessionFactory sessionFactory) {
    super(mainDao, elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  @Override
  public int getJobTotalBuckets() {
    return 12;
  }

  /**
   * @deprecated method scheduled for deletion
   */
  @Override
  @Deprecated
  public String getLegacySourceTable() {
    return "COLTRL_T";
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

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    JobRunner.runStandalone(CollateralIndividualIndexerJob.class, args);
  }

}
