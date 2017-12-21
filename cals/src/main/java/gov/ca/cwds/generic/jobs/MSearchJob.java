package gov.ca.cwds.generic.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.generic.dao.cms.ReplicatedOtherAdultInPlacemtHomeDao;
import gov.ca.cwds.generic.data.persistence.cms.rep.ReplicatedOtherAdultInPlacemtHome;
import gov.ca.cwds.generic.jobs.inject.JobRunner;
import gov.ca.cwds.generic.jobs.inject.LastRunFile;
import gov.ca.cwds.inject.CmsSessionFactory;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Job to load Other Adult In Placement Home from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class MSearchJob extends
    BasePersonIndexerJob<ReplicatedOtherAdultInPlacemtHome, ReplicatedOtherAdultInPlacemtHome> {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(MSearchJob.class);

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param dao OtherAdultInPlacemtHome DAO
   * @param esDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public MSearchJob(final ReplicatedOtherAdultInPlacemtHomeDao dao, final ElasticsearchDao esDao,
      @LastRunFile final String lastJobRunTimeFilename, final ObjectMapper mapper,
      @CmsSessionFactory SessionFactory sessionFactory) {
    super(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  @Override
  protected void doInitialLoadJdbc() {
    extractHibernate();
  }

  @Override
  protected int extractHibernate() {
    LOGGER.info("MSEARCH!");
    final Client client = this.esDao.getClient();

    final SearchRequestBuilder srb1 =
        client.prepareSearch().setQuery(QueryBuilders.idsQuery().addIds("JRRwMqs06Q")).setSize(1);
    final SearchRequestBuilder srb2 = client.prepareSearch().setQuery(QueryBuilders
        .multiMatchQuery("N6dhOan15A", "cases.focus_child.legacy_descriptor.legacy_id")).setSize(1);
    final MultiSearchResponse sr = client.prepareMultiSearch().add(srb1).add(srb2).get();

    // Fetch **ALL** individual responses from MultiSearchResponse#getResponses().
    long totalHits = 0;
    for (MultiSearchResponse.Item item : sr.getResponses()) {
      final SearchResponse response = item.getResponse();
      totalHits += response.getHits().getTotalHits();

      for (SearchHit hit : response.getHits().getHits()) {
        LOGGER.info("hit: {}",
            ToStringBuilder.reflectionToString(hit, ToStringStyle.DEFAULT_STYLE)); // NOSONAR
      }
    }

    LOGGER.info("hits: {}", totalHits);
    return (int) totalHits;
  }

  /**
   * @deprecated scheduled for removal
   */
  @Deprecated
  @Override
  public String getLegacySourceTable() {
    return "OTH_ADLT";
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
