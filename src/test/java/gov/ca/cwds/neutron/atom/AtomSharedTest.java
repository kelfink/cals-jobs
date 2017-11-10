package gov.ca.cwds.neutron.atom;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.neutron.flight.FlightLog;

public class AtomSharedTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(AtomSharedTest.class);

  private static class TestAtomShared implements AtomShared {

    private final FlightLog track = new FlightLog();

    @Override
    public FlightLog getFlightLog() {
      return track;
    }

    @Override
    public ElasticsearchDao getEsDao() {
      return null;
    }

    @Override
    public Logger getLogger() {
      return LOGGER;
    }

    @Override
    public FlightPlan getFlightPlan() {
      return null;
    }

  }

  @Test
  public void type() throws Exception {
    assertThat(AtomShared.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    AtomShared target = new TestAtomShared();
    assertThat(target, notNullValue());
  }

  @Test
  public void nameThread_Args__String() throws Exception {
    AtomShared target = new TestAtomShared();
    String title = "wedgie";
    target.nameThread(title);
  }

}
