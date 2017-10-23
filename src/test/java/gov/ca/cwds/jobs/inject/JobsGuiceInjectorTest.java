package gov.ca.cwds.jobs.inject;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import gov.ca.cwds.data.CmsSystemCodeSerializer;
import gov.ca.cwds.data.cms.SystemCodeDao;
import gov.ca.cwds.data.cms.SystemMetaDao;
import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.PersonJobTester;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.test.TestDenormalizedEntity;
import gov.ca.cwds.jobs.test.TestIndexerJob;
import gov.ca.cwds.jobs.test.TestNormalizedEntity;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.api.domain.cms.SystemCodeCache;

public class JobsGuiceInjectorTest
    extends PersonJobTester<TestNormalizedEntity, TestDenormalizedEntity> {
  JobsGuiceInjector target;

  @Override
  @Before
  public void setup() throws Exception {
    target = new JobsGuiceInjector();
  }

  @Test
  public void type() throws Exception {
    assertNotNull(JobsGuiceInjector.class);
  }

  @Test
  public void instantiation() throws Exception {
    assertNotNull(target);
  }

  @Test
  @Ignore
  public void configure_Args__() throws Exception {
    final Path path = Paths.get(this.getClass().getResource("/es-test.yaml").getFile());
    final Injector injector =
        Guice.createInjector(new JobsGuiceInjector(null, path.toFile(), "last time file"));
    // target.configure(); // Cannot call this directly. Guice calls it for us.
  }

  @Test
  public void elasticsearchClient_Args__() throws Exception {
    Client actual = target.elasticsearchClient();
    Client expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void elasticSearchConfig_Args__() throws Exception {
    ElasticsearchConfiguration actual = target.elasticSearchConfig();
    ElasticsearchConfiguration expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void buildInjector_Args__JobOptions() throws Exception {
    JobOptions opts = mock(JobOptions.class);
    Injector actual = JobsGuiceInjector.buildInjector(opts);
    Injector expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void newContinuousJob_Args__Class__JobOptions() throws Exception {
    Class<? extends BasePersonIndexerJob<TestNormalizedEntity, TestDenormalizedEntity>> klass =
        TestIndexerJob.class;
    Object actual = JobsGuiceInjector.newContinuousJob(klass, opts);
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void newJob_Args__Class__JobOptions() throws Exception {
    Class<? extends BasePersonIndexerJob<TestNormalizedEntity, TestDenormalizedEntity>> klass =
        TestIndexerJob.class;
    Object actual = JobsGuiceInjector.newJob(klass, opts);
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test(expected = NeutronException.class)
  public void newJob_Args__Class__JobOptions_T__NeutronException() throws Exception {
    Class<? extends BasePersonIndexerJob<TestNormalizedEntity, TestDenormalizedEntity>> klass =
        TestIndexerJob.class;
    JobsGuiceInjector.newJob(klass, opts);
  }

  @Test
  public void newJob_Args__Class__StringArray() throws Exception {
    Class<? extends BasePersonIndexerJob<TestNormalizedEntity, TestDenormalizedEntity>> klass =
        TestIndexerJob.class;
    String[] args = new String[] {};
    Object actual = JobsGuiceInjector.newJob(klass, args);
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void provideSystemCodeCache_Args__SystemCodeDao__SystemMetaDao() throws Exception {
    JobsGuiceInjector target = new JobsGuiceInjector();
    SystemCodeDao systemCodeDao = mock(SystemCodeDao.class);
    SystemMetaDao systemMetaDao = mock(SystemMetaDao.class);
    SystemCodeCache actual = target.provideSystemCodeCache(systemCodeDao, systemMetaDao);
    SystemCodeCache expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void provideCmsSystemCodeSerializer_Args__SystemCodeCache() throws Exception {
    JobsGuiceInjector target = new JobsGuiceInjector();
    SystemCodeCache systemCodeCache = mock(SystemCodeCache.class);
    CmsSystemCodeSerializer actual = target.provideCmsSystemCodeSerializer(systemCodeCache);
    CmsSystemCodeSerializer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void makeTransportClient_Args__ElasticsearchConfiguration__boolean() throws Exception {
    JobsGuiceInjector target = new JobsGuiceInjector();
    ElasticsearchConfiguration config = mock(ElasticsearchConfiguration.class);
    boolean es55 = false;
    TransportClient actual = target.makeTransportClient(config, es55);
    TransportClient expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getOpts_Args__() throws Exception {
    JobsGuiceInjector target = new JobsGuiceInjector();
    JobOptions actual = target.getOpts();
    JobOptions expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setOpts_Args__JobOptions() throws Exception {
    JobsGuiceInjector target = new JobsGuiceInjector();
    JobOptions opts = mock(JobOptions.class);
    target.setOpts(opts);
  }

  @Test
  public void getInjector_Args__() throws Exception {
    Injector actual = JobsGuiceInjector.getInjector();
    Injector expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

}
