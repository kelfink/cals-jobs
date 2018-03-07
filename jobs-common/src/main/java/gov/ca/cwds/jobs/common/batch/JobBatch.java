package gov.ca.cwds.jobs.common.batch;

import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;

import java.time.LocalDateTime;
import java.util.stream.Stream;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public class JobBatch {

    private Stream<ChangedEntityIdentifier> identifiers;
    private LocalDateTime timestamp;

    public JobBatch(Stream<ChangedEntityIdentifier> identifiers, LocalDateTime timestamp) {
        this.identifiers = identifiers;
        this.timestamp = timestamp;
    }

    public Stream<ChangedEntityIdentifier> getChangedEntityIdentifiers() {
        return identifiers;
    }

    public void setChangedEntityIdentifiers(Stream<ChangedEntityIdentifier> changedEntities) {
        this.identifiers = changedEntities;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
