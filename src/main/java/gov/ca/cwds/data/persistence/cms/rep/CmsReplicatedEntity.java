package gov.ca.cwds.data.persistence.cms.rep;

import java.io.Serializable;
import java.util.Date;

/**
 * Replicated entity interface adds common CMS replication columns to Hibernate entity classes.
 * 
 * @author CWDS API Team
 */
public interface CmsReplicatedEntity extends Serializable {

  /**
   * Getter for replication operation.
   * 
   * @return replication operation
   */
  CmsReplicationOperation getReplicationOperation();

  /**
   * Getter for replication date.
   * 
   * @return replication date
   */
  Date getReplicationDate();

  /**
   * Setter for replication operation (I = insert, U = update, D = delete).
   * 
   * @param replicationOperation SQL operation that triggered the replication of this record..
   */
  void setReplicationOperation(CmsReplicationOperation replicationOperation);

  /**
   * Setter for replication date.
   * 
   * @param replicationDate when this record replicated
   */
  void setReplicationDate(Date replicationDate);
}
