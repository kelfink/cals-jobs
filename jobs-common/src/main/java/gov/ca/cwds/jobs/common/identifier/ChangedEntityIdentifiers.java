package gov.ca.cwds.jobs.common.identifier;

import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.savepoint.SavePoint;
import java.util.HashMap;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.drools.core.util.CompositeIterator;

/**
 * Created by Alexander Serbin on 3/6/2018.
 */
public class ChangedEntityIdentifiers<S extends SavePoint> {

  protected HashMap<String, ChangedEntityIdentifier<S>> toBeDeleted = new HashMap<>();
  protected HashMap<String, ChangedEntityIdentifier<S>> toBeInserted = new HashMap<>();
  protected HashMap<String, ChangedEntityIdentifier<S>> toBeUpdated = new HashMap<>();

  public void add(ChangedEntityIdentifier<S> identifier) {
    if (RecordChangeOperation.D == identifier.getRecordChangeOperation()) {
      toBeDeleted.put(identifier.getId(), identifier);
    } else if (RecordChangeOperation.I == identifier.getRecordChangeOperation()) {
      toBeInserted.put(identifier.getId(), identifier);
    } else if (RecordChangeOperation.U == identifier.getRecordChangeOperation()) {
      toBeUpdated.put(identifier.getId(), identifier);
    }
  }

  private Iterable<ChangedEntityIdentifier<S>> newIterable() {
    compact();
    return () -> new CompositeIterator<>(
        toBeDeleted.values().iterator(),
        toBeInserted.values().iterator(),
        toBeUpdated.values().iterator()
    );
  }

  public Stream<ChangedEntityIdentifier<S>> newStream() {
    return StreamSupport.stream(this.newIterable().spliterator(), false);
  }

  private void compact() {
    toBeDeleted.forEach((id, e) -> {
      toBeInserted.remove(id);
      toBeUpdated.remove(id);
    });
    toBeInserted.forEach((id, e) -> toBeUpdated.remove(id));
  }

}
