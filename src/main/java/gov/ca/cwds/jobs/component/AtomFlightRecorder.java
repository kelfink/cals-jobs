package gov.ca.cwds.jobs.component;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.queue.CircularFifoQueue;

public interface AtomFlightRecorder {

  Map<Class<?>, CircularFifoQueue<FlightRecord>> getTrackHistory();

  void addTrack(Class<?> klazz, FlightRecord track);

  FlightRecord getLastTrack(final Class<?> klazz);

  List<FlightRecord> getHistory(final Class<?> klazz);

}
