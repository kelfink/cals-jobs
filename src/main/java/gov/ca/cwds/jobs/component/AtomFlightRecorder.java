package gov.ca.cwds.jobs.component;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.queue.CircularFifoQueue;

public interface AtomFlightRecorder {

  Map<Class<?>, CircularFifoQueue<FlightLog>> getTrackHistory();

  void addTrack(Class<?> klazz, FlightLog track);

  FlightLog getLastTrack(final Class<?> klazz);

  List<FlightLog> getHistory(final Class<?> klazz);

}
