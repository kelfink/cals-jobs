package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
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

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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

  @JsonPropertyOrder(alphabetic = true)
  static final class TestNormalizedEntry {
    private String id;

    private String name;

  }

  @JsonPropertyOrder(alphabetic = true)
  static final class TestNormalizedEntity
      implements PersistentObject, ApiPersonAware, ApiTypedIdentifier<String> {

    private String id;

    private String firstName;

    private String lastName;

    private String title;

    public TestNormalizedEntity(String id) {
      this.id = id;
    }

    @Override
    public Serializable getPrimaryKey() {
      return id;
    }

    @Override
    public String getId() {
      return id;
    }

    @Override
    public void setId(String id) {
      this.id = id;
    }

    @Override
    public Date getBirthDate() {
      return null;
    }

    @Override
    public String getFirstName() {
      return firstName;
    }

    @Override
    public String getGender() {
      return null;
    }

    @Override
    public String getLastName() {
      return lastName;
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

    public String getName() {
      return firstName;
    }

    public void setName(String name) {
      this.firstName = name;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }

    public void setLastName(String lastName) {
      this.lastName = lastName;
    }

  }

  /**
   * Denormalized
   */
  static final class TestDenormalizedEntity implements ApiGroupNormalizer<TestNormalizedEntity> {

    private String id;
    private String[] names;

    public TestDenormalizedEntity() {

    }

    public TestDenormalizedEntity(String id, String... names) {
      this.id = id;
      this.names = names;
    }

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
      return "NOBUENO";
    }

    @Override
    public String getViewName() {
      return "VW_NUTTIN";
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
  public void setup() throws Exception {
    sessionFactory = mock(SessionFactory.class);
    dao = new TestNormalizedEntityDao(sessionFactory);
    esDao = mock(ElasticsearchDao.class);
    tempFile = tempFolder.newFile("tempFile.txt");
    lastJobRunTimeFilename = tempFile.getAbsolutePath();
    target = new TestIndexerJob(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  protected void runKillThread() {
    new Thread(() -> {
      try {
        Thread.sleep(1100); // NOSONAR
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } finally {
        target.doneExtract = true;
        target.doneLoad = true;
        target.doneTransform = true;
        target.fatalError = true;
      }
    }).start();
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
    String expected = "VW_NUTTIN";
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
    String expected = " ORDER BY x.clt_identifier ";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void jsonify_Args__Object() throws Exception {
    TestNormalizedEntity obj = new TestNormalizedEntity("xyz");
    obj.setName("whatever");
    obj.setLastName("whatever");
    // obj.setLastName("whatever");
    String actual = target.jsonify(obj);
    String expected =
        "{\"birthDate\":null,\"firstName\":\"whatever\",\"gender\":null,\"id\":\"xyz\",\"lastName\":\"whatever\",\"middleName\":null,\"name\":\"whatever\",\"nameSuffix\":null,\"primaryKey\":\"xyz\",\"sensitivityIndicator\":null,\"soc158SealedClientIndicator\":null,\"ssn\":null,\"title\":null}";
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
    doThrow(new SQLException()).when(rs).getString(any());
    doThrow(new SQLException()).when(rs).next();
    try {
      // when
      target.extract(rs);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
      // then
    }
  }

  // @Test
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

  // @Test
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

  // @Test
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
    final String json =
        "[{\"first_name\":null,\"middle_name\":null,\"last_name\":null,\"name_suffix\":null,\"date_of_birth\":null,\"gender\":null,\"ssn\":null,\"type\":\"gov.ca.cwds.jobs.BasePersonIndexerJobTest$TestNormalizedEntity\","
            + "\"source\":\"{\\\"birthDate\\\":null,\\\"firstName\\\":null,\\\"gender\\\":null,\\\"id\\\":\\\"7ApWVDF00h\\\",\\\""
            + "\\\"lastName\\\":null,\\\"middleName\\\":null,\\\"name\\\":null,\\\"nameSuffix\\\":null,\\\"primaryKey\\\":\\\"7ApWVDF00h\\\",\\\"ssn\\\":null}\","
            + "\"legacy_source_table\":null,\"legacy_id\":null,\"addresses\":[],\"phone_numbers\":[],\"languages\":[],\"screenings\":[],\"referrals\":[],\"relationships\":[],\"cases\":[],\"id\":\"7ApWVDF00h\"}]";
    ElasticSearchPerson[] expected = mapper.readValue(json, ElasticSearchPerson[].class);
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
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
    final String key = "7ApWVDF00h";
    TestNormalizedEntity p = new TestNormalizedEntity(key);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ElasticSearchPerson actual = target.buildElasticSearchPerson(p);
    // then
    // e.g. : verify(mocked).called();
    ElasticSearchPerson expected = new ElasticSearchPerson();
    expected.setId(key);
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void buildElasticSearchPerson_Args__Object_T__JsonProcessingException() throws Exception {
    TestNormalizedEntity p = new TestNormalizedEntity("7ApWVDF00h");
    // doThrow(new SQLException()).when(rs).getString(any());
    // doThrow(new SQLException()).when(rs).next();
    try {
      target.buildElasticSearchPerson(p);
      fail("Expected exception was not thrown!");
    } catch (JsonProcessingException e) {
      // then
    }
  }

  // @Test
  public void buildElasticSearchPersonDoc_Args__ApiPersonAware() throws Exception {
    // given
    ApiPersonAware p = new TestNormalizedEntity("abc123");
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ElasticSearchPerson actual = target.buildElasticSearchPersonDoc(p);
    // then
    // e.g. : verify(mocked).called();
    final String json =
        "{\"first_name\":null,\"middle_name\":null,\"last_name\":null,\"name_suffix\":null,"
            + "\"date_of_birth\":null,\"gender\":null,\"ssn\":null,\"type\":\"gov.ca.cwds.jobs.BasePersonIndexerJobTest$TestNormalizedEntity\","
            + "\"source\":\"{\\\"id\\\":\\\"abc123\\\",\\\"name\\\":null,\\\"middleName\\\":null,\\\"firstName\\\":null,\\\"ssn\\\":null,\\\"lastName\\\":null,"
            + "\\\"gender\\\":null,\\\"birthDate\\\":null,\\\"nameSuffix\\\":null,\\\"primaryKey\\\":\\\"abc123\\\"}"
            + "\",\"legacy_source_table\":null,\"legacy_id\":null,\"addresses\":[],\"phone_numbers\":[],\"languages\":[],\"screenings\":[],\"referrals\":[],\"relationships\":[],\"cases\":[],\"id\":\"abc123\"}";

    ElasticSearchPerson expected = mapper.readValue(json, ElasticSearchPerson.class);
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
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
    String expected = "IDENTIFIER";
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

  // @Test
  public void normalize_Args__List() throws Exception {
    List<TestDenormalizedEntity> recs = new ArrayList<>();
    recs.add(new TestDenormalizedEntity("123", "one", "two", "three", "four"));
    List<TestNormalizedEntity> actual = target.normalize(recs);
    List<TestNormalizedEntity> expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void normalizeSingle_Args__List() throws Exception {
    // given
    List<TestDenormalizedEntity> recs = new ArrayList<>();
    recs.add(new TestDenormalizedEntity("abc1234"));
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    TestNormalizedEntity actual = target.normalizeSingle(recs);
    // then
    // e.g. : verify(mocked).called();
    TestNormalizedEntity expected = new TestNormalizedEntity("abc1234");
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
    int expected = 1;
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

  // @Test
  public void prepareDocument_Args__BulkProcessor__Object() throws Exception {
    // given
    BulkProcessor bp = mock(BulkProcessor.class);
    TestNormalizedEntity t = new TestNormalizedEntity("abc12345");
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.prepareDocument(bp, t);
    // then
    // e.g. : verify(mocked).called();
  }

  // @Test
  public void prepareDocument_Args__BulkProcessor__Object_T__IOException() throws Exception {
    // given
    BulkProcessor bp = mock(BulkProcessor.class);
    TestNormalizedEntity t = new TestNormalizedEntity("1234");
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
    TestNormalizedEntity t = new TestNormalizedEntity("abc12345");
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
    TestNormalizedEntity t = new TestNormalizedEntity("abc12345");
    String elementName = null;
    List list = new ArrayList();
    ESOptionalCollection[] keep = new ESOptionalCollection[] {};
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.prepareInsertCollections(esp, t, elementName, list, keep);
    // then
    // e.g. : verify(mocked).called();
  }

  // @Test
  public void prepareInsertCollections_Args__ElasticSearchPerson__Object__String__List__ESOptionalCollectionArray_T__JsonProcessingException()
      throws Exception {
    // given
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    TestNormalizedEntity t = new TestNormalizedEntity("abc12345");
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

  // @Test
  public void prepareUpsertJson_Args__ElasticSearchPerson__Object__String__List__ESOptionalCollectionArray()
      throws Exception {
    // given
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    TestNormalizedEntity t = new TestNormalizedEntity("abc12345");
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
    TestNormalizedEntity t = new TestNormalizedEntity("abc12345");
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

  // @Test
  public void prepareUpsertRequestNoChecked_Args__ElasticSearchPerson__Object() throws Exception {
    // given
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    TestNormalizedEntity t = new TestNormalizedEntity("abc12345");
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    UpdateRequest actual = target.prepareUpsertRequestNoChecked(esp, t);
    // then
    // e.g. : verify(mocked).called();
    UpdateRequest expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void prepareUpsertRequest_Args__ElasticSearchPerson__Object() throws Exception {
    // given
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    TestNormalizedEntity t = new TestNormalizedEntity("abc12345");
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
    TestNormalizedEntity t = new TestNormalizedEntity("abc12345");
    // e.g. : given(mocked.called()).willReturn(1);
    try {
      // when
      target.prepareUpsertRequest(esp, t);
      fail("Expected exception was not thrown!");
    } catch (IOException e) {
      // then
    }
  }

  // @Test
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
    TestNormalizedEntity t = new TestNormalizedEntity("abc12345");
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    List<? extends ApiTypedIdentifier<String>> actual = target.getOptionalCollection(esp, t);
    // then
    // e.g. : verify(mocked).called();
    List<? extends ApiTypedIdentifier<String>> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void doInitialLoadJdbc_Args__() throws Exception {
    try {
      runKillThread();
      target.doInitialLoadJdbc();
      fail("Expected exception was not thrown!");
    } catch (IOException e) {
    }
  }

  // @Test
  public void doInitialLoadJdbc_Args___T__IOException() throws Exception {
    try {
      runKillThread();
      target.doInitialLoadJdbc();
      fail("Expected exception was not thrown!");
    } catch (IOException e) {
    }
  }

  // @Test
  public void threadExtractJdbc_Args__() throws Exception {
    runKillThread();
    target.threadExtractJdbc();
  }

  @Test
  public void threadTransform_Args__() throws Exception {
    runKillThread();
    target.threadTransform();
  }

  // @Test
  public void threadLoad_Args__() throws Exception {
    runKillThread();
    target.threadLoad();
  }

  // @Test
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

  // @Test
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

  // @Test
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

  // @Test
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

  // @Test
  public void buildBucketList_Args__String() throws Exception {
    // given
    String table = "SOMETBL";
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

  // @Test
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
    target.close();
  }

  // @Test
  public void close_Args___T__IOException() throws Exception {
    doThrow(new IOException()).when(esDao).close();
    try {
      target.close();
      fail("Expected exception was not thrown!");
    } catch (IOException e) {
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

  // @Test
  public void pullBucketRange_Args__String__String() throws Exception {
    // given
    String minId = "1";
    String maxId = "2";
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    List<TestNormalizedEntity> actual = target.pullBucketRange(minId, maxId);
    // then
    // e.g. : verify(mocked).called();
    List<Object> expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
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
    String expected = "NOBUENO";
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
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

  // @Test
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

  // @Test
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
