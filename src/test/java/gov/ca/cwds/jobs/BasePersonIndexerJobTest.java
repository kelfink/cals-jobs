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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.update.UpdateRequest;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Injector;

import gov.ca.cwds.dao.cms.BatchBucket;
import gov.ca.cwds.data.ApiTypedIdentifier;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPerson.ESOptionalCollection;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.std.ApiPersonAware;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.exception.JobsException;
import gov.ca.cwds.jobs.test.SimpleTestSystemCodeCache;
import gov.ca.cwds.jobs.test.TestDenormalizedEntity;
import gov.ca.cwds.jobs.test.TestIndexerJob;
import gov.ca.cwds.jobs.test.TestNormalizedEntity;
import gov.ca.cwds.jobs.test.TestNormalizedEntityDao;

public class BasePersonIndexerJobTest {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  SessionFactory sessionFactory;
  TestNormalizedEntityDao dao;
  ElasticsearchDao esDao;
  File tempFile;
  String lastJobRunTimeFilename;
  ObjectMapper mapper = ElasticSearchPerson.MAPPER;
  TestIndexerJob target;

  @BeforeClass
  public static void setupTests() {
    SimpleTestSystemCodeCache.init();
  }

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
    String actual = target.getInitialLoadViewName();
    String expected = "VW_NUTTIN";
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  // public void getJdbcOrderBy_Args__() throws Exception {
  // String actual = target.getJdbcOrderBy();
  // String expected = " ORDER BY x.clt_identifier ";
  // assertThat(actual, is(equalTo(expected)));
  // }

