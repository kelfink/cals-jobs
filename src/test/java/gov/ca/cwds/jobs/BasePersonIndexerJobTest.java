package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.update.UpdateRequest;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Injector;

import gov.ca.cwds.dao.cms.BatchBucket;
import gov.ca.cwds.data.ApiTypedIdentifier;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPerson.ESOptionalCollection;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.ApiSystemCodeCache;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.data.std.ApiPersonAware;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.exception.JobsException;
import gov.ca.cwds.jobs.inject.LastRunFile;


public class BasePersonIndexerJobTest {

  static final class TestNormalizedEntity implements PersistentObject, ApiPersonAware {

    private String id;

    public TestNormalizedEntity(String id) {
      this.id = id;
    }

    @Override
    public Serializable getPrimaryKey() {
      return null;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    @Override
    public Date getBirthDate() {
      return null;
    }

    @Override
    public String getFirstName() {
      return null;
    }

    @Override
    public String getGender() {
      return null;
    }

    @Override
    public String getLastName() {
      return null;
    }

    @Override
    public String getMiddleName() {
      return null;
    }

    @Override
    public String getNameSuffix() {
      return null;
    }

    @Override
    public String getSsn() {
      return null;
    }

  }

  /**
   * Denormalized
   */
  static final class TestDenormalizedEntity implements ApiGroupNormalizer<TestNormalizedEntity> {

    @Override
    public Class<TestNormalizedEntity> getNormalizationClass() {
      return TestNormalizedEntity.class;
    }

    @Override
    public Object getNormalizationGroupKey() {
      return null;
    }

    @Override
    public TestNormalizedEntity normalize(Map<Object, TestNormalizedEntity> arg0) {
      return null;
    }

  }

  /**
   * DAO
   */
  static final class TestNormalizedEntityDao extends BaseDaoImpl<TestNormalizedEntity> {

    public TestNormalizedEntityDao(SessionFactory sessionFactory) {
      super(sessionFactory);
    }

  }

