package gov.ca.cwds.jobs.schedule;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.jobs.component.AtomFlightRecorder;
import gov.ca.cwds.jobs.component.FlightLog;
import gov.ca.cwds.jobs.component.FlightSummary;

public class FlightRecorder implements ApiMarker, AtomFlightRecorder {

  private static final int QUEUE_SIZE = 200;

  private final Map<Class<?>, CircularFifoQueue<FlightLog>> trackHistory =
      new ConcurrentHashMap<>();

  private final Map<Class<?>, FlightLog> lastFlightLogs = new ConcurrentHashMap<>();

  private final Map<DefaultFlightSchedule, FlightSummary> flightSummaries =
      new EnumMap<>(DefaultFlightSchedule.class);

  @Override
  public Map<Class<?>, CircularFifoQueue<FlightLog>> getFlightLogHistory() {
    return trackHistory;
  }

  @Override
  public void addFlightLog(Class<?> klazz, FlightLog track) {
    lastFlightLogs.put(klazz, track);

    if (!trackHistory.containsKey(klazz)) {
      trackHistory.put(klazz, new CircularFifoQueue<>(QUEUE_SIZE));
    }
    trackHistory.get(klazz).add(track);
  }

  @Override
  public FlightLog getLastFlightLog(final Class<?> klazz) {
    return lastFlightLogs.get(klazz);
  }

  @Override
  public List<FlightLog> getHistory(final Class<?> klazz) {
    return trackHistory.containsKey(klazz) ? new ArrayList<>(trackHistory.get(klazz))
        : new ArrayList<>();
  }

  @Override
  public synchronized void summarizeFlight(DefaultFlightSchedule flightSchedule,
      FlightLog flightLog) {
    FlightSummary summary = flightSummaries.get(flightSchedule);
    if (summary == null) {
      summary = new FlightSummary();
      flightSummaries.put(flightSchedule, summary);
    }

    summary.accumulate(flightLog);
  }

  @Override
  public FlightSummary getFlightSummary(DefaultFlightSchedule flightSchedule) {
    return flightSummaries.get(flightSchedule.getRocketClass());
  }

}
