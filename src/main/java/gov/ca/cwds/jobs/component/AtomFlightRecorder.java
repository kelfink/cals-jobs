package gov.ca.cwds.jobs.component;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import gov.ca.cwds.jobs.schedule.DefaultFlightSchedule;

public interface AtomFlightRecorder {

  Map<Class<?>, CircularFifoQueue<FlightLog>> getFlightLogHistory();

  void addTrack(Class<?> klazz, FlightLog track);

  FlightLog getLastFlightLog(final Class<?> klazz);

  List<FlightLog> getHistory(final Class<?> klazz);

  void summarizeFlight(DefaultFlightSchedule flightSchedule, FlightLog flightLog);

  FlightSummary getFlightSummary(DefaultFlightSchedule flightSchedule);
}
