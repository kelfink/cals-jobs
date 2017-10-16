package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.hibernate.SessionFactory;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.data.es.Elasticsearch5xDao;
import gov.ca.cwds.jobs.component.Job;
import gov.ca.cwds.jobs.config.JobConfiguration;
import gov.ca.cwds.jobs.facility.FacilityProcessor;
import gov.ca.cwds.jobs.facility.FacilityRowMapper;
import gov.ca.cwds.jobs.util.JobProcessor;
import gov.ca.cwds.jobs.util.JobReader;
import gov.ca.cwds.jobs.util.JobWriter;

public class FacilityIndexerJobTest {

  private static class TestFacilityIndexerJob extends FacilityIndexerJob {

    public TestFacilityIndexerJob(File config) {
      super(config);
    }

    @Override
    protected TransportClient produceTransportClient(Settings settings) {
      TransportClient client = mock(TransportClient.class);
      return client;
    }

  }

  @Test
  public void type() throws Exception {
    assertThat(FacilityIndexerJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    File config = null;
    TestFacilityIndexerJob target = new TestFacilityIndexerJob(config);
    assertThat(target, notNullValue());
  }

  @Test
  public void elasticsearchClient_Args__JobConfiguration() throws Exception {
    File config = null;
    TestFacilityIndexerJob target = new TestFacilityIndexerJob(config);

    JobConfiguration config_ = mock(JobConfiguration.class);
    when(config_.getElasticsearchCluster()).thenReturn("elasticsearch");
    when(config_.getElasticsearchHost()).thenReturn("localhost");
    when(config_.getElasticsearchPort()).thenReturn("9200");

    Client actual = target.elasticsearchClient(config_);
    assertThat(actual, notNullValue());
  }

  @Test
  public void elasticsearchDao_Args__Client__JobConfiguration() throws Exception {
    File config = null;
    TestFacilityIndexerJob target = new TestFacilityIndexerJob(config);
    Client client = mock(Client.class);
    JobConfiguration configuration = mock(JobConfiguration.class);
    Elasticsearch5xDao actual = target.elasticsearchDao(client, configuration);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void config_Args__() throws Exception {
    File config = null;
    TestFacilityIndexerJob target = new TestFacilityIndexerJob(config);
    JobConfiguration actual = target.config();
    JobConfiguration expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  @Ignore
  public void lisItemReader_Args__JobConfiguration__FacilityRowMapper__SessionFactory()
      throws Exception {
    File config = null;
    TestFacilityIndexerJob target = new TestFacilityIndexerJob(config);
    JobConfiguration jobConfiguration = mock(JobConfiguration.class);
    FacilityRowMapper facilityRowMapper = mock(FacilityRowMapper.class);
    SessionFactory sessionFactory = mock(SessionFactory.class);
    JobReader actual = target.lisItemReader(jobConfiguration, facilityRowMapper, sessionFactory);
    JobReader expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void lisItemProcessor_Args__() throws Exception {
    File config = null;
    TestFacilityIndexerJob target = new TestFacilityIndexerJob(config);
    JobProcessor actual = target.lisItemProcessor();
    JobProcessor expected = new FacilityProcessor();
    assertThat(actual.getClass(), is(equalTo(expected.getClass())));
  }

  @Test
  @Ignore
  public void lisItemWriter_Args__Elasticsearch5xDao__ObjectMapper() throws Exception {
    File config = null;
    TestFacilityIndexerJob target = new TestFacilityIndexerJob(config);
    Elasticsearch5xDao elasticsearchDao = mock(Elasticsearch5xDao.class);
    ObjectMapper objectMapper = mock(ObjectMapper.class);
    JobWriter actual = target.lisItemWriter(elasticsearchDao, objectMapper);
    JobWriter expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  // @Ignore
  public void lisItemWriter_Args__JobReader__JobProcessor__JobWriter() throws Exception {
    File config = null;
    TestFacilityIndexerJob target = new TestFacilityIndexerJob(config);
    JobReader jobReader = mock(JobReader.class);
    JobProcessor jobProcessor = mock(JobProcessor.class);
    JobWriter jobWriter = mock(JobWriter.class);
    Job actual = target.lisItemWriter(jobReader, jobProcessor, jobWriter);
    assertThat(actual, is(notNullValue()));
  }

}
