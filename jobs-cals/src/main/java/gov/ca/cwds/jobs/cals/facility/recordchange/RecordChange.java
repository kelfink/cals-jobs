package gov.ca.cwds.jobs.cals.facility.recordchange;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author CWDS TPT-2
 */

@MappedSuperclass
public class RecordChange implements PersistentObject {

  @Id
  @Column(name = "ID")
  private String id;

  @Enumerated(EnumType.STRING)
  @Column(name = "CHANGE_OPERATION", updatable = false)
  private RecordChangeOperation recordChangeOperation;

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

  @Override
  public Serializable getPrimaryKey() {
    return getId();
  }
}
