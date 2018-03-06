package gov.ca.cwds.jobs.common.batch;

import gov.ca.cwds.jobs.common.ChangedEntityInformation;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public class JobBatch {

    private List<ChangedEntityInformation> changedEntities;
    private LocalDateTime timestamp;

    public List<ChangedEntityInformation> getChangedEntities() {
        return changedEntities;
    }

    public void setChangedEntities(List<ChangedEntityInformation> changedEntities) {
        this.changedEntities = changedEntities;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
