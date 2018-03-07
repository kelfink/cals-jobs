package gov.ca.cwds.jobs.common.identifier;

import gov.ca.cwds.jobs.common.RecordChangeOperation;
import org.drools.core.util.CompositeIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by Alexander Serbin on 3/6/2018.
 */
public class ChangedEntityIdentifiers {

    private static final Logger LOG = LoggerFactory.getLogger(ChangedEntityIdentifiers.class);

    protected HashMap<String, ChangedEntityIdentifier> toBeDeleted = new HashMap<>();
    protected HashMap<String, ChangedEntityIdentifier> toBeInserted = new HashMap<>();
    protected HashMap<String, ChangedEntityIdentifier> toBeUpdated = new HashMap<>();

    public void add(ChangedEntityIdentifier identifier) {
        if (RecordChangeOperation.D == identifier.getRecordChangeOperation()) {
            toBeDeleted.put(identifier.getId(), identifier);
        } else if (RecordChangeOperation.I == identifier.getRecordChangeOperation()) {
            toBeInserted.put(identifier.getId(), identifier);
        } else if (RecordChangeOperation.U == identifier.getRecordChangeOperation()) {
            toBeUpdated.put(identifier.getId(), identifier);
        }
    }

    private Iterable<ChangedEntityIdentifier> newIterable() {
        compact();
        return () -> new CompositeIterator<>(
                toBeDeleted.values().iterator(),
                toBeInserted.values().iterator(),
                toBeUpdated.values().iterator()
        );
    }

    public Stream<ChangedEntityIdentifier> newStream() {
        return StreamSupport.stream(this.newIterable().spliterator(), true);
    }

    private void compact() {
        toBeDeleted.forEach((id, e) -> {
            toBeInserted.remove(id);
            toBeUpdated.remove(id);
        });
        toBeInserted.forEach((id, e) -> toBeUpdated.remove(id));
    }

}
