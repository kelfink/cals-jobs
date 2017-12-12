package gov.ca.cwds.neutron.rocket.referral;

import java.sql.ResultSet;
import java.sql.SQLException;

import gov.ca.cwds.data.persistence.cms.rep.CmsReplicationOperation;
import gov.ca.cwds.data.std.ApiMarker;

/**
 * Convenient carrier bean for client referral keys.
 * 
 * @author CWDS API Team
 */
public class MinClientReferral implements ApiMarker {

  private static final long serialVersionUID = 1L;

  private String clientId;
  private String referralId;
  private String sensitivity;
  private CmsReplicationOperation clientReplicationOperation = CmsReplicationOperation.U;

  public MinClientReferral(String clientId, String referralId, String sensitivity,
      String clientReplOp) {
    this.clientId = clientId;
    this.referralId = referralId;
    this.sensitivity = sensitivity;
    this.clientReplicationOperation = CmsReplicationOperation.strToRepOp(clientReplOp);
  }

  public static MinClientReferral extract(final ResultSet rs) throws SQLException {
    return new MinClientReferral(rs.getString("FKCLIENT_T"), rs.getString("FKREFERL_T"),
        rs.getString("SENSTV_IND"), rs.getString("CLT_IBMSNAP_OPERATION"));
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getReferralId() {
    return referralId;
  }

  public void setReferralId(String referralId) {
    this.referralId = referralId;
  }

  public String getSensitivity() {
    return sensitivity;
  }

  public void setSensitivity(String sensitivity) {
    this.sensitivity = sensitivity;
  }

  public CmsReplicationOperation getClientReplicationOperation() {
    return clientReplicationOperation;
  }

  public void setClientReplicationOperation(CmsReplicationOperation clientReplicationOperation) {
    this.clientReplicationOperation = clientReplicationOperation;
  }

}
