package gov.ca.cwds.jobs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedCollateralIndividualDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedCollateralIndividual;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.inject.LastRunFile;

/**
 * Job to load collateral individuals from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class CollateralIndividualIndexerJob
    extends BasePersonIndexerJob<ReplicatedCollateralIndividual> {

  private static final Logger LOGGER = LogManager.getLogger(CollateralIndividualIndexerJob.class);

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param mainDao Attorney DAO
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
  protected List<Pair<String, String>> getPartitionRanges() {
    List<Pair<String, String>> ret = new ArrayList<>();
    ret.add(Pair.of(" ", "CpE9999999"));
    ret.add(Pair.of("CpE9999999", "EE99999998"));
    ret.add(Pair.of("EE99999998", "GUE9999997"));
    ret.add(Pair.of("GUE9999997", "I999999996"));
    ret.add(Pair.of("I999999996", "LpE9999995"));
    ret.add(Pair.of("LpE9999995", "NE99999994"));
    ret.add(Pair.of("NE99999994", "PUE9999993"));
    ret.add(Pair.of("PUE9999993", "R999999992"));
    ret.add(Pair.of("R999999992", "UpE9999991"));
    ret.add(Pair.of("UpE9999991", "WE99999990"));
    ret.add(Pair.of("WE99999990", "YUE999999Z"));
    ret.add(Pair.of("YUE999999Z", "099999999Y"));
    ret.add(Pair.of("099999999Y", "3pE999999X"));
    ret.add(Pair.of("3pE999999X", "5E9999999W"));
    ret.add(Pair.of("5E9999999W", "7UE999999V"));
    ret.add(Pair.of("7UE999999V", "999999999U"));
    return ret;
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    LOGGER.info("Run Collateral Individual indexer job");
    try {
      runJob(CollateralIndividualIndexerJob.class, args);
    } catch (JobsException e) {
      LOGGER.error("STOPPING BATCH: " + e.getMessage(), e);
      throw e;
    }
  }

}
