package gov.ca.cwds.jobs;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.ns.EsIntakeScreeningDao;
import gov.ca.cwds.dao.ns.IntakeScreeningDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.model.cms.JobResultSetAware;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.ns.EsIntakeScreening;
import gov.ca.cwds.data.persistence.ns.IntakeScreening;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.inject.NsSessionFactory;
import gov.ca.cwds.jobs.inject.LastRunFile;

/**
 * Job to load Intake Screening from PostgreSQL into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class IntakeScreeningJob extends BasePersonIndexerJob<IntakeScreening, EsIntakeScreening>
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
  public IntakeScreeningJob(final IntakeScreeningDao normalizedDao,
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
      LOGGER.error("ERROR READING PG VIEW", e);
      throw new JobsException("ERROR READING PG VIEW", e);
    } finally {
      doneExtract = true;
    }

    LOGGER.warn("DONE: Stage #1: NS View Reader");
  }

  @Override
  protected Pair<String, String> prepareJson(ElasticSearchPerson esp, IntakeScreening s)
      throws JsonProcessingException, IOException {
    final String insertJson = mapper.writeValueAsString(esp);

    String updateJson = insertJson;
    try {
      updateJson = jsonBuilder().startObject().startObject("screenings")
          .array("screenings", mapper.writeValueAsString(s.toEsScreening())).endObject().string();
    } catch (Exception e) {
      LOGGER.error("ERROR BUILDING SCREENING UPDATE: {}", e.getMessage(), e);
      throw new JobsException("ERROR BUILDING SCREENING UPDATE", e);
    }

    return Pair.<String, String>of(insertJson, updateJson);
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
  protected IntakeScreening reduceSingle(List<EsIntakeScreening> recs) {
    return reduce(recs).get(0);
  }

  @Override
  protected List<IntakeScreening> reduce(List<EsIntakeScreening> recs) {
    final int len = (int) (recs.size() * 1.25);
    Map<Object, IntakeScreening> map = new LinkedHashMap<>(len);
    for (PersistentObject rec : recs) {
      ApiGroupNormalizer<IntakeScreening> reducer = (EsIntakeScreening) rec;
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
    } catch (JobsException e) {
      LOGGER.error("STOPPING BATCH: " + e.getMessage(), e);
      throw e;
    }
  }

}

