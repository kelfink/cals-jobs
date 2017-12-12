package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.update.UpdateRequest;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.dao.cms.ReplicatedPersonCasesDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.EsPersonCase;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonCases;
import gov.ca.cwds.neutron.flight.FlightPlan;
import gov.ca.cwds.neutron.launch.FlightRecorder;

public class CaseHistoryIndexerJobTest extends Goddard {

  private static class TestCaseHistoryIndexerJob extends CaseHistoryIndexerJob {

    public TestCaseHistoryIndexerJob(ReplicatedPersonCasesDao dao, ElasticsearchDao esDao,
        String lastJobRunTimeFilename, ObjectMapper mapper, SessionFactory sessionFactory,
        FlightRecorder jobHistory, FlightPlan opts) {
      super(dao, esDao, lastJobRunTimeFilename, mapper, opts);
    }

  }

  ReplicatedPersonCasesDao dao;
  TestCaseHistoryIndexerJob target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    dao = new ReplicatedPersonCasesDao(sessionFactory);
    target = new TestCaseHistoryIndexerJob(dao, esDao, lastRunFile, MAPPER,
        sessionFactory, flightRecorder, flightPlan);
  }

  @Test
  public void type() throws Exception {
    assertThat(CaseHistoryIndexerJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void getInitialLoadQuery_Args__String() throws Exception {
    final String dbSchemaName = "CWSRS1";
    final String actual = target.getInitialLoadQuery(dbSchemaName);
    assertThat(actual, notNullValue());
  }

  @Test
  public void mustDeleteLimitedAccessRecords_Args__() throws Exception {
    final boolean actual = target.mustDeleteLimitedAccessRecords();
    final boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void prepareUpsertRequest_Args__ElasticSearchPerson__ReplicatedPersonCases()
      throws Exception {
    ElasticSearchPerson esp = new ElasticSearchPerson();
    ReplicatedPersonCases cases = new ReplicatedPersonCases(DEFAULT_CLIENT_ID);
    UpdateRequest actual = target.prepareUpsertRequest(esp, cases);
    assertThat(actual, notNullValue());
  }

  @Test
  public void normalizeSingle_Args__List() throws Exception {
    List<EsPersonCase> recs = new ArrayList<EsPersonCase>();
    ReplicatedPersonCases actual = target.normalizeSingle(recs);
    ReplicatedPersonCases expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__List() throws Exception {
    List<EsPersonCase> recs = new ArrayList<EsPersonCase>();
    List<ReplicatedPersonCases> actual = target.normalize(recs);
    assertThat(actual, notNullValue());
  }

}
