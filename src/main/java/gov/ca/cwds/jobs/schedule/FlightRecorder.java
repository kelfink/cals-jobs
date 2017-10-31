package gov.ca.cwds.jobs.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.jobs.component.AtomFlightRecorder;
import gov.ca.cwds.jobs.component.FlightRecord;

public class FlightRecorder implements ApiMarker, AtomFlightRecorder {

  private final Map<Class<?>, CircularFifoQueue<FlightRecord>> trackHistory =
      new ConcurrentHashMap<>();

  private final Map<Class<?>, FlightRecord> lastTracks = new ConcurrentHashMap<>();

  public Map<Class<?>, CircularFifoQueue<FlightRecord>> getTrackHistory() {
    return trackHistory;
  }

  public void addTrack(Class<?> klazz, FlightRecord track) {
    lastTracks.put(klazz, track);

    if (!trackHistory.containsKey(klazz)) {
      trackHistory.put(klazz, new CircularFifoQueue<>(96));
    }
    trackHistory.get(klazz).add(track);
  }

  public FlightRecord getLastTrack(final Class<?> klazz) {
    return lastTracks.get(klazz);
  }

  public List<FlightRecord> getHistory(final Class<?> klazz) {
    return new ArrayList<>(trackHistory.get(klazz));
  }

}
