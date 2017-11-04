package gov.ca.cwds.jobs.inject;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.File;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Binder;
import com.google.inject.Injector;

import gov.ca.cwds.data.CmsSystemCodeSerializer;
import gov.ca.cwds.jobs.PersonJobTester;
import gov.ca.cwds.jobs.component.AtomLaunchScheduler;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.test.TestDenormalizedEntity;
import gov.ca.cwds.jobs.test.TestIndexerJob;
import gov.ca.cwds.jobs.test.TestNormalizedEntity;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.api.domain.cms.SystemCodeCache;

public class HyperCubeTest extends PersonJobTester<TestNormalizedEntity, TestDenormalizedEntity> {

  public static class TestHyperCube extends HyperCube {

    PersonJobTester lastTest;

    public TestHyperCube(final FlightPlan opts, final File esConfigFile,
        String lastJobRunTimeFilename) {
      super(opts, esConfigFile, lastJobRunTimeFilename);
    }

    @Override
    public void init() {
      this.lastTest = HyperCubeTest.lastTester;
    }

    @Override
    protected SessionFactory makeCmsSessionFactory() {
      return new Configuration().configure("test-lis-hibernate.cfg.xml").buildSessionFactory();
      // return lastTest.sessionFactory;
    }

    @Override
    protected SessionFactory makeNsSessionFactory() {
      return new Configuration().configure("test-lis-hibernate.cfg.xml").buildSessionFactory();
      // return lastTest.sessionFactory;
    }

    @Override
    protected boolean isScaffoldSystemCodeCache() {
      return true;
    }

    @Override
    protected SystemCodeCache scaffoldSystemCodeCache() {
      return mock(SystemCodeCache.class);
    }

    // @Override
    // @Provides
    // @Singleton
    // public SystemCodeCache provideSystemCodeCache(SystemCodeDao systemCodeDao,
    // SystemMetaDao systemMetaDao) {
    // return mock(SystemCodeCache.class);
    // }

  }

  public static PersonJobTester<TestNormalizedEntity, TestDenormalizedEntity> lastTester;

  HyperCube target;

  public HyperCube makeOurOwnCube(FlightPlan plan) {
    return target;
  }

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    opts = new FlightPlan();
    opts.setEsConfigLoc("config/local.yaml");
    target = new TestHyperCube(opts, new File(opts.getEsConfigLoc()), lastJobRunTimeFilename);

    target.setHibernateConfigCms("test-h2-cms.xml");
    target.setHibernateConfigNs("test-h2-ns.xml");

    final Binder binder = mock(Binder.class);
    target.setTestBinder(binder);

    HyperCube.setCubeMaker(opts -> this.makeOurOwnCube(opts));

    lastTester = this;
  }

  @Test
  public void type() throws Exception {
    assertNotNull(HyperCube.class);
  }

  @Test
  public void instantiation() throws Exception {
    assertNotNull(target);
  }

  @Test
  public void elasticSearchConfig_Args__() throws Exception {
    ElasticsearchConfiguration actual = target.elasticSearchConfig();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void provideSystemCodeCache_Args__SystemCodeDao__SystemMetaDao() throws Exception {
    SystemCodeCache actual = target.provideSystemCodeCache(systemCodeDao, systemMetaDao);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void provideCmsSystemCodeSerializer_Args__SystemCodeCache() throws Exception {
    final SystemCodeCache systemCodeCache = mock(SystemCodeCache.class);
    CmsSystemCodeSerializer actual = target.provideCmsSystemCodeSerializer(systemCodeCache);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getOpts_Args__() throws Exception {
    final FlightPlan actual = target.getOpts();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void setOpts_Args__JobOptions() throws Exception {
    target.setOpts(opts);
  }

  @Test
  public void buildInjector_Args__FlightPlan() throws Exception {
    final Injector actual = HyperCube.buildInjector(opts);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void newJob_Args__Class__FlightPlan() throws Exception {
    final Class klass = TestIndexerJob.class;
    Object actual = HyperCube.newJob(klass, opts);
    assertThat(actual, is(notNullValue()));
  }

  @Test(expected = NeutronException.class)
  public void newJob_Args__Class__FlightPlan_T__NeutronException() throws Exception {
    final Class klass = TestIndexerJob.class;

    opts = new FlightPlan();
    opts.setEsConfigLoc("config/local.yaml");
    opts.setSimulateLaunch(true);
    target = new TestHyperCube(opts, new File(opts.getEsConfigLoc()), lastJobRunTimeFilename);

    target.setHibernateConfigCms("/test-h2-cms.xml");
    target.setHibernateConfigNs("/test-h2-ns.xml");
    target.setTestBinder(mock(Binder.class));
    HyperCube.setInstance(target);

    HyperCube.newJob(klass, opts);
  }

  @Test
  public void newJob_Args__Class__StringArray() throws Exception {
    final Class klass = TestIndexerJob.class;
    final String[] args = new String[] {"-c", "config/local.yaml", "-l",
        "/Users/CWS-NS3/client_indexer_time.txt", "-t", "4", "-S"};
    Object actual = HyperCube.newJob(klass, args);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void configure_Args__() throws Exception {
    target.configure();
  }

  @Test
  public void bindDaos_Args__() throws Exception {
    target.bindDaos();
  }

  @Test
  public void makeCmsSessionFactory_Args__() throws Exception {
    final SessionFactory actual = target.makeCmsSessionFactory();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void makeNsSessionFactory_Args__() throws Exception {
    final SessionFactory actual = target.makeNsSessionFactory();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void provideLaunchDirector_Args__() throws Exception {
    AtomLaunchScheduler actual = target.provideLaunchDirector();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void makeTransportClient_Args__ElasticsearchConfiguration__boolean() throws Exception {
    ElasticsearchConfiguration config = mock(ElasticsearchConfiguration.class);
    boolean es55 = false;
    TransportClient actual = target.makeTransportClient(config, es55);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void elasticsearchClient_Args__() throws Exception {
    Client actual = target.elasticsearchClient();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void setOpts_Args__FlightPlan() throws Exception {
    target.setOpts(opts);
  }

  @Test
  public void getInjector_Args__() throws Exception {
    Injector actual = HyperCube.getInjector();
    Injector expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getHibernateConfigCms_Args__() throws Exception {
    String actual = target.getHibernateConfigCms();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void setHibernateConfigCms_Args__String() throws Exception {
    String hibernateConfigCms = null;
    target.setHibernateConfigCms(hibernateConfigCms);
  }

  @Test
  public void getHibernateConfigNs_Args__() throws Exception {
    String actual = target.getHibernateConfigNs();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void setHibernateConfigNs_Args__String() throws Exception {
    String hibernateConfigNs = null;
    target.setHibernateConfigNs(hibernateConfigNs);
  }

}
