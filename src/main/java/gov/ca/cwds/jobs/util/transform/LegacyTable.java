package gov.ca.cwds.jobs.util.transform;

/**
 * Legacy system tables
 * 
 * @author CWDS API Team
 */
public class LegacyTable {

  public static final LegacyTable CLIENT_T = new LegacyTable("CLIENT_T", "Client");

  public static final LegacyTable COLTRL_T = new LegacyTable("COLTRL_T", "Collateral individual");

  public static final LegacyTable EDPRVCNT = new LegacyTable("EDPRVCNT", "Education provider");

  public static final LegacyTable ATTRNY_T = new LegacyTable("ATTRNY_T", "Attorney");

  public static final LegacyTable CLN_RELT = new LegacyTable("CLN_RELT", "Client Relationship");

  public static final LegacyTable OTH_ADLT = new LegacyTable("OTH_ADLT", "Adult in placement home");

  public static final LegacyTable OTH_KIDT = new LegacyTable("OTH_KIDT", "Child in placement home");

  public static final LegacyTable OCL_NM_T =
      new LegacyTable("OCL_NM_T", "Alias or other client name");

  public static final LegacyTable REPTR_T = new LegacyTable("REPTR_T", "Reporter");

  public static final LegacyTable SVC_PVRT = new LegacyTable("SVC_PVRT", "Service provider");

  public static final LegacyTable SB_PVDRT =
      new LegacyTable("SB_PVDRT", "Substitute care provider");

  public static final LegacyTable CASE_T = new LegacyTable("CASE_T", "Case");

  public static final LegacyTable STFPERST = new LegacyTable("STFPERST", "Staff");

  public static final LegacyTable REFERL_T = new LegacyTable("REFERL_T", "Referral");

  public static final LegacyTable ALLGTN_T = new LegacyTable("ALLGTN_T", "Allegation");

  public static final LegacyTable ADDRS_T = new LegacyTable("ADDRS_T", "Address");

  private String name;
  private String description;

  private LegacyTable(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
}
