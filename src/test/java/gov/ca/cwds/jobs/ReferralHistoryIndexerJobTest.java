package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.update.UpdateRequest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import gov.ca.cwds.dao.cms.ReplicatedPersonReferralsDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.persistence.cms.EsPersonReferral;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonReferrals;
import gov.ca.cwds.jobs.config.JobOptionsTest;

public class ReferralHistoryIndexerJobTest extends PersonJobTester {

  // ====================
  // TEST MEMBERS:
  // ====================

  ReplicatedPersonReferralsDao dao;
  ReferralHistoryIndexerJob target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();

    dao = new ReplicatedPersonReferralsDao(sessionFactory);
    target =
        new ReferralHistoryIndexerJob(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
    target.setOpts(JobOptionsTest.makeGeneric());
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
    Object actual = target.getDenormalizedClass();
    Object expected = EsPersonReferral.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getViewName_Args__() throws Exception {
    String actual = target.getInitialLoadViewName();
    String expected = "VW_MQT_REFRL_ONLY";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getJdbcOrderBy_Args__() throws Exception {
    String actual = target.getJdbcOrderBy().trim();
    String expected = "";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacySourceTable_Args__() throws Exception {
    String actual = target.getLegacySourceTable();
    String expected = "REFERL_T";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  @Ignore
  public void normalizeSingle_Args__List() throws Exception {
    List<EsPersonReferral> recs = new ArrayList<EsPersonReferral>();
    EsPersonReferral addMe = new EsPersonReferral();
    addMe.setClientId("qz11234567");
    recs.add(addMe);
    ReplicatedPersonReferrals actual = target.normalizeSingle(recs);
    ReplicatedPersonReferrals expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__List() throws Exception {
    List<EsPersonReferral> recs = new ArrayList<EsPersonReferral>();
    List<ReplicatedPersonReferrals> actual = target.normalize(recs);
    List<ReplicatedPersonReferrals> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  @Ignore
  public void prepareUpsertRequest_Args__ElasticSearchPerson__ReplicatedPersonReferrals()
      throws Exception {
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    ReplicatedPersonReferrals referrals = mock(ReplicatedPersonReferrals.class);
    UpdateRequest actual = target.prepareUpsertRequest(esp, referrals);
    UpdateRequest expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void prepareUpsertRequest_Args__ElasticSearchPerson__ReplicatedPersonReferrals_T__IOException()
      throws Exception {
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    ReplicatedPersonReferrals referrals = mock(ReplicatedPersonReferrals.class);
    try {
      target.prepareUpsertRequest(esp, referrals);
      fail("Expected exception was not thrown!");
    } catch (IOException e) {
    }
  }

  @Test
  @Ignore
  public void extract_Args__ResultSet() throws Exception {
    EsPersonReferral actual = target.extract(rs);
    EsPersonReferral expected = new EsPersonReferral();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void extract_Args__ResultSet_T__SQLException() throws Exception {
    doThrow(new SQLException()).when(rs).getString(any());
    try {
      target.extract(rs);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
    }
  }

  @Test
  @Ignore
  public void main_Args__StringArray() throws Exception {
    String[] args = new String[] {};
    ReferralHistoryIndexerJob.main(args);
  }

  @Test
  public void getInitialLoadViewName_Args__() throws Exception {
    String actual = target.getInitialLoadViewName();
    assertThat(actual, notNullValue());
  }

  @Test
  @Ignore
  public void getInitialLoadQuery_Args__String() throws Exception {
    String dbSchemaName = null;
    String actual = target.getInitialLoadQuery(dbSchemaName);
    assertThat(actual, notNullValue());
  }

  @Test
  @Ignore
  public void pullRange_Args__Pair() throws Exception {
    Pair<String, String> p = mock(Pair.class);
    target.pullRange(p);
  }

  @Test
  @Ignore
  public void threadExtractJdbc_Args__() throws Exception {
    target.threadExtractJdbc();
  }

  @Test
  public void useTransformThread_Args__() throws Exception {
    boolean actual = target.useTransformThread();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPartitionRanges_Args__() throws Exception {
    final List actual = target.getPartitionRanges();
    assertThat(actual, notNullValue());
  }

  @Test
  public void mustDeleteLimitedAccessRecords_Args__() throws Exception {
    boolean actual = target.mustDeleteLimitedAccessRecords();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void extractReferral_Args__ResultSet() throws Exception {
    EsPersonReferral actual = target.extractReferral(rs);
    EsPersonReferral expected = null;
    assertThat(actual, notNullValue());
  }

  @Test
  public void extractReferral_Args__ResultSet_T__SQLException() throws Exception {
    when(rs.next()).thenThrow(new SQLException());
    when(rs.getString(any(Integer.class))).thenThrow(new SQLException());
    try {
      target.extractReferral(rs);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
    }
  }

  @Test
  public void extractAllegation_Args__ResultSet() throws Exception {
    EsPersonReferral actual = target.extractAllegation(rs);
    assertThat(actual, notNullValue());
  }

  @Test(expected = SQLException.class)
  public void extractAllegation_Args__ResultSet_T__SQLException() throws Exception {
    when(rs.next()).thenThrow(new SQLException());
    when(rs.getString(any(Integer.class))).thenThrow(new SQLException());
    target.extractAllegation(rs);
  }

  @Test
  public void isRangeSelfManaging_Args__() throws Exception {
    boolean actual = target.isRangeSelfManaging();
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getConnection_Args__() throws Exception {
    Connection actual = target.getConnection();
    assertThat(actual, notNullValue());
  }

  @Test
  public void getConnection_Args___T__SQLException() throws Exception {
    when(cp.getConnection()).thenThrow(SQLException.class);
    try {
      target.getConnection();
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
    }
  }

  @Test
  public void getConnection_Args___T__InterruptedException() throws Exception {
    when(cp.getConnection()).thenThrow(InterruptedException.class);
    try {
      target.getConnection();
      fail("Expected exception was not thrown!");
    } catch (InterruptedException e) {
    }
  }

  @Test
  public void allocateThreadMemory_Args__() throws Exception {
    target.allocateThreadMemory();
  }

}
