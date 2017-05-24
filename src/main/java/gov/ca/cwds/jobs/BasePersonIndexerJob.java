package gov.ca.cwds.jobs;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.query.NativeQuery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import gov.ca.cwds.dao.ApiLegacyAware;
import gov.ca.cwds.dao.ApiMultiplePersonAware;
import gov.ca.cwds.dao.ApiScreeningAware;
import gov.ca.cwds.dao.cms.BatchBucket;
import gov.ca.cwds.dao.cms.ReplicatedClientDao;
import gov.ca.cwds.data.ApiTypedIdentifier;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.DaoException;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPerson.ESOptionalCollection;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonAddress;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonPhone;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonScreening;
import gov.ca.cwds.data.es.Elasticsearch5xDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.model.cms.JobResultSetAware;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.ApiSystemCodeCache;
import gov.ca.cwds.data.persistence.cms.rep.CmsReplicatedEntity;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedClient;
import gov.ca.cwds.data.std.ApiAddressAware;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.data.std.ApiLanguageAware;
import gov.ca.cwds.data.std.ApiMultipleAddressesAware;
import gov.ca.cwds.data.std.ApiMultipleLanguagesAware;
import gov.ca.cwds.data.std.ApiMultiplePhonesAware;
import gov.ca.cwds.data.std.ApiPersonAware;
import gov.ca.cwds.data.std.ApiPhoneAware;
import gov.ca.cwds.inject.SystemCodeCache;
import gov.ca.cwds.jobs.inject.JobsGuiceInjector;
import gov.ca.cwds.jobs.inject.LastRunFile;
import gov.ca.cwds.jobs.transform.EntityNormalizer;
import gov.ca.cwds.rest.api.domain.DomainChef;


// import static org.elasticsearch.common.xcontent.XContentFactory.*;


/**
 * Base person batch job to load clients from CMS into ElasticSearch.
 * 
 * <p>
 * This class implements {@link AutoCloseable} and automatically closes common resources, such as
 * {@link Elasticsearch5xDao} and Hibernate {@link SessionFactory}.
 * </p>
 * 
 * <p>
 * <strong>Auto mode ("smart" mode)</strong> takes the same parameters as last run and determines
 * whether the job has never been run. If the last run date is older than 50 years, then then assume
 * that the job is populating ElasticSearch for the first time and run all initial batch loads.
 * </p>
 * 
 * <h3>Command Line:</h3>
 * 
 * <pre>
 * {@code java gov.ca.cwds.jobs.ClientIndexerJob -c config/local.yaml -l /Users/CWS-NS3/client_indexer_time.txt}
 * </pre>
 * 
 * @author CWDS API Team
 * @param <T> ES storable, replicated Person persistence class
 * @param <M> MQT entity class, if any, or T
 * @see JobOptions
 */
