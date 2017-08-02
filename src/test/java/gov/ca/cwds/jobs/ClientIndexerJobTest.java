package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.dao.cms.ReplicatedClientDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.EsClientAddress;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedClient;

/**
 * 
 * @author CWDS API Team
 */
@SuppressWarnings("javadoc")
public class ClientIndexerJobTest {

  @BeforeClass
  public static void setupClass() {
    BasePersonIndexerJob.setTestMode(true);
  }

  @Test
  public void type() throws Exception {
    assertThat(ClientIndexerJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    // ReplicatedClientDao clientDao = null;
    // ElasticsearchDao elasticsearchDao = null;
    // String lastJobRunTimeFilename = null;
    // ObjectMapper mapper = null;
    // SessionFactory sessionFactory = null;
    // ClientIndexerJob target = new ClientIndexerJob(clientDao, elasticsearchDao,
    // lastJobRunTimeFilename, mapper, sessionFactory);
    // assertThat(target, notNullValue());
  }

  // @Test(expected = JobsException.class)
  public void main_Args$StringArray() throws Exception {
    final String[] args = new String[] {};
    ClientIndexerJob.main(args);
  }

  // @Test(expected = JobsException.class)
  public void main_Args__StringArray__t_je() throws Exception {
    String[] args = new String[] {};
    ClientIndexerJob.main(args);
  }

  // @Test(expected = JobsException.class)
  public void main_Args__bucket_range() throws Exception {
    final String[] args = new String[] {"-c", "config/local.yaml", "-r", "21-22", "-b", "500"};
    ClientIndexerJob.main(args);
  }

  // @Test(expected = JobsException.class)
  public void main_Args__bucket_range_not_digit() throws Exception {
    final String[] args = new String[] {"-c", "config/local.yaml", "-r", "abc-xyz", "-b", "500"};
    ClientIndexerJob.main(args);
  }

  // @Test
  public void extract_Args__ResultSet() throws Exception {
    ReplicatedClientDao clientDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    ClientIndexerJob target = new ClientIndexerJob(clientDao, elasticsearchDao,
        lastJobRunTimeFilename, mapper, sessionFactory);
    ResultSet rs = mock(ResultSet.class);
    EsClientAddress actual = target.extract(rs);
    EsClientAddress expected = new EsClientAddress();
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void extract_Args__ResultSet_T__SQLException() throws Exception {
    ReplicatedClientDao clientDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    ClientIndexerJob target = new ClientIndexerJob(clientDao, elasticsearchDao,
        lastJobRunTimeFilename, mapper, sessionFactory);
    ResultSet rs = mock(ResultSet.class);
    try {
      target.extract(rs);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
    }

  }

  @Test
  public void getDenormalizedClass_Args__() throws Exception {
    ReplicatedClientDao clientDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    ClientIndexerJob target = new ClientIndexerJob(clientDao, elasticsearchDao,
        lastJobRunTimeFilename, mapper, sessionFactory);
    Object actual = target.getDenormalizedClass();
    Object expected = EsClientAddress.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getViewName_Args__() throws Exception {
    ReplicatedClientDao clientDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    ClientIndexerJob target = new ClientIndexerJob(clientDao, elasticsearchDao,
        lastJobRunTimeFilename, mapper, sessionFactory);
    String actual = target.getInitialLoadViewName();
    String expected = "MQT_CLIENT_ADDRESS";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__List() throws Exception {
    ReplicatedClientDao clientDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    ClientIndexerJob target = new ClientIndexerJob(clientDao, elasticsearchDao,
        lastJobRunTimeFilename, mapper, sessionFactory);
    List<EsClientAddress> recs = new ArrayList<EsClientAddress>();
    List<ReplicatedClient> actual = target.normalize(recs);
    List<ReplicatedClient> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

}
