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

  // @Override
  // protected UpdateRequest prepareUpsertRequest(ElasticSearchPerson esp, IntakeParticipant p)
  // throws IOException {
  //
  // StringBuilder buf = new StringBuilder();
  // buf.append("{\"screenings\":[");
  //
  // if (!p.getScreenings().isEmpty()) {
  // try {
  // buf.append(p.getScreenings().values().stream().map(this::jsonify).sorted(String::compareTo)
  // .collect(Collectors.joining(",")));
  // } catch (Exception e) {
  // LOGGER.error("ERROR SERIALIZING SCREENING", e);
  // throw new JobsException(e);
  // }
  // }
  //
  // buf.append("]}");
  //
  // final String insertJson = mapper.writeValueAsString(esp);
  // final String updateJson = buf.toString();
  // LOGGER.info("updateJson: {}", updateJson);
  //
  // final String alias = esDao.getConfig().getElasticsearchAlias();
  // final String docType = esDao.getConfig().getElasticsearchDocType();
  //
  // // WARNING: XContentType.JSON option adds escapes in 2.x.
  // return new UpdateRequest(alias, docType, esp.getId()).doc(updateJson)
  // .upsert(new IndexRequest(alias, docType, esp.getId()).source(insertJson));
  // }

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

