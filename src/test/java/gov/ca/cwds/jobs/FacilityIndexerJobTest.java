package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.File;

import org.elasticsearch.client.Client;
import org.hibernate.SessionFactory;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.data.es.Elasticsearch5xDao;
import gov.ca.cwds.jobs.config.JobConfiguration;
import gov.ca.cwds.jobs.facility.FacilityProcessor;
import gov.ca.cwds.jobs.facility.FacilityRowMapper;
import gov.ca.cwds.jobs.util.JobProcessor;
import gov.ca.cwds.jobs.util.JobReader;
import gov.ca.cwds.jobs.util.JobWriter;

public class FacilityIndexerJobTest {

  @Test
  public void type() throws Exception {
    assertThat(FacilityIndexerJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    File config = null;
    FacilityIndexerJob target = new FacilityIndexerJob(config);
    assertThat(target, notNullValue());
  }

  @Test
  public void main_Args__StringArray() throws Exception {
    // given
    String[] args = new String[] {};

    // when
    FacilityIndexerJob.main(args);
    // then
    // e.g. : verify(mocked).called();
  }

  // @Test
  public void configure_Args__() throws Exception {
    File config = null;
    FacilityIndexerJob target = new FacilityIndexerJob(config);
    // given

    // when
    target.configure();
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void elasticsearchClient_Args__JobConfiguration() throws Exception {
    File config = null;
    FacilityIndexerJob target = new FacilityIndexerJob(config);
    // given
    JobConfiguration config_ = mock(JobConfiguration.class);

    // when
    Client actual = target.elasticsearchClient(config_);
    // then
    // e.g. : verify(mocked).called();
    Client expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void elasticsearchDao_Args__Client__JobConfiguration() throws Exception {
    File config = null;
    FacilityIndexerJob target = new FacilityIndexerJob(config);
    // given
    Client client = mock(Client.class);
    JobConfiguration configuration = mock(JobConfiguration.class);

    // when
    Elasticsearch5xDao actual = target.elasticsearchDao(client, configuration);
    // then
    // e.g. : verify(mocked).called();
    Elasticsearch5xDao expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void config_Args__() throws Exception {

    File config = null;
    FacilityIndexerJob target = new FacilityIndexerJob(config);
    // given

    // when
    JobConfiguration actual = target.config();
    // then
    // e.g. : verify(mocked).called();
    JobConfiguration expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void lisItemReader_Args__JobConfiguration__FacilityRowMapper__SessionFactory()
      throws Exception {
    File config = null;
    FacilityIndexerJob target = new FacilityIndexerJob(config);
    // given
    JobConfiguration jobConfiguration = mock(JobConfiguration.class);
    FacilityRowMapper facilityRowMapper = mock(FacilityRowMapper.class);
    SessionFactory sessionFactory = mock(SessionFactory.class);

    // when
    JobReader actual = target.lisItemReader(jobConfiguration, facilityRowMapper, sessionFactory);
    // then
    // e.g. : verify(mocked).called();
    JobReader expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void lisItemProcessor_Args__() throws Exception {
    File config = null;
    FacilityIndexerJob target = new FacilityIndexerJob(config);
    // given

    // when
    JobProcessor actual = target.lisItemProcessor();
    // then
    // e.g. : verify(mocked).called();
    JobProcessor expected = new FacilityProcessor();
    assertThat(actual.getClass(), is(equalTo(expected.getClass())));
  }

  // @Test
  public void lisItemWriter_Args__Elasticsearch5xDao__ObjectMapper() throws Exception {
    File config = null;
    FacilityIndexerJob target = new FacilityIndexerJob(config);
    // given
    Elasticsearch5xDao elasticsearchDao = mock(Elasticsearch5xDao.class);
    ObjectMapper objectMapper = mock(ObjectMapper.class);

    // when
    JobWriter actual = target.lisItemWriter(elasticsearchDao, objectMapper);
    // then
    // e.g. : verify(mocked).called();
    JobWriter expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void lisItemWriter_Args__JobReader__JobProcessor__JobWriter() throws Exception {
    File config = null;
    FacilityIndexerJob target = new FacilityIndexerJob(config);
    // given
    JobReader jobReader = mock(JobReader.class);
    JobProcessor jobProcessor = mock(JobProcessor.class);
    JobWriter jobWriter = mock(JobWriter.class);

    // when
    Job actual = target.lisItemWriter(jobReader, jobProcessor, jobWriter);
    // then
    // e.g. : verify(mocked).called();
    Job expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

}
