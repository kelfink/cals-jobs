package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
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
import java.util.concurrent.LinkedBlockingDeque;

import javax.persistence.Query;

import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.StringType;
import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.dao.cms.BatchBucket;
import gov.ca.cwds.data.ApiTypedIdentifier;
import gov.ca.cwds.data.DaoException;
import gov.ca.cwds.data.es.ElasticSearchPerson.ESOptionalCollection;
import gov.ca.cwds.jobs.component.FlightRecord;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.defaults.NeutronIntegerDefaults;
import gov.ca.cwds.jobs.exception.JobsException;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.LaunchCommand;
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
    target =
        new TestIndexerJob(dao, esDao, lastJobRunTimeFilename, MAPPER, sessionFactory, jobHistory);
    target.setOpts(opts);
    target.setTrack(track);
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

  @Test
  public void extract_Args__ResultSet() throws Exception {
    final Object actual = target.extract(rs);
  }

  @Test
  public void buildBulkProcessor_Args__() throws Exception {
    final BulkProcessor actual = target.buildBulkProcessor();
    assertThat(actual, notNullValue());
  }

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
    final BulkProcessor bp = mock(BulkProcessor.class);
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
    final TestNormalizedEntity t = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    final DocWriteRequest actual = target.prepareUpsertRequestNoChecked(esp, t);
    assertThat(actual, notNullValue());
  }

  @Test
  public void prepareUpsertRequest_Args__ElasticSearchPerson__Object() throws Exception {
    final TestNormalizedEntity t = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    final UpdateRequest actual = target.prepareUpsertRequest(esp, t);
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
    final TestNormalizedEntity t = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    final List<? extends ApiTypedIdentifier<String>> actual = target.getOptionalCollection(esp, t);
    final List<? extends ApiTypedIdentifier<String>> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void threadNormalize_Args__() throws Exception {
    try {
      runKillThread(target, NeutronIntegerDefaults.POLL_MILLIS.getValue() + 800L);
      final TestDenormalizedEntity m = new TestDenormalizedEntity(DEFAULT_CLIENT_ID, "1", "2", "3");
      target.queueNormalize.push(m);
      target.threadNormalize();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      markTestDone();
    }
  }

  @Test
  public void extractLastRunRecsFromTable_Args__Date() throws Exception {
    final NativeQuery<TestDenormalizedEntity> q = mock(NativeQuery.class);
    when(session.getNamedNativeQuery(any())).thenReturn(q);

    final List<TestDenormalizedEntity> list = new ArrayList<>();
    TestDenormalizedEntity t = new TestDenormalizedEntity(DEFAULT_CLIENT_ID, "1", "2", "3");
    list.add(t);
    when(q.list()).thenReturn(list);

    final List<TestNormalizedEntity> actual = target.extractLastRunRecsFromTable(lastRunTime);
    assertThat(actual, notNullValue());
  }

  @Test(expected = DaoException.class)
  public void extractLastRunRecsFromTable_Args__Date__error() throws Exception {
    final NativeQuery<TestDenormalizedEntity> q = mock(NativeQuery.class);
    when(session.getNamedNativeQuery(any())).thenThrow(HibernateException.class);

    final List<TestNormalizedEntity> actual = target.extractLastRunRecsFromTable(lastRunTime);
    assertThat(actual, notNullValue());
  }

  @Test
  public void extractLastRunRecsFromView_Args__Date() throws Exception {
    final NativeQuery<TestDenormalizedEntity> qn = mock(NativeQuery.class);
    when(session.getNamedNativeQuery(any())).thenReturn(qn);

    final List<TestDenormalizedEntity> recs = new ArrayList<>();
    final TestDenormalizedEntity rec = new TestDenormalizedEntity(DEFAULT_CLIENT_ID, "one", "two");
    recs.add(rec);

    final List<TestNormalizedEntity> actual =
        target.extractLastRunRecsFromView(lastRunTime, new HashSet<String>());
    assertThat(actual, notNullValue());
  }

  @Test(expected = SQLException.class)
  public void extractLastRunRecsFromView_Args__Date__SQLException() throws Exception {
    final NativeQuery<TestDenormalizedEntity> qn = mock(NativeQuery.class);
    when(session.getNamedNativeQuery(any())).thenReturn(qn);
    when(session.beginTransaction()).thenThrow(SQLException.class);
    when(session.getTransaction()).thenThrow(SQLException.class);

    final List<TestNormalizedEntity> actual =
        target.extractLastRunRecsFromView(lastRunTime, new HashSet<String>());
  }

  @Test(expected = HibernateException.class)
  public void extractLastRunRecsFromView_Args__Date__HibernateException() throws Exception {
    final NativeQuery<TestDenormalizedEntity> qn = mock(NativeQuery.class);
    when(session.getNamedNativeQuery(any())).thenReturn(qn);
    when(session.beginTransaction()).thenThrow(HibernateException.class);
    when(session.getTransaction()).thenThrow(HibernateException.class);

    final List<TestNormalizedEntity> actual =
        target.extractLastRunRecsFromView(lastRunTime, new HashSet<String>());
  }

  @Test
  public void getPartitionRanges_Args__() throws Exception {
    final javax.persistence.Query q = mock(javax.persistence.Query.class);
    when(em.createNativeQuery(any(String.class), any(Class.class))).thenReturn(q);
    when(q.setParameter(any(String.class), any(String.class))).thenReturn(q);

    final List<?> actual = target.getPartitionRanges();
    assertThat(actual, notNullValue());
  }

  @Test
  public void close_Args__() throws Exception {
    target.close();
  }

  @Test(expected = IOException.class)
  public void close_Args___T__IOException() throws Exception {
    doThrow(new IOException()).when(esDao).close();
    target.close();
  }

  @Test
  public void finish_Args__() throws Exception {
    target.finish();
  }

  @Test(expected = JobsException.class)
  public void finish_Args__error() throws Exception {
    target.setFakeMarkDone(true);
    target.setFakeFinish(false);
    doThrow(new JobsException("whatever")).when(esDao).close();
    target.finish();
  }

  @Test
  public void extractHibernate_Args__() throws Exception {
    final Query q = mock(Query.class);
    when(em.createNativeQuery(any(String.class), any(Class.class))).thenReturn(q);
    when(q.getResultList()).thenReturn(new ArrayList<TestDenormalizedEntity>());
    when(q.setParameter(any(String.class), any(String.class))).thenReturn(q);

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
    when(nq.setHibernateFlushMode(any(FlushMode.class))).thenReturn(nq);
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

    target.setFakeRanges(true);
    int actual = target.extractHibernate();
    int expected = 1;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getOpts_Args__() throws Exception {
    FlightPlan actual = target.getOpts();
    assertThat(actual, notNullValue());
  }

  @Test
  public void setOpts_Args__JobOptions() throws Exception {
    target.setOpts(opts);
  }

  @Test
  public void isTestMode_Args__() throws Exception {
    boolean actual = LaunchCommand.isTestMode();
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setTestMode_Args__boolean() throws Exception {
    boolean testMode = false;
    LaunchCommand.setTestMode(testMode);
  }

  @Test
  public void doLastRun_Args__Date() throws Exception {
    final NativeQuery<TestDenormalizedEntity> qn = mock(NativeQuery.class);
    when(session.getNamedNativeQuery(any())).thenReturn(qn);

    final NativeQuery<TestDenormalizedEntity> q = mock(NativeQuery.class);
    when(session.getNamedNativeQuery(any())).thenReturn(q);

    final List<TestDenormalizedEntity> recs = new ArrayList<>();
    final TestDenormalizedEntity rec = new TestDenormalizedEntity(DEFAULT_CLIENT_ID, "one", "two");
    recs.add(rec);

    final List<TestDenormalizedEntity> list = new ArrayList<>();
    TestDenormalizedEntity t = new TestDenormalizedEntity(DEFAULT_CLIENT_ID, "1", "2", "3");
    list.add(t);
    when(q.list()).thenReturn(list);

    final Set<String> deletionSet = new HashSet<>();
    deletionSet.add("xyz1234567");

    final MultiSearchRequestBuilder mBuilder = mock(MultiSearchRequestBuilder.class);
    final MultiSearchResponse multiResponse = mock(MultiSearchResponse.class);
    final SearchRequestBuilder sBuilder = mock(SearchRequestBuilder.class);
    final MultiSearchResponse.Item item = mock(MultiSearchResponse.Item.class);
    final MultiSearchResponse.Item[] items = new MultiSearchResponse.Item[1];
    items[0] = item;

    final SearchHits hits = mock(SearchHits.class);
    final SearchHit hit = mock(SearchHit.class);
    final SearchHit[] hitArray = {hit};
    final SearchResponse sr = mock(SearchResponse.class);

    when(client.prepareMultiSearch()).thenReturn(mBuilder);
    when(client.prepareSearch()).thenReturn(sBuilder);

    when(mBuilder.add(any(SearchRequestBuilder.class))).thenReturn(mBuilder);
    when(mBuilder.get()).thenReturn(multiResponse);
    when(sBuilder.setQuery(any())).thenReturn(sBuilder);
    when(multiResponse.getResponses()).thenReturn(items);
    when(item.getResponse()).thenReturn(sr);
    when(sr.getHits()).thenReturn(hits);
    when(hits.getHits()).thenReturn(hitArray);

    when(hit.docId()).thenReturn(12345);
    when(hit.getSourceAsString())
        .thenReturn(IOUtils.toString(getClass().getResourceAsStream("/fixtures/es_person.json")));

    final Date actual = target.doLastRun(lastRunTime);
    assertThat(actual, notNullValue());
  }

  @Test
  public void doLastRun_Args__Date__error() throws Exception {
    final NativeQuery<TestDenormalizedEntity> qn = mock(NativeQuery.class);
    when(session.getNamedNativeQuery(any(String.class))).thenReturn(qn);

    final Date actual = target.doLastRun(lastRunTime);
    assertThat(actual, notNullValue());
  }

  @Test
  public void _run_Args__Date() throws Exception {
    final NativeQuery<TestDenormalizedEntity> qn = mock(NativeQuery.class);
    when(session.getNamedNativeQuery(any(String.class))).thenReturn(qn);

    final javax.persistence.Query q = mock(javax.persistence.Query.class);
    when(em.createNativeQuery(any(String.class), any(Class.class))).thenReturn(q);
    when(opts.isLastRunMode()).thenReturn(true);

    final Date actual = target.executeJob(lastRunTime);
    assertThat(actual, notNullValue());
  }

  @Test
  public void _run_Args__Date__auto() throws Exception {
    final javax.persistence.Query q = mock(javax.persistence.Query.class);
    when(em.createNativeQuery(any(String.class), any(Class.class))).thenReturn(q);
    when(q.getResultList()).thenReturn(new ArrayList<TestDenormalizedEntity>());
    when(q.setParameter(any(String.class), any(String.class))).thenReturn(q);
    when(opts.isLastRunMode()).thenReturn(true);

    final Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, -50);
    lastRunTime = cal.getTime();

    final Date actual = target.executeJob(lastRunTime);
    assertThat(actual, notNullValue());
  }

  @Test(expected = NeutronException.class)
  public void _run_Args__Date__error() throws Exception {
    final javax.persistence.Query q = mock(javax.persistence.Query.class);
    when(em.createNativeQuery(any(String.class), any(Class.class))).thenReturn(q);
    when(esDao.getConfig()).thenThrow(JobsException.class);

    final Date actual = target.executeJob(lastRunTime);
    assertThat(actual, notNullValue());
  }

  @Test
  public void threadRetrieveByJdbc_Args__() throws Exception {
    when(rs.next()).thenReturn(true, false);
    runKillThread(target);
    target.threadRetrieveByJdbc();
    markTestDone();
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

  @Test
  public void addToIndexQueue_Args__Object() throws Exception {
    TestNormalizedEntity norm = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    target.addToIndexQueue(norm);
  }

  @Test(expected = JobsException.class)
  public void addToIndexQueue_Args__interrupt() throws Exception {
    TestNormalizedEntity norm = new TestNormalizedEntity(DEFAULT_CLIENT_ID);

    LinkedBlockingDeque deque = mock(LinkedBlockingDeque.class);
    when(deque.add(any(TestNormalizedEntity.class))).thenThrow(InterruptedException.class);
    doThrow(new InterruptedException()).when(deque).putLast(any(TestNormalizedEntity.class));

    target.setQueueIndex(deque);
    target.addToIndexQueue(norm);
  }

  @Test
  public void useTransformThread_Args__() throws Exception {
    boolean actual = target.useTransformThread();
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void doInitialLoadJdbc_Args__() throws Exception {
    runKillThread(target);
    target.doInitialLoadJdbc();
    markTestDone();
  }

  @Test(expected = NeutronException.class)
  public void doInitialLoadJdbc_Args__error() throws Exception {
    when(rs.next()).thenReturn(true, false);
    target.setBlowUpNameThread(true);

    runKillThread(target);
    target.doInitialLoadJdbc();
    markTestDone();
  }

  @Test
  public void bulkPrepare_Args__BulkProcessor__int() throws Exception {
    final TestNormalizedEntity entity = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    target.queueIndex.add(entity);

    final BulkProcessor bp = mock(BulkProcessor.class);
    int cntr = 0;

    try {
      runKillThread(target, 4000L);
      int actual = target.bulkPrepare(bp, cntr);
      int expected = 1;
      assertThat(actual, is(equalTo(expected)));
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      markTestDone();
    }
  }

  @Test
  public void threadIndex_Args__() throws Exception {
    runKillThread(target);
    target.threadIndex();
    markTestDone();
  }

  @Test
  public void prepLastRunDoc_Args__BulkProcessor__Object() throws Exception {
    final BulkProcessor bp = mock(BulkProcessor.class);
    final TestNormalizedEntity p = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    target.prepareDocumentTrapIO(bp, p);
  }

  @Test(expected = JobsException.class)
  public void prepareDocumentTrapIO__error() throws Exception {
    final BulkProcessor bp = mock(BulkProcessor.class);
    final TestNormalizedEntity p = new TestNormalizedEntity(DEFAULT_CLIENT_ID);

    final FlightRecord track = mock(FlightRecord.class);
    when(track.trackBulkPrepared()).thenThrow(IOException.class);

    target.setTrack(track);
    target.prepareDocumentTrapIO(bp, p);
  }

  @Test
  public void calcLastRunDate_Args__Date() throws Exception {
    final Date actual = target.calcLastRunDate(lastRunTime);
    assertThat(actual, notNullValue());
  }

  @Test
  public void isRangeSelfManaging_Args__() throws Exception {
    final boolean actual = target.isInitialLoadJdbc();
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
    target.prepHibernateLastChange(session, lastRunTime);
  }

  @Test
  public void getLegacySourceTable_Args__() throws Exception {
    final String actual = target.getLegacySourceTable();
    String expected = "CRAP_T";
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
    LaunchCommand.setTestMode(true);

    final NativeQuery<TestDenormalizedEntity> q = mock(NativeQuery.class);
    when(session.getNamedNativeQuery(any(String.class))).thenReturn(q);
    when(q.setString(any(String.class), any(String.class))).thenReturn(q);
    when(q.setParameter(any(String.class), any(String.class), any(StringType.class))).thenReturn(q);
    when(q.setFlushMode(any(FlushMode.class))).thenReturn(q);
    when(q.setHibernateFlushMode(any(FlushMode.class))).thenReturn(q);
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
  public void refreshMQT() throws Exception {
    final NativeQuery<TestDenormalizedEntity> q = mock(NativeQuery.class);
    when(session.getNamedNativeQuery(any(String.class))).thenReturn(q);
    when(q.setString(any(String.class), any(String.class))).thenReturn(q);
    when(q.setParameter(any(String.class), any(String.class), any(StringType.class))).thenReturn(q);
    when(nq.setFlushMode(any(FlushMode.class))).thenReturn(nq);
    when(q.setHibernateFlushMode(any(FlushMode.class))).thenReturn(q);
    when(q.setReadOnly(any(Boolean.class))).thenReturn(q);
    when(q.setCacheMode(any(CacheMode.class))).thenReturn(q);
    when(q.setFetchSize(any(Integer.class))).thenReturn(q);
    when(q.setCacheable(any(Boolean.class))).thenReturn(q);

    final FlightPlan opts = new FlightPlan();
    opts.setRefreshMqt(true);
    opts.setEsConfigLoc("config/local.yaml");
    target.setOpts(opts);
    target.refreshMQT();
  }

  @Test
  public void getQueueIndex() throws Exception {
    LinkedBlockingDeque actual = target.getQueueIndex();
    assertThat(actual, notNullValue());
  }

  @Test
  public void awaitBulkProcessorClose() throws Exception {
    final BulkProcessor bp = mock(BulkProcessor.class);
    target.setFakeBulkProcessor(false);
    target.awaitBulkProcessorClose(bp);
  }

  @Test
  public void handleDeletes_Args__Set__BulkProcessor() throws Exception {
    final Set<String> deletionResults = new HashSet<>();
    deletionResults.add(DEFAULT_CLIENT_ID);
    final BulkProcessor bp = mock(BulkProcessor.class);
    target.handleDeletes(deletionResults, bp);
  }

}
