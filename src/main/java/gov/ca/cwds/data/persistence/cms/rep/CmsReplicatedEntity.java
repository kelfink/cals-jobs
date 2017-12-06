package gov.ca.cwds.data.persistence.cms.rep;

import java.util.Date;

import gov.ca.cwds.dao.ApiLegacyAware;
import gov.ca.cwds.data.persistence.PersistentObject;

/**
 * Provides common CMS replication columns and checks for Hibernate CMS entity classes.
 * 
 * @author CWDS API Team
 */
public interface CmsReplicatedEntity extends ApiLegacyAware, PersistentObject {

  /**
   * Determines whether record was deleted from companion transactional table.
   * 
   * @param t another replicated entity
   * @return true if deleted
   */
  static boolean isDelete(CmsReplicatedEntity t) {
    return t.getReplicationOperation() == CmsReplicationOperation.D;
  }

  /**
   * @return the embedded replication object
   */
  EmbeddableCmsReplicatedEntity getReplicatedEntity();

  /**
   * Getter for replication operation.
   * 
   * @return replication operation
   */
  default CmsReplicationOperation getReplicationOperation() {
    return getReplicatedEntity().getReplicationOperation();
  }

  /**
   * Getter for replication date.
   * 
   * @return replication date
   */
  default Date getReplicationDate() {
    return getReplicatedEntity().getReplicationDate();
  }

  /**
   * Setter for replication operation (I = insert, U = update, D = delete).
   * 
   * @param replicationOperation SQL operation that triggered the replication of this record..
   */
  default void setReplicationOperation(CmsReplicationOperation replicationOperation) {
    getReplicatedEntity().setReplicationOperation(replicationOperation);
  }

  /**
   * Setter for replication date.
   * 
   * @param replicationDate when this record replicated
   */
  default void setReplicationDate(Date replicationDate) {
    getReplicatedEntity().setReplicationDate(replicationDate);
  }

}
