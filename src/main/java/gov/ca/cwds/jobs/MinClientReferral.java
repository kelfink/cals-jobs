package gov.ca.cwds.jobs;

import java.sql.ResultSet;
import java.sql.SQLException;

import gov.ca.cwds.data.std.ApiMarker;

/**
 * Carrier bean for client referral keys.
 * 
 * @author CWDS API Team
 */
public class MinClientReferral implements ApiMarker {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  String clientId;
  String referralId;
  String sensitivity;

  public MinClientReferral(String clientId, String referralId, String sensitivity) {
    this.clientId = clientId;
    this.referralId = referralId;
    this.sensitivity = sensitivity;
  }

  public static MinClientReferral extract(final ResultSet rs) throws SQLException {
    return new MinClientReferral(rs.getString("FKCLIENT_T"), rs.getString("FKREFERL_T"),
        rs.getString("SENSTV_IND"));
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

}
