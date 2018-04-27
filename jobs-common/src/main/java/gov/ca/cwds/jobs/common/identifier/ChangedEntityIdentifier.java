package gov.ca.cwds.jobs.common.identifier;

import gov.ca.cwds.jobs.common.RecordChangeOperation;
import java.time.LocalDateTime;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public class ChangedEntityIdentifier {

  private String id;

  private RecordChangeOperation recordChangeOperation;

  private LocalDateTime timestamp;

  public ChangedEntityIdentifier(String id, RecordChangeOperation recordChangeOperation,
      LocalDateTime timestamp) {
    this.id = id;
    this.recordChangeOperation = recordChangeOperation;
    this.timestamp = timestamp;
  }

  public String getId() {
    return id;
  }

  public Integer getIntId() {
    return Integer.valueOf(id);
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

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return "id=" + id + ", recordChangeOperation=" + recordChangeOperation +
        ", timestamp=" + timestamp + "\n";
  }
}
