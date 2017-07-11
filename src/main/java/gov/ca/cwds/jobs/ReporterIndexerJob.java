package gov.ca.cwds.jobs;

import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedReporterDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedReporter;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.inject.LastRunFile;

/**
 * Job to load reporters from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class ReporterIndexerJob
    extends BasePersonIndexerJob<ReplicatedReporter, ReplicatedReporter> {

  /**
   * Construct job with all required dependencies.
   * 
   * @param dao Client DAO
   * @param esDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public ReporterIndexerJob(final ReplicatedReporterDao dao, final ElasticsearchDao esDao,
      @LastRunFile final String lastJobRunTimeFilename, final ObjectMapper mapper,
      @CmsSessionFactory SessionFactory sessionFactory) {
    super(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  @Override
  protected String getIdColumn() {
    return "FKREFERL_T";
  }

  @Override
  @Deprecated
  protected String getLegacySourceTable() {
    return "REPTR_T";
  }

  // @Override
  // protected List<Pair<String, String>> getPartitionRanges() {
  // List<Pair<String, String>> ret = new ArrayList<>();
  // ret.add(Pair.of(" ", "CpE9999999"));
  // ret.add(Pair.of("CpE9999999", "EE99999998"));
  // ret.add(Pair.of("EE99999998", "GUE9999997"));
  // ret.add(Pair.of("GUE9999997", "I999999996"));
  // ret.add(Pair.of("I999999996", "LpE9999995"));
  // ret.add(Pair.of("LpE9999995", "NE99999994"));
  // ret.add(Pair.of("NE99999994", "PUE9999993"));
  // ret.add(Pair.of("PUE9999993", "R999999992"));
  // ret.add(Pair.of("R999999992", "UpE9999991"));
  // ret.add(Pair.of("UpE9999991", "WE99999990"));
  // ret.add(Pair.of("WE99999990", "YUE999999Z"));
  // ret.add(Pair.of("YUE999999Z", "099999999Y"));
  // ret.add(Pair.of("099999999Y", "3pE999999X"));
  // ret.add(Pair.of("3pE999999X", "5E9999999W"));
  // ret.add(Pair.of("5E9999999W", "7UE999999V"));
  // ret.add(Pair.of("7UE999999V", "999999999U"));
  // return ret;
  // }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    runMain(ReporterIndexerJob.class, args);
  }

}
