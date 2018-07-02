package gov.ca.cwds.jobs.common.utils;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import gov.ca.cwds.jobs.common.util.ConsumerCounter;
import org.junit.Test;

public class ConsumerCounterTest {

  @Test
  public void type() {
    assertThat(ConsumerCounter.class, notNullValue());
  }

  @Test
  public void testConsumerCounter() {
    assertEquals(0, ConsumerCounter.getCounter());
    ConsumerCounter.incrementCounter();
    assertEquals(1, ConsumerCounter.getCounter());
    ConsumerCounter.addToCounter(10);
    assertEquals(11, ConsumerCounter.getCounter());
  }
}
