package gov.ca.cwds.jobs;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.ns.EsIntakeScreeningDao;
import gov.ca.cwds.dao.ns.IntakeScreeningDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.model.cms.JobResultSetAware;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.ns.EsIntakeScreening;
import gov.ca.cwds.data.persistence.ns.IntakeScreening;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.inject.NsSessionFactory;
import gov.ca.cwds.jobs.inject.LastRunFile;

/**
 * Job to load Clients from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class NsScreeningJob extends BasePersonIndexerJob<IntakeScreening, EsIntakeScreening>
    implements JobResultSetAware<EsIntakeScreening> {

  private static final Logger LOGGER = LogManager.getLogger(NsScreeningJob.class);

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
  public NsScreeningJob(final IntakeScreeningDao normalizedDao,
      final EsIntakeScreeningDao denormalizedDao, final ElasticsearchDao elasticsearchDao,
      @LastRunFile final String lastJobRunTimeFilename, final ObjectMapper mapper,
      @NsSessionFactory SessionFactory sessionFactory) {
    super(normalizedDao, elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
    this.viewDao = denormalizedDao;
  }

  @Override
  protected void initLoadStage1ReadMaterializedRecords() {
    Thread.currentThread().setName("reader");
    LOGGER.warn("BEGIN: Stage #1: NS View Reader");

    try {
      final List<EsIntakeScreening> results = this.viewDao.findAll();

      for (EsIntakeScreening es : results) {
        // Hand the baton to the next runner ...
        denormalizedQueue.putLast(es);
      }

    } catch (Exception e) {
      LOGGER.error("ERROR READING PG VIEW", e);
      throw new JobsException("ERROR READING PG VIEW", e);
    } finally {
      isReaderDone = true;
    }

    LOGGER.warn("DONE: Stage #1: NS View Reader");
  }

  @Override
  protected Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    return EsIntakeScreening.class;
  }

  @Override
  public String getMqtName() {
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
      runJob(NsScreeningJob.class, args);
    } catch (JobsException e) {
      LOGGER.error("STOPPING BATCH: " + e.getMessage(), e);
      throw e;
    }
  }

}

