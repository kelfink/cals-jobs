package gov.ca.cwds.data.persistence.cms;

import java.util.Date;

import gov.ca.cwds.dao.cms.BaseCmsReplicated;
import gov.ca.cwds.dao.cms.CmsReplicatedEntity;
import gov.ca.cwds.dao.cms.CmsReplicationOperation;
import gov.ca.cwds.data.persistence.PersistentObject;

/**
 * {@link PersistentObject} representing a Client as a {@link CmsReplicatedEntity}.
 * 
 * @author CWDS API Team
 */
public class ReplicatedClient extends Client implements CmsReplicatedEntity {

  /**
   * 
   */
  private static final long serialVersionUID = 6160989831851057517L;

  private BaseCmsReplicated replicated = new BaseCmsReplicated();

  @Override
  public CmsReplicationOperation getReplicationOperation() {
    return replicated.getReplicationOperation();
  }

  @Override
  public void setReplicationOperation(CmsReplicationOperation replicationOperation) {
    replicated.setReplicationOperation(replicationOperation);
  }

  @Override
  public Date getReplicationDate() {
    return replicated.getReplicationDate();
  }

  @Override
  public void setReplicationDate(Date replicationDate) {
    replicated.setReplicationDate(replicationDate);
  }

}
