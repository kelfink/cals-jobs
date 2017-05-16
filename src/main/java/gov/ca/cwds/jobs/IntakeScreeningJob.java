package gov.ca.cwds.jobs;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.ns.EsIntakeScreeningDao;
import gov.ca.cwds.dao.ns.IntakeParticipantDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.model.cms.JobResultSetAware;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.ns.EsIntakeScreening;
import gov.ca.cwds.data.persistence.ns.IntakeParticipant;
import gov.ca.cwds.data.persistence.ns.IntakeScreening;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.inject.NsSessionFactory;
import gov.ca.cwds.jobs.inject.LastRunFile;

// For Elasticsearch jsonBuilder():
// import static org.elasticsearch.common.xcontent.XContentFactory.*;

/**
 * Job to load Intake Screening from PostgreSQL into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class IntakeScreeningJob extends BasePersonIndexerJob<IntakeParticipant, EsIntakeScreening>
    implements JobResultSetAware<EsIntakeScreening> {

  private static final Logger LOGGER = LogManager.getLogger(IntakeScreeningJob.class);

  private EsIntakeScreeningDao viewDao;

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param normalizedDao Intake Screening DAO
   * @param denormalizedDao view Dao
   * @param elasticsearchDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public IntakeScreeningJob(final IntakeParticipantDao normalizedDao,
      final EsIntakeScreeningDao denormalizedDao, final ElasticsearchDao elasticsearchDao,
      @LastRunFile final String lastJobRunTimeFilename, final ObjectMapper mapper,
      @NsSessionFactory SessionFactory sessionFactory) {
    super(normalizedDao, elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
    this.viewDao = denormalizedDao;
  }

  @Override
  protected void threadExtractJdbc() {
    Thread.currentThread().setName("reader");
    LOGGER.warn("BEGIN: Stage #1: NS View Reader");

    try {
      final List<EsIntakeScreening> results = this.viewDao.findAll();
      for (EsIntakeScreening es : results) {
        // Hand the baton to the next runner ...
        queueTransform.putLast(es);
      }

    } catch (Exception e) {
      fatalError = true;
      LOGGER.error("ERROR READING PG VIEW", e);
      throw new JobsException("ERROR READING PG VIEW", e);
    } finally {
      doneExtract = true;
    }

    LOGGER.warn("DONE: Stage #1: NS View Reader");
  }

  protected String serializeScreening(IntakeScreening s) {
    String ret = "";
    try {
      ret = mapper.writeValueAsString(s.toEsScreening());
    } catch (Exception e) { // NOSONAR
      LOGGER.warn("ERROR SERIALIZING SCREENING ID {} TO JSON", s.getId());
    }
    return ret;
  }

  @Override
  protected UpdateRequest prepareUpsertRequest(ElasticSearchPerson esp, IntakeParticipant p)
      throws IOException {

    // If at first you don't succeed, cheat. :-)
    StringBuilder buf = new StringBuilder();
    buf.append("{\"screenings\":[");

    if (!p.getScreenings().isEmpty()) {
      try {
        buf.append(p.getScreenings().values().stream().map(this::serializeScreening)
            .sorted(String::compareTo).collect(Collectors.joining(",")));
      } catch (Exception e) {
        LOGGER.error("ERROR SERIALIZING SCREENING", e);
        throw new JobsException(e);
      }
    }

    buf.append("]}");

    final String insertJson = mapper.writeValueAsString(esp);
    final String updateJson = buf.toString();
    LOGGER.info("updateJson: {}", updateJson);

    final String alias = esDao.getConfig().getElasticsearchAlias();
    final String docType = esDao.getConfig().getElasticsearchDocType();

    return new UpdateRequest(alias, docType, esp.getId()).doc(updateJson, XContentType.JSON)
        .upsert(new IndexRequest(alias, docType, esp.getId()).source(insertJson, XContentType.JSON));
  }

  @Override
  protected Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    return EsIntakeScreening.class;
  }

  @Override
  public String getViewName() {
    return "VW_SCREENING_HISTORY";
  }

  @Override
  protected IntakeParticipant reduceSingle(List<EsIntakeScreening> recs) {
    final List<IntakeParticipant> results = reduce(recs);
    return !results.isEmpty() ? reduce(recs).get(0) : null;
  }

  @Override
  protected List<IntakeParticipant> reduce(List<EsIntakeScreening> recs) {
    // The "transform" step would typically run in the same thread.
    // Therefore, you *could* safely reuse the same map object.
    final int len = (int) (recs.size() * 1.25);
    Map<Object, IntakeParticipant> map = new LinkedHashMap<>(len);
    for (PersistentObject rec : recs) {
      ApiGroupNormalizer<IntakeParticipant> reducer = (EsIntakeScreening) rec;
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
    LOGGER.info("Run Intake Screening job");
    try {
      runJob(IntakeScreeningJob.class, args);
    } catch (Exception e) {
      LOGGER.fatal("FATAL ERROR! STOPPING BATCH: " + e.getMessage(), e);
      throw e;
    }
  }

}

