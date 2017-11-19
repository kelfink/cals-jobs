package gov.ca.cwds.neutron.util.transform;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.update.UpdateRequest;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.dao.ApiLegacyAware;
import gov.ca.cwds.data.es.ElasticSearchLegacyDescriptor;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPerson.ESOptionalCollection;
import gov.ca.cwds.data.es.ElasticSearchPersonAddress;
import gov.ca.cwds.data.es.ElasticSearchPersonLanguage;
import gov.ca.cwds.data.es.ElasticSearchPersonPhone;
import gov.ca.cwds.data.es.ElasticSearchPersonScreening;
import gov.ca.cwds.data.es.ElasticSearchRaceAndEthnicity;
import gov.ca.cwds.data.es.ElasticSearchSystemCode;
import gov.ca.cwds.data.persistence.cms.rep.CmsReplicatedEntity;
import gov.ca.cwds.data.std.ApiPersonAware;
import gov.ca.cwds.jobs.Goddard;
import gov.ca.cwds.jobs.test.SimpleAddress;
import gov.ca.cwds.jobs.test.TestDenormalizedEntity;
import gov.ca.cwds.jobs.test.TestIndexerJob;
import gov.ca.cwds.jobs.test.TestNormalizedEntity;
import gov.ca.cwds.jobs.test.TestNormalizedEntityDao;
import gov.ca.cwds.jobs.test.TestNormalizedEntry;
import gov.ca.cwds.jobs.test.TestOnlyApiPersonAware;
import gov.ca.cwds.neutron.atom.AtomPersonDocPrep;
import gov.ca.cwds.neutron.flight.FlightLog;
import gov.ca.cwds.rest.api.domain.cms.LegacyTable;

public class ElasticTransformerTest extends Goddard {

  TestNormalizedEntityDao dao;
  TestIndexerJob target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();

