package gov.ca.cwds.jobs;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.ns.EsIntakeScreeningDao;
import gov.ca.cwds.dao.ns.IntakeParticipantDao;
import gov.ca.cwds.data.ApiTypedIdentifier;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPerson.ESOptionalCollection;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.model.cms.JobResultSetAware;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.ns.EsIntakeScreening;
import gov.ca.cwds.data.persistence.ns.IntakeParticipant;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.inject.NsSessionFactory;
import gov.ca.cwds.jobs.inject.LastRunFile;
import gov.ca.cwds.jobs.transform.EntityNormalizer;

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

  private static final ESOptionalCollection[] KEEP_COLLECTIONS =
      new ESOptionalCollection[] {ESOptionalCollection.SCREENING};

  private EsIntakeScreeningDao viewDao;

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param normalizedDao Intake Screening DAO
   * @param viewDao view Dao
   * @param esDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public IntakeScreeningJob(final IntakeParticipantDao normalizedDao,
      final EsIntakeScreeningDao viewDao, final ElasticsearchDao esDao,
      @LastRunFile final String lastJobRunTimeFilename, final ObjectMapper mapper,
      @NsSessionFactory SessionFactory sessionFactory) {
    super(normalizedDao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
    this.viewDao = viewDao;
  }

  @Override
  protected void threadExtractJdbc() {
    Thread.currentThread().setName("extract");
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

  @Override
  protected Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    return EsIntakeScreening.class;
  }

  @Override
  public String getViewName() {
    return "VW_SCREENING_HISTORY";
  }

  /**
   * Which optional ES collections to retain for insert JSON. Child classes that populate optional
   * collections should override this method.
   * 
   * @return array of optional collections to keep in insert JSON
   */
  @Override
  protected ESOptionalCollection[] keepCollections() {
    return KEEP_COLLECTIONS;
  }

  /**
   * Get the optional element name populated by this job or null if none.
   * 
   * @return optional element name populated by this job or null if none
   */
  @Override
  protected String getOptionalElementName() {
    return "screenings";
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void setInsertCollections(ElasticSearchPerson esp, IntakeParticipant t,
      List<? extends ApiTypedIdentifier<String>> list) {
    esp.setScreenings((List<ElasticSearchPerson.ElasticSearchPersonScreening>) list);
  }

  /**
   * Return the optional collection used to build the update JSON, if any. Child classes that
   * populate optional collections should override this method.
   * 
   * @param esp ES person document object
   * @param t normalized type
   * @return List of ES person elements
   */
  @Override
  protected List<? extends ApiTypedIdentifier<String>> getOptionalCollection(
      ElasticSearchPerson esp, IntakeParticipant t) {
    return esp.getScreenings();
  }

  @Override
  protected List<IntakeParticipant> normalize(List<EsIntakeScreening> recs) {
    return EntityNormalizer.<IntakeParticipant, EsIntakeScreening>normalizeList(recs);
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