public abstract class BasePersonIndexerJob<T extends PersistentObject, M extends ApiGroupNormalizer<?>>
    extends LastSuccessfulRunJob implements AutoCloseable, JobResultSetAware<M> {

  private static final Logger LOGGER = LogManager.getLogger(BasePersonIndexerJob.class);

  private static final ESOptionalCollection[] KEEP_COLLECTIONS =
      new ESOptionalCollection[] {ESOptionalCollection.NONE};

  private static final List<? extends ApiTypedIdentifier<String>> EMPTY_OPTIONAL_LIST =
      new ArrayList<>();

  private static final int DEFAULT_BATCH_WAIT = 25;
  private static final int DEFAULT_BUCKETS = 1;

  private static final int LOG_EVERY = 5000;
  private static final int ES_BULK_SIZE = 2000;

  private static final int SLEEP_MILLIS = 2500;
  private static final int POLL_MILLIS = 3000;

  /**
   * Obsolete. Doesn't optimize on DB2 z/OS, though on "smaller" tables (single digit millions) it
   * runs just fine.
   * 
   * @see #doInitialLoadJdbc()
   */
  @Deprecated
  private static final String QUERY_BUCKET_LIST =
      "SELECT z.bucket, MIN(z.THE_ID_COL) AS minId, MAX(z.THE_ID_COL) AS maxId, COUNT(*) AS bucketCount "
          + "FROM (SELECT (y.rn / (total_cnt/THE_TOTAL_BUCKETS)) + 1 AS bucket, y.rn, y.THE_ID_COL FROM ( "
          + "SELECT c.THE_ID_COL, ROW_NUMBER() OVER (ORDER BY 1) AS rn, COUNT(*) OVER (ORDER BY 1) AS total_cnt "
          + "FROM {h-schema}THE_TABLE c ORDER BY c.THE_ID_COL) y ORDER BY y.rn "
          + ") z GROUP BY z.bucket FOR READ ONLY ";

  private static ApiSystemCodeCache systemCodes;

  /**
   * Guice Injector used for all Job instances during the life of this batch JVM.
   */
  protected static Injector injector;

  /**
   * Needed for unit tests where resources may not close properly.
   */
  private static boolean testMode = false;

  /**
   * Jackson ObjectMapper.
   */
  protected final ObjectMapper mapper;

  /**
   * Main DAO for the supported persistence class.
   */
  protected final BaseDaoImpl<T> jobDao;

  /**
   * Elasticsearch client DAO.
   */
  protected final ElasticsearchDao esDao;

  /**
   * Hibernate session factory.
   */
  protected final SessionFactory sessionFactory;

  /**
   * Command line options for this job.
   */
  protected JobOptions opts;

  /**
   * Running count of records prepared for bulk indexing.
   */
  protected AtomicInteger recsPrepared = new AtomicInteger(0);

  /**
   * Running count of records before bulk indexing.
   */
  protected AtomicInteger recsBulkBefore = new AtomicInteger(0);

  /**
   * Running count of records after bulk indexing.
   */
  protected AtomicInteger recsBulkAfter = new AtomicInteger(0);

  /**
   * Running count of errors during bulk indexing.
   */
  protected AtomicInteger recsBulkError = new AtomicInteger(0);

  /**
   * Official start time.
   */
  protected final long startTime = System.currentTimeMillis();

  /**
   * Queue of raw, denormalized records waiting to be normalized.
   */
  protected LinkedBlockingDeque<M> queueTransform = new LinkedBlockingDeque<>(100000);

  /**
   * Queue of normalized records waiting to publish to Elasticsearch.
   */
  protected LinkedBlockingDeque<T> queueLoad = new LinkedBlockingDeque<>(50000);

  /**
   * Completion flag for <strong>Extract</strong> method {@link #threadExtractJdbc()}.
   */
  protected boolean doneExtract = false;

  /**
   * Completion flag for <strong>Transform</strong> method {@link #threadTransform()}.
   */
  protected boolean doneTransform = false;

  /**
   * Completion flag for <strong>Load</strong> method {@link #threadLoad()}.
   */
  protected boolean doneLoad = false;

  // ======================
  // CONSTRUCTOR:
  // ======================

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param jobDao Person DAO, such as {@link ReplicatedClientDao}
   * @param esDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public BasePersonIndexerJob(final BaseDaoImpl<T> jobDao, final ElasticsearchDao esDao,
      @LastRunFile final String lastJobRunTimeFilename, final ObjectMapper mapper,
      SessionFactory sessionFactory) {
    super(lastJobRunTimeFilename);
    this.jobDao = jobDao;
    this.esDao = esDao;
    this.mapper = mapper;
    this.sessionFactory = sessionFactory;
  }

  /**
   * Get the view or materialized query table name, if used. Any child classes relying on a
   * denormalized view must define the name.
   * 
   * @return name of view or materialized query table or null if none
   */
  public String getViewName() {
    return null;
  }

  /**
   * Optional method to customize JDBC ORDER BY clause on initial load.
   * 
   * @return custom ORDER BY clause for JDBC
   */
  public String getJdbcOrderBy() {
    return " ORDER BY x.clt_identifier ";
  }

  /**
   * Serialize object to JSON.
   * 
   * @param obj object to serialize
   * @return JSON for this screening
   */
  protected String jsonify(Object obj) {
    String ret = "";
    try {
      ret = mapper.writeValueAsString(obj);
    } catch (Exception e) { // NOSONAR
      LOGGER.warn("ERROR SERIALIZING OBJECT {} TO JSON", obj);
    }
    return ret;
  }

  @Override
  public M extractFromResultSet(ResultSet rs) throws SQLException {
    return null;
  }

  /**
   * Log every {@link #LOG_EVERY} records.
   * 
   * @param cntr record count
   * @param action action message (extract, transform, load, etc)
   * @param args variable message arguments
   */
  protected void logEvery(int cntr, String action, String... args) {
    if (cntr > 0 && (cntr % LOG_EVERY) == 0) {
      LOGGER.info("{} {} {}", action, cntr, args);
    }
  }

  /**
   * Instantiate one Elasticsearch BulkProcessor per working thread.
   * 
   * @return Elasticsearch BulkProcessor
   */
  protected BulkProcessor buildBulkProcessor() {
    return BulkProcessor.builder(esDao.getClient(), new BulkProcessor.Listener() {
      @Override
      public void beforeBulk(long executionId, BulkRequest request) {
        recsBulkBefore.getAndAdd(request.numberOfActions());
        LOGGER.warn("Ready to execute bulk of {} actions", request.numberOfActions());
      }

      @Override
      public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
        recsBulkAfter.getAndAdd(request.numberOfActions());
        LOGGER.warn("Executed bulk of {} actions", request.numberOfActions());
      }

      @Override
      public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
        recsBulkError.getAndIncrement();
        LOGGER.error("ERROR EXECUTING BULK", failure);
      }
    }).setBulkActions(ES_BULK_SIZE).setConcurrentRequests(0).setName("jobs_bp").build();
  }

  // ======================
  // INJECTION:
  // ======================

  /**
   * Build the Guice Injector once, which is used for all Job instances during the life of this
   * batch JVM.
   * 
   * @param opts command line options
   * @return Guice Injector
   * @throws JobsException if unable to construct dependencies
   */
  protected static synchronized Injector buildInjector(final JobOptions opts) throws JobsException {
    if (injector == null) {
      try {
        injector = Guice
            .createInjector(new JobsGuiceInjector(new File(opts.esConfigLoc), opts.lastRunLoc));
      } catch (CreationException e) {
        final String msg = MessageFormat.format("Unable to create dependencies {}", e.getMessage());
        LOGGER.fatal(msg, e);
        throw new JobsException(msg, e);
      }
    }

    return injector;
  }

  /**
   * Prepare a batch job with all required dependencies.
   * 
   * @param klass batch job class
   * @param args command line arguments
   * @return batch job, ready to run
   * @param <T> Person persistence type
   * @throws JobsException if unable to parse command line or load dependencies
   */
  public static <T extends BasePersonIndexerJob<?, ?>> T newJob(final Class<T> klass,
      String... args) throws JobsException {
    try {
      final JobOptions opts = JobOptions.parseCommandLine(args);
      final T ret = buildInjector(opts).getInstance(klass);
      ret.setOpts(opts);
      return ret;
    } catch (CreationException e) {
      final String msg = MessageFormat.format("UNABLE TO CREATE DEPENDENCIES! {}", e.getMessage());
      LOGGER.error(msg, e);
      throw new JobsException(msg, e);
    }
  }

  /**
   * Batch job entry point.
   * 
   * <p>
   * This method automatically closes the Hibernate session factory and ElasticSearch DAO and EXITs
   * the JVM.
   * </p>
   * 
   * @param klass batch job class
   * @param args command line arguments
   * @param <T> Person persistence type
   * @throws JobsException unexpected runtime error
   */
  public static <T extends BasePersonIndexerJob<?, ?>> void runJob(final Class<T> klass,
      String... args) throws JobsException {
    int exitCode = 0;
    try (final T job = newJob(klass, args)) { // Close resources automatically.
      job.run();
    } catch (Throwable e) { // NOSONAR
      // Intentionally catch a Throwable, not an Exception.
      // Close resources forcibly, if necessary, by system exit.
      exitCode = 1;
      LOGGER.error("JOB FAILED: {}", e.getMessage(), e);
      throw new JobsException("JOB FAILED! " + e.getMessage(), e);
    } finally {
      // WARNING: kills the JVM in testing but may be needed to shutdown resources.
      if (!isTestMode()) {
        Runtime.getRuntime().exit(exitCode); // NOSONAR
      }
    }
  }

  // ===================
  // BUILD ES PERSON:
  // ===================

  /**
   * Handle both {@link ApiMultiplePersonAware} and {@link ApiPersonAware} implementations of type
   * T.
   * 
   * @param p instance of type T
   * @return array of person documents
   * @throws JsonProcessingException on parse error
   * @see #buildElasticSearchPersonDoc(ApiPersonAware)
   * @see #buildElasticSearchPerson(PersistentObject)
   */
  protected ElasticSearchPerson[] buildElasticSearchPersons(T p) throws JsonProcessingException {
    ElasticSearchPerson[] ret;
    if (p instanceof ApiMultiplePersonAware) {
      final ApiPersonAware[] persons = ((ApiMultiplePersonAware) p).getPersons();
      ret = new ElasticSearchPerson[persons.length];
      int i = 0;
      for (ApiPersonAware px : persons) {
        ret[i++] = buildElasticSearchPersonDoc(px);
      }
    } else {
      ret = new ElasticSearchPerson[] {buildElasticSearchPerson(p)};
    }
    return ret;
  }

  /**
   * Produce an ElasticSearchPerson suitable as an Elasticsearch person document.
   * 
   * @param p ApiPersonAware persistence object
   * @return populated ElasticSearchPerson
   * @throws JsonProcessingException if unable to serialize JSON
   */
  protected ElasticSearchPerson buildElasticSearchPerson(T p) throws JsonProcessingException {
    return buildElasticSearchPersonDoc((ApiPersonAware) p);
  }

  /**
   * Produce an ElasticSearchPerson objects suitable for an Elasticsearch person document.
   * 
   * @param p ApiPersonAware persistence object
   * @return populated ElasticSearchPerson
   * @throws JsonProcessingException if unable to serialize JSON
   */
  protected ElasticSearchPerson buildElasticSearchPersonDoc(ApiPersonAware p)
      throws JsonProcessingException {
    ApiPersonAware pa = p;

    List<String> languages = null;
    List<ElasticSearchPerson.ElasticSearchPersonPhone> phones = null;
    List<ElasticSearchPersonAddress> addresses = null;
    List<ElasticSearchPersonScreening> screenings = null;

    if (p instanceof ApiMultipleLanguagesAware) {
      ApiMultipleLanguagesAware mlx = (ApiMultipleLanguagesAware) p;
      languages = new ArrayList<>();
      for (ApiLanguageAware lx : mlx.getLanguages()) {
        final ElasticSearchPerson.ElasticSearchPersonLanguage lang =
            ElasticSearchPerson.ElasticSearchPersonLanguage.findBySysId(lx.getLanguageSysId());
        if (lang != null) {
          languages.add(lang.getDescription());
        }
      }
    } else if (p instanceof ApiLanguageAware) {
      languages = new ArrayList<>();
      ApiLanguageAware lx = (ApiLanguageAware) p;
      final ElasticSearchPerson.ElasticSearchPersonLanguage lang =
          ElasticSearchPerson.ElasticSearchPersonLanguage.findBySysId(lx.getLanguageSysId());
      if (lang != null) {
        languages.add(lang.getDescription());
      }
    }

    if (p instanceof ApiMultiplePhonesAware) {
      phones = new ArrayList<>();
      ApiMultiplePhonesAware mphx = (ApiMultiplePhonesAware) p;
      for (ApiPhoneAware phx : mphx.getPhones()) {
        phones.add(new ElasticSearchPersonPhone(phx));
      }
    } else if (p instanceof ApiPhoneAware) {
      phones = new ArrayList<>();
      ApiPhoneAware phx = (ApiPhoneAware) p;
      phones.add(new ElasticSearchPersonPhone(phx));
    }

    if (p instanceof ApiMultipleAddressesAware) {
      addresses = new ArrayList<>();
      ApiMultipleAddressesAware madrx = (ApiMultipleAddressesAware) p;
      for (ApiAddressAware adrx : madrx.getAddresses()) {
        addresses.add(new ElasticSearchPersonAddress(adrx));
      }
    } else if (p instanceof ApiAddressAware) {
      addresses = new ArrayList<>();
      addresses.add(new ElasticSearchPersonAddress((ApiAddressAware) p));
    }

    if (p instanceof ApiScreeningAware) {
      screenings = new ArrayList<>();
      for (ElasticSearchPersonScreening scr : ((ApiScreeningAware) p).getEsScreenings()) {
        screenings.add(scr);
      }
    }

    // Write persistence object to Elasticsearch Person document.
    ElasticSearchPerson ret;

    LOGGER.debug("p.getPrimaryKey()={}", p.getPrimaryKey());
    if (p.getPrimaryKey() == null) {
      LOGGER.warn("STOP");
    }

    ret = new ElasticSearchPerson(p.getPrimaryKey().toString(), // id
        pa.getFirstName(), // first name
        pa.getLastName(), // last name
        pa.getMiddleName(), // middle name
        pa.getNameSuffix(), // name suffix
        pa.getGender(), // gender
        DomainChef.cookDate(pa.getBirthDate()), // birth date
        pa.getSsn(), // SSN
        pa.getClass().getName(), // type
        this.mapper.writeValueAsString(p), // source
        null, // omit highlights
        addresses, phones, languages, screenings);

    return ret;
  }

  /**
   * Identifier column for this table. Defaults to "IDENTIFIER".
   * 
   * @return Identifier column
   */
  protected String getIdColumn() {
    return "IDENTIFIER";
  }

  /**
   * Getter for the entity class of this job's view or materialized query table, if any, or null if
   * none.
   * 
   * @return entity class of view or materialized query table
   */
  protected Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    return null;
  }

  /**
   * Default reduce method just returns the input. Child classes may customize this method to
   * convert (reduce) denormalized result sets to normalized entities.
   * 
   * @param recs entity records
   * @return unmodified entity records
   * @see EntityNormalizer
   */
  @SuppressWarnings("unchecked")
  protected List<T> reduce(List<M> recs) {
    return (List<T>) recs;
  }

  /**
   * Reduce/normalize view records for a single grouping (such as all the same client) into a
   * normalized entity bean, consisting of a parent object and its child objects.
   * 
   * @param recs denormalized view beans
   * @return normalized entity bean instance
   */
  protected T reduceSingle(List<M> recs) {
    final List<T> list = reduce(recs);
    return list != null && !list.isEmpty() ? list.get(0) : null;
  }

  /**
   * Override to customize the default number of buckets by job.
   * 
   * @return default total buckets
   */
  protected int getJobTotalBuckets() {
    return DEFAULT_BUCKETS;
  }

  /**
   * True if the Job class intends to reduce denormalized results to normalized ones.
   * 
   * @return true if class overrides {@link #reduce(List)}
   */
  protected final boolean isViewNormalizer() {
    return getDenormalizedClass() != null;
  }

  // ===================
  // COMMON JSON:
  // ===================

  /**
   * Publish a Person record to Elasticsearch with a bulk processor.
   * 
   * <p>
   * Child implementations may customize this method and generate different JSON for create/insert
   * and update to prevent overwriting data from other jobs.
   * </p>
   * 
   * @param bp {@link #buildBulkProcessor()} for this thread
   * @param t Person record to write
   * @throws JsonProcessingException if unable to serialize JSON
   * @throws IOException if unable to prepare request
   * @see #prepareUpsertRequest(ElasticSearchPerson, PersistentObject)
   */
  protected void prepareDocument(BulkProcessor bp, T t) throws IOException {
    final ElasticSearchPerson[] docs = buildElasticSearchPersons(t);
    for (ElasticSearchPerson esp : docs) {
      bp.add(prepareUpsertRequest(esp, t));
      recsPrepared.getAndIncrement();
    }
  }

  /**
   * Set optional ES person collections before serializing JSON for insert. Child classes which
   * handle optional collections should override this method.
   *
   * <p>
   * <strong>Example:</strong>
   * </p>
   * 
   * <pre>
   * {@code esp.setScreenings((List<ElasticSearchPerson.ElasticSearchPersonScreening>) col);}
   * </pre>
   * 
   * @param esp ES document, already prepared by
   *        {@link #buildElasticSearchPersonDoc(ApiPersonAware)}
   * @param t target ApiPersonAware instance
   * @param list list of ES child objects
   */
  protected void setInsertCollections(ElasticSearchPerson esp, T t,
      List<? extends ApiTypedIdentifier<String>> list) {
    // Default, no-op.
  }

  /**
   * Set optional ES person collections to null so that they are not overwritten by accident. Child
   * classes do not normally override this method.
   * 
   * @param esp ES document, already prepared by
   *        {@link #buildElasticSearchPersonDoc(ApiPersonAware)}
   * @param t target ApiPersonAware instance
   * @param elementName target ES element for update
   * @param list list of ES child objects
   * @param keep ES sections to keep
   * @throws JsonProcessingException on JSON processing error
   */
  protected void prepareInsertCollections(ElasticSearchPerson esp, T t, String elementName,
      List<? extends ApiTypedIdentifier<String>> list, ESOptionalCollection... keep)
      throws JsonProcessingException {

    // Null out optional collections for updates.
    esp.clearOptionalCollections(keep);

    // Child classes: Set optional collections before serializing the insert JSON.
    setInsertCollections(esp, t, list);
  }

  /**
   * Prepare upsert JSON (update and insert). Child classes do not normally override this method.
   * 
   * @param esp ES document, already prepared by
   *        {@link #buildElasticSearchPersonDoc(ApiPersonAware)}
   * @param t target ApiPersonAware instance
   * @param elementName target ES element for update
   * @param list list of ES child objects
   * @param keep ES sections to keep
   * @return Pair of JSON, left = update, right = insert
   * @throws JsonProcessingException on JSON processing error
   */
  protected Pair<String, String> prepareUpsertJson(ElasticSearchPerson esp, T t, String elementName,
      List<? extends ApiTypedIdentifier<String>> list, ESOptionalCollection... keep)
      throws JsonProcessingException {

    // Child classes: Set optional collections before serializing the insert JSON.
    prepareInsertCollections(esp, t, elementName, list, keep);
    final String insertJson = mapper.writeValueAsString(esp);

    String updateJson;
    if (StringUtils.isNotBlank(elementName)) {
      StringBuilder buf = new StringBuilder();
      buf.append("{\"").append(elementName).append("\":[");

      if (list != null && !list.isEmpty()) {
        buf.append(list.stream().map(this::jsonify).sorted(String::compareTo)
            .collect(Collectors.joining(",")));
      }

      buf.append("]}");
      updateJson = buf.toString();
    } else {
      updateJson = insertJson;
    }

    return Pair.of(updateJson, insertJson);
  }

  /**
   * Prepare sections of a document for update. Elasticsearch automatically updates the provided
   * sections. Some jobs should only write sub-documents, such as screenings or allegations, from a
   * new data source, like Intake PostgreSQL, but should NOT overwrite document details from legacy.
   * 
   * <p>
   * Default handler just serializes the whole ElasticSearchPerson instance to JSON and returns the
   * same JSON for both insert and update. Child classes should override this method and null out
   * any fields that should not be updated.
   * </p>
   * 
   * @param esp ES document, already prepared by
   *        {@link #buildElasticSearchPersonDoc(ApiPersonAware)}
   * @param t target ApiPersonAware instance
   * @return left = insert JSON, right = update JSON throws JsonProcessingException on JSON parse
   *         error
   * @throws JsonProcessingException on JSON parse error
   * @throws IOException on Elasticsearch disconnect
   */
  protected UpdateRequest prepareUpsertRequest(ElasticSearchPerson esp, T t) throws IOException {
    String id = esp.getId();

    // Set id and legacy id.
    if (t instanceof ApiLegacyAware) {
      ApiLegacyAware l = (ApiLegacyAware) t;
      final boolean hasLegacyId =
          StringUtils.isNotBlank(l.getLegacyId()) && l.getLegacyId().trim().length() == 10;

      if (hasLegacyId) {
        id = l.getLegacyId();
        esp.setLegacyId(id);
      } else {
        id = esp.getId();
      }
    } else if (t instanceof CmsReplicatedEntity) {
      esp.setLegacyId(t.getPrimaryKey().toString());
    }

    // Set the legacy source table, if appropriate for this job.
    if (StringUtils.isNotBlank(getLegacySourceTable())) {
      esp.setLegacySourceTable(getLegacySourceTable());
    }

    // Child classes may override these methods as needed.
    final Pair<String, String> json = prepareUpsertJson(esp, t, getOptionalElementName(),
        getOptionalCollection(esp, t), keepCollections());

    final String alias = esDao.getConfig().getElasticsearchAlias();
    final String docType = esDao.getConfig().getElasticsearchDocType();

    // Update if doc exists, insert if it does not.
    return new UpdateRequest(alias, docType, id).doc(json.getLeft())
        .upsert(new IndexRequest(alias, docType, id).source(json.getRight()));
  }

  // ===================
  // OPTIONAL ELEMENTS:
  // ===================

  /**
   * Which optional ES collections to retain for insert JSON. Child classes that populate optional
   * collections should override this method.
   * 
   * @return array of optional collections to keep in insert JSON
   */
  protected ESOptionalCollection[] keepCollections() {
    return KEEP_COLLECTIONS;
  }

  /**
   * Get the optional element name populated by this job or null if none.
   * 
   * @return optional element name populated by this job or null if none
   */
  protected String getOptionalElementName() {
    return null;
  }

  /**
   * Return the optional collection used to build the update JSON, if any. Child classes that
   * populate optional collections should override this method.
   * 
   * @param esp ES person document object
   * @param t normalized type
   * @return List of ES person elements
   */
  protected List<? extends ApiTypedIdentifier<String>> getOptionalCollection(
      ElasticSearchPerson esp, T t) {
    return EMPTY_OPTIONAL_LIST;
  }

  // =================
  // RUN THREADS:
  // =================

  /**
   * ENTRY POINT FOR INITIAL LOAD.
   * 
   * @throws IOException on JDBC error or Elasticsearch disconnect
   */
  protected void doInitialLoadJdbc() throws IOException {
    Thread.currentThread().setName("main");
    try {
      new Thread(this::threadExtractJdbc).start(); // Extract
      new Thread(this::threadTransform).start(); // Transform
      new Thread(this::threadLoad).start(); // Load

      while (!(fatalError || (doneExtract && doneTransform && doneLoad))) {
        LOGGER.debug("runInitialLoad: sleep");
        Thread.sleep(SLEEP_MILLIS);

        try {
          this.jobDao.find("abc123"); // dummy call, keep connection pool alive.
        } catch (HibernateException he) { // NOSONAR
          LOGGER.debug("USING DIRECT JDBC. IGNORE HIBERNATE ERROR: {}", he.getMessage());
        } catch (Exception e) {
          LOGGER.warn("Hibernate keep-alive error: {}", e.getMessage(), e);
        }
      }

      Thread.sleep(SLEEP_MILLIS);
      this.close();
      final long endTime = System.currentTimeMillis();
      LOGGER.warn("TOTAL ELAPSED TIME: " + ((endTime - startTime) / 1000) + " SECONDS");
      LOGGER.warn("DONE: doInitialLoadViaJdbc");

    } catch (InterruptedException ie) { // NOSONAR
      LOGGER.error("interrupted: {}", ie.getMessage(), ie);
      fatalError = true;
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      LOGGER.error("GENERAL EXCEPTION: {}", e.getMessage(), e);
      fatalError = true;
      throw new JobsException(e);
    } finally {
      doneExtract = true;
    }
  }

  /**
   * The "extract" part of ETL. Single producer, staged consumers.
   */
  protected void threadExtractJdbc() {
    Thread.currentThread().setName("extract");
    LOGGER.warn("BEGIN: Stage #1: extract");

    try {
      Connection con = jobDao.getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
          .getService(ConnectionProvider.class).getConnection();
      con.setSchema(System.getProperty("DB_CMS_SCHEMA"));
      con.setAutoCommit(false);
      con.setReadOnly(true); // WARNING: fails with Postgres.

      // Linux MQT lacks ORDER BY clause. Must sort manually.
      // Detect platform or always force ORDER BY clause.

      StringBuilder buf = new StringBuilder();
      buf.append("SELECT x.* FROM ").append(System.getProperty("DB_CMS_SCHEMA")).append(".")
          .append(getViewName()).append(" x ").append(getJdbcOrderBy()).append(" FOR READ ONLY");
      final String query = buf.toString();

      try (Statement stmt = con.createStatement()) {
        stmt.setFetchSize(5000); // faster
        stmt.setMaxRows(0);
        stmt.setQueryTimeout(100000);
        final ResultSet rs = stmt.executeQuery(query); // NOSONAR

        int cntr = 0;
        while (rs.next()) {
          // Hand the baton to the next runner ...
          logEvery(++cntr, "Retrieved", "recs");
          queueTransform.putLast(extractFromResultSet(rs));
        }

        con.commit();
      } finally {
        // The statement closes automatically.
      }

    } catch (Exception e) {
      fatalError = true;
      LOGGER.error("BATCH ERROR! {}", e.getMessage(), e);
      throw new JobsException(e.getMessage(), e);
    } finally {
      doneExtract = true;
    }

    LOGGER.warn("DONE: Stage #1: Extract");
  }

  /**
   * The "transform" part of ETL. Single thread consumer, second stage of initial load. Convert
   * denormalized view records to normalized ones and pass to the load queue.
   */
  protected void threadTransform() {
    Thread.currentThread().setName("transform");
    LOGGER.warn("BEGIN: Stage #2: Transform");

    int cntr = 0;
    Object lastId = new Object();
    M m;
    List<M> groupRecs = new ArrayList<>();

    while (!(fatalError || (doneExtract && queueTransform.isEmpty()))) {
      try {
        while ((m = queueTransform.pollFirst(POLL_MILLIS, TimeUnit.MILLISECONDS)) != null) {
          logEvery(++cntr, "Transformed", "recs");

          // NOTE: Assumes that records are sorted by group key.
          if (!lastId.equals(m.getGroupKey()) && cntr > 1) {
            final T t = reduceSingle(groupRecs);
            if (t != null) {
              queueLoad.putLast(t);
              groupRecs.clear(); // Single thread, re-use memory.
            }
          }

          groupRecs.add(m);
          lastId = m.getGroupKey();
        }

        // Last bundle.
        if (!groupRecs.isEmpty()) {
          final T t = reduceSingle(groupRecs);
          if (t != null) {
            queueLoad.putLast(t);
            groupRecs.clear(); // Single thread, re-use memory.
          }
        }

      } catch (InterruptedException e) { // NOSONAR
        LOGGER.warn("Transformer interrupted!");
        fatalError = true;
        Thread.currentThread().interrupt();
      } catch (Exception e) {
        LOGGER.fatal("Transformer: fatal error {}", e.getMessage(), e);
        fatalError = true;
        throw new JobsException("Transformer: fatal error", e);
      } finally {
        doneTransform = true;
      }
    }

    LOGGER.warn("DONE: Stage #2: Transform");
  }

  /**
   * The "load" part of ETL. Read from normalized record queue and push to ES.
   */
  protected void threadLoad() {
    Thread.currentThread().setName("load");
    final BulkProcessor bp = buildBulkProcessor();
    int cntr = 0;
    T t;

    LOGGER.warn("BEGIN: Stage #3: Loader");
    try {
      while (!(fatalError || (doneExtract && doneTransform && queueLoad.isEmpty()))) {
        while ((t = queueLoad.pollFirst(POLL_MILLIS, TimeUnit.MILLISECONDS)) != null) {
          logEvery(++cntr, "Published", "recs to ES");
          prepareDocument(bp, t);
        }
      }

      // Just to be sure ...
      while ((t = queueLoad.pollFirst(POLL_MILLIS, TimeUnit.MILLISECONDS)) != null) {
        logEvery(++cntr, "Published", "recs to ES");
        prepareDocument(bp, t);
      }

      LOGGER.info("Flush ES bulk processor ...");
      bp.flush();
      Thread.sleep(SLEEP_MILLIS);
      LOGGER.info("Flush ES bulk processor again ...");
      bp.flush();

      LOGGER.info("Waiting to close ES bulk processor ...");
      bp.awaitClose(DEFAULT_BATCH_WAIT, TimeUnit.SECONDS);
      LOGGER.info("Closed ES bulk processor");

    } catch (InterruptedException e) { // NOSONAR
      LOGGER.warn("Publisher interrupted!");
      fatalError = true;
      Thread.currentThread().interrupt();
    } catch (JsonProcessingException e) {
      fatalError = true;
      LOGGER.error("Publisher: JsonProcessingException! {}", e.getMessage(), e);
      throw new JobsException("JSON error", e);
    } catch (Exception e) {
      fatalError = true;
      LOGGER.fatal("Publisher: fatal error {}", e.getMessage(), e);
      throw new JobsException("Publisher: fatal error", e);
    } finally {
      doneLoad = true;
    }

    LOGGER.warn("DONE: Stage #3: ES loader");
  }

  // =================
  // LAST RUN MODE:
  // =================

  /**
   * ENTRY POINT FOR LAST RUN.
   *
   * <p>
   * Fetch all records for the next batch run, either by bucket or last successful run date. Pulls
   * either from an MQT via {@link #extractLastRunRecsFromView(Date)}, if
   * {@link #isViewNormalizer()} is overridden, else from the base table directly via
   * {@link #extractLastRunRecsFromTable(Date)}.
   * </p>
   * 
   * @param lastRunDt last time the batch ran successfully.
   * @return List of results to process
   * @see gov.ca.cwds.jobs.LastSuccessfulRunJob#_run(java.util.Date)
   */
  protected Date doLastRun(Date lastRunDt) {
    try {
      // One bulk processor for "last run" operations. BulkProcessor itself is thread-safe.
      final BulkProcessor bp = buildBulkProcessor();
      final List<T> results = this.isViewNormalizer() ? extractLastRunRecsFromView(lastRunDt)
          : extractLastRunRecsFromTable(lastRunDt);

      if (results != null && !results.isEmpty()) {
        LOGGER.info(MessageFormat.format("Found {0} people to index", results.size()));

        // Spawn a reasonable number of threads to process all results.
        results.stream().forEach(p -> {
          try {
            // Write persistence object to Elasticsearch Person document.
            prepareDocument(bp, p);
          } catch (JsonProcessingException e) {
            fatalError = true;
            LOGGER.error("ERROR WRITING JSON: {}", e.getMessage(), e);
            throw new JobsException("ERROR WRITING JSON", e);
          } catch (IOException e) {
            fatalError = true;
            LOGGER.error("IO EXCEPTION: {}", e.getMessage(), e);
            throw new JobsException("IO EXCEPTION", e);
          } finally {
            doneLoad = true;
          }
        });

        // Track counts.
        recsPrepared.getAndAdd(results.size());
      }

      // Give it time to finish the last batch.
      LOGGER.warn("Waiting on ElasticSearch to finish last batch ...");
      bp.awaitClose(DEFAULT_BATCH_WAIT, TimeUnit.SECONDS);
      return new Date(this.startTime);

    } catch (Exception e) {
      fatalError = true;
      LOGGER.error("General Exception: {}", e.getMessage(), e);
      throw new JobsException("General Exception: " + e.getMessage(), e);
    } finally {
      doneLoad = true;
    }
  }

  // ====================
  // RUN MODE DECISION:
  // ====================

  /**
   * Lambda runs a number of threads up to max processor cores. Queued jobs wait until a worker
   * thread is available.
   * 
   * <p>
   * Auto mode ("smart" mode) takes the same parameters as last run and determines whether the job
   * has never been run. If the last run date is older than 50 years, then then assume that the job
   * is populating ElasticSearch for the first time and run all initial batch loads.
   * </p>
   * 
   * {@inheritDoc}
   * 
   * @see gov.ca.cwds.jobs.LastSuccessfulRunJob#_run(java.util.Date)
   */
  @Override
  public Date _run(Date lastSuccessfulRunTime) {
    try {
      // GUICE DOES NOT INJECT THE SYSCODE TRANSLATOR INTO STATIC MEMBERS/METHODS.
      final ApiSystemCodeCache sysCodeCache = injector.getInstance(ApiSystemCodeCache.class);
      setSystemCodes(sysCodeCache);
      ElasticSearchPerson.setSystemCodes(sysCodeCache);

      // If the index is missing, create it.
      LOGGER.debug("Create index if missing");
      esDao.createIndexIfNeeded(esDao.getConfig().getElasticsearchAlias());
      LOGGER.debug("availableProcessors={}", Runtime.getRuntime().availableProcessors());

      // Smart/auto mode:
      final Calendar cal = Calendar.getInstance();
      cal.add(Calendar.YEAR, -50);
      final boolean autoMode = this.opts.lastRunMode && lastSuccessfulRunTime.before(cal.getTime());

      if (autoMode) {
        LOGGER.warn("AUTO MODE!");
        getOpts().setStartBucket(1);
        getOpts().setEndBucket(1);
        getOpts().setTotalBuckets(getJobTotalBuckets());

        if (this.getDenormalizedClass() != null) {
          LOGGER.warn("LOAD FROM VIEW VIA JDBC!");
          doInitialLoadJdbc();
        } else {
          LOGGER.warn("LOAD REPLICATED TABLE QUERY VIA HIBERNATE!");
          extractHibernate();
        }

      } else if (this.opts == null || this.opts.lastRunMode) {
        LOGGER.warn("LAST RUN MODE!");
        doLastRun(lastSuccessfulRunTime);
      } else {
        LOGGER.warn("DIRECT BUCKET MODE!");
        extractHibernate();
      }

      // Result stats:
      LOGGER.info("Prepared {} records to index", recsPrepared);
      LOGGER.info("STATS: \nrecsBulkBefore:  {}\nrecsBulkAfter:  {}\nrecsBulkError: {}",
          recsBulkBefore, recsBulkAfter, recsBulkError);
      LOGGER.warn("Updating last successful run time to {}", jobDateFormat.format(startTime));
      return new Date(this.startTime);

    } catch (Exception e) {
      fatalError = true;
      LOGGER.error("General Exception: {}", e.getMessage(), e);
      throw new JobsException("General Exception: " + e.getMessage(), e);
    } finally {

      // Set ETL completion flags to done.
      doneExtract = true;
      doneTransform = true;
      doneLoad = true;

      try {
        this.close();
      } catch (IOException io) {
        LOGGER.warn("IOException on close! {}", io.getMessage(), io);
      }
    }
  }

  // ===================
  // PULL RECORDS:
  // ===================

  /**
   * Pull records changed since the last successful run.
   * 
   * <p>
   * If this job defines a denormalized view entity, then pull from that. Otherwise, pull from the
   * table entity.
   * </p>
   * 
   * @param lastRunTime last successful run time
   * @return List of normalized entities
   */
  protected List<T> extractLastRunRecsFromTable(Date lastRunTime) {
    LOGGER.info("last successful run: {}", lastRunTime);
    final Class<?> entityClass = jobDao.getEntityClass();
    final String namedQueryName = entityClass.getName() + ".findAllUpdatedAfter";
    Session session = jobDao.getSessionFactory().getCurrentSession();

    Transaction txn = null;
    try {
      txn = session.beginTransaction();
      NativeQuery<T> q = session.getNamedNativeQuery(namedQueryName);
      q.setTimestamp("after", new Timestamp(lastRunTime.getTime()));

      ImmutableList.Builder<T> results = new ImmutableList.Builder<>();
      final List<T> recs = q.list();

      LOGGER.warn("FOUND {} RECORDS", recs.size());
      results.addAll(recs);

      session.clear();
      txn.commit();
      return results.build();
    } catch (HibernateException h) {
      fatalError = true;
      LOGGER.error("EXTRACT ERROR! {}", h.getMessage(), h);
      if (txn != null) {
        txn.rollback();
      }
      throw new DaoException(h);
    } finally {
      doneExtract = true;
    }
  }

  /**
   * Pull from view for last run mode.
   * 
   * @param lastRunTime last successful run time
   * @return List of normalized entities
   */
  protected List<T> extractLastRunRecsFromView(Date lastRunTime) {
    LOGGER.info("PULL VIEW: last successful run: {}", lastRunTime);

    final Class<?> entityClass = getDenormalizedClass(); // view entity class
    final String namedQueryName = entityClass.getName() + ".findAllUpdatedAfter";
    Session session = jobDao.getSessionFactory().getCurrentSession();

    Object lastId = new Object();
    Transaction txn = null;

    try {
      txn = session.beginTransaction();
      NativeQuery<M> q = session.getNamedNativeQuery(namedQueryName);
      q.setTimestamp("after", new Timestamp(lastRunTime.getTime()));

      ImmutableList.Builder<T> results = new ImmutableList.Builder<>();
      final List<M> recs = q.list();
      LOGGER.warn("FOUND {} RECORDS", recs.size());

      // Convert denormalized view rows to normalized persistence objects.
      List<M> groupRecs = new ArrayList<>();
      for (M m : recs) {
        if (!lastId.equals(m.getGroupKey()) && !groupRecs.isEmpty()) {
          results.add(reduceSingle(groupRecs));
          groupRecs.clear();
        }

        groupRecs.add(m);
        lastId = m.getGroupKey();
      }

      if (!groupRecs.isEmpty()) {
        results.add(reduceSingle(groupRecs));
      }

      session.clear();
      txn.commit();
      return results.build();
    } catch (HibernateException h) {
      LOGGER.error("EXTRACT ERROR! {}", h.getMessage(), h);
      fatalError = true;
      if (txn != null) {
        txn.rollback();
      }
      throw new JobsException(h);
    } finally {
      doneExtract = true;
    }
  }

  /**
   * Build the bucket list at runtime.
   * 
   * @param table the driver table
   * @return batch buckets
   */
  @SuppressWarnings("unchecked")
  protected List<BatchBucket> buildBucketList(String table) {
    List<BatchBucket> ret = new ArrayList<>();

    Transaction txn = null;
    try {
      LOGGER.info("FETCH DYNAMIC BUCKET LIST FOR TABLE {}", table);
      final Session session = jobDao.getSessionFactory().getCurrentSession();
      txn = session.beginTransaction();
      final long totalBuckets = opts.getTotalBuckets() < getJobTotalBuckets() ? getJobTotalBuckets()
          : opts.getTotalBuckets();
      final javax.persistence.Query q = jobDao.getSessionFactory().createEntityManager()
          .createNativeQuery(QUERY_BUCKET_LIST.replaceAll("THE_TABLE", table)
              .replaceAll("THE_ID_COL", getIdColumn()).replaceAll("THE_TOTAL_BUCKETS",
                  String.valueOf(totalBuckets)),
              BatchBucket.class);

      ret = q.getResultList();
      session.clear();
      txn.commit();
    } catch (HibernateException e) {
      LOGGER.error("BATCH ERROR! ", e);
      fatalError = true;
      if (txn != null) {
        txn.rollback();
      }
      throw new DaoException(e);
    } finally {
      doneLoad = true;
    }

    return ret;
  }

  /**
   * Get the table or view used to allocate bucket ranges. Called on full load only.
   * 
   * @return the table or view used to allocate bucket ranges
   */
  protected String getBucketDriverTable() {
    String ret = null;
    final Table tbl = this.jobDao.getEntityClass().getDeclaredAnnotation(Table.class);
    if (tbl != null) {
      ret = tbl.name();
    }

    return ret;
  }

  /**
   * Return a list of partition keys to optimize batch SELECT statements. See ReplicatedClient
   * native named query, "findPartitionedBuckets".
   * 
   * @return list of partition key pairs
   * @see ReplicatedClient
   */
  protected List<Pair<String, String>> getPartitionRanges() {
    LOGGER.warn("DETERMINE BUCKET RANGES ...");
    List<Pair<String, String>> ret = new ArrayList<>();
    List<BatchBucket> buckets = buildBucketList(getBucketDriverTable());

    for (BatchBucket b : buckets) {
      LOGGER.warn("BUCKET RANGE: {} to {}", b.getMinId(), b.getMaxId());
      ret.add(Pair.of(b.getMinId(), b.getMaxId()));
    }

    return ret;
  }

  // ===================
  // MANAGE RESOURCES:
  // ===================

  @Override
  public synchronized void close() throws IOException {
    if (fatalError || (doneExtract && doneTransform && doneLoad)) {
      LOGGER.warn("CLOSING CONNECTIONS!!");

      if (this.esDao != null) {
        LOGGER.warn("CLOSING ES DAO");
        this.esDao.close();
      }

      if (this.sessionFactory != null) {
        LOGGER.warn("CLOSING SESSION FACTORY");
        this.sessionFactory.close();
      }

    } else {
      LOGGER.warn("CLOSE: FALSE ALARM");
    }
  }

  /**
   * Finish job and close resources. Default implementation exits the JVM.
   */
  @Override
  protected synchronized void finish() {
    LOGGER.warn("FINISH JOB AND SHUTDOWN!");
    try {
      this.doneExtract = true;
      this.doneLoad = true;
      this.doneTransform = true;

      close();
      // LogManager.shutdown(); // Flush appenders.
      Thread.sleep(SLEEP_MILLIS); // NOSONAR

      // Shutdown all remaining resources, even those not attached to this job.
      Runtime.getRuntime().exit(0); // NOSONAR

    } catch (InterruptedException e) {
      fatalError = true;
      Thread.currentThread().interrupt();
    } catch (IOException ioe) {
      fatalError = true;
      LOGGER.fatal("ERROR FINISHING JOB: {}", ioe.getMessage(), ioe);
      throw new JobsException(ioe);
    }
  }

  // ===========================
  // DEPRECATED:
  // ===========================

  /**
   * Divide work into buckets: pull a unique range of identifiers so that no bucket results overlap.
   * 
   * @param minId start of identifier range
   * @param maxId end of identifier range
   * @return collection of entity results
   */
  protected List<T> pullBucketRange(String minId, String maxId) {
    LOGGER.info("PULL BUCKET RANGE {} to {}", minId, maxId);
    final Class<?> entityClass =
        getDenormalizedClass() != null ? getDenormalizedClass() : jobDao.getEntityClass();
    final String namedQueryName = entityClass.getName() + ".findBucketRange";
    Session session = jobDao.getSessionFactory().getCurrentSession();

    Transaction txn = null;
    try {
      txn = session.beginTransaction();
      NativeQuery<T> q = session.getNamedNativeQuery(namedQueryName);
      q.setString("min_id", minId).setString("max_id", maxId);

      // No reduction/normalization.
      ImmutableList.Builder<T> results = new ImmutableList.Builder<>();
      results.addAll(q.list());

      session.clear();
      txn.commit();
      return results.build();
    } catch (HibernateException e) {
      fatalError = true;
      LOGGER.error("BATCH ERROR! {}", e.getMessage(), e);
      if (txn != null) {
        txn.rollback();
      }
      throw new DaoException(e);
    }
  }

  /**
   * Pull replicated records from named query "findBucketRange".
   * 
   * <p>
   * Thread safety: both BulkProcessor are ElasticsearchDao are thread-safe.
   * </p>
   * 
   * @return number of records processed
   * @see #pullBucketRange(String, String)
   */
  protected int extractHibernate() {
    final List<Pair<String, String>> buckets = getPartitionRanges();

    for (Pair<String, String> b : buckets) {
      final String minId = b.getLeft();
      final String maxId = b.getRight();
      final List<T> results = pullBucketRange(minId, maxId);

      if (results != null && !results.isEmpty()) {
        final BulkProcessor bp = buildBulkProcessor();
        results.stream().forEach(p -> {
          try {
            prepareDocument(bp, p);
          } catch (JsonProcessingException e) {
            // TODO: log the offending record.
            throw new JobsException("JSON error", e);
          } catch (IOException e) {
            // TODO: log the offending record.
            throw new JobsException("IO error", e);
          }
        });

        try {
          bp.awaitClose(DEFAULT_BATCH_WAIT, TimeUnit.SECONDS);
        } catch (Exception e2) {
          fatalError = true;
          throw new JobsException("ERROR EXTRACTING VIA HIBERNATE!", e2);
        } finally {
          doneExtract = true;
        }

        // Track counts.
        recsPrepared.getAndAdd(results.size());
      }
    }

    return recsPrepared.get();
  }

  // ===========================
  // ACCESSORS:
  // ===========================

  /**
   * Getter for this job's options.
   * 
   * @return this job's options
   */
  public JobOptions getOpts() {
    return opts;
  }

  /**
   * Setter for this job's options.
   * 
   * @param opts this job's options
   */
  public void setOpts(JobOptions opts) {
    this.opts = opts;
  }

  /**
   * Get the legacy source table for this job, if any.
   * 
   * @return legacy source table
   */
  protected String getLegacySourceTable() {
    return null;
  }

  /**
   * Getter for CMS system code cache.
   * 
   * @return reference to CMS system code cache
   */
  public static ApiSystemCodeCache getSystemCodes() {
    return systemCodes;
  }

  /**
   * Store a reference to the singleton CMS system code cache for quick convenient access.
   * 
   * @param sysCodeCache CMS system code cache
   */
  @Inject
  public static void setSystemCodes(@SystemCodeCache ApiSystemCodeCache sysCodeCache) {
    systemCodes = sysCodeCache;
  }

  public static boolean isTestMode() {
    return testMode;
  }

  public static void setTestMode(boolean testMode) {
    BasePersonIndexerJob.testMode = testMode;
  }

}

