package gov.ca.cwds.data.persistence.cms.rep;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.hibernate.annotations.Type;

import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.neutron.util.NeutronDateUtils;

@Embeddable
public class EmbeddableCmsReplicatedEntity implements ApiMarker {

  private static final long serialVersionUID = 1L;

  @Enumerated(EnumType.STRING)
  @Column(name = "IBMSNAP_OPERATION", updatable = false)
  private CmsReplicationOperation replicationOperation;

  @Type(type = "timestamp")
  @Column(name = "IBMSNAP_LOGMARKER", updatable = false)
  private Date replicationDate;

  // =======================
  // CmsReplicatedEntity:
  // =======================

  public CmsReplicationOperation getReplicationOperation() {
    return replicationOperation;
  }

  public void setReplicationOperation(CmsReplicationOperation replicationOperation) {
    this.replicationOperation = replicationOperation;
  }

  public Date getReplicationDate() {
    return NeutronDateUtils.freshDate(replicationDate);
  }

  public void setReplicationDate(Date replicationDate) {
    this.replicationDate = NeutronDateUtils.freshDate(replicationDate);
  }

}
