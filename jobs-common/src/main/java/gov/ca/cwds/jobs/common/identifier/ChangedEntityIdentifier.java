package gov.ca.cwds.jobs.common.identifier;

import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.savepoint.SavePoint;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public class ChangedEntityIdentifier<S extends SavePoint> {

  private String id;

  private RecordChangeOperation recordChangeOperation;

  private S savePoint;

  public ChangedEntityIdentifier(String id, S savePoint) {
    this.id = id;
    this.savePoint = savePoint;
  }

  public ChangedEntityIdentifier(String id, RecordChangeOperation recordChangeOperation,
      S savePoint) {
    this(id, savePoint);
    this.recordChangeOperation = recordChangeOperation;
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

  public S getSavePoint() {
    return savePoint;
  }

  public void setSavePoint(S savePoint) {
    this.savePoint = savePoint;
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
        ", savePoint=" + savePoint + "\n";
  }
}
