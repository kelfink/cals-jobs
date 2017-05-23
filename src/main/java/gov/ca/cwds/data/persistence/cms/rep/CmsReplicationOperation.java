package gov.ca.cwds.data.persistence.cms.rep;

/**
 * CRUD operations on replication column, IBMSNAP_OPERATION.
 * 
 * <ul>
 * <li>I: Insert</li>
 * <li>U: Update</li>
 * <li>D: Delete</li>
 * </ul>
 * 
 * @author CWDS API Team
 */
public enum CmsReplicationOperation {

  /**
   * Insert.
   */
  I,

  /**
   * Update.
   */
  U,

  /**
   * Delete.
   */
  D;

  /**
   * Convert IBM replication operation to enum.
   * 
   * @param op replication operation, IUD
   * @return enumerated type
   */
  public static CmsReplicationOperation strToRepOp(String op) {
    return op != null ? CmsReplicationOperation.valueOf(op) : null;
  }

}
