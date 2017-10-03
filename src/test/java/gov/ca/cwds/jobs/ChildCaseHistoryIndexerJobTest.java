package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import gov.ca.cwds.dao.cms.ReplicatedPersonCasesDao;
import gov.ca.cwds.data.persistence.cms.EsChildPersonCase;
import gov.ca.cwds.data.persistence.cms.EsPersonCase;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonCases;

public class ChildCaseHistoryIndexerJobTest
    extends PersonJobTester<ReplicatedPersonCases, EsPersonCase> {

  ReplicatedPersonCasesDao dao;
  ChildCaseHistoryIndexerJob target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    dao = new ReplicatedPersonCasesDao(this.sessionFactory);
    target =
        new ChildCaseHistoryIndexerJob(dao, esDao, lastJobRunTimeFilename, MAPPER, sessionFactory);
  }

  @Test
  public void type() throws Exception {
    assertThat(ChildCaseHistoryIndexerJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void extract_Args__ResultSet() throws Exception {
    EsChildPersonCase actual = target.extract(rs);
    assertThat(actual, notNullValue());
  }

  @Test
  @Ignore
  public void extract_Args__ResultSet_T__SQLException() throws Exception {
    try {
      target.extract(rs);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
    }
  }

  @Test
  public void getDenormalizedClass_Args__() throws Exception {
    Object actual = target.getDenormalizedClass();
    Object expected = EsChildPersonCase.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getViewName_Args__() throws Exception {
    String actual = target.getInitialLoadViewName();
    String expected = "MQT_CASE_HIST";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getJdbcOrderBy_Args__() throws Exception {
    String actual = target.getJdbcOrderBy();
    String expected = " ORDER BY FOCUS_CHILD_ID, CASE_ID, PARENT_ID ";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  @Ignore
  public void main_Args__StringArray() throws Exception {
    String[] args = new String[] {};
    ChildCaseHistoryIndexerJob.main(args);
  }

}
