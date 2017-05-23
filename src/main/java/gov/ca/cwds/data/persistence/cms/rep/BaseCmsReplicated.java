package gov.ca.cwds.data.persistence.cms.rep;

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
public class BaseCmsReplicated implements Serializable, CmsReplicatedEntity {

  /**
   * Base serialization version.
   */
  private static final long serialVersionUID = 1L;

  @Enumerated(EnumType.STRING)
  @Column(name = "IBMSNAP_OPERATION", updatable = false)
  private CmsReplicationOperation replicationOperation;

  @Type(type = "timestamp")
  @Column(name = "IBMSNAP_LOGMARKER", updatable = false)
  private Date replicationDate;

  /*
   * (non-Javadoc)
   * 
   * @see gov.ca.cwds.dao.cms.CmsReplicatedEntity#getReplicationOperation()
   */
  @Override
  public CmsReplicationOperation getReplicationOperation() {
    return replicationOperation;
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.ca.cwds.dao.cms.CmsReplicatedEntity#getReplicationDate()
   */
  @Override
  public Date getReplicationDate() {
    return replicationDate;
  }

  @Override
  public void setReplicationOperation(CmsReplicationOperation replicationOperation) {
    this.replicationOperation = replicationOperation;
  }

  @Override
  public void setReplicationDate(Date replicationDate) {
    this.replicationDate = replicationDate;
  }

}

