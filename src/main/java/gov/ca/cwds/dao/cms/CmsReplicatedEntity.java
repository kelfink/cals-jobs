package gov.ca.cwds.dao.cms;

import java.io.Serializable;
import java.util.Date;


/**
 * Entity interface adds common CMS replication columns for Hibernate.
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
   * Setter for replication operation.
   * 
   * @param replicationOperation replication operation
   */
  void setReplicationOperation(CmsReplicationOperation replicationOperation);

  /**
   * Getter for replication date.
   * 
   * @return replication date
   */
  Date getReplicationDate();

  /**
   * Getter for replication date.
   * 
   * @param replicationDate replication date
   */
  void setReplicationDate(Date replicationDate);

}
