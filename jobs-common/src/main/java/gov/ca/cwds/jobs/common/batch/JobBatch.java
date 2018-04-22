package gov.ca.cwds.jobs.common.batch;

import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public class JobBatch {

  private List<ChangedEntityIdentifier> identifiers;
  private LocalDateTime timestamp;
  private Integer id;

  public JobBatch(List<ChangedEntityIdentifier> identifiers) {
    this.identifiers = identifiers;
    this.timestamp = null;
    this.id = null;
  }

  public JobBatch(List<ChangedEntityIdentifier> identifiers, LocalDateTime timestamp) {
    this.identifiers = identifiers;
    this.timestamp = timestamp;
    this.id = null;
  }

  public JobBatch(List<ChangedEntityIdentifier> identifiers, LocalDateTime timestamp, Integer id) {
    this.identifiers = identifiers;
    this.timestamp = timestamp;
    this.id = id;
  }

  public List<ChangedEntityIdentifier> getChangedEntityIdentifiers() {
    return identifiers;
  }

  public void addIdentifier(ChangedEntityIdentifier identifier) {
    this.identifiers.add(identifier);
  }

  public void addIdentifiers(List<ChangedEntityIdentifier> identifiers) {
    this.identifiers.addAll(identifiers);
  }

  public void setChangedEntityIdentifiers(List<ChangedEntityIdentifier> changedEntities) {
    this.identifiers = changedEntities;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public void calculateTimestamp() {
    timestamp = identifiers.get(identifiers.size() - 1).getTimestamp();
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getSize() {
    return identifiers.size();
  }

  public boolean isEmptyTimestamp() {
    return timestamp == null;
  }

  @Override
  public String toString() {
    return "batch size = " + identifiers.size() + ", timestamp is " + timestamp;
  }
}