  static final class TestIndexerJob
      extends BasePersonIndexerJob<TestNormalizedEntity, TestDenormalizedEntity> {

    public TestIndexerJob(final TestNormalizedEntityDao mainDao,
        final ElasticsearchDao elasticsearchDao, @LastRunFile final String lastJobRunTimeFilename,
        final ObjectMapper mapper, @CmsSessionFactory SessionFactory sessionFactory) {
      super(mainDao, elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
    }

    @Override
    protected String getLegacySourceTable() {
      return "ATTRNY_T";
    }

  }

  // ====================
  // TEST MEMBERS:
  // ====================

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  SessionFactory sessionFactory;
  TestNormalizedEntityDao dao;
  ElasticsearchDao esDao;
  File tempFile;
  String lastJobRunTimeFilename;
  ObjectMapper mapper = ElasticSearchPerson.MAPPER;
  TestIndexerJob target;

  @Before
  protected void setup() throws Exception {
    sessionFactory = mock(SessionFactory.class);
    dao = new TestNormalizedEntityDao(sessionFactory);
    esDao = mock(ElasticsearchDao.class);
    tempFile = tempFolder.newFile("tempFile.txt");
    lastJobRunTimeFilename = tempFile.getAbsolutePath();
    target = new TestIndexerJob(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
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
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getViewName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getJdbcOrderBy_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getJdbcOrderBy();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void jsonify_Args__Object() throws Exception {
    // given
    Object obj = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.jsonify(obj);
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void extract_Args__ResultSet() throws Exception {
    // given
    ResultSet rs = mock(ResultSet.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Object actual = target.extract(rs);
    // then
    // e.g. : verify(mocked).called();
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void extract_Args__ResultSet_T__SQLException() throws Exception {
    // given
    ResultSet rs = mock(ResultSet.class);
    // e.g. : given(mocked.called()).willReturn(1);
    try {
      // when
      target.extract(rs);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
      // then
    }
  }

  @Test
  public void logEvery_Args__int__String__ObjectArray() throws Exception {
    // given
    int cntr = 0;
    String action = null;
    Object[] args = new Object[] {};
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.logEvery(cntr, action, args);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void buildBulkProcessor_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    BulkProcessor actual = target.buildBulkProcessor();
    // then
    // e.g. : verify(mocked).called();
    BulkProcessor expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void buildInjector_Args__JobOptions() throws Exception {
    // given
    JobOptions opts = mock(JobOptions.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Injector actual = BasePersonIndexerJob.buildInjector(opts);
    // then
    // e.g. : verify(mocked).called();
    Injector expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void buildInjector_Args__JobOptions_T__JobsException() throws Exception {
    // given
    JobOptions opts = mock(JobOptions.class);
    // e.g. : given(mocked.called()).willReturn(1);
    try {
      // when
      BasePersonIndexerJob.buildInjector(opts);
      fail("Expected exception was not thrown!");
    } catch (JobsException e) {
      // then
    }
  }

  // @Test
  // public void newJob_Args__Class__StringArray() throws Exception {
  // // given
  // Class<Object> klass = mock(Class.class);
  // String[] args = new String[] {};
  // // e.g. : given(mocked.called()).willReturn(1);
  // // when
  // Object actual = BasePersonIndexerJob.newJob(klass, args);
  // // then
  // // e.g. : verify(mocked).called();
  // Object expected = null;
  // assertThat(actual, is(equalTo(expected)));
  // }
  //
  // @Test
  // public void newJob_Args__Class__StringArray_T__JobsException() throws Exception {
  // // given
  // Class<Object> klass = mock(Class.class);
  // String[] args = new String[] {};
  // // e.g. : given(mocked.called()).willReturn(1);
  // try {
  // // when
  // BasePersonIndexerJob.newJob(klass, args);
  // fail("Expected exception was not thrown!");
  // } catch (JobsException e) {
  // // then
  // }
  // }
  //
  // @Test
  // public void runJob_Args__Class__StringArray() throws Exception {
  // // given
  // Class<Object> klass = mock(Class.class);
  // String[] args = new String[] {};
  // // e.g. : given(mocked.called()).willReturn(1);
  // // when
  // BasePersonIndexerJob.runJob(klass, args);
  // // then
  // // e.g. : verify(mocked).called();
  // }
  //
  // @Test
  // public void runJob_Args__Class__StringArray_T__JobsException() throws Exception {
  // // given
  // Class<Object> klass = mock(Class.class);
  // String[] args = new String[] {};
  // // e.g. : given(mocked.called()).willReturn(1);
  // try {
  // // when
  // BasePersonIndexerJob.runJob(klass, args);
  // fail("Expected exception was not thrown!");
  // } catch (JobsException e) {
  // // then
  // }
  // }

  @Test
  public void buildElasticSearchPersons_Args__Object() throws Exception {
    // given
    TestNormalizedEntity p = new TestNormalizedEntity("7ApWVDF00h");
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ElasticSearchPerson[] actual = target.buildElasticSearchPersons(p);
    // then
    // e.g. : verify(mocked).called();
    ElasticSearchPerson[] expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void buildElasticSearchPersons_Args__Object_T__JsonProcessingException() throws Exception {
    // given
    TestNormalizedEntity p = new TestNormalizedEntity("7ApWVDF00h");
    // e.g. : given(mocked.called()).willReturn(1);
    try {
      // when
      target.buildElasticSearchPersons(p);
      fail("Expected exception was not thrown!");
    } catch (JsonProcessingException e) {
      // then
    }
  }

  @Test
  public void buildElasticSearchPerson_Args__Object() throws Exception {
    // given
    TestNormalizedEntity p = new TestNormalizedEntity("7ApWVDF00h");
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ElasticSearchPerson actual = target.buildElasticSearchPerson(p);
    // then
    // e.g. : verify(mocked).called();
    ElasticSearchPerson expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void buildElasticSearchPerson_Args__Object_T__JsonProcessingException() throws Exception {
    // given
    TestNormalizedEntity p = new TestNormalizedEntity("7ApWVDF00h");
    // e.g. : given(mocked.called()).willReturn(1);
    try {
      // when
      target.buildElasticSearchPerson(p);
      fail("Expected exception was not thrown!");
    } catch (JsonProcessingException e) {
      // then
    }
  }

  @Test
  public void buildElasticSearchPersonDoc_Args__ApiPersonAware() throws Exception {
    // given
    ApiPersonAware p = mock(ApiPersonAware.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ElasticSearchPerson actual = target.buildElasticSearchPersonDoc(p);
    // then
    // e.g. : verify(mocked).called();
    ElasticSearchPerson expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void buildElasticSearchPersonDoc_Args__ApiPersonAware_T__JsonProcessingException()
      throws Exception {
    // given
    ApiPersonAware p = mock(ApiPersonAware.class);
    // e.g. : given(mocked.called()).willReturn(1);
    try {
      // when
      target.buildElasticSearchPersonDoc(p);
      fail("Expected exception was not thrown!");
    } catch (JsonProcessingException e) {
      // then
    }
  }

  @Test
  public void getIdColumn_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getIdColumn();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void ifNull_Args__String() throws Exception {
    // given
    String value = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.ifNull(value);
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getDenormalizedClass_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Object actual = target.getDenormalizedClass();
    // then
    // e.g. : verify(mocked).called();
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__List() throws Exception {
    // given
    List<TestDenormalizedEntity> recs = new ArrayList<>();
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    List<TestNormalizedEntity> actual = target.normalize(recs);
    // then
    // e.g. : verify(mocked).called();
    List<TestNormalizedEntity> expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalizeSingle_Args__List() throws Exception {
    // given
    List<TestDenormalizedEntity> recs = new ArrayList<>();
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    TestNormalizedEntity actual = target.normalizeSingle(recs);
    // then
    // e.g. : verify(mocked).called();
    TestNormalizedEntity expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getJobTotalBuckets_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    int actual = target.getJobTotalBuckets();
    // then
    // e.g. : verify(mocked).called();
    int expected = 0;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isViewNormalizer_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    boolean actual = target.isViewNormalizer();
    // then
    // e.g. : verify(mocked).called();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void prepareDocument_Args__BulkProcessor__Object() throws Exception {
    // given
    BulkProcessor bp = mock(BulkProcessor.class);
    TestNormalizedEntity t = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.prepareDocument(bp, t);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void prepareDocument_Args__BulkProcessor__Object_T__IOException() throws Exception {
    // given
    BulkProcessor bp = mock(BulkProcessor.class);
    TestNormalizedEntity t = null;
    // e.g. : given(mocked.called()).willReturn(1);
    try {
      // when
      target.prepareDocument(bp, t);
      fail("Expected exception was not thrown!");
    } catch (IOException e) {
      // then
    }
  }

  @Test
  public void setInsertCollections_Args__ElasticSearchPerson__Object__List() throws Exception {
    // given
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    TestNormalizedEntity t = null;
    List list = new ArrayList();
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setInsertCollections(esp, t, list);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void prepareInsertCollections_Args__ElasticSearchPerson__Object__String__List__ESOptionalCollectionArray()
      throws Exception {
    // given
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    TestNormalizedEntity t = null;
    String elementName = null;
    List list = new ArrayList();
    ESOptionalCollection[] keep = new ESOptionalCollection[] {};
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.prepareInsertCollections(esp, t, elementName, list, keep);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void prepareInsertCollections_Args__ElasticSearchPerson__Object__String__List__ESOptionalCollectionArray_T__JsonProcessingException()
      throws Exception {
    // given
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    TestNormalizedEntity t = null;
    String elementName = null;
    List list = new ArrayList();
    ESOptionalCollection[] keep = new ESOptionalCollection[] {};
    // e.g. : given(mocked.called()).willReturn(1);
    try {
      // when
      target.prepareInsertCollections(esp, t, elementName, list, keep);
      fail("Expected exception was not thrown!");
    } catch (JsonProcessingException e) {
      // then
    }
  }

  @Test
  public void prepareUpsertJson_Args__ElasticSearchPerson__Object__String__List__ESOptionalCollectionArray()
      throws Exception {
    // given
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    TestNormalizedEntity t = null;
    String elementName = null;
    List list = new ArrayList();
    ESOptionalCollection[] keep = new ESOptionalCollection[] {};
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Pair<String, String> actual = target.prepareUpsertJson(esp, t, elementName, list, keep);
    // then
    // e.g. : verify(mocked).called();
    Pair<String, String> expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void prepareUpsertJson_Args__ElasticSearchPerson__Object__String__List__ESOptionalCollectionArray_T__JsonProcessingException()
      throws Exception {
    // given
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    TestNormalizedEntity t = null;
    String elementName = null;
    List list = new ArrayList();
    ESOptionalCollection[] keep = new ESOptionalCollection[] {};
    // e.g. : given(mocked.called()).willReturn(1);
    try {
      // when
      target.prepareUpsertJson(esp, t, elementName, list, keep);
      fail("Expected exception was not thrown!");
    } catch (JsonProcessingException e) {
      // then
    }
  }

  @Test
  public void prepareUpsertRequestNoChecked_Args__ElasticSearchPerson__Object() throws Exception {
    // given
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    TestNormalizedEntity t = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    UpdateRequest actual = target.prepareUpsertRequestNoChecked(esp, t);
    // then
    // e.g. : verify(mocked).called();
    UpdateRequest expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void prepareUpsertRequest_Args__ElasticSearchPerson__Object() throws Exception {
    // given
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    TestNormalizedEntity t = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    UpdateRequest actual = target.prepareUpsertRequest(esp, t);
    // then
    // e.g. : verify(mocked).called();
    UpdateRequest expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void prepareUpsertRequest_Args__ElasticSearchPerson__Object_T__IOException()
      throws Exception {
    // given
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    TestNormalizedEntity t = null;
    // e.g. : given(mocked.called()).willReturn(1);
    try {
      // when
      target.prepareUpsertRequest(esp, t);
      fail("Expected exception was not thrown!");
    } catch (IOException e) {
      // then
    }
  }

  @Test
  public void keepCollections_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ESOptionalCollection[] actual = target.keepCollections();
    // then
    // e.g. : verify(mocked).called();
    ESOptionalCollection[] expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getOptionalElementName_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getOptionalElementName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getOptionalCollection_Args__ElasticSearchPerson__Object() throws Exception {
    // given
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    TestNormalizedEntity t = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    List<? extends ApiTypedIdentifier<String>> actual = target.getOptionalCollection(esp, t);
    // then
    // e.g. : verify(mocked).called();
    List<? extends ApiTypedIdentifier<String>> expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void doInitialLoadJdbc_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.doInitialLoadJdbc();
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void doInitialLoadJdbc_Args___T__IOException() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    try {
      // when
      target.doInitialLoadJdbc();
      fail("Expected exception was not thrown!");
    } catch (IOException e) {
      // then
    }
  }

  @Test
  public void threadExtractJdbc_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.threadExtractJdbc();
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void threadTransform_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.threadTransform();
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void threadLoad_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.threadLoad();
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void doLastRun_Args__Date() throws Exception {
    // given
    Date lastRunDt = mock(Date.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Date actual = target.doLastRun(lastRunDt);
    // then
    // e.g. : verify(mocked).called();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void _run_Args__Date() throws Exception {
    // given
    Date lastSuccessfulRunTime = mock(Date.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Date actual = target._run(lastSuccessfulRunTime);
    // then
    // e.g. : verify(mocked).called();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void extractLastRunRecsFromTable_Args__Date() throws Exception {
    // given
    Date lastRunTime = mock(Date.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    List<TestNormalizedEntity> actual = target.extractLastRunRecsFromTable(lastRunTime);
    // then
    // e.g. : verify(mocked).called();
    List<Object> expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void extractLastRunRecsFromView_Args__Date() throws Exception {
    // given
    Date lastRunTime = mock(Date.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    List<TestNormalizedEntity> actual = target.extractLastRunRecsFromView(lastRunTime);
    // then
    // e.g. : verify(mocked).called();
    List<Object> expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void buildBucketList_Args__String() throws Exception {
    // given
    String table = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    List<BatchBucket> actual = target.buildBucketList(table);
    // then
    // e.g. : verify(mocked).called();
    List<BatchBucket> expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getDriverTable_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getDriverTable();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPartitionRanges_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    List actual = target.getPartitionRanges();
    // then
    // e.g. : verify(mocked).called();
    List expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void close_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.close();
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void close_Args___T__IOException() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    try {
      // when
      target.close();
      fail("Expected exception was not thrown!");
    } catch (IOException e) {
      // then
    }
  }

  @Test
  public void finish_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.finish();
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void pullBucketRange_Args__String__String() throws Exception {
    // given
    String minId = null;
    String maxId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    List<TestNormalizedEntity> actual = target.pullBucketRange(minId, maxId);
    // then
    // e.g. : verify(mocked).called();
    List<Object> expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void extractHibernate_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    int actual = target.extractHibernate();
    // then
    // e.g. : verify(mocked).called();
    int expected = 0;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getOpts_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    JobOptions actual = target.getOpts();
    // then
    // e.g. : verify(mocked).called();
    JobOptions expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setOpts_Args__JobOptions() throws Exception {
    // given
    JobOptions opts = mock(JobOptions.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setOpts(opts);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getLegacySourceTable_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getLegacySourceTable();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getSystemCodes_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ApiSystemCodeCache actual = BasePersonIndexerJob.getSystemCodes();
    // then
    // e.g. : verify(mocked).called();
    ApiSystemCodeCache expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void runMain_Args__Class__StringArray() throws Exception {
    // given
    Class klass = null;
    String[] args = new String[] {};
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    BasePersonIndexerJob.runMain(klass, args);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void setSystemCodes_Args__ApiSystemCodeCache() throws Exception {
    // given
    ApiSystemCodeCache sysCodeCache = mock(ApiSystemCodeCache.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    BasePersonIndexerJob.setSystemCodes(sysCodeCache);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void isTestMode_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    boolean actual = BasePersonIndexerJob.isTestMode();
    // then
    // e.g. : verify(mocked).called();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setTestMode_Args__boolean() throws Exception {
    // given
    boolean testMode = false;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    BasePersonIndexerJob.setTestMode(testMode);
    // then
    // e.g. : verify(mocked).called();
  }

}
