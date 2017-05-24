package gov.ca.cwds.jobs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedClientDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.model.cms.JobResultSetAware;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.EsClientAddress;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedClient;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.inject.LastRunFile;

/**
 * Job to load Clients from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class ClientIndexerJob extends BasePersonIndexerJob<ReplicatedClient, EsClientAddress>
    implements JobResultSetAware<EsClientAddress> {

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
  public EsClientAddress extractFromResultSet(ResultSet rs) throws SQLException {
    return EsClientAddress.extractFromResultSet(rs);
  }

  @Override
  protected Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    return EsClientAddress.class;
  }

  @Override
  public String getViewName() {
    return "ES_CLIENT_ADDRESS";
  }

  @Override
  protected String getLegacySourceTable() {
    return "CLIENT_T";
  }

  @Override
  protected List<ReplicatedClient> reduce(List<EsClientAddress> recs) {
    final int len = (int) (recs.size() * 1.25);
    Map<Object, ReplicatedClient> map = new LinkedHashMap<>(len);
    for (PersistentObject rec : recs) {
      ApiGroupNormalizer<ReplicatedClient> reducer = (EsClientAddress) rec;
      reducer.reduce(map);
    }

    return map.values().stream().collect(Collectors.toList());
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
    } catch (Exception e) {
      LOGGER.fatal("STOPPING BATCH: " + e.getMessage(), e);
      throw e;
    }
  }

}

