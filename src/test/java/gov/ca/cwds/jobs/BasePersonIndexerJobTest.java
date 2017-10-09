package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.StringType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.core.JsonProcessingException;

import gov.ca.cwds.dao.cms.BatchBucket;
import gov.ca.cwds.data.ApiTypedIdentifier;
import gov.ca.cwds.data.DaoException;
import gov.ca.cwds.data.es.ElasticSearchPerson.ESOptionalCollection;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.exception.JobsException;
import gov.ca.cwds.jobs.inject.JobRunner;
import gov.ca.cwds.jobs.test.TestDenormalizedEntity;
import gov.ca.cwds.jobs.test.TestIndexerJob;
import gov.ca.cwds.jobs.test.TestNormalizedEntity;
import gov.ca.cwds.jobs.test.TestNormalizedEntityDao;
import gov.ca.cwds.jobs.util.jdbc.JobDB2Utils;

public class BasePersonIndexerJobTest
    extends PersonJobTester<TestNormalizedEntity, TestDenormalizedEntity> {

  TestNormalizedEntityDao dao;
  TestIndexerJob target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    dao = new TestNormalizedEntityDao(sessionFactory);
    target = new TestIndexerJob(dao, esDao, lastJobRunTimeFilename, MAPPER, sessionFactory);
    target.setOpts(opts);
  }

  @Test
  public void type() throws Exception {
    assertThat(BasePersonIndexerJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void getViewName_Args__() throws Exception {
    String actual = target.getInitialLoadViewName();
    String expected = "VW_NUTTIN";
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  // public void jsonify_Args__Object() throws Exception {
  // TestNormalizedEntity obj = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
  // obj.setName("whatever");
  // obj.setLastName("whatever");
  // String actual = target.jsonify(obj);
  // final String expected =
  // "{\"birthDate\":null,\"firstName\":\"whatever\",\"gender\":null,\"id\":\"abc1234567\",\"lastName\":\"whatever\",\"legacyDescriptor\":{},\"legacyId\":\"abc1234567\",\"middleName\":null,\"name\":\"whatever\",\"nameSuffix\":null,\"primaryKey\":\"abc1234567\",\"sensitivityIndicator\":null,\"soc158SealedClientIndicator\":null,\"ssn\":null,\"title\":null}";
  // // assertThat(actual, is(equalTo(expected)));
  // assertThat(actual, is(notNullValue()));
  // }

  @Test
  public void extract_Args__ResultSet() throws Exception {
    final Object actual = target.extract(rs);
    // You survived. Good enough.
  }

  @Test
  public void buildBulkProcessor_Args__() throws Exception {
    final BulkProcessor actual = target.buildBulkProcessor();
    assertThat(actual, notNullValue());
  }

  // @Test
  // public void buildElasticSearchPersons_Args__Object() throws Exception {
  // final TestNormalizedEntity p = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
  // ElasticSearchPerson[] actual = target.buildElasticSearchPersons(p);
  // assertThat(actual, is(notNullValue()));
  // }

  // @Test
  // public void buildElasticSearchPerson_Args__Object() throws Exception {
  // final String key = DEFAULT_CLIENT_ID;
  // TestNormalizedEntity p = new TestNormalizedEntity(key);
  // ElasticSearchPerson actual = target.buildElasticSearchPerson(p);
  // ElasticSearchPerson expected = new ElasticSearchPerson();
  // expected.setId(key);
  // // assertThat(actual, is(equalTo(expected)));
  // assertThat(actual, is(notNullValue()));
  // }

  // @Test
  // public void buildElasticSearchPersonDoc_Args__ApiPersonAware() throws Exception {
  // ApiPersonAware p = new TestNormalizedEntity("abc123");
  // ElasticSearchPerson actual = target.buildElasticSearchPersonDoc(p);
  // final String json =
  // "{\"first_name\":null,\"middle_name\":null,\"last_name\":null,\"name_suffix\":null,"
  // +
  // "\"date_of_birth\":null,\"gender\":null,\"ssn\":null,\"type\":\"gov.ca.cwds.jobs.BasePersonIndexerJobTest$TestNormalizedEntity\","
  // +
  // "\"source\":\"{\\\"id\\\":\\\"abc123\\\",\\\"name\\\":null,\\\"middleName\\\":null,\\\"firstName\\\":null,\\\"ssn\\\":null,\\\"lastName\\\":null,"
  // +
  // "\\\"gender\\\":null,\\\"birthDate\\\":null,\\\"nameSuffix\\\":null,\\\"primaryKey\\\":\\\"abc123\\\"}"
  // +
  // "\",\"legacy_source_table\":null,\"legacy_id\":null,\"addresses\":[],\"phone_numbers\":[],\"languages\":[],\"screenings\":[],\"referrals\":[],\"relationships\":[],\"cases\":[],\"id\":\"abc123\"}";
  // ElasticSearchPerson expected = MAPPER.readValue(json, ElasticSearchPerson.class);
  // // assertThat(actual, is(equalTo(expected)));
  // assertThat(actual, notNullValue());
  // }

  // @Test
  // public void buildElasticSearchPersonDoc_Args__ApiPersonAware_T__JsonProcessingException()
  // throws Exception {
  // ApiPersonAware p = mock(ApiPersonAware.class);
  // try {
  // target.buildElasticSearchPersonDoc(p);
  // fail("Expected exception was not thrown!");
  // } catch (JsonProcessingException e) {
  // }
  // }

  @Test
  public void getIdColumn_Args__() throws Exception {
    String actual = target.getIdColumn();
    String expected = "IDENTIFIER";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getDenormalizedClass_Args__() throws Exception {
    Object actual = target.getDenormalizedClass();
    assertThat(actual, notNullValue());
  }

  @Test
  public void normalize_Args__List() throws Exception {
    final List<TestDenormalizedEntity> recs = new ArrayList<>();
    recs.add(new TestDenormalizedEntity(DEFAULT_CLIENT_ID, "one", "two", "three", "four"));
    final List<TestNormalizedEntity> actual = target.normalize(recs);
    final List<TestNormalizedEntity> expected = new ArrayList<>();
    final TestNormalizedEntity expect = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    expected.add(expect);
    // assertThat(actual, is(equalTo(expected)));
    assertThat(actual, notNullValue());
  }

  @Test
  public void normalizeSingle_Args__List() throws Exception {
    final List<TestDenormalizedEntity> recs = new ArrayList<>();
    recs.add(new TestDenormalizedEntity(DEFAULT_CLIENT_ID));

    final TestNormalizedEntity actual = target.normalizeSingle(recs);
    final TestNormalizedEntity expected = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getJobTotalBuckets_Args__() throws Exception {
    int actual = target.getJobTotalBuckets();
    int expected = 1;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isViewNormalizer_Args__() throws Exception {
    boolean actual = target.isViewNormalizer();
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void prepareDocument_Args__BulkProcessor__Object() throws Exception {
    BulkProcessor bp = mock(BulkProcessor.class);
    TestNormalizedEntity t = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    target.prepareDocument(bp, t);
  }

  @Test
  public void setInsertCollections_Args__ElasticSearchPerson__Object__List() throws Exception {
    TestNormalizedEntity t = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    List list = new ArrayList();
    target.setInsertCollections(esp, t, list);
  }

  // @Test
  // public void
  // prepareInsertCollections_Args__ElasticSearchPerson__Object__String__List__ESOptionalCollectionArray()
  // throws Exception {
  // TestNormalizedEntity t = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
  // String elementName = "slop";
  // List list = new ArrayList();
  // ESOptionalCollection[] keep = new ESOptionalCollection[] {};
  // target.prepareInsertCollections(esp, t, elementName, list, keep);
  // }

  // @Test
  // public void
  // prepareUpsertJson_Args__ElasticSearchPerson__Object__String__List__ESOptionalCollectionArray()
  // throws Exception {
  // TestNormalizedEntity t = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
  // String elementName = "slop";
  // List list = new ArrayList();
  // ESOptionalCollection[] keep = new ESOptionalCollection[] {};
  // final Pair<String, String> actual =
  // target.prepareUpsertJson(target, esp, t, elementName, list, keep);
  // assertThat(actual, notNullValue());
  // }

  @Test
  public void prepareUpsertRequestNoChecked_Args__ElasticSearchPerson__Object() throws Exception {
    TestNormalizedEntity t = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    DocWriteRequest actual = target.prepareUpsertRequestNoChecked(esp, t);
    assertThat(actual, notNullValue());
  }

  @Test
  public void prepareUpsertRequest_Args__ElasticSearchPerson__Object() throws Exception {
    TestNormalizedEntity t = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    UpdateRequest actual = target.prepareUpsertRequest(esp, t);
    assertThat(actual, notNullValue());
  }

  @Test
  public void keepCollections_Args__() throws Exception {
    final ESOptionalCollection[] actual = target.keepCollections();
    assertThat(actual, notNullValue());
  }

  @Test
  public void getOptionalElementName_Args__() throws Exception {
    String actual = target.getOptionalElementName();
  }

  @Test
  public void getOptionalCollection_Args__ElasticSearchPerson__Object() throws Exception {
    TestNormalizedEntity t = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    final List<? extends ApiTypedIdentifier<String>> actual = target.getOptionalCollection(esp, t);
    final List<? extends ApiTypedIdentifier<String>> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void threadTransform_Args__() throws Exception {
    runKillThread(target);
    target.threadNormalize();
    sleepItOff();
  }

  @Test
  public void extractLastRunRecsFromTable_Args__Date() throws Exception {
    final NativeQuery<TestDenormalizedEntity> q = mock(NativeQuery.class);
    when(session.getNamedNativeQuery(any())).thenReturn(q);

    final List<TestNormalizedEntity> actual = target.extractLastRunRecsFromTable(lastRunTime);
    assertThat(actual, notNullValue());
  }

  @Test
  public void extractLastRunRecsFromTable_Args__Date__error() throws Exception {
    final NativeQuery<TestDenormalizedEntity> q = mock(NativeQuery.class);
    when(session.getNamedNativeQuery(any())).thenReturn(q);

    final List<TestNormalizedEntity> actual = target.extractLastRunRecsFromTable(lastRunTime);
    assertThat(actual, notNullValue());
  }

  @Test
  public void extractLastRunRecsFromView_Args__Date() throws Exception {
    final NativeQuery<TestDenormalizedEntity> qn = mock(NativeQuery.class);
    when(session.getNamedNativeQuery(any())).thenReturn(qn);

    final List<TestNormalizedEntity> actual =
        target.extractLastRunRecsFromView(lastRunTime, new HashSet<String>());
    assertThat(actual, notNullValue());
  }

  @Test(expected = SQLException.class)
  public void extractLastRunRecsFromView_Args__Date__SQLException() throws Exception {
    final NativeQuery<TestDenormalizedEntity> qn = mock(NativeQuery.class);
    when(session.getNamedNativeQuery(any())).thenReturn(qn);
    when(session.beginTransaction()).thenThrow(SQLException.class);

    final List<TestNormalizedEntity> actual =
        target.extractLastRunRecsFromView(lastRunTime, new HashSet<String>());
  }

  @Test(expected = HibernateException.class)
  public void extractLastRunRecsFromView_Args__Date__HibernateException() throws Exception {
    final NativeQuery<TestDenormalizedEntity> qn = mock(NativeQuery.class);
    when(session.getNamedNativeQuery(any())).thenReturn(qn);
    when(session.beginTransaction()).thenThrow(HibernateException.class);

    final List<TestNormalizedEntity> actual =
        target.extractLastRunRecsFromView(lastRunTime, new HashSet<String>());
  }

  @Test
  public void getPartitionRanges_Args__() throws Exception {
    final javax.persistence.Query q = mock(javax.persistence.Query.class);
    when(em.createNativeQuery(any(String.class), any(Class.class))).thenReturn(q);

    final List<?> actual = target.getPartitionRanges();
    assertThat(actual, notNullValue());
  }

  @Test
  public void close_Args__() throws Exception {
    target.close();
  }

  @Test
  public void close_Args___T__IOException() throws Exception {
    doThrow(new IOException()).when(esDao).close();
    try {
      target.close();
      target.markFailed();
      fail("Expected exception was not thrown!");
    } catch (IOException e) {
    }
  }

  @Test
  public void finish_Args__() throws Exception {
    target.finish();
  }

  @Test(expected = JobsException.class)
  public void finish_Args__error() throws Exception {
    target.reset();
    target.setFakeMarkDone(true);
    target.setFakeFinish(false);
    Mockito.doThrow(new JobsException("whatever")).when(esDao).close();
    target.finish();
  }

  @Test
  public void extractHibernate_Args__() throws Exception {
    final Query q = mock(Query.class);
    when(em.createNativeQuery(any(String.class), any(Class.class))).thenReturn(q);

    final List<BatchBucket> buckets = new ArrayList<>();
    final BatchBucket b = new BatchBucket();
    b.setBucket(1);
    b.setBucketCount(1);
    b.setMinId("1");
    b.setMaxId("2");
    buckets.add(b);
    when(q.getResultList()).thenReturn(buckets);

    final NativeQuery<TestDenormalizedEntity> nq = mock(NativeQuery.class);
    when(session.getNamedNativeQuery(any(String.class))).thenReturn(nq);
    when(nq.setString(any(String.class), any(String.class))).thenReturn(nq);
    when(nq.setParameter(any(String.class), any(String.class), any(StringType.class)))
        .thenReturn(nq);
    when(nq.setFlushMode(any(FlushMode.class))).thenReturn(nq);
    when(nq.setReadOnly(any(Boolean.class))).thenReturn(nq);
    when(nq.setCacheMode(any(CacheMode.class))).thenReturn(nq);
    when(nq.setFetchSize(any(Integer.class))).thenReturn(nq);
    when(nq.setCacheable(any(Boolean.class))).thenReturn(nq);

    final ScrollableResults results = mock(ScrollableResults.class);
    when(nq.scroll(any(ScrollMode.class))).thenReturn(results);
    when(results.next()).thenReturn(true).thenReturn(false);

    final TestNormalizedEntity[] entities = new TestNormalizedEntity[1];
    TestNormalizedEntity entity = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    entity.setFirstName("Fred");
    entity.setLastName("Meyer");
    entities[0] = entity;
    when(results.get()).thenReturn(entities);

    int actual = target.extractHibernate();
    int expected = 1;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getOpts_Args__() throws Exception {
    JobOptions actual = target.getOpts();
    assertThat(actual, notNullValue());
  }

  @Test
  public void setOpts_Args__JobOptions() throws Exception {
    target.setOpts(opts);
  }

  @Test
  public void isTestMode_Args__() throws Exception {
    boolean actual = JobRunner.isTestMode();
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setTestMode_Args__boolean() throws Exception {
    boolean testMode = false;
    JobRunner.setTestMode(testMode);
  }

  @Test
  public void buildBucketList_Args__String() throws Exception {
    final javax.persistence.Query q = mock(javax.persistence.Query.class);
    when(em.createNativeQuery(any(String.class), any(Class.class))).thenReturn(q);

    final String table = "SOMETBL";
    final List<BatchBucket> actual = target.buildBucketList(table);
    assertThat(actual, notNullValue());
  }

  @Test(expected = DaoException.class)
  public void buildBucketList_Args__String__error() throws Exception {
    when(sessionFactory.getCurrentSession()).thenThrow(DaoException.class);

    final javax.persistence.Query q = mock(javax.persistence.Query.class);
    when(em.createNativeQuery(any(String.class), any(Class.class))).thenReturn(q);

    final String table = "SOMETBL";
    final List<BatchBucket> actual = target.buildBucketList(table);
    assertThat(actual, notNullValue());
  }

  // @Test(expected = JobsException.class)
  @Test
  public void doLastRun_Args__Date() throws Exception {
    final NativeQuery<TestDenormalizedEntity> qn = mock(NativeQuery.class);
    when(session.getNamedNativeQuery(any(String.class))).thenReturn(qn);

    final Date actual = target.doLastRun(lastRunTime);
    assertThat(actual, notNullValue());
  }

  @Test
  public void _run_Args__Date() throws Exception {
    final javax.persistence.Query q = mock(javax.persistence.Query.class);
    when(em.createNativeQuery(any(String.class), any(Class.class))).thenReturn(q);

    final Date actual = target._run(lastRunTime);
    assertThat(actual, notNullValue());
  }

  @Test
  public void _run_Args__Date__auto() throws Exception {
    final javax.persistence.Query q = mock(javax.persistence.Query.class);
    when(em.createNativeQuery(any(String.class), any(Class.class))).thenReturn(q);
    when(opts.isLastRunMode()).thenReturn(true);

    final Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, -50);
    lastRunTime = cal.getTime();

    final Date actual = target._run(lastRunTime);
    assertThat(actual, notNullValue());
  }

  @Test(expected = JobsException.class)
  public void _run_Args__Date__error() throws Exception {
    final javax.persistence.Query q = mock(javax.persistence.Query.class);
    when(em.createNativeQuery(any(String.class), any(Class.class))).thenReturn(q);

    when(esDao.getConfig()).thenThrow(JobsException.class);

    final Date actual = target._run(lastRunTime);
    assertThat(actual, notNullValue());
  }

  @Test
  public void threadExtractJdbc_Args__() throws Exception {
    runKillThread(target);
    target.threadRetrieveByJdbc();
    sleepItOff();
  }

  @Test
  public void getInitialLoadViewName_Args__() throws Exception {
    String actual = target.getInitialLoadViewName();
    assertThat(actual, notNullValue());
  }

  @Test
  public void getInitialLoadQuery_Args__String() throws Exception {
    final String dbSchemaName = "CWSRS1";
    final String actual = target.getInitialLoadQuery(dbSchemaName);
    // assertThat(actual, notNullValue());
  }

  @Test
  public void getJdbcOrderBy_Args__() throws Exception {
    final String actual = target.getJdbcOrderBy();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void mustDeleteLimitedAccessRecords_Args__() throws Exception {
    boolean actual = target.mustDeleteLimitedAccessRecords();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isDelete_Args__Object() throws Exception {
    TestNormalizedEntity t = null;
    boolean actual = target.isDelete(t);
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void bulkDelete_Args__String() throws Exception {
    String id = DEFAULT_CLIENT_ID;
    DeleteRequest actual = target.bulkDelete(id);
    assertThat(actual, notNullValue());
  }

  @Test(expected = JsonProcessingException.class)
  @Ignore
  public void bulkDelete_Args__String_T__JsonProcessingException() throws Exception {
    String id = DEFAULT_CLIENT_ID;
    target.bulkDelete(id);
    fail("Expected exception was not thrown!");
  }

  @Test
  public void addToIndexQueue_Args__Object() throws Exception {
    TestNormalizedEntity norm = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    target.addToIndexQueue(norm);
  }

  @Test
  public void useTransformThread_Args__() throws Exception {
    boolean actual = target.useTransformThread();
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  // public void newJob_Args__Class__StringArray() throws Exception {
  // Class<Object> klass = mock(Class.class);
  // String[] args = new String[] {};
  // Object actual = BasePersonIndexerJob.newJob(klass, args);
  // Object expected = null;
  // assertThat(actual, is(equalTo(expected)));
  // }
  //
  // @Test
  // public void newJob_Args__Class__StringArray_T__JobsException() throws Exception {
  // Class<Object> klass = mock(Class.class);
  // String[] args = new String[] {};
  // try {
  // BasePersonIndexerJob.newJob(klass, args);
  // fail("Expected exception was not thrown!");
  // } catch (JobsException e) {
  // }
  // }

  // @Test
  // public void runJob_Args__Class__StringArray() throws Exception {
  // Class<Object> klass = mock(Class.class);
  // String[] args = new String[] {};
  // BasePersonIndexerJob.runJob(klass, args);
  // }
  //
  // @Test
  // public void runJob_Args__Class__StringArray_T__JobsException() throws Exception {
  // Class<Object> klass = mock(Class.class);
  // String[] args = new String[] {};
  // try {
  // BasePersonIndexerJob.runJob(klass, args);
  // fail("Expected exception was not thrown!");
  // } catch (JobsException e) {
  // }
  // }

  @Test
  public void doInitialLoadJdbc_Args__() throws Exception {
    runKillThread(target);
    target.doInitialLoadJdbc();
    sleepItOff();
  }

  @Test(expected = JobsException.class)
  public void doInitialLoadJdbc_Args__error() throws Exception {
    runKillThread(target);
    target.doInitialLoadJdbc();
    sleepItOff();
  }

  @Test(expected = InterruptedException.class)
  public void bulkPrepare_Args__BulkProcessor__int() throws Exception {
    BulkProcessor bp = mock(BulkProcessor.class);
    int cntr = 0;
    int actual = target.bulkPrepare(bp, cntr);
    int expected = 0;
    sleepItOff();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void threadIndex_Args__() throws Exception {
    runKillThread(target);
    target.threadIndex();
    sleepItOff();
  }

  @Test
  public void prepLastRunDoc_Args__BulkProcessor__Object() throws Exception {
    BulkProcessor bp = mock(BulkProcessor.class);
    TestNormalizedEntity p = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    target.prepareDocumentTrapIO(bp, p);
  }

  @Test
  public void calcLastRunDate_Args__Date() throws Exception {
    final Date actual = target.calcLastRunDate(lastRunTime);
    assertThat(actual, notNullValue());
  }

  @Test
  public void isRangeSelfManaging_Args__() throws Exception {
    final boolean actual = target.providesInitialKeyRanges();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void extractLastRunRecsFromView_Args__Date__Set() throws Exception {
    final NativeQuery<TestDenormalizedEntity> qn = mock(NativeQuery.class);
    when(session.getNamedNativeQuery(any())).thenReturn(qn);

    final List<TestDenormalizedEntity> denorms = new ArrayList<>();
    TestDenormalizedEntity m = new TestDenormalizedEntity(DEFAULT_CLIENT_ID);
    denorms.add(m);
    when(qn.list()).thenReturn(denorms);

    final Set<String> deletionResults = mock(Set.class);
    final List<TestNormalizedEntity> actual =
        target.extractLastRunRecsFromView(lastRunTime, deletionResults);
    assertThat(actual, notNullValue());
  }

  @Test(expected = JobsException.class)
  public void extractLastRunRecsFromView_Args__sql_error() throws Exception {
    final NativeQuery<TestDenormalizedEntity> qn = mock(NativeQuery.class);
    // when(session.getNamedNativeQuery(any())).thenReturn(qn);
    when(session.getNamedNativeQuery(any())).thenThrow(SQLException.class);

    final List<TestDenormalizedEntity> denorms = new ArrayList<>();
    TestDenormalizedEntity m = new TestDenormalizedEntity(DEFAULT_CLIENT_ID);
    denorms.add(m);
    when(qn.list()).thenReturn(denorms);

    final Set<String> deletionResults = mock(Set.class);
    final List<TestNormalizedEntity> actual =
        target.extractLastRunRecsFromView(lastRunTime, deletionResults);
    assertThat(actual, notNullValue());
  }

  @Test
  public void prepHibernatePull_Args__Session__Transaction__Date() throws Exception {
    target.prepHibernateLastChange(session, transaction, lastRunTime);
  }

  @Test
  public void getLegacySourceTable_Args__() throws Exception {
    final String actual = target.getLegacySourceTable();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getDBSchemaName_Args__() throws Exception {
    final String actual = target.getDBSchemaName();
    String expected = "CWSRS1";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isDB2OnZOS_Args__() throws Exception {
    boolean actual = target.isDB2OnZOS();
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void enableParallelism_Args__Connection() throws Exception {
    JobDB2Utils.enableParallelism(con);
  }

  @Test
  public void testGetEsDao() {
    assertThat(target.getEsDao(), notNullValue());
  }

  @Test
  public void testLoadRecsForDeletion() {

    final List<TestNormalizedEntity> deletionRecs = new ArrayList<>();
    TestNormalizedEntity entity = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    deletionRecs.add(entity);

    NativeQuery<TestNormalizedEntity> nq = mock(NativeQuery.class);
    when(session.getNamedNativeQuery(any())).thenReturn(nq);
    when(nq.list()).thenReturn(deletionRecs);

    final Set<String> deletionSet = new HashSet<>();
    deletionSet.add(DEFAULT_CLIENT_ID);
    target.loadRecsForDeletion(TestNormalizedEntity.class, session, lastRunTime, deletionSet);
  }

  @Test
  public void pullBucketRange_Args__String__String() throws Exception {
    JobRunner.setTestMode(true);

    final NativeQuery<TestDenormalizedEntity> q = mock(NativeQuery.class);
    when(session.getNamedNativeQuery(any(String.class))).thenReturn(q);
    when(q.setString(any(String.class), any(String.class))).thenReturn(q);
    when(q.setParameter(any(String.class), any(String.class), any(StringType.class))).thenReturn(q);
    when(q.setFlushMode(any(FlushMode.class))).thenReturn(q);
    when(q.setReadOnly(any(Boolean.class))).thenReturn(q);
    when(q.setCacheMode(any(CacheMode.class))).thenReturn(q);
    when(q.setFetchSize(any(Integer.class))).thenReturn(q);
    when(q.setCacheable(any(Boolean.class))).thenReturn(q);

    final ScrollableResults results = mock(ScrollableResults.class);
    when(q.scroll(any(ScrollMode.class))).thenReturn(results);
    when(results.next()).thenReturn(true).thenReturn(false);

    final TestNormalizedEntity[] entities = new TestNormalizedEntity[1];
    TestNormalizedEntity entity = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    entity.setFirstName("Fred");
    entity.setLastName("Meyer");
    entities[0] = entity;
    when(results.get()).thenReturn(entities);

    final String minId = "1";
    final String maxId = "2";
    final List<TestNormalizedEntity> actual = target.pullBucketRange(minId, maxId);
    assertThat(actual, notNullValue());
  }

  @Test
  public void testNormalizeLoop() throws Exception {
    final List<TestDenormalizedEntity> grpRecs = new ArrayList<>();
    int cntr = 0;
    Object lastId = new Object();
    TestDenormalizedEntity x = new TestDenormalizedEntity("xyz9876543");
    grpRecs.add(x);

    TestDenormalizedEntity entity = new TestDenormalizedEntity(DEFAULT_CLIENT_ID);
    target.queueNormalize.add(entity);
    target.normalizeLoop(grpRecs, lastId, cntr);
  }

  @Test
  @Ignore
  public void prepHibernatePull_Args__Session__Transaction__Date_T__SQLException()
      throws Exception {
    try {
      target.prepHibernateLastChange(session, transaction, lastRunTime);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
    }
  }

}
