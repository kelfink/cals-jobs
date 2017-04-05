package gov.ca.cwds.jobs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedClientDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedClient;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.inject.LastRunFile;

/**
 * Job to load Clients from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class ClientIndexerJob extends BasePersonIndexerJob<ReplicatedClient> {

  private static final Logger LOGGER = LogManager.getLogger(ClientIndexerJob.class);

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param clientDao Client DAO
   * @param elasticsearchDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public ClientIndexerJob(final ReplicatedClientDao clientDao,
      final ElasticsearchDao elasticsearchDao, @LastRunFile final String lastJobRunTimeFilename,
      final ObjectMapper mapper, @CmsSessionFactory SessionFactory sessionFactory) {
    super(clientDao, elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  @Override
  protected List<Pair<String, String>> getPartitionRanges() {
    List<Pair<String, String>> ret = new ArrayList<>();

    // LINUX TEST (ASCII):
    ret.add(Pair.of("0000000000", "ZZZZZZZZZZ"));

    // PRODUCTION (EBCDIC):
    // ret.add(Pair.of(" ", "B3bMRWu8NV"));
    // ret.add(Pair.of("B3bMRWu8NV", "DW5GzxJ30A"));
    // ret.add(Pair.of("DW5GzxJ30A", "FNOBbaG6qq"));
    // ret.add(Pair.of("FNOBbaG6qq", "HJf1EJe25X"));
    // ret.add(Pair.of("HJf1EJe25X", "JCoyq0Iz36"));
    // ret.add(Pair.of("JCoyq0Iz36", "LvijYcj01S"));
    // ret.add(Pair.of("LvijYcj01S", "Npf4LcB3Lr"));
    // ret.add(Pair.of("Npf4LcB3Lr", "PiJ6a0H49S"));
    // ret.add(Pair.of("PiJ6a0H49S", "RbL4aAL34A"));
    // ret.add(Pair.of("RbL4aAL34A", "S3qiIdg0BN"));
    // ret.add(Pair.of("S3qiIdg0BN", "0Ltok9y5Co"));
    // ret.add(Pair.of("0Ltok9y5Co", "2CFeyJd49S"));
    // ret.add(Pair.of("2CFeyJd49S", "4w3QDw136B"));
    // ret.add(Pair.of("4w3QDw136B", "6p9XaHC10S"));
    // ret.add(Pair.of("6p9XaHC10S", "8jw5J580MQ"));
    // ret.add(Pair.of("8jw5J580MQ", "9999999999"));
    return ret;
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    LOGGER.info("Run Client indexer job");
    try {
      runJob(ClientIndexerJob.class, args);
    } catch (JobsException e) {
      LOGGER.error("STOPPING BATCH: " + e.getMessage(), e);
      throw e;
    }
  }

}

