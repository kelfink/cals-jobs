package gov.ca.cwds.jobs.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import gov.ca.cwds.jobs.component.JobProgressTrack;

public class NeutronJobProgressHistory {

  private final Map<Class<?>, CircularFifoQueue<JobProgressTrack>> trackHistory =
      new ConcurrentHashMap<>();

  private final Map<Class<?>, JobProgressTrack> lastTracks = new ConcurrentHashMap<>();

  public Map<Class<?>, CircularFifoQueue<JobProgressTrack>> getTrackHistory() {
    return trackHistory;
  }

  public void addTrack(Class<?> klazz, JobProgressTrack track) {
    lastTracks.put(klazz, track);

    if (!trackHistory.containsKey(klazz)) {
      trackHistory.put(klazz, new CircularFifoQueue<>(96));
    }
    trackHistory.get(klazz).add(track);
  }

  public JobProgressTrack getLastTrack(final Class<?> klazz) {
    return lastTracks.get(klazz);
  }

  public List<JobProgressTrack> getHistory(final Class<?> klazz) {
    return new ArrayList<>(trackHistory.get(klazz));
  }

}
