package gov.ca.cwds.jobs.component;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.test.TestDenormalizedEntity;

public class AtomInitialLoadTest {

  private static class TestAtomInitialLoad implements AtomInitialLoad<TestDenormalizedEntity> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestAtomInitialLoad.class);

    @Override
    public JobProgressTrack getTrack() {
      return null;
    }

    @Override
    public ElasticsearchDao getEsDao() {
      return null;
    }

    @Override
    public Logger getLogger() {
      return null;
    }

    @Override
    public JobOptions getOpts() {
      return null;
    }

    @Override
    public BaseDaoImpl<TestDenormalizedEntity> getJobDao() {
      return null;
    }

  }

  AtomInitialLoad target;

  @Before
  public void setup() throws Exception {
    target = new TestAtomInitialLoad();
  }

  @Test
  public void type() throws Exception {
    assertThat(AtomInitialLoad.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void limitRange_Args__List() throws Exception {
    List allKeyPairs = new ArrayList();
    List<Pair<String, String>> actual = target.limitRange(allKeyPairs);
    List<Pair<String, String>> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isInitialLoadJdbc_Args__() throws Exception {
    boolean actual = target.isInitialLoadJdbc();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getInitialLoadViewName_Args__() throws Exception {
    String actual = target.getInitialLoadViewName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getInitialLoadQuery_Args__String() throws Exception {
    String dbSchemaName = null;
    String actual = target.getInitialLoadQuery(dbSchemaName);
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getJobTotalBuckets_Args__() throws Exception {
    int actual = target.getJobTotalBuckets();
    int expected = 1;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isDelete_Args__Object() throws Exception {
    TestDenormalizedEntity t = null;
    boolean actual = target.isDelete(t);
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

}
