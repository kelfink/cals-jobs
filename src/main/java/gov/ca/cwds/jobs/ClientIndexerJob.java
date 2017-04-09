package gov.ca.cwds.jobs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.BatchBucket;
import gov.ca.cwds.dao.cms.EsClientAddress;
import gov.ca.cwds.dao.cms.ReplicatedClientDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedClient;
import gov.ca.cwds.data.std.ApiReduce;
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
  protected Class<? extends ApiReduce<? extends PersistentObject>> getMqtClass() {
    return EsClientAddress.class;
  }

  @Override
  protected boolean isReducer() {
    return true;
  }

  @Override
  // protected List<T> reduce(List<ApiReduce<? extends PersistentObject>> recs) {
  protected List<ReplicatedClient> reduce(List<? extends PersistentObject> recs) {
    final int len = (int) (recs.size() * 1.25);
    Map<Object, ReplicatedClient> map = new LinkedHashMap<>(len);
    for (PersistentObject rec : recs) {
      ApiReduce<ReplicatedClient> reducer = (EsClientAddress) rec;
      reducer.reduce(map);
    }

    return map.values().stream().collect(Collectors.toList());
  }

  @Override
  protected List<Pair<String, String>> getPartitionRanges() {
    List<Pair<String, String>> ret = new ArrayList<>();
    List<BatchBucket> buckets = buildBucketList("CLIENT_T");

    for (BatchBucket b : buckets) {
      LOGGER.warn("DYNAMIC CLIENT BUCKET: {} to {}", b.getMinId(), b.getMaxId());
      ret.add(Pair.of(b.getMinId(), b.getMaxId()));
    }

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

