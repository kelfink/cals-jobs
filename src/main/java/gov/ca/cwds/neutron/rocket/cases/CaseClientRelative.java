package gov.ca.cwds.neutron.rocket.cases;

import java.sql.ResultSet;
import java.sql.SQLException;

import gov.ca.cwds.data.persistence.cms.rep.CmsReplicationOperation;
import gov.ca.cwds.data.std.ApiMarker;

/**
 * Convenient carrier bean for client/case/relative keys.
 * 
 * @author CWDS API Team
 */
public class CaseClientRelative implements ApiMarker {

  private static final long serialVersionUID = 1L;

  private String focusClientId;
  private String clientId;
  private String caseId;

  private String clientSensitivity;
  private String caseSensitivity;

  private CmsReplicationOperation clientReplicationOperation = CmsReplicationOperation.U;

  public CaseClientRelative(String clientId, String caseId, String clientSensitivity,
      String caseSensitivity, String clientReplOp) {
    this.clientId = clientId;
    this.caseId = caseId;
    this.clientSensitivity = clientSensitivity;
    this.caseSensitivity = caseSensitivity;
    this.clientReplicationOperation = CmsReplicationOperation.strToRepOp(clientReplOp);
  }

  public static CaseClientRelative extract(final ResultSet rs) throws SQLException {
    final CaseClientRelative ret = new CaseClientRelative(rs.getString("FKCLIENT_T"),
        rs.getString("FK_T"), rs.getString("SENSTV_IND"), rs.getString("SENSTV_IND"),
        rs.getString("CLT_IBMSNAP_OPERATION"));
    return ret;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getCaseId() {
    return caseId;
  }

  public void setCaseId(String referralId) {
    this.caseId = referralId;
  }

  public String getClientSensitivity() {
    return clientSensitivity;
  }

  public void setClientSensitivity(String sensitivity) {
    this.clientSensitivity = sensitivity;
  }

  public CmsReplicationOperation getClientReplicationOperation() {
    return clientReplicationOperation;
  }

  public void setClientReplicationOperation(CmsReplicationOperation clientReplicationOperation) {
    this.clientReplicationOperation = clientReplicationOperation;
  }

  public String getCaseSensitivity() {
    return caseSensitivity;
  }

  public void setCaseSensitivity(String caseSensitivity) {
    this.caseSensitivity = caseSensitivity;
  }

}
