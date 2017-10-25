package gov.ca.cwds.jobs;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedOtherAdultInPlacemtHomeDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherAdultInPlacemtHome;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.annotation.LastRunFile;
import gov.ca.cwds.jobs.schedule.JobRunner;
import gov.ca.cwds.jobs.service.NeutronElasticValidator;

/**
 * Job to load Other Adult In Placement Home from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class MSearchJob extends
    BasePersonIndexerJob<ReplicatedOtherAdultInPlacemtHome, ReplicatedOtherAdultInPlacemtHome> {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(MSearchJob.class);

  private transient NeutronElasticValidator validator;

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param dao OtherAdultInPlacemtHome DAO
   * @param esDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   * @param validator document validation logic
   */
  @Inject
  public MSearchJob(final ReplicatedOtherAdultInPlacemtHomeDao dao, final ElasticsearchDao esDao,
      @LastRunFile final String lastJobRunTimeFilename, final ObjectMapper mapper,
      @CmsSessionFactory SessionFactory sessionFactory, final NeutronElasticValidator validator) {
    super(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
    this.validator = validator;
  }

  /**
   * Run in full mode!
   */
  @Override
  public Date executeJob(Date lastSuccessfulRunTime) {
    LOGGER.info("MSEARCH!");
    final Client esClient = this.esDao.getClient();

    final SearchRequestBuilder srb2 = esClient.prepareSearch().setQuery(QueryBuilders
        .multiMatchQuery("N6dhOan15A", "cases.focus_child.legacy_descriptor.legacy_id"));
    final MultiSearchResponse sr =
        esClient.prepareMultiSearch().add(esClient.prepareSearch().setQuery(QueryBuilders.idsQuery()
            .addIds("Ahr3T2S0BN", "Bn0LhX6aah", "DUy4ET400b", "AkxX6G50Ki"))).add(srb2).get();

    // Fetch **ALL** individual responses from MultiSearchResponse#getResponses().
    long totalHits = 0;
    for (MultiSearchResponse.Item item : sr.getResponses()) {
      final SearchResponse response = item.getResponse();
      final SearchHits hits = response.getHits();
      totalHits += hits.getTotalHits();

      for (SearchHit hit : hits.getHits()) {
        LOGGER.info("hit: {}",
            ToStringBuilder.reflectionToString(hit, ToStringStyle.MULTI_LINE_STYLE)); // NOSONAR
      }
    }

    LOGGER.info("es host: {}", validator.getEsDao().getConfig().getElasticsearchHost());
    LOGGER.info("total hits: {}", totalHits);
    return lastSuccessfulRunTime;
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    JobRunner.runStandalone(MSearchJob.class, args);
  }

}
