package gov.ca.cwds.generic.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import gov.ca.cwds.data.ApiTypedIdentifier;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPerson.ESOptionalCollection;
import gov.ca.cwds.data.es.ElasticSearchPersonScreening;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.generic.dao.ns.EsIntakeScreeningDao;
import gov.ca.cwds.generic.dao.ns.IntakeParticipantDao;
import gov.ca.cwds.generic.data.persistence.ns.EsIntakeScreening;
import gov.ca.cwds.generic.data.persistence.ns.IntakeParticipant;
import gov.ca.cwds.generic.jobs.exception.JobsException;
import gov.ca.cwds.generic.jobs.inject.JobRunner;
import gov.ca.cwds.generic.jobs.inject.LastRunFile;
import gov.ca.cwds.generic.jobs.util.jdbc.JobResultSetAware;
import gov.ca.cwds.generic.jobs.util.transform.EntityNormalizer;
import gov.ca.cwds.inject.NsSessionFactory;
import java.util.List;
import javax.persistence.Table;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Job loads Intake Screenings from PostgreSQL into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class IntakeScreeningJob extends BasePersonIndexerJob<IntakeParticipant, EsIntakeScreening>
    implements JobResultSetAware<EsIntakeScreening> {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(IntakeScreeningJob.class);

  private static final ESOptionalCollection[] KEEP_COLLECTIONS =
      new ESOptionalCollection[] {ESOptionalCollection.SCREENING};

  private transient EsIntakeScreeningDao viewDao;

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
  protected void threadRetrieveByJdbc() {
    Thread.currentThread().setName("extract");
    LOGGER.info("BEGIN: Stage #1: NS View Reader");

    try {
      final List<EsIntakeScreening> results = this.viewDao.findAll();
      for (EsIntakeScreening es : results) {
        queueNormalize.putLast(es);
      }

    } catch (Exception e) {
      markFailed();
      throw new JobsException("ERROR READING PG VIEW", e);
    } finally {
      markRetrieveDone();
    }

    LOGGER.info("DONE: Stage #1: NS View Reader");
  }

  @Override
  public Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    return EsIntakeScreening.class;
  }

  @Override
  public String getInitialLoadViewName() {
    return getDenormalizedClass().getDeclaredAnnotation(Table.class).name();
  }

  /**
   * Which optional ES collections to retain for insert JSON. Child classes that populate optional
   * collections should override this method.
   * 
   * @return array of optional collections to keep in insert JSON
   */
  @Override
  public ESOptionalCollection[] keepCollections() {
    return KEEP_COLLECTIONS;
  }

  @Override
  public String getOptionalElementName() {
    return "screenings";
  }

  @SuppressWarnings("unchecked")
  @Override
  public void setInsertCollections(ElasticSearchPerson esp, IntakeParticipant t,
      List<? extends ApiTypedIdentifier<String>> list) {
    esp.setScreenings((List<ElasticSearchPersonScreening>) list);
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
  public List<? extends ApiTypedIdentifier<String>> getOptionalCollection(ElasticSearchPerson esp,
      IntakeParticipant t) {
    return esp.getScreenings();
  }

  @Override
  public List<IntakeParticipant> normalize(List<EsIntakeScreening> recs) {
    return EntityNormalizer.<IntakeParticipant, EsIntakeScreening>normalizeList(recs);
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    JobRunner.runStandalone(IntakeScreeningJob.class, args);
  }

}
