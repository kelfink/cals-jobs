package gov.ca.cwds.dao.cms;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.hibernate.annotations.Type;

/**
 * Entity class adds common CMS replication columns.
 * 
 * @author CWDS API Team
 */
public class CmsReplicaCommon implements Serializable {

  /**
   * Base serialization version.
   */
  private static final long serialVersionUID = 1L;

  /**
   * CRUD operations on replication column IBMSNAP_OPERATION.
   * 
   * <ul>
   * <li>I: Insert</li>
   * <li>U: Update</li>
   * <li>D: Delete</li>
   * </ul>
   * 
   * @author CWDS API Team
   */
  @SuppressWarnings("javadoc")
  public enum CmsReplicationOperation {
    I, U, D;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "IBMSNAP_OPERATION", updatable = false)
  private CmsReplicationOperation replicationOperation;

  @Type(type = "timestamp")
  @Column(name = "IBMSNAP_LOGMARKER", updatable = false)
  private Date replicationDate;

  /**
   * Getter for replication operation.
   * 
   * @return replication operation
   */
  public CmsReplicationOperation getReplicationOperation() {
    return replicationOperation;
  }

  /**
   * Setter for replication operation.
   * 
   * @param replicationOperation replication operation
   */
  public void setReplicationOperation(CmsReplicationOperation replicationOperation) {
    this.replicationOperation = replicationOperation;
  }

  /**
   * Getter for replication date.
   * 
   * @return replication date
   */
  public Date getReplicationDate() {
    return replicationDate;
  }

  /**
   * Getter for replication date.
   * 
   * @param replicationDate replication date
   */
  public void setReplicationDate(Date replicationDate) {
    this.replicationDate = replicationDate;
  }

}

