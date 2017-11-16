package gov.ca.cwds.jobs.util.elastic;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.data.es.Elasticsearch5xDao;
import gov.ca.cwds.jobs.Goddard;

public class ElasticJobWriterTest extends Goddard {

  ElasticJobWriter target;
  Elasticsearch5xDao es5xDao;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();

    es5xDao = mock(Elasticsearch5xDao.class);
    Client client = mock(Client.class);
    Settings settings = Settings.builder().build();
    when(es5xDao.getClient()).thenReturn(client);
    when(client.settings()).thenReturn(settings);

    target = new ElasticJobWriter(es5xDao, mapper);
  }

  @Test
  public void type() throws Exception {
    assertThat(ElasticJobWriter.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void write_Args__List() throws Exception {
    List<Object> items = new ArrayList<Object>();
    target.write(items);
  }

  @Test
  public void destroy_Args__() throws Exception {
    target.destroy();
  }

}
