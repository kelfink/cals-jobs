package gov.ca.cwds.jobs;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedOtherClientNameDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherClientName;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.inject.LastRunFile;
import gov.ca.cwds.jobs.util.transform.EntityNormalizer;

/**
 * Job to load Other Client Name from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class OtherClientNameIndexerJob
    extends BasePersonIndexerJob<ReplicatedOtherClientName, ReplicatedOtherClientName> {

  private static final Logger LOGGER = LogManager.getLogger(OtherClientNameIndexerJob.class);

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param dao OtherClientName DAO
   * @param elasticsearchDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public OtherClientNameIndexerJob(final ReplicatedOtherClientNameDao dao,
      final ElasticsearchDao elasticsearchDao, @LastRunFile final String lastJobRunTimeFilename,
      final ObjectMapper mapper, @CmsSessionFactory SessionFactory sessionFactory) {
    super(dao, elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  @Override
  protected ReplicatedOtherClientName normalizeSingle(List<ReplicatedOtherClientName> recs) {
    return normalize(recs).get(0);
  }

  @Override
  protected List<ReplicatedOtherClientName> normalize(List<ReplicatedOtherClientName> recs) {
    return EntityNormalizer
        .<ReplicatedOtherClientName, ReplicatedOtherClientName>normalizeList(recs);
  }

  @Override
  protected UpdateRequest prepareUpsertRequest(ElasticSearchPerson esp, ReplicatedOtherClientName p)
      throws IOException {

    // If at first you don't succeed, cheat. :-)
    StringBuilder buf = new StringBuilder();
    buf.append("{\"akas\":[");

    // if (!p.getRelations().isEmpty()) {
    // try {
    // buf.append(p.getRelations().stream().map(this::jsonify).sorted(String::compareTo)
    // .collect(Collectors.joining(",")));
    // } catch (Exception e) {
    // LOGGER.error("ERROR SERIALIZING OTHER CLIENT NAMES", e);
    // throw new JobsException(e);
    // }
    // }

    buf.append("]}");

    final String insertJson = mapper.writeValueAsString(esp);
    final String updateJson = buf.toString();

    final String alias = esDao.getConfig().getElasticsearchAlias();
    final String docType = esDao.getConfig().getElasticsearchDocType();

    return new UpdateRequest(alias, docType, esp.getId()).doc(updateJson)
        .upsert(new IndexRequest(alias, docType, esp.getId()).source(insertJson));
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

  @Override
  public String getViewName() {
    return "OCL_NM_T";
  }

  @Override
  @Deprecated
  protected String getLegacySourceTable() {
    return getViewName();
  }

  /**
   * Optional method to customize JDBC ORDER BY clause on initial load.
   * 
   * @return custom ORDER BY clause for JDBC query
   */
  @Override
  public String getJdbcOrderBy() {
    return " ORDER BY x.fkclient_t ";
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    runMain(OtherClientNameIndexerJob.class, args);
  }

}
