package gov.ca.cwds.jobs.common;

import java.time.LocalDateTime;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public class ChangedEntityInformation {

    private String id;

    private RecordChangeOperation recordChangeOperation;

    private LocalDateTime timestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RecordChangeOperation getRecordChangeOperation() {
        return recordChangeOperation;
    }

    public void setRecordChangeOperation(RecordChangeOperation recordChangeOperation) {
        this.recordChangeOperation = recordChangeOperation;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
