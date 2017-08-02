package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.SessionFactory;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.dao.cms.ReplicatedPersonCasesDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.EsParentPersonCase;

public class ParentCaseHistoryIndexerJobTest {

  @Test
  public void type() throws Exception {
    assertThat(ParentCaseHistoryIndexerJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ReplicatedPersonCasesDao clientDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    ParentCaseHistoryIndexerJob target = new ParentCaseHistoryIndexerJob(clientDao,
        elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
    assertThat(target, notNullValue());
  }

  @Test
  public void extract_Args__ResultSet() throws Exception {
    ReplicatedPersonCasesDao clientDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    ParentCaseHistoryIndexerJob target = new ParentCaseHistoryIndexerJob(clientDao,
        elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
    ResultSet rs = mock(ResultSet.class);
    EsParentPersonCase actual = target.extract(rs);
    EsParentPersonCase expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void extract_Args__ResultSet_T__SQLException() throws Exception {
    ReplicatedPersonCasesDao clientDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    ParentCaseHistoryIndexerJob target = new ParentCaseHistoryIndexerJob(clientDao,
        elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
    ResultSet rs = mock(ResultSet.class);
    try {
      target.extract(rs);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
    }
  }

  @Test
  public void getDenormalizedClass_Args__() throws Exception {
    ReplicatedPersonCasesDao clientDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    ParentCaseHistoryIndexerJob target = new ParentCaseHistoryIndexerJob(clientDao,
        elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
    Object actual = target.getDenormalizedClass();
    Object expected = EsParentPersonCase.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getViewName_Args__() throws Exception {
    ReplicatedPersonCasesDao clientDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    ParentCaseHistoryIndexerJob target = new ParentCaseHistoryIndexerJob(clientDao,
        elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
    String actual = target.getInitialLoadViewName();
    String expected = "VW_MQT_PARENT_CASE_HIST";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getJdbcOrderBy_Args__() throws Exception {
    ReplicatedPersonCasesDao clientDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    ParentCaseHistoryIndexerJob target = new ParentCaseHistoryIndexerJob(clientDao,
        elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
    String actual = target.getJdbcOrderBy();
    String expected = " ORDER BY PARENT_PERSON_ID, CASE_ID, PARENT_ID ";
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void main_Args__StringArray() throws Exception {
    String[] args = new String[] {};
    ParentCaseHistoryIndexerJob.main(args);
  }

}
