package gov.ca.cwds.data.es;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class ElasticsearchConfiguration5xTest {

  ElasticsearchConfiguration5x target;

  @Before
  public void setup() throws Exception {
    target = new ElasticsearchConfiguration5x();
  }

  @Test
  public void type() throws Exception {
    assertThat(ElasticsearchConfiguration5x.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void getElasticsearchHost_Args__() throws Exception {
    String actual = target.getElasticsearchHost();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getElasticsearchPort_Args__() throws Exception {
    String actual = target.getElasticsearchPort();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getElasticsearchCluster_Args__() throws Exception {
    String actual = target.getElasticsearchCluster();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getElasticsearchAlias_Args__() throws Exception {
    String actual = target.getElasticsearchAlias();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getElasticsearchDocType_Args__() throws Exception {
    String actual = target.getElasticsearchDocType();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

}
