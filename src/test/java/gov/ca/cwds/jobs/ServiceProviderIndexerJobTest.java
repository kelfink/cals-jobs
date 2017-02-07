package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.hibernate.SessionFactory;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.data.cms.ServiceProviderDao;
import gov.ca.cwds.data.es.ElasticsearchDao;

public class ServiceProviderIndexerJobTest {

  @Test
  public void test_type() throws Exception {
    assertThat(ServiceProviderIndexerJob.class, notNullValue());
  }

  @Test
  public void test_instantiation() throws Exception {
    ServiceProviderDao mainDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    ServiceProviderIndexerJob target = new ServiceProviderIndexerJob(mainDao, elasticsearchDao,
        lastJobRunTimeFilename, mapper, sessionFactory);
    assertThat(target, notNullValue());
  }

  // @Test
  // public void test_main_Args__StringArray() throws Exception {
  // // given
  // String[] args = new String[] {};
  // // e.g. : given(mocked.called()).willReturn(1);
  // // when
  // ServiceProviderIndexerJob.main(args);
  // // then
  // // e.g. : verify(mocked).called();
  // }

}
