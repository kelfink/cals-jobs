package gov.ca.cwds.jobs.util.transform;

import org.apache.commons.lang3.StringUtils;

/**
 * Legacy system tables
 * 
 * @author CWDS API Team
 */
public enum LegacyTable {

  //
  // CHECKSTYLE:OFF
  //

  CLIENT_T("Client"),

  COLTRL_T("Collateral individual"),

  EDPRVCNT("Education provider"),

  ATTRNY_T("Attorney"),

  CLN_RELT("Client Relationship"),

  OTH_ADLT("Adult in placement home"),

  OTH_KIDT("Child in placement home"),

  OCL_NM_T("Alias or other client name"),

  REPTR_T("Reporter"),

  SVC_PVRT("Service provider"),

  SB_PVDRT("Substitute care provider"),

  CASE_T("Case"),

  STFPERST("Staff"),

  REFERL_T("Referral"),

  ALLGTN_T("Allegation"),

  ADDRS_T("Address");

  //
  // CHECKSTYLE:ON
  //

  private String description;

  private LegacyTable(String description) {
    this.description = description;
  }

  public String getName() {
    return this.name();
  }

  public String getDescription() {
    return description;
  }

  /**
   * Lookup a legacy table by name.
   * 
   * @param tableName The legacy table name
   * @return LegacyTable for given name if found, null otherwise.
   */
  public static LegacyTable lookupByName(String tableName) {
    if (StringUtils.isBlank(tableName)) {
      return null;
    }

    LegacyTable legacyTable = null;
    for (LegacyTable lt : LegacyTable.values()) {
      if (lt.getName().equals(tableName.trim())) {
        legacyTable = lt;
        break;
      }
    }
    return legacyTable;
  }
}
