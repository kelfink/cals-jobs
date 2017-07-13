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
import java.util.List;

import org.elasticsearch.action.update.UpdateRequest;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.dao.cms.ReplicatedPersonReferralsDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.EsPersonReferral;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonReferrals;

public class ReferralHistoryIndexerJobTest {

  // ====================
  // TEST MEMBERS:
  // ====================

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  SessionFactory sessionFactory;
  ReplicatedPersonReferralsDao dao;
  ElasticsearchDao esDao;
  File tempFile;
  String lastJobRunTimeFilename;
  ObjectMapper mapper = ElasticSearchPerson.MAPPER;
  ReferralHistoryIndexerJob target;

  @Before
  public void setup() throws Exception {
    sessionFactory = mock(SessionFactory.class);
    dao = new ReplicatedPersonReferralsDao(sessionFactory);
    esDao = mock(ElasticsearchDao.class);
    tempFile = tempFolder.newFile("tempFile.txt");
    lastJobRunTimeFilename = tempFile.getAbsolutePath();
    target =
        new ReferralHistoryIndexerJob(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  @Test
  public void type() throws Exception {
    assertThat(ReferralHistoryIndexerJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void getDenormalizedClass_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Object actual = target.getDenormalizedClass();
    // then
    // e.g. : verify(mocked).called();
    Object expected = EsPersonReferral.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getViewName_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getInitialLoadViewName();
    // then
    // e.g. : verify(mocked).called();
    String expected = "MQT_REFERRAL_HIST";
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
    String expected = " ORDER BY CLIENT_ID ";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacySourceTable_Args__() throws Exception {
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getLegacySourceTable();
    // then
    // e.g. : verify(mocked).called();
    String expected = "REFERL_T";
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void normalizeSingle_Args__List() throws Exception {
    // given
    List<EsPersonReferral> recs = new ArrayList<EsPersonReferral>();
    EsPersonReferral addMe = new EsPersonReferral();
    addMe.setClientId("1234");
    recs.add(addMe);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ReplicatedPersonReferrals actual = target.normalizeSingle(recs);
    // then
    // e.g. : verify(mocked).called();
    ReplicatedPersonReferrals expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__List() throws Exception {
    // given
    List<EsPersonReferral> recs = new ArrayList<EsPersonReferral>();
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    List<ReplicatedPersonReferrals> actual = target.normalize(recs);
    // then
    // e.g. : verify(mocked).called();
    List<ReplicatedPersonReferrals> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void prepareUpsertRequest_Args__ElasticSearchPerson__ReplicatedPersonReferrals()
      throws Exception {
    // given
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    ReplicatedPersonReferrals referrals = mock(ReplicatedPersonReferrals.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    UpdateRequest actual = target.prepareUpsertRequest(esp, referrals);
    // then
    // e.g. : verify(mocked).called();
    UpdateRequest expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void prepareUpsertRequest_Args__ElasticSearchPerson__ReplicatedPersonReferrals_T__IOException()
      throws Exception {
    // given
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    ReplicatedPersonReferrals referrals = mock(ReplicatedPersonReferrals.class);
    // e.g. : given(mocked.called()).willReturn(1);
    try {
      // when
      target.prepareUpsertRequest(esp, referrals);
      fail("Expected exception was not thrown!");
    } catch (IOException e) {
      // then
    }
  }

  // @Test
  public void extract_Args__ResultSet() throws Exception {
    // given
    ResultSet rs = mock(ResultSet.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    EsPersonReferral actual = target.extract(rs);
    // then
    // e.g. : verify(mocked).called();
    EsPersonReferral expected = new EsPersonReferral();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void extract_Args__ResultSet_T__SQLException() throws Exception {
    // given
    ResultSet rs = mock(ResultSet.class);
    doThrow(new SQLException()).when(rs).getString(any());
    try {
      // when
      target.extract(rs);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
      // then
    }
  }

  // @Test
  public void main_Args__StringArray() throws Exception {
    // given
    String[] args = new String[] {};
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ReferralHistoryIndexerJob.main(args);
    // then
    // e.g. : verify(mocked).called();
  }

}