    dao = new TestNormalizedEntityDao(sessionFactory);
    target = new TestIndexerJob(dao, esDao, lastRunFile, MAPPER, sessionFactory, flightRecorder);
    target.setFlightPlan(flightPlan);
    target.setFlightLog(flightRecord);
  }

  @Test
  public void type() throws Exception {
    assertThat(ElasticTransformer.class, notNullValue());
  }

  @Test
  public void handleLanguage_Args__ApiPersonAware() throws Exception {
    ApiPersonAware p = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    List<ElasticSearchPersonLanguage> actual = ElasticTransformer.buildLanguage(p);
    List<ElasticSearchPersonLanguage> expected = new ArrayList<>();
    expected.add(new ElasticSearchPersonLanguage("1249", null, false));
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void handlePhone_Args__ApiPersonAware() throws Exception {
    ApiPersonAware p = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    List<ElasticSearchPersonPhone> actual = ElasticTransformer.buildPhone(p);
    List<ElasticSearchPersonPhone> expected = null;
    assertThat(actual, notNullValue());
  }

  @Test
  public void handleAddress_Args__ApiPersonAware() throws Exception {
    final TestOnlyApiPersonAware p = new TestOnlyApiPersonAware();
    final SimpleAddress addr =
        new SimpleAddress("Provo", "Utah", "UT", "206 Hinckley Center", "84602");
    p.addAddress(addr);

    final List<ElasticSearchPersonAddress> actual = ElasticTransformer.buildAddress(p);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void handleScreening_Args__ApiPersonAware() throws Exception {
    final ApiPersonAware p = new TestOnlyApiPersonAware();
    List<ElasticSearchPersonScreening> actual = ElasticTransformer.buildScreening(p);
    List<ElasticSearchPersonScreening> expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void buildElasticSearchPersonDoc_Args__ObjectMapper__ApiPersonAware() throws Exception {
    TestNormalizedEntity p = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    ElasticSearchPerson actual = ElasticTransformer.buildElasticSearchPersonDoc(MAPPER, p);
    // ElasticSearchPerson expected = mapper.readValue(
    // this.getClass().getResourceAsStream("/fixtures/ElasticTransformerTestFixture.json"),
    // ElasticSearchPerson.class);
    assertThat(actual, is(notNullValue()));
  }

  @Test(expected = IllegalArgumentException.class)
  public void buildElasticSearchPersonDoc_Args__ObjectMapper__ApiPersonAware_T__JsonProcessingException()
      throws Exception {
    final ApiPersonAware p = mock(ApiPersonAware.class);
    doThrow(new IllegalArgumentException("whatever")).when(p).getPrimaryKey();
    try {
      ElasticTransformer.buildElasticSearchPersonDoc(MAPPER, p);
      fail("Expected exception was not thrown!");
    } catch (JsonProcessingException e) {
    }
  }

  @Test
  public void handleLegacyDescriptor_Args__ApiPersonAware() throws Exception {
    final ApiPersonAware p = new TestOnlyApiPersonAware();
    ElasticSearchLegacyDescriptor actual = ElasticTransformer.buildLegacyDescriptor(p);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void jsonify_Args__Object() throws Exception {
    TestNormalizedEntity t = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    String actual = ElasticTransformer.jsonify(t);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void pushToBulkProcessor_Args__JobProgressTrack__BulkProcessor__DocWriteRequest()
      throws Exception {
    FlightLog track = mock(FlightLog.class);
    BulkProcessor bp = mock(BulkProcessor.class);
    DocWriteRequest<?> t = mock(DocWriteRequest.class);
    ElasticTransformer.pushToBulkProcessor(track, bp, t);
  }

  @Test
  public void determineId_Args__ApiLegacyAware__ElasticSearchPerson() throws Exception {
    ApiLegacyAware l = mock(ApiLegacyAware.class);
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    String actual = ElasticTransformer.determineId(l, esp);
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void determineId_Args__CmsReplicatedEntity__ElasticSearchPerson() throws Exception {
    CmsReplicatedEntity l =
        new TestDenormalizedEntity(DEFAULT_CLIENT_ID, "dave", "jey", "mariam", "jim");
    ElasticSearchPerson esp = new ElasticSearchPerson();
    String actual = ElasticTransformer.determineId(l, esp);
    String expected = DEFAULT_CLIENT_ID;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void prepareUpsertRequest_Args__AtomPersonDocPrep__String__String__ElasticSearchPerson__Object()
      throws Exception {
    AtomPersonDocPrep<TestNormalizedEntity> docPrep = target;
    String alias = null;
    String docType = null;
    ElasticSearchPerson esp = new ElasticSearchPerson();
    TestNormalizedEntity t = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    UpdateRequest actual = ElasticTransformer.<TestNormalizedEntity>prepareUpsertRequest(docPrep,
        alias, docType, esp, t);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void prepareUpsertJson_Args__AtomPersonDocPrep__ElasticSearchPerson__Object__String__List__ESOptionalCollectionArray()
      throws Exception {
    final AtomPersonDocPrep<TestNormalizedEntity> docPrep = target;
    final ElasticSearchPerson esp = new ElasticSearchPerson();

    final TestNormalizedEntity t = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    t.addEntry(new TestNormalizedEntry("xyz1234567", "crap"));

    final String elementName = "relationships";
    List list = new ArrayList();
    ESOptionalCollection[] keep = new ESOptionalCollection[] {};
    final Pair<String, String> actual = ElasticTransformer
        .<TestNormalizedEntity>prepareUpsertJson(docPrep, esp, t, elementName, list, keep);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void buildElasticSearchPersons_Args__PersistentObject() throws Exception {
    TestNormalizedEntity p = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    p.addEntry(new TestNormalizedEntry("xyz1234567", "crap"));
    ElasticSearchPerson[] actual = ElasticTransformer.buildElasticSearchPersons(p);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void buildElasticSearchPerson_Args__ApiPersonAware() throws Exception {
    ApiPersonAware p = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    ElasticSearchPerson actual = ElasticTransformer.buildElasticSearchPerson(p);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void createLegacyDescriptor_Args__String__Date__LegacyTable() throws Exception {
    String legacyId = DEFAULT_CLIENT_ID;
    Date legacyLastUpdated = new Date();
    LegacyTable legacyTable = LegacyTable.CLIENT;
    ElasticSearchLegacyDescriptor actual =
        ElasticTransformer.createLegacyDescriptor(legacyId, legacyLastUpdated, legacyTable);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void handleRaceEthnicity_Args__ApiPersonAware() throws Exception {
    ApiPersonAware p = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    ElasticSearchRaceAndEthnicity actual = ElasticTransformer.buildRaceEthnicity(p);
    ElasticSearchRaceAndEthnicity expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void handleClientCounty_Args__ApiPersonAware() throws Exception {
    ApiPersonAware p = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    ElasticSearchSystemCode actual = ElasticTransformer.buildClientCounty(p);
    ElasticSearchSystemCode expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void buildElasticSearchPersonDoc_Args__ApiPersonAware() throws Exception {
    ApiPersonAware p = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    ElasticSearchPerson actual = ElasticTransformer.buildElasticSearchPersonDoc(p);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getMapper_Args__() throws Exception {
    ObjectMapper actual = ElasticTransformer.getMapper();
    ObjectMapper expected = MAPPER;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setMapper_Args__ObjectMapper() throws Exception {
    ObjectMapper mapper = mock(ObjectMapper.class);
    ElasticTransformer.setMapper(mapper);
  }

}
