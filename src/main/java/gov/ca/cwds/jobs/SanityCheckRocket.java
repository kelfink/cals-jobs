package gov.ca.cwds.jobs;

import java.util.Date;

import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedOtherAdultInPlacemtHomeDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherAdultInPlacemtHome;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.component.AtomLaunchScheduler;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.FlightRecorder;
import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.neutron.jetpack.ConditionalLogger;
import gov.ca.cwds.neutron.jetpack.JetPackLogger;
import gov.ca.cwds.neutron.jetpack.JobLogs;

/**
 * Test Elasticsearch mass search capability for automatic validation.
 * 
 * @author CWDS API Team
 */
public class SanityCheckRocket extends
    BasePersonIndexerJob<ReplicatedOtherAdultInPlacemtHome, ReplicatedOtherAdultInPlacemtHome> {

  private static final long serialVersionUID = 1L;

  private static final ConditionalLogger LOGGER = new JetPackLogger(SanityCheckRocket.class);

  private transient AtomLaunchScheduler launchScheduler;

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param dao OtherAdultInPlacemtHome DAO
   * @param esDao ElasticSearch DAO
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   * @param jobHistory job history
   * @param opts command line options
   * @param launchScheduler launch scheduler
   */
  @Inject
  public SanityCheckRocket(final ReplicatedOtherAdultInPlacemtHomeDao dao,
      final ElasticsearchDao esDao, final ObjectMapper mapper,
      @CmsSessionFactory SessionFactory sessionFactory, FlightRecorder jobHistory, FlightPlan opts,
      AtomLaunchScheduler launchScheduler) {
    super(dao, esDao, opts.getLastRunLoc(), mapper, sessionFactory, jobHistory, opts);
    this.launchScheduler = launchScheduler;
  }

  @Override
  public Date executeJob(Date lastSuccessfulRunTime) {
    LOGGER.info("MSEARCH!");
    final Client esClient = this.esDao.getClient();

    final SearchRequestBuilder srb2 = esClient.prepareSearch().setQuery(QueryBuilders
        .multiMatchQuery("N6dhOan15A", "cases.focus_child.legacy_descriptor.legacy_id"));
    final MultiSearchResponse sr =
        esClient.prepareMultiSearch()
            .add(esClient.prepareSearch().setQuery(QueryBuilders.idsQuery().addIds("Ahr3T2S0BN",
                "Bn0LhX6aah", "DUy4ET400b", "AkxX6G50Ki", "E5pf1dg0Py", "CtMFii209X")))
            .add(srb2).get();

    long totalHits = 0;
    for (MultiSearchResponse.Item item : sr.getResponses()) {
      final SearchResponse response = item.getResponse();
      final SearchHits hits = response.getHits();
      totalHits += hits.getTotalHits();

      try {
        for (SearchHit hit : hits.getHits()) {
          final String json = hit.getSourceAsString();
          LOGGER.info("json: {}", json);
          final ElasticSearchPerson person = readPerson(json);
          LOGGER.info("person: {}", person);
        }
      } catch (NeutronException e) {
        LOGGER.warn("whatever", e);
      }
    }

    LOGGER.info("total hits: {}", totalHits);

    try {
      launchScheduler.stopScheduler(false);
    } catch (Exception e) {
      JobLogs.runtime(LOGGER, e, "FAILED TO STOP SCHEDULER! {}", e.getMessage());
    }

    return lastSuccessfulRunTime;
  }

  public AtomLaunchScheduler getLaunchScheduler() {
    return launchScheduler;
  }

  public void setLaunchScheduler(AtomLaunchScheduler launchScheduler) {
    this.launchScheduler = launchScheduler;
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    LaunchCommand.runStandalone(SanityCheckRocket.class, args);
  }

}
