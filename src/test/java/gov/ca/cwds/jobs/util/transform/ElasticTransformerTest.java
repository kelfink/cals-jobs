package gov.ca.cwds.jobs.util.transform;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchLegacyDescriptor;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonAddress;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonScreening;
import gov.ca.cwds.data.std.ApiPersonAware;
import gov.ca.cwds.jobs.TestNormalizedEntity;

public class ElasticTransformerTest {

  @Test
  public void type() throws Exception {
    assertThat(ElasticTransformer.class, notNullValue());
  }

  @Test
  public void handleLanguage_Args__ApiPersonAware() throws Exception {
    // given
    ApiPersonAware p = mock(ApiPersonAware.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    List<String> actual = ElasticTransformer.handleLanguage(p);
    // then
    // e.g. : verify(mocked).called();
    List<String> expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void handlePhone_Args__ApiPersonAware() throws Exception {
    // given
    ApiPersonAware p = new TestNormalizedEntity("abc123");
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    List<ElasticSearchPerson.ElasticSearchPersonPhone> actual = ElasticTransformer.handlePhone(p);
    // then
    // e.g. : verify(mocked).called();
    List<ElasticSearchPerson.ElasticSearchPersonPhone> expected = null;
    assertThat(actual, notNullValue());
  }

  @Test
  public void handleAddress_Args__ApiPersonAware() throws Exception {
    // given
    ApiPersonAware p = mock(ApiPersonAware.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    List<ElasticSearchPersonAddress> actual = ElasticTransformer.handleAddress(p);
    // then
    // e.g. : verify(mocked).called();
    List<ElasticSearchPersonAddress> expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void handleScreening_Args__ApiPersonAware() throws Exception {
    // given
    ApiPersonAware p = mock(ApiPersonAware.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    List<ElasticSearchPersonScreening> actual = ElasticTransformer.handleScreening(p);
    // then
    // e.g. : verify(mocked).called();
    List<ElasticSearchPersonScreening> expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void buildElasticSearchPersonDoc_Args__ObjectMapper__ApiPersonAware() throws Exception {
    // given
    ObjectMapper mapper = ElasticSearchPerson.MAPPER;
    TestNormalizedEntity p = new TestNormalizedEntity("abc12340x2");
    // when(p.getPrimaryKey()).thenReturn("abc12340x2");
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ElasticSearchPerson actual = ElasticTransformer.buildElasticSearchPersonDoc(mapper, p);
    // then
    // e.g. : verify(mocked).called();
    //
    ElasticSearchPerson expected = mapper.readValue(
        this.getClass().getResourceAsStream("/fixtures/ElasticTransformerTestFixture.json"),
        ElasticSearchPerson.class);
    assertThat(actual, is(equalTo(expected)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void buildElasticSearchPersonDoc_Args__ObjectMapper__ApiPersonAware_T__JsonProcessingException()
      throws Exception {
    // given
    ObjectMapper mapper = mock(ObjectMapper.class);
    ApiPersonAware p = mock(ApiPersonAware.class);
    doThrow(new IllegalArgumentException("whatever")).when(p).getPrimaryKey();
    // e.g. : given(mocked.called()).willReturn(1);
    try {
      // when
      ElasticTransformer.buildElasticSearchPersonDoc(mapper, p);
      fail("Expected exception was not thrown!");
    } catch (JsonProcessingException e) {
      // then
    }
  }

  @Test
  public void createLegacyDescriptor_Args__String__Date__LegacyTable() throws Exception {
    // given
    String legacyId = null;
    Date legacyLastUpdated = mock(Date.class);
    LegacyTable legacyTable = LegacyTable.OCL_NM_T;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ElasticSearchLegacyDescriptor actual =
        ElasticTransformer.createLegacyDescriptor(legacyId, legacyLastUpdated, legacyTable);
    // then
    // e.g. : verify(mocked).called();
    ElasticSearchLegacyDescriptor expected = new ElasticSearchLegacyDescriptor();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void handleLegacyDescriptor_Args__ApiPersonAware() throws Exception {
    // given
    ApiPersonAware p = mock(ApiPersonAware.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ElasticSearchLegacyDescriptor actual = ElasticTransformer.handleLegacyDescriptor(p);
    // then
    // e.g. : verify(mocked).called();
    ElasticSearchLegacyDescriptor expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

}
