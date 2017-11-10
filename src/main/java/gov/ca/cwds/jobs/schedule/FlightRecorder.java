package gov.ca.cwds.jobs.schedule;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.neutron.atom.AtomFlightRecorder;
import gov.ca.cwds.neutron.flight.FlightLog;
import gov.ca.cwds.neutron.flight.FlightSummary;

public class FlightRecorder implements ApiMarker, AtomFlightRecorder {

  private static final int QUEUE_SIZE = 200;

  private final Map<Class<?>, CircularFifoQueue<FlightLog>> flightHistory =
      new ConcurrentHashMap<>();

  private final Map<Class<?>, FlightLog> lastFlightLogs = new ConcurrentHashMap<>();

  private final Map<StandardFlightSchedule, FlightSummary> flightSummaries =
      new EnumMap<>(StandardFlightSchedule.class);

  @Override
  public Map<Class<?>, CircularFifoQueue<FlightLog>> getFlightLogHistory() {
    return flightHistory;
  }

  @Override
  public void addFlightLog(Class<?> klazz, FlightLog track) {
    lastFlightLogs.put(klazz, track);

    if (!flightHistory.containsKey(klazz)) {
      flightHistory.put(klazz, new CircularFifoQueue<>(QUEUE_SIZE));
    }
    flightHistory.get(klazz).add(track);
  }

  @Override
  public FlightLog getLastFlightLog(final Class<?> klazz) {
    return lastFlightLogs.get(klazz);
  }

  @Override
  public List<FlightLog> getHistory(final Class<?> klazz) {
    return flightHistory.containsKey(klazz) ? new ArrayList<>(flightHistory.get(klazz))
        : new ArrayList<>();
  }

  @Override
  public synchronized void summarizeFlight(StandardFlightSchedule flightSchedule,
      FlightLog flightLog) {
    FlightSummary summary = flightSummaries.get(flightSchedule);
    if (summary == null) {
      summary = new FlightSummary(flightSchedule);
      flightSummaries.put(flightSchedule, summary);
    }

    summary.accumulate(flightLog);
  }

  @Override
  public FlightSummary getFlightSummary(StandardFlightSchedule flightSchedule) {
    return flightSummaries.get(flightSchedule);
  }

}
