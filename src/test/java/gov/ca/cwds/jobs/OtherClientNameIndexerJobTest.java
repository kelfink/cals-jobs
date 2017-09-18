package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.update.UpdateRequest;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.dao.cms.ReplicatedAkaDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.ReplicatedAkas;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherClientName;

/**
 * 
 * @author CWDS API Team
 */
@SuppressWarnings("javadoc")
public class OtherClientNameIndexerJobTest extends PersonJobTester {

  ReplicatedAkaDao dao;
  OtherClientNameIndexerJob target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    dao = new ReplicatedAkaDao(sessionFactory);
    target =
        new OtherClientNameIndexerJob(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
    target.setOpts(opts);
  }

  @Test
  public void testType() throws Exception {
    assertThat(OtherClientNameIndexerJob.class, notNullValue());
  }

  @Test
  public void testInstantiation() throws Exception {
    ReplicatedAkaDao otherClientNameDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    OtherClientNameIndexerJob target = new OtherClientNameIndexerJob(otherClientNameDao,
        elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
    assertThat(target, notNullValue());
  }

  @Test
  public void testfindAllUpdatedAfterNamedQueryExists() throws Exception {
    Query query = session.getNamedQuery(
        "gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherClientName.findAllUpdatedAfter");
    assertThat(query, is(notNullValue()));
  }

  @Test
  public void type() throws Exception {
    assertThat(OtherClientNameIndexerJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void getPartitionRanges_Args__() throws Exception {
    final List actual = target.getPartitionRanges();
    List expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacySourceTable_Args__() throws Exception {
    final String actual = target.getLegacySourceTable();
    final String expected = "OCL_NM_T";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void extract_Args__ResultSet() throws Exception {
    final ReplicatedOtherClientName actual = target.extract(rs);
    assertThat(actual, notNullValue());
  }

  @Test
  public void getDenormalizedClass_Args__() throws Exception {
    final Object actual = target.getDenormalizedClass();
    final Object expected = ReplicatedOtherClientName.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalizeSingle_Args__List() throws Exception {
    final List<ReplicatedOtherClientName> recs = new ArrayList<ReplicatedOtherClientName>();
    final ReplicatedAkas actual = target.normalizeSingle(recs);
    assertThat(actual, notNullValue());
  }

  @Test
  public void normalize_Args__List() throws Exception {
    final List<ReplicatedOtherClientName> recs = new ArrayList<ReplicatedOtherClientName>();
    final List<ReplicatedAkas> actual = target.normalize(recs);
    assertThat(actual, notNullValue());
  }

  @Test
  public void prepareUpsertRequest_Args__ElasticSearchPerson__ReplicatedAkas() throws Exception {
    ElasticSearchPerson esp = new ElasticSearchPerson();
    final ReplicatedAkas p = new ReplicatedAkas(DEFAULT_CLIENT_ID);
    UpdateRequest actual = target.prepareUpsertRequest(esp, p);
    assertThat(actual, notNullValue());
  }

  @Test
  public void prepareUpsertRequest_Args__ElasticSearchPerson__ReplicatedAkas_T__IOException()
      throws Exception {
    ElasticSearchPerson esp = new ElasticSearchPerson();
    ReplicatedAkas p = new ReplicatedAkas(DEFAULT_CLIENT_ID);
    try {
      target.prepareUpsertRequest(esp, p);
      fail("Expected exception was not thrown!");
    } catch (IOException e) {
    }
  }

  @Test
  public void getInitialLoadViewName_Args__() throws Exception {
    final String actual = target.getInitialLoadViewName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getJdbcOrderBy_Args__() throws Exception {
    final String actual = target.getJdbcOrderBy();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getInitialLoadQuery_Args__String() throws Exception {
    String dbSchemaName = "CWSRS1";
    final String actual = target.getInitialLoadQuery(dbSchemaName);
    assertThat(actual, notNullValue());
  }

  @Test
  @Ignore
  public void main_Args__StringArray() throws Exception {
    String[] args = new String[] {};
    OtherClientNameIndexerJob.main(args);
  }

}
