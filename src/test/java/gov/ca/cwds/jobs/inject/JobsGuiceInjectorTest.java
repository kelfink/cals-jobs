package gov.ca.cwds.jobs.inject;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.elasticsearch.client.Client;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import gov.ca.cwds.rest.ElasticsearchConfiguration;

public class JobsGuiceInjectorTest {

  @Test
  public void type() throws Exception {
    assertNotNull(JobsGuiceInjector.class);
  }

  @Test
  public void instantiation() throws Exception {
    final JobsGuiceInjector target = new JobsGuiceInjector();
    assertNotNull(target);
  }

  @Test
  public void configure_Args__() throws Exception {
    final Path path = Paths.get(this.getClass().getResource("/es-test.yaml").getFile());
    final Injector injector =
        Guice.createInjector(new JobsGuiceInjector(path.toFile(), "last time file"));
    // target.configure(); // Cannot call this directly. Guice calls it for us.
  }

  @Test
  public void elasticsearchClient_Args__() throws Exception {
    JobsGuiceInjector target = new JobsGuiceInjector();
    Client actual = target.elasticsearchClient();
    Client expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void elasticSearchConfig_Args__() throws Exception {
    JobsGuiceInjector target = new JobsGuiceInjector();
    ElasticsearchConfiguration actual = target.elasticSearchConfig();
    ElasticsearchConfiguration expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

}