  @Test
  public void jsonify_Args__Object() throws Exception {
    TestNormalizedEntity obj = new TestNormalizedEntity("xyz");
    obj.setName("whatever");
    obj.setLastName("whatever");
    // obj.setLastName("whatever");
    String actual = target.jsonify(obj);

    String expected =
        "{\"birthDate\":null,\"firstName\":\"whatever\",\"gender\":null,\"id\":\"xyz\",\"lastName\":\"whatever\",\"legacyDescriptor\":{},\"legacyId\":\"xyz\",\"middleName\":null,\"name\":\"whatever\",\"nameSuffix\":null,\"primaryKey\":\"xyz\",\"sensitivityIndicator\":null,\"soc158SealedClientIndicator\":null,\"ssn\":null,\"title\":null}";
    // assertThat(actual, is(equalTo(expected)));
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void extract_Args__ResultSet() throws Exception {
    ResultSet rs = mock(ResultSet.class);
    Object actual = target.extract(rs);
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void extract_Args__ResultSet_T__SQLException() throws Exception {
    ResultSet rs = mock(ResultSet.class);
    doThrow(new SQLException()).when(rs).getString(any());
    doThrow(new SQLException()).when(rs).next();
    try {
      target.extract(rs);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
    }
  }

  // @Test
  public void buildBulkProcessor_Args__() throws Exception {
    BulkProcessor actual = target.buildBulkProcessor();
    BulkProcessor expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void buildInjector_Args__JobOptions() throws Exception {
    JobOptions opts = mock(JobOptions.class);
    Injector actual = BasePersonIndexerJob.buildInjector(opts);
    Injector expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void buildInjector_Args__JobOptions_T__JobsException() throws Exception {
    JobOptions opts = mock(JobOptions.class);
    try {
      BasePersonIndexerJob.buildInjector(opts);
      fail("Expected exception was not thrown!");
    } catch (JobsException e) {
    }
  }

  @Test
  public void buildElasticSearchPersons_Args__Object() throws Exception {
    TestNormalizedEntity p = new TestNormalizedEntity("7ApWVDF00h");
    ElasticSearchPerson[] actual = target.buildElasticSearchPersons(p);
    // final String json =
    // "[{\"first_name\":null,\"middle_name\":null,\"last_name\":null,\"name_suffix\":null,"
    // +
    // "\"date_of_birth\":null,\"gender\":null,\"ssn\":null,\"type\":\"gov.ca.cwds.jobs.test.TestNormalizedEntity\","
    // + "\"sensitivity_indicator\":null,\"soc158_sealed_client_indicator\":null,"
    // +
    // "\"source\":\"{\"addresses\":[{\"city\":\"Sacramento\",\"county\":\"Sacramento\",\"state\":\"CA\",\"streetAddress\":\"1234\",\"zip\":\"95660\",\"addressId\":null,\"stateCd\":null,\"streetName\":null,\"streetNumber\":null,\"apiAdrZip4\":null,\"apiAdrUnitNumber\":null,\"apiAdrAddressType\":null,\"apiAdrUnitType\":null}],\"birthDate\":null,\"firstName\":null,\"gender\":null,\"id\":\"7ApWVDF00h\",\"lastName\":null,\"legacyDescriptor\":{},\"legacyId\":\"7ApWVDF00h\",\"middleName\":null,\"name\":null,\"nameSuffix\":null,\"phones\":[{\"phoneId\":\"abc1234567\",\"phoneNumber\":\"408-374-2790\",\"phoneNumberExtension\":\"\",\"phoneType\":{}}],\"primaryKey\":\"7ApWVDF00h\",\"sensitivityIndicator\":null,\"soc158SealedClientIndicator\":null,\"ssn\":null,\"title\":null}\",\"legacy_descriptor\":{},\"legacy_source_table\":null,\"legacy_id\":null,\"addresses\":[{\"id\":null,\"city\":\"Sacramento\",\"state_code\":null,\"state_name\":null,\"zip\":\"95660\",\"legacy_descriptor\":{}}],\"phone_numbers\":[{\"number\":\"408-374-2790\",\"type\":\"Home\"}],\"languages\":[],\"screenings\":[],\"referrals\":[],\"relationships\":[],\"cases\":[],\"akas\":[],\"id\":\"7ApWVDF00h\"}]";
    // ElasticSearchPerson[] expected = mapper.readValue(json, ElasticSearchPerson[].class);
    // assertThat(actual, is(equalTo(expected)));
    assertThat(actual, is(notNullValue()));
  }

  // @Test
  public void buildElasticSearchPersons_Args__Object_T__JsonProcessingException() throws Exception {
    TestNormalizedEntity p = new TestNormalizedEntity("7ApWVDF00h");
    try {
      target.buildElasticSearchPersons(p);
      fail("Expected exception was not thrown!");
    } catch (JsonProcessingException e) {
    }
  }

  @Test
  public void buildElasticSearchPerson_Args__Object() throws Exception {
    final String key = "7ApWVDF00h";
    TestNormalizedEntity p = new TestNormalizedEntity(key);
    ElasticSearchPerson actual = target.buildElasticSearchPerson(p);
    ElasticSearchPerson expected = new ElasticSearchPerson();
    expected.setId(key);
    // assertThat(actual, is(equalTo(expected)));
    assertThat(actual, is(notNullValue()));
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
    }
  }

  // @Test
  public void buildElasticSearchPersonDoc_Args__ApiPersonAware() throws Exception {
    ApiPersonAware p = new TestNormalizedEntity("abc123");
    ElasticSearchPerson actual = target.buildElasticSearchPersonDoc(p);
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
    ApiPersonAware p = mock(ApiPersonAware.class);
    try {
      target.buildElasticSearchPersonDoc(p);
      fail("Expected exception was not thrown!");
    } catch (JsonProcessingException e) {
    }
  }

  @Test
  public void getIdColumn_Args__() throws Exception {
    String actual = target.getIdColumn();
    String expected = "IDENTIFIER";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void ifNull_Args__String() throws Exception {
    String value = null;
    String actual = target.ifNull(value);
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getDenormalizedClass_Args__() throws Exception {
    Object actual = target.getDenormalizedClass();
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void normalize_Args__List() throws Exception {
    List<TestDenormalizedEntity> recs = new ArrayList<>();
    recs.add(new TestDenormalizedEntity("123", "one", "two", "three", "four"));
    List<TestNormalizedEntity> actual = target.normalize(recs);
    List<TestNormalizedEntity> expected = new ArrayList<>();
    TestNormalizedEntity expect = new TestNormalizedEntity("123");
    expected.add(expect);
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void normalizeSingle_Args__List() throws Exception {
    List<TestDenormalizedEntity> recs = new ArrayList<>();
    recs.add(new TestDenormalizedEntity("abc1234"));
    TestNormalizedEntity actual = target.normalizeSingle(recs);
    TestNormalizedEntity expected = new TestNormalizedEntity("abc1234");
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
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void prepareDocument_Args__BulkProcessor__Object() throws Exception {
    BulkProcessor bp = mock(BulkProcessor.class);
    TestNormalizedEntity t = new TestNormalizedEntity("abc12345");
    target.prepareDocument(bp, t);
  }

  // @Test
  public void prepareDocument_Args__BulkProcessor__Object_T__IOException() throws Exception {
    BulkProcessor bp = mock(BulkProcessor.class);
    TestNormalizedEntity t = new TestNormalizedEntity("1234");
    try {
      target.prepareDocument(bp, t);
      fail("Expected exception was not thrown!");
    } catch (IOException e) {
    }
  }

  @Test
  public void setInsertCollections_Args__ElasticSearchPerson__Object__List() throws Exception {
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    TestNormalizedEntity t = new TestNormalizedEntity("abc12345");
    List list = new ArrayList();
    target.setInsertCollections(esp, t, list);
  }

  @Test
  public void prepareInsertCollections_Args__ElasticSearchPerson__Object__String__List__ESOptionalCollectionArray()
      throws Exception {
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    TestNormalizedEntity t = new TestNormalizedEntity("abc12345");
    String elementName = null;
    List list = new ArrayList();
    ESOptionalCollection[] keep = new ESOptionalCollection[] {};
    target.prepareInsertCollections(esp, t, elementName, list, keep);
  }

  // @Test
  public void prepareInsertCollections_Args__ElasticSearchPerson__Object__String__List__ESOptionalCollectionArray_T__JsonProcessingException()
      throws Exception {
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    TestNormalizedEntity t = new TestNormalizedEntity("abc12345");
    String elementName = null;
    List list = new ArrayList();
    ESOptionalCollection[] keep = new ESOptionalCollection[] {};
    try {
      target.prepareInsertCollections(esp, t, elementName, list, keep);
      fail("Expected exception was not thrown!");
    } catch (JsonProcessingException e) {
    }
  }

  // @Test
  public void prepareUpsertJson_Args__ElasticSearchPerson__Object__String__List__ESOptionalCollectionArray()
      throws Exception {
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    TestNormalizedEntity t = new TestNormalizedEntity("abc12345");
    String elementName = null;
    List list = new ArrayList();
    ESOptionalCollection[] keep = new ESOptionalCollection[] {};
    Pair<String, String> actual = target.prepareUpsertJson(esp, t, elementName, list, keep);
    Pair<String, String> expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void prepareUpsertJson_Args__ElasticSearchPerson__Object__String__List__ESOptionalCollectionArray_T__JsonProcessingException()
      throws Exception {
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    TestNormalizedEntity t = new TestNormalizedEntity("abc12345");
    String elementName = null;
    List list = new ArrayList();
    ESOptionalCollection[] keep = new ESOptionalCollection[] {};
    try {
      target.prepareUpsertJson(esp, t, elementName, list, keep);
      fail("Expected exception was not thrown!");
    } catch (JsonProcessingException e) {
    }
  }

  // @Test
  public void prepareUpsertRequestNoChecked_Args__ElasticSearchPerson__Object() throws Exception {
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    TestNormalizedEntity t = new TestNormalizedEntity("abc12345");
    UpdateRequest actual = target.prepareUpsertRequestNoChecked(esp, t);
    UpdateRequest expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void prepareUpsertRequest_Args__ElasticSearchPerson__Object() throws Exception {
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    TestNormalizedEntity t = new TestNormalizedEntity("abc12345");
    UpdateRequest actual = target.prepareUpsertRequest(esp, t);
    UpdateRequest expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void prepareUpsertRequest_Args__ElasticSearchPerson__Object_T__IOException()
      throws Exception {
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    TestNormalizedEntity t = new TestNormalizedEntity("abc12345");
    try {
      target.prepareUpsertRequest(esp, t);
      fail("Expected exception was not thrown!");
    } catch (IOException e) {
    }
  }

  // @Test
  public void keepCollections_Args__() throws Exception {
    ESOptionalCollection[] actual = target.keepCollections();
    ESOptionalCollection[] expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getOptionalElementName_Args__() throws Exception {
    String actual = target.getOptionalElementName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getOptionalCollection_Args__ElasticSearchPerson__Object() throws Exception {
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    TestNormalizedEntity t = new TestNormalizedEntity("abc12345");
    List<? extends ApiTypedIdentifier<String>> actual = target.getOptionalCollection(esp, t);
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
    Date lastRunDt = mock(Date.class);
    Date actual = target.doLastRun(lastRunDt);
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void _run_Args__Date() throws Exception {
    Date lastSuccessfulRunTime = mock(Date.class);
    Date actual = target._run(lastSuccessfulRunTime);
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void extractLastRunRecsFromTable_Args__Date() throws Exception {
    Date lastRunTime = mock(Date.class);
    List<TestNormalizedEntity> actual = target.extractLastRunRecsFromTable(lastRunTime);
    List<Object> expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void extractLastRunRecsFromView_Args__Date() throws Exception {
    Date lastRunTime = mock(Date.class);
    List<TestNormalizedEntity> actual =
        target.extractLastRunRecsFromView(lastRunTime, new HashSet<String>());
    List<Object> expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void buildBucketList_Args__String() throws Exception {
    String table = "SOMETBL";
    List<BatchBucket> actual = target.buildBucketList(table);
    List<BatchBucket> expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getDriverTable_Args__() throws Exception {
    String actual = target.getDriverTable();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void getPartitionRanges_Args__() throws Exception {
    List actual = target.getPartitionRanges();
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
    target.finish();
  }

  // @Test
  public void pullBucketRange_Args__String__String() throws Exception {
    String minId = "1";
    String maxId = "2";
    List<TestNormalizedEntity> actual = target.pullBucketRange(minId, maxId);
    List<Object> expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void extractHibernate_Args__() throws Exception {
    int actual = target.extractHibernate();
    int expected = 0;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getOpts_Args__() throws Exception {
    JobOptions actual = target.getOpts();
    JobOptions expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setOpts_Args__JobOptions() throws Exception {
    JobOptions opts = mock(JobOptions.class);
    target.setOpts(opts);
  }

  // @Test
  public void runMain_Args__Class__StringArray() throws Exception {
    Class klass = null;
    String[] args = new String[] {};
    BasePersonIndexerJob.runMain(klass, args);
  }

  // @Test
  public void isTestMode_Args__() throws Exception {
    boolean actual = BasePersonIndexerJob.isTestMode();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setTestMode_Args__boolean() throws Exception {
    boolean testMode = false;
    BasePersonIndexerJob.setTestMode(testMode);
  }

}
