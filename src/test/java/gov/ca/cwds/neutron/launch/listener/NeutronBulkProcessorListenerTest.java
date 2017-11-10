package gov.ca.cwds.neutron.launch.listener;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.neutron.flight.FlightLog;
import gov.ca.cwds.neutron.launch.listener.NeutronBulkProcessorListener;

public class NeutronBulkProcessorListenerTest {

  FlightLog track;
  NeutronBulkProcessorListener target;

  @Before
  public void setup() throws Exception {
    track = new FlightLog();
    target = new NeutronBulkProcessorListener(track);
  }

  @Test
  public void type() throws Exception {
    assertThat(NeutronBulkProcessorListener.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void beforeBulk_Args__long__BulkRequest() throws Exception {
    long executionId = 0L;
    BulkRequest request = mock(BulkRequest.class);
    target.beforeBulk(executionId, request);
  }

  @Test
  public void afterBulk_Args__long__BulkRequest__BulkResponse() throws Exception {
    long executionId = 0L;
    BulkRequest request = mock(BulkRequest.class);
    BulkResponse response = mock(BulkResponse.class);
    target.afterBulk(executionId, request, response);
  }

  @Test
  public void afterBulk_Args__long__BulkRequest__Throwable() throws Exception {
    long executionId = 0L;
    BulkRequest request = mock(BulkRequest.class);
    Throwable failure = null;
    target.afterBulk(executionId, request, failure);
  }

}
