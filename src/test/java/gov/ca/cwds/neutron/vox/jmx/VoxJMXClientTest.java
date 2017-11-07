package gov.ca.cwds.neutron.vox.jmx;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.jobs.exception.NeutronException;

public class VoxJMXClientTest {

  String host;
  String port;
  String rocketName;
  VoxJMXClient target;

  @Before
  public void setup() throws Exception {
    host = "localhost";
    port = "1098";
    rocketName = "reporter";

    target = new VoxJMXClient(host, port);
  }

  @Test
  public void type() throws Exception {
    assertThat(VoxJMXClient.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void connect_Args__() throws Exception {
    target.connect();
  }

  @Test(expected = NeutronException.class)
  public void connect_Args___T__NeutronException() throws Exception {
    target.connect();
    fail("Expected exception was not thrown!");
  }

  @Test
  public void close_Args__() throws Exception {
    target.close();
  }

  @Test
  public void close_Args___T__Exception() throws Exception {
    try {
      target.close();
      fail("Expected exception was not thrown!");
    } catch (Exception e) {
    }
  }

  @Test
  public void proxy_Args__String() throws Exception {
    Object actual = target.proxy(rocketName);
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void proxy_Args__String_T__NeutronException() throws Exception {
    try {
      target.proxy(rocketName);
      fail("Expected exception was not thrown!");
    } catch (NeutronException e) {
    }
  }

  // @Test
  // public void main_Args__StringArray() throws Exception {
  // String[] args = new String[] {};
  // VoxJMXClient.main(args);
  // }

  // @Test
  // public void main_Args__StringArray_T__IOException() throws Exception {
  // String[] args = new String[] {};
  // try {
  // VoxJMXClient.main(args);
  // fail("Expected exception was not thrown!");
  // } catch (IOException e) {
  // }
  // }

  // @Test
  // public void main_Args__StringArray_T__MalformedObjectNameException() throws Exception {
  // String[] args = new String[] {};
  // try {
  // VoxJMXClient.main(args);
  // fail("Expected exception was not thrown!");
  // } catch (MalformedObjectNameException e) {
  // }
  // }

}
