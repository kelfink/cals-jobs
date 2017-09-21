package gov.ca.cwds.jobs.util.transform;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.data.es.ElasticSearchLegacyDescriptor;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPersonAddress;
import gov.ca.cwds.data.es.ElasticSearchPersonLanguage;
import gov.ca.cwds.data.es.ElasticSearchPersonPhone;
import gov.ca.cwds.data.es.ElasticSearchPersonScreening;
import gov.ca.cwds.data.std.ApiPersonAware;
import gov.ca.cwds.jobs.PersonJobTester;
import gov.ca.cwds.jobs.test.SimpleAddress;
import gov.ca.cwds.jobs.test.TestNormalizedEntity;
import gov.ca.cwds.jobs.test.TestOnlyApiPersonAware;

public class ElasticTransformerTest extends PersonJobTester {

  @Test
  public void type() throws Exception {
    assertThat(ElasticTransformer.class, notNullValue());
  }

  @Test
  public void handleLanguage_Args__ApiPersonAware() throws Exception {
    ApiPersonAware p = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    List<ElasticSearchPersonLanguage> actual = ElasticTransformer.handleLanguage(p);
    List<ElasticSearchPersonLanguage> expected = new ArrayList<>();
    expected.add(new ElasticSearchPersonLanguage("1249", null, false));
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void handlePhone_Args__ApiPersonAware() throws Exception {
    ApiPersonAware p = new TestNormalizedEntity(DEFAULT_CLIENT_ID);
    List<ElasticSearchPersonPhone> actual = ElasticTransformer.handlePhone(p);
    List<ElasticSearchPersonPhone> expected = null;
    assertThat(actual, notNullValue());
  }

  @Test
  public void handleAddress_Args__ApiPersonAware() throws Exception {
    final TestOnlyApiPersonAware p = new TestOnlyApiPersonAware();
    SimpleAddress addr = new SimpleAddress("Provo", "Utah", "UT", "206 Hinckley Center", "84602");
    p.addAddress(addr);

    final List<ElasticSearchPersonAddress> actual = ElasticTransformer.handleAddress(p);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void handleScreening_Args__ApiPersonAware() throws Exception {
    final ApiPersonAware p = new TestOnlyApiPersonAware();
    List<ElasticSearchPersonScreening> actual = ElasticTransformer.handleScreening(p);
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
    ObjectMapper mapper = mock(ObjectMapper.class);
    final ApiPersonAware p = mock(ApiPersonAware.class);
    doThrow(new IllegalArgumentException("whatever")).when(p).getPrimaryKey();
    try {
      ElasticTransformer.buildElasticSearchPersonDoc(mapper, p);
      fail("Expected exception was not thrown!");
    } catch (JsonProcessingException e) {
    }
  }

  @Test
  public void handleLegacyDescriptor_Args__ApiPersonAware() throws Exception {
    final ApiPersonAware p = new TestOnlyApiPersonAware();
    ElasticSearchLegacyDescriptor actual = ElasticTransformer.handleLegacyDescriptor(p);
    assertThat(actual, is(notNullValue()));
  }

}
