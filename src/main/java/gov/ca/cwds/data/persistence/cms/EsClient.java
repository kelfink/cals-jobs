package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.Type;

import gov.ca.cwds.data.es.ElasticSearchPersonAka;
import gov.ca.cwds.data.es.ElasticSearchSafetyAlert;
import gov.ca.cwds.data.es.ElasticSearchSystemCode;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.rep.CmsReplicationOperation;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedAddress;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedClient;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedClientAddress;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.neutron.util.NeutronDateUtils;
import gov.ca.cwds.neutron.util.transform.ElasticTransformer;
import gov.ca.cwds.rest.api.domain.DomainChef;
import gov.ca.cwds.rest.api.domain.cms.LegacyTable;
import gov.ca.cwds.rest.api.domain.cms.SystemCodeCache;

/**
 * Entity bean for the skinny index.
 * 
 * <p>
 * Implements {@link ApiGroupNormalizer} and converts to {@link ReplicatedClient}.
 * </p>
 * 
 * REFRESH TABLE CWSRSQ.ES_REL_CLN_RELT_CLIENT ;
 * 
 * @author CWDS API Team
 */
@Entity
@Table(name = "VW_LST_CLIENT_ADDRESS")
@NamedNativeQuery(name = "gov.ca.cwds.data.persistence.cms.EsClient.findAllUpdatedAfter",
    query = "SELECT x.* FROM {h-schema}VW_LST_CLIENT_ADDRESS x WHERE x.CLT_IDENTIFIER IN ( "
        + "SELECT x1.CLT_IDENTIFIER FROM {h-schema}VW_LST_CLIENT_ADDRESS x1 "
        + "WHERE x1.LAST_CHG > :after " + ") ORDER BY CLT_IDENTIFIER FOR READ ONLY WITH UR ",
    resultClass = EsClient.class, readOnly = true)

@NamedNativeQuery(
    name = "gov.ca.cwds.data.persistence.cms.EsClient.findAllUpdatedAfterWithUnlimitedAccess",
    query = "SELECT x.* FROM {h-schema}VW_LST_CLIENT_ADDRESS x WHERE x.CLT_IDENTIFIER IN ( "
        + "SELECT x1.CLT_IDENTIFIER FROM {h-schema}VW_LST_CLIENT_ADDRESS x1 "
        + "WHERE x1.LAST_CHG > :after "
        + ") AND x.CLT_SENSTV_IND = 'N' ORDER BY CLT_IDENTIFIER FOR READ ONLY WITH UR",
    resultClass = EsClient.class, readOnly = true)

@NamedNativeQuery(
    name = "gov.ca.cwds.data.persistence.cms.EsClient.findAllUpdatedAfterWithLimitedAccess",
    query = "SELECT x.* FROM {h-schema}VW_LST_CLIENT_ADDRESS x WHERE x.CLT_IDENTIFIER IN ( "
        + "SELECT x1.CLT_IDENTIFIER FROM {h-schema}VW_LST_CLIENT_ADDRESS x1 "
        + "WHERE x1.LAST_CHG > :after "
        + ") AND x.CLT_SENSTV_IND != 'N' ORDER BY CLT_IDENTIFIER FOR READ ONLY WITH UR ",
    resultClass = EsClient.class, readOnly = true)
public class EsClient implements PersistentObject, ApiGroupNormalizer<ReplicatedClient>,
    Comparable<EsClient>, Comparator<EsClient> {

  private static final long serialVersionUID = 1L;

  @Type(type = "timestamp")
  @Column(name = "LAST_CHG", updatable = false)
  private Date lastChange;

  // ==============================
  // CLIENT_T: (client)
  // ==============================

  @Column(name = "CLT_ADJDEL_IND")
  private String cltAdjudicatedDelinquentIndicator;

  @Column(name = "CLT_ADPTN_STCD")
  private String cltAdoptionStatusCode;

  @Column(name = "CLT_ALN_REG_NO")
  private String cltAlienRegistrationNumber;

  @Column(name = "CLT_BIRTH_CITY")
  private String cltBirthCity;

  @Type(type = "short")
  @Column(name = "CLT_B_CNTRY_C")
  private Short cltBirthCountryCodeType;

  @Type(type = "date")
  @Column(name = "CLT_BIRTH_DT")
  private Date cltBirthDate;

  @Column(name = "CLT_BR_FAC_NM")
  private String cltBirthFacilityName;

  @Type(type = "short")
  @Column(name = "CLT_B_STATE_C")
  private Short cltBirthStateCodeType;

  @Column(name = "CLT_BP_VER_IND")
  private String cltBirthplaceVerifiedIndicator;

  @Column(name = "CLT_CHLD_CLT_B")
  private String cltChildClientIndicatorVar;

  @Column(name = "CLT_CL_INDX_NO")
  private String cltClientIndexNumber;

  @Column(name = "CLT_COMMNT_DSC")
  private String cltCommentDescription;

  @Column(name = "CLT_COM_FST_NM")
  private String cltCommonFirstName;

  @Column(name = "CLT_COM_LST_NM")
  private String cltCommonLastName;

  @Column(name = "CLT_COM_MID_NM")
  private String cltCommonMiddleName;

  @Type(type = "date")
  @Column(name = "CLT_CONF_ACTDT")
  private Date cltConfidentialityActionDate;

  @Column(name = "CLT_CONF_EFIND")
  private String cltConfidentialityInEffectIndicator;

  @Type(type = "date")
  @Column(name = "CLT_CREATN_DT")
  private Date cltCreationDate;

  @Column(name = "CLT_CURRCA_IND")
  private String cltCurrCaChildrenServIndicator;

  @Column(name = "CLT_COTH_DESC")
  private String cltCurrentlyOtherDescription;

  @Column(name = "CLT_CURREG_IND")
  private String cltCurrentlyRegionalCenterIndicator;

  @Type(type = "date")
  @Column(name = "CLT_DEATH_DT")
  private Date cltDeathDate;

  @Column(name = "CLT_DTH_DT_IND")
  private String cltDeathDateVerifiedIndicator;

  @Column(name = "CLT_DEATH_PLC")
  private String cltDeathPlace;

  @Column(name = "CLT_DTH_RN_TXT")
  private String cltDeathReasonText;

  @Column(name = "CLT_DRV_LIC_NO")
  private String cltDriverLicenseNumber;

  @Type(type = "short")
  @Column(name = "CLT_D_STATE_C")
  private Short cltDriverLicenseStateCodeType;

  @Column(name = "CLT_EMAIL_ADDR")
  @ColumnTransformer(read = "trim(CLT_EMAIL_ADDR)")
  private String cltEmailAddress;

  @Column(name = "CLT_EST_DOB_CD")
  private String cltEstimatedDobCode;

  @Column(name = "CLT_ETH_UD_CD")
  private String cltEthUnableToDetReasonCode;

  @Type(type = "date")
  @Column(name = "CLT_FTERM_DT")
  private Date cltFatherParentalRightTermDate;

  @Column(name = "CLT_GENDER_CD")
  private String cltGenderCode;

  @Column(name = "CLT_HEALTH_TXT")
  private String cltHealthSummaryText;

  @Column(name = "CLT_HISP_UD_CD")
  private String cltHispUnableToDetReasonCode;

  @Column(name = "CLT_HISP_CD")
  private String cltHispanicOriginCode;

  @Id
  @Column(name = "CLT_IDENTIFIER")
  private String cltId;

  @Type(type = "short")
  @Column(name = "CLT_I_CNTRY_C")
  private Short cltImmigrationCountryCodeType;

  @Type(type = "short")
  @Column(name = "CLT_IMGT_STC")
  private Short cltImmigrationStatusType;

  @Column(name = "CLT_INCAPC_CD")
  private String cltIncapacitatedParentCode;

  @Column(name = "CLT_HCARE_IND")
  private String cltIndividualHealthCarePlanIndicator;

  @Column(name = "CLT_LIMIT_IND")
  private String cltLimitationOnScpHealthIndicator;

  @Column(name = "CLT_LITRATE_CD")
  private String cltLiterateCode;

  @Column(name = "CLT_MAR_HIST_B")
  private String cltMaritalCohabitatnHstryIndicatorVar;

  @Type(type = "short")
  @Column(name = "CLT_MRTL_STC")
  private Short cltMaritalStatusType;

  @Column(name = "CLT_MILT_STACD")
  private String cltMilitaryStatusCode;

  @Type(type = "date")
  @Column(name = "CLT_MTERM_DT")
  private Date cltMotherParentalRightTermDate;

  @Column(name = "CLT_NMPRFX_DSC")
  private String cltNamePrefixDescription;

  @Type(type = "short")
  @Column(name = "CLT_NAME_TPC")
  private Short cltNameType;

  @Column(name = "CLT_OUTWRT_IND")
  private String cltOutstandingWarrantIndicator;

  @Column(name = "CLT_PREVCA_IND")
  private String cltPrevCaChildrenServIndicator;

  @Column(name = "CLT_POTH_DESC")
  private String cltPrevOtherDescription;

  @Column(name = "CLT_PREREG_IND")
  private String cltPrevRegionalCenterIndicator;

  @Type(type = "short")
  @Column(name = "CLT_P_ETHNCTYC")
  private Short cltPrimaryEthnicityType;

  @Type(type = "short")
  @Column(name = "CLT_P_LANG_TPC")
  private Short cltPrimaryLanguageType;

  @Type(type = "short")
  @Column(name = "CLT_RLGN_TPC")
  private Short cltReligionType;

  @Type(type = "short")
  @Column(name = "CLT_S_LANG_TC")
  private Short cltSecondaryLanguageType;

  @Column(name = "CLT_SNTV_HLIND")
  private String cltSensitiveHlthInfoOnFileIndicator;

  @Column(name = "CLT_SENSTV_IND")
  private String cltSensitivityIndicator;

  @Column(name = "CLT_SOCPLC_CD")
  private String cltSoc158PlacementCode;

  @Column(name = "CLT_SOC158_IND")
  private String cltSoc158SealedClientIndicator;

  @Column(name = "CLT_SSN_CHG_CD")
  private String cltSocialSecurityNumChangedCode;

  @Column(name = "CLT_SS_NO")
  private String cltSocialSecurityNumber;

  @Column(name = "CLT_SUFX_TLDSC")
  private String cltSuffixTitleDescription;

  @Column(name = "CLT_TRBA_CLT_B")
  private String cltTribalAncestryClientIndicatorVar;

  @Column(name = "CLT_TR_MBVRT_B")
  private String cltTribalMembrshpVerifctnIndicatorVar;

  @Column(name = "CLT_UNEMPLY_CD")
  private String cltUnemployedParentCode;

  @Column(name = "CLT_ZIPPY_IND")
  private String cltZippyCreatedIndicator;

  @Enumerated(EnumType.STRING)
  @Column(name = "CLT_IBMSNAP_OPERATION", updatable = false)
  private CmsReplicationOperation cltReplicationOperation;

  @Type(type = "timestamp")
  @Column(name = "CLT_IBMSNAP_LOGMARKER", updatable = false)
  private Date cltReplicationDate;

  @Column(name = "CLT_LST_UPD_ID")
  private String cltLastUpdatedId;

  @Type(type = "timestamp")
  @Column(name = "CLT_LST_UPD_TS")
  private Date cltLastUpdatedTime;

  // =======================
  // CL_ADDRT: (address)
  // =======================

  @Enumerated(EnumType.STRING)
  @Column(name = "CLA_IBMSNAP_OPERATION", updatable = false)
  private CmsReplicationOperation claReplicationOperation;

  @Type(type = "timestamp")
  @Column(name = "CLA_IBMSNAP_LOGMARKER", updatable = false)
  private Date claReplicationDate;

  @Column(name = "CLA_LST_UPD_ID")
  private String claLastUpdatedId;

  @Type(type = "timestamp")
  @Column(name = "CLA_LST_UPD_TS")
  private Date claLastUpdatedTime;

  @Id
  @Column(name = "CLA_IDENTIFIER")
  private String claId;

  @Column(name = "CLA_FKADDRS_T")
  private String claFkAddress;

  @Column(name = "CLA_FKCLIENT_T")
  private String claFkClient;

  @Column(name = "CLA_FKREFERL_T")
  private String claFkReferral;

  @Type(type = "short")
  @Column(name = "CLA_ADDR_TPC")
  private Short claAddressType;

  @Column(name = "CLA_HOMLES_IND")
  private String claHomelessInd;

  @Column(name = "CLA_BK_INMT_ID")
  private String claBkInmtId;

  @Type(type = "date")
  @Column(name = "CLA_EFF_END_DT")
  private Date claEffectiveEndDate;

  @Type(type = "date")
  @Column(name = "CLA_EFF_STRTDT")
  private Date claEffectiveStartDate;

  // =======================
  // ADDRS_T: (address)
  // =======================

  @Id
  @Column(name = "ADR_IDENTIFIER")
  private String adrId;

  @Enumerated(EnumType.STRING)
  @Column(name = "ADR_IBMSNAP_OPERATION", updatable = false)
  private CmsReplicationOperation adrReplicationOperation;

  @Type(type = "timestamp")
  @Column(name = "ADR_IBMSNAP_LOGMARKER", updatable = false)
  private Date adrReplicationDate;

  @Column(name = "ADR_CITY_NM")
  private String adrCity;

  @Column(name = "ADR_EMRG_TELNO")
  private BigDecimal adrEmergencyNumber;

  @Type(type = "integer")
  @Column(name = "ADR_EMRG_EXTNO")
  private Integer adrEmergencyExtension;

  @Column(name = "ADR_FRG_ADRT_B")
  private String adrFrgAdrtB;

  @Type(type = "short")
  @Column(name = "ADR_GVR_ENTC")
  private Short adrGovernmentEntityCd;

  @Column(name = "ADR_MSG_TEL_NO")
  private BigDecimal adrMessageNumber;

  @Type(type = "integer")
  @Column(name = "ADR_MSG_EXT_NO")
  private Integer adrMessageExtension;

  @Column(name = "ADR_HEADER_ADR")
  private String adrHeaderAddress;

  @Column(name = "ADR_PRM_TEL_NO")
  private BigDecimal adrPrimaryNumber;

  @Type(type = "integer")
  @Column(name = "ADR_PRM_EXT_NO")
  private Integer adrPrimaryExtension;

  @Type(type = "short")
  @Column(name = "ADR_STATE_C")
  private Short adrState;

  @Column(name = "ADR_STREET_NM")
  @ColumnTransformer(read = "trim(ADR_STREET_NM)")
  private String adrStreetName;

  @Column(name = "ADR_STREET_NO")
  private String adrStreetNumber;

  @Column(name = "ADR_ZIP_NO")
  private String adrZip;

  @Column(name = "ADR_ADDR_DSC")
  private String adrAddressDescription;

  @Type(type = "short")
  @Column(name = "ADR_ZIP_SFX_NO")
  private Short adrZip4;

  @Column(name = "ADR_POSTDIR_CD")
  private String adrPostDirCd;

  @Column(name = "ADR_PREDIR_CD")
  private String adrPreDirCd;

  @Type(type = "short")
  @Column(name = "ADR_ST_SFX_C")
  private Short adrStreetSuffixCd;

  @Type(type = "short")
  @Column(name = "ADR_UNT_DSGC")
  private Short adrUnitDesignationCd;

  @Column(name = "ADR_UNIT_NO")
  private String adrUnitNumber;

  // ================================
  // CLIENT_CNTY: (client county)
  // ================================

  @Id
  @Column(name = "CLC_CLIENT_ID")
  private String clientCountyId;

  @Type(type = "short")
  @Column(name = "CLC_GVR_ENTC")
  private Short clientCounty;

  @Column(name = "CLC_CNTY_RULE")
  private String clientCountyRule;

  // ================================
  // CLSCP_ET: (race & ethnicity)
  // ================================

  @Id
  @Column(name = "ETH_IDENTIFIER")
  private String clientEthnicityId;

  @Type(type = "short")
  @Column(name = "ETHNICITY_CODE")
  private Short clientEthnicityCode;

  // ================================
  // SAF_ALRT: (safety alerts)
  // ================================

  @Id
  @Column(name = "SAL_THIRD_ID")
  private String safetyAlertId;

  @Column(name = "SAL_ACTV_RNC")
  @Type(type = "short")
  private Short safetyAlertActivationReasonCode;

  @Column(name = "SAL_ACTV_DT")
  @Type(type = "date")
  private Date safetyAlertActivationDate;

  @Column(name = "SAL_ACTV_GEC")
  @Type(type = "short")
  private Short safetyAlertActivationCountyCode;

  @Column(name = "SAL_ACTV_TXT")
  private String safetyAlertActivationExplanation;

  @Column(name = "SAL_DACT_DT")
  @Type(type = "date")
  private Date safetyAlertDeactivationDate;

  @Column(name = "SAL_DACT_GEC")
  @Type(type = "short")
  private Short safetyAlertDeactivationCountyCode;

  @Column(name = "SAL_DACT_TXT")
  private String safetyAlertDeactivationExplanation;

  @Column(name = "SAL_LST_UPD_ID")
  private String safetyAlertLastUpdatedId;

  @Column(name = "SAL_LST_UPD_TS")
  @Type(type = "timestamp")
  private Date safetyAlertLastUpdatedTimestamp;

  @Enumerated(EnumType.STRING)
  @Column(name = "SAL_IBMSNAP_OPERATION", updatable = false)
  private CmsReplicationOperation safetyAlertLastUpdatedOperation;

  @Column(name = "SAL_IBMSNAP_LOGMARKER")
  @Type(type = "timestamp")
  private Date safetyAlertReplicationTimestamp;

  // =====================================
  // OCL_NM_T: (other client name / AKA)
  // =====================================

  @Id
  @Column(name = "ONM_THIRD_ID")
  private String akaId;

  @Column(name = "ONM_FIRST_NM")
  private String akaFirstName;

  @Column(name = "ONM_LAST_NM")
  private String akaLastName;

  @Column(name = "ONM_MIDDLE_NM")
  private String akaMiddleName;

  @Column(name = "ONM_NMPRFX_DSC")
  private String akaNamePrefixDescription;

  @Type(type = "short")
  @Column(name = "ONM_NAME_TPC")
  private Short akaNameType;

  @Column(name = "ONM_SUFX_TLDSC")
  private String akaSuffixTitleDescription;

  @Column(name = "ONM_LST_UPD_ID")
  private String akaLastUpdatedId;

  @Column(name = "ONM_LST_UPD_TS")
  @Type(type = "timestamp")
  private Date akaLastUpdatedTimestamp;

  @Enumerated(EnumType.STRING)
  @Column(name = "ONM_IBMSNAP_OPERATION", updatable = false)
  private CmsReplicationOperation akaLastUpdatedOperation;

  @Column(name = "ONM_IBMSNAP_LOGMARKER")
  @Type(type = "timestamp")
  private Date akaReplicationTimestamp;

  // ====================================
  // CASE_T: (is there an open case)
  // =====================================

  @Column(name = "CAS_IDENTIFIER")
  private String openCaseId;

  /**
   * Build an EsClient from the incoming ResultSet.
   * 
   * @param rs incoming tuple
   * @return a populated EsClient
   * @throws SQLException if unable to convert types or stream breaks, etc.
   */
  public static EsClient extract(final ResultSet rs) throws SQLException {
    final EsClient ret = new EsClient();
    ret.cltSensitivityIndicator = rs.getString("CLT_SENSTV_IND");
    ret.cltSoc158SealedClientIndicator = rs.getString("CLT_SOC158_IND");
    ret.cltAdjudicatedDelinquentIndicator = rs.getString("CLT_ADJDEL_IND");
    ret.cltAdoptionStatusCode = rs.getString("CLT_ADPTN_STCD");
    ret.cltAlienRegistrationNumber = rs.getString("CLT_ALN_REG_NO");
    ret.cltBirthCity = rs.getString("CLT_BIRTH_CITY");
    ret.cltBirthCountryCodeType = rs.getShort("CLT_B_CNTRY_C");
    ret.cltBirthDate = rs.getDate("CLT_BIRTH_DT");
    ret.cltBirthFacilityName = rs.getString("CLT_BR_FAC_NM");
    ret.cltBirthStateCodeType = rs.getShort("CLT_B_STATE_C");
    ret.cltBirthplaceVerifiedIndicator = rs.getString("CLT_BP_VER_IND");
    ret.cltChildClientIndicatorVar = rs.getString("CLT_CHLD_CLT_B");
    ret.cltClientIndexNumber = rs.getString("CLT_CL_INDX_NO");
    ret.cltCommentDescription = rs.getString("CLT_COMMNT_DSC");
    ret.cltCommonFirstName = rs.getString("CLT_COM_FST_NM");
    ret.cltCommonLastName = rs.getString("CLT_COM_LST_NM");
    ret.cltCommonMiddleName = rs.getString("CLT_COM_MID_NM");
    ret.cltConfidentialityActionDate = rs.getDate("CLT_CONF_ACTDT");
    ret.cltConfidentialityInEffectIndicator = rs.getString("CLT_CONF_EFIND");
    ret.cltCreationDate = rs.getDate("CLT_CREATN_DT");
    ret.cltCurrCaChildrenServIndicator = rs.getString("CLT_CURRCA_IND");
    ret.cltCurrentlyOtherDescription = rs.getString("CLT_COTH_DESC");
    ret.cltCurrentlyRegionalCenterIndicator = rs.getString("CLT_CURREG_IND");
    ret.cltDeathDate = rs.getDate("CLT_DEATH_DT");
    ret.cltDeathDateVerifiedIndicator = rs.getString("CLT_DTH_DT_IND");
    ret.cltDeathPlace = rs.getString("CLT_DEATH_PLC");
    ret.cltDeathReasonText = rs.getString("CLT_DTH_RN_TXT");
    ret.cltDriverLicenseNumber = rs.getString("CLT_DRV_LIC_NO");
    ret.cltDriverLicenseStateCodeType = rs.getShort("CLT_D_STATE_C");
    ret.cltEmailAddress = rs.getString("CLT_EMAIL_ADDR");
    ret.cltEstimatedDobCode = rs.getString("CLT_EST_DOB_CD");
    ret.cltEthUnableToDetReasonCode = rs.getString("CLT_ETH_UD_CD");
    ret.cltFatherParentalRightTermDate = rs.getDate("CLT_FTERM_DT");
    ret.cltGenderCode = rs.getString("CLT_GENDER_CD");
    ret.cltHealthSummaryText = rs.getString("CLT_HEALTH_TXT");
    ret.cltHispUnableToDetReasonCode = rs.getString("CLT_HISP_UD_CD");
    ret.cltHispanicOriginCode = rs.getString("CLT_HISP_CD");
    ret.cltId = rs.getString("CLT_IDENTIFIER");
    ret.cltImmigrationCountryCodeType = rs.getShort("CLT_I_CNTRY_C");
    ret.cltImmigrationStatusType = rs.getShort("CLT_IMGT_STC");
    ret.cltIncapacitatedParentCode = rs.getString("CLT_INCAPC_CD");
    ret.cltIndividualHealthCarePlanIndicator = rs.getString("CLT_HCARE_IND");
    ret.cltLimitationOnScpHealthIndicator = rs.getString("CLT_LIMIT_IND");
    ret.cltLiterateCode = rs.getString("CLT_LITRATE_CD");
    ret.cltMaritalCohabitatnHstryIndicatorVar = rs.getString("CLT_MAR_HIST_B");
    ret.cltMaritalStatusType = rs.getShort("CLT_MRTL_STC");
    ret.cltMilitaryStatusCode = rs.getString("CLT_MILT_STACD");
    ret.cltMotherParentalRightTermDate = rs.getDate("CLT_MTERM_DT");
    ret.cltNamePrefixDescription = rs.getString("CLT_NMPRFX_DSC");
    ret.cltNameType = rs.getShort("CLT_NAME_TPC");
    ret.cltOutstandingWarrantIndicator = rs.getString("CLT_OUTWRT_IND");
    ret.cltPrevCaChildrenServIndicator = rs.getString("CLT_PREVCA_IND");
    ret.cltPrevOtherDescription = rs.getString("CLT_POTH_DESC");
    ret.cltPrevRegionalCenterIndicator = rs.getString("CLT_PREREG_IND");
    ret.cltPrimaryEthnicityType = rs.getShort("CLT_P_ETHNCTYC");

    ret.clientEthnicityCode = rs.getShort("ETHNICITY_CODE");
    ret.clientCounty = rs.getShort("CLC_GVR_ENTC");

    ret.clientCountyId = rs.getString("CLC_CLIENT_ID");
    ret.clientEthnicityId = rs.getString("ETHNICITY_CODE");

    // Languages
    ret.cltPrimaryLanguageType = rs.getShort("CLT_P_LANG_TPC");
    ret.cltSecondaryLanguageType = rs.getShort("CLT_S_LANG_TC");

    ret.cltReligionType = rs.getShort("CLT_RLGN_TPC");
    ret.cltSensitiveHlthInfoOnFileIndicator = rs.getString("CLT_SNTV_HLIND");
    ret.cltSoc158PlacementCode = rs.getString("CLT_SOCPLC_CD");
    ret.cltSocialSecurityNumChangedCode = rs.getString("CLT_SSN_CHG_CD");
    ret.cltSocialSecurityNumber = rs.getString("CLT_SS_NO");
    ret.cltSuffixTitleDescription = rs.getString("CLT_SUFX_TLDSC");
    ret.cltTribalAncestryClientIndicatorVar = rs.getString("CLT_TRBA_CLT_B");
    ret.cltTribalMembrshpVerifctnIndicatorVar = rs.getString("CLT_TR_MBVRT_B");
    ret.cltUnemployedParentCode = rs.getString("CLT_UNEMPLY_CD");
    ret.cltZippyCreatedIndicator = rs.getString("CLT_ZIPPY_IND");
    ret.cltLastUpdatedId = rs.getString("CLT_LST_UPD_ID");
    ret.cltLastUpdatedTime = rs.getTimestamp("CLT_LST_UPD_TS");

    ret.setCltReplicationOperation(
        CmsReplicationOperation.strToRepOp(rs.getString("CLT_IBMSNAP_OPERATION")));
    ret.setCltReplicationDate(rs.getDate("CLT_IBMSNAP_LOGMARKER"));

    ret.claLastUpdatedId = rs.getString("CLA_LST_UPD_ID");
    ret.claLastUpdatedTime = rs.getTimestamp("CLA_LST_UPD_TS");
    ret.claId = rs.getString("CLA_IDENTIFIER");
    ret.claFkAddress = rs.getString("CLA_FKADDRS_T");
    ret.claFkClient = rs.getString("CLA_FKCLIENT_T");
    ret.claFkReferral = rs.getString("CLA_FKREFERL_T");
    ret.claAddressType = rs.getShort("CLA_ADDR_TPC");
    ret.claHomelessInd = rs.getString("CLA_HOMLES_IND");
    ret.claBkInmtId = rs.getString("CLA_BK_INMT_ID");
    ret.claEffectiveEndDate = rs.getDate("CLA_EFF_END_DT");
    ret.claEffectiveStartDate = rs.getDate("CLA_EFF_STRTDT");

    ret.setClaReplicationOperation(
        CmsReplicationOperation.strToRepOp(rs.getString("CLA_IBMSNAP_OPERATION")));
    ret.setClaReplicationDate(rs.getDate("CLA_IBMSNAP_LOGMARKER"));

    ret.adrId = rs.getString("ADR_IDENTIFIER");
    ret.adrCity = rs.getString("ADR_CITY_NM");
    ret.adrEmergencyNumber = rs.getBigDecimal("ADR_EMRG_TELNO");
    ret.adrEmergencyExtension = rs.getInt("ADR_EMRG_EXTNO");
    ret.adrFrgAdrtB = rs.getString("ADR_FRG_ADRT_B");
    ret.adrGovernmentEntityCd = rs.getShort("ADR_GVR_ENTC");
    ret.adrMessageNumber = rs.getBigDecimal("ADR_MSG_TEL_NO");
    ret.adrMessageExtension = rs.getInt("ADR_MSG_EXT_NO");
    ret.adrHeaderAddress = rs.getString("ADR_HEADER_ADR");
    ret.adrPrimaryNumber = rs.getBigDecimal("ADR_PRM_TEL_NO");
    ret.adrPrimaryExtension = rs.getInt("ADR_PRM_EXT_NO");
    ret.adrState = rs.getShort("ADR_STATE_C");
    ret.adrStreetName = rs.getString("ADR_STREET_NM");
    ret.adrStreetNumber = rs.getString("ADR_STREET_NO");
    ret.adrZip = rs.getString("ADR_ZIP_NO");
    ret.adrAddressDescription = rs.getString("ADR_ADDR_DSC");
    ret.adrZip4 = rs.getShort("ADR_ZIP_SFX_NO");
    ret.adrPostDirCd = rs.getString("ADR_POSTDIR_CD");
    ret.adrPreDirCd = rs.getString("ADR_PREDIR_CD");
    ret.adrStreetSuffixCd = rs.getShort("ADR_ST_SFX_C");
    ret.adrUnitDesignationCd = rs.getShort("ADR_UNT_DSGC");
    ret.adrUnitNumber = rs.getString("ADR_UNIT_NO");

    ret.adrReplicationOperation =
        CmsReplicationOperation.strToRepOp(rs.getString("ADR_IBMSNAP_OPERATION"));
    ret.adrReplicationDate = rs.getDate("ADR_IBMSNAP_LOGMARKER");

    //
    // Safety alert
    //
    ret.safetyAlertId = rs.getString("SAL_THIRD_ID");
    ret.safetyAlertActivationCountyCode = rs.getShort("SAL_ACTV_GEC");
    ret.safetyAlertActivationDate = rs.getDate("SAL_ACTV_DT");
    ret.safetyAlertActivationExplanation = rs.getString("SAL_ACTV_TXT");
    ret.safetyAlertActivationReasonCode = rs.getShort("SAL_ACTV_RNC");
    ret.safetyAlertDeactivationCountyCode = rs.getShort("SAL_DACT_GEC");
    ret.safetyAlertDeactivationDate = rs.getDate("SAL_DACT_DT");
    ret.safetyAlertDeactivationExplanation = rs.getString("SAL_DACT_TXT");
    ret.safetyAlertLastUpdatedId = rs.getString("SAL_LST_UPD_ID");
    ret.safetyAlertLastUpdatedTimestamp = rs.getTimestamp("SAL_LST_UPD_TS");
    ret.safetyAlertLastUpdatedOperation =
        CmsReplicationOperation.strToRepOp(rs.getString("SAL_IBMSNAP_OPERATION"));
    ret.safetyAlertReplicationTimestamp = rs.getTimestamp("SAL_IBMSNAP_LOGMARKER");

    //
    // Other name (AKA)
    //
    ret.akaId = rs.getString("ONM_THIRD_ID");
    ret.akaFirstName = rs.getString("ONM_FIRST_NM");
    ret.akaLastName = rs.getString("ONM_LAST_NM");
    ret.akaMiddleName = rs.getString("ONM_MIDDLE_NM");
    ret.akaNamePrefixDescription = rs.getString("ONM_NMPRFX_DSC");
    ret.akaNameType = rs.getShort("ONM_NAME_TPC");
    ret.akaSuffixTitleDescription = rs.getString("ONM_SUFX_TLDSC");
    ret.akaLastUpdatedId = rs.getString("ONM_LST_UPD_ID");
    ret.akaLastUpdatedTimestamp = rs.getTimestamp("ONM_LST_UPD_TS");
    ret.akaLastUpdatedOperation =
        CmsReplicationOperation.strToRepOp(rs.getString("ONM_IBMSNAP_OPERATION"));
    ret.akaReplicationTimestamp = rs.getTimestamp("ONM_IBMSNAP_LOGMARKER");

    //
    // is there an open case? (get its id)
    //
    ret.openCaseId = rs.getString("CAS_IDENTIFIER");

    //
    // Last change (overall)
    //
    ret.lastChange = rs.getTimestamp("LAST_CHG");

    return ret;
  }

  @Override
  public Class<ReplicatedClient> getNormalizationClass() {
    return ReplicatedClient.class;
  }

  @Override
  public ReplicatedClient normalize(Map<Object, ReplicatedClient> map) {
    final boolean isClientAdded = map.containsKey(this.cltId);
    ReplicatedClient ret = isClientAdded ? map.get(this.cltId) : new ReplicatedClient();

    if (!isClientAdded) {
      // Populate core client attributes.
      ret.adjudicatedDelinquentIndicator = getCltAdjudicatedDelinquentIndicator();
      ret.adoptionStatusCode = getCltAdoptionStatusCode();
      ret.alienRegistrationNumber = getCltAlienRegistrationNumber();
      ret.birthCity = getCltBirthCity();
      ret.birthCountryCodeType = getCltBirthCountryCodeType();
      ret.birthDate = getCltBirthDate();
      ret.birthFacilityName = getCltBirthFacilityName();
      ret.birthplaceVerifiedIndicator = getCltBirthplaceVerifiedIndicator();
      ret.birthStateCodeType = getCltBirthStateCodeType();
      ret.childClientIndicatorVar = getCltChildClientIndicatorVar();
      ret.clientIndexNumber = getCltClientIndexNumber();
      ret.commentDescription = getCltCommentDescription();
      ret.commonFirstName = getCltCommonFirstName();
      ret.commonLastName = getCltCommonLastName();
      ret.commonMiddleName = getCltCommonMiddleName();
      ret.confidentialityActionDate = getCltConfidentialityActionDate();
      ret.confidentialityInEffectIndicator = getCltConfidentialityInEffectIndicator();
      ret.creationDate = getCltCreationDate();
      ret.currCaChildrenServIndicator = getCltCurrCaChildrenServIndicator();
      ret.currentlyOtherDescription = getCltCurrentlyOtherDescription();
      ret.currentlyRegionalCenterIndicator = getCltCurrentlyRegionalCenterIndicator();
      ret.deathDate = getCltDeathDate();
      ret.deathDateVerifiedIndicator = getCltDeathDateVerifiedIndicator();
      ret.deathPlace = getCltDeathPlace();
      ret.deathReasonText = getCltDeathReasonText();
      ret.driverLicenseNumber = getCltDriverLicenseNumber();
      ret.driverLicenseStateCodeType = getCltDriverLicenseStateCodeType();
      ret.emailAddress = getCltEmailAddress();
      ret.estimatedDobCode = getCltEstimatedDobCode();
      ret.ethUnableToDetReasonCode = getCltEthUnableToDetReasonCode();
      ret.fatherParentalRightTermDate = cltFatherParentalRightTermDate;
      ret.commonFirstName = getCltCommonFirstName();
      ret.genderCode = getCltGenderCode();
      ret.healthSummaryText = getCltHealthSummaryText();
      ret.hispanicOriginCode = getCltHispanicOriginCode();
      ret.hispUnableToDetReasonCode = getCltHispUnableToDetReasonCode();
      ret.id = getCltId();
      ret.immigrationCountryCodeType = getCltImmigrationCountryCodeType();
      ret.immigrationStatusType = getCltImmigrationStatusType();
      ret.incapacitatedParentCode = getCltIncapacitatedParentCode();
      ret.individualHealthCarePlanIndicator = getCltIndividualHealthCarePlanIndicator();
      ret.commonLastName = getCltCommonLastName();
      ret.limitationOnScpHealthIndicator = getCltLimitationOnScpHealthIndicator();
      ret.literateCode = getCltLiterateCode();
      ret.maritalCohabitatnHstryIndicatorVar = getCltMaritalCohabitatnHstryIndicatorVar();
      ret.maritalStatusType = getCltMaritalStatusType();
      ret.commonMiddleName = getCltCommonMiddleName();
      ret.militaryStatusCode = getCltMilitaryStatusCode();
      ret.motherParentalRightTermDate = getCltMotherParentalRightTermDate();
      ret.namePrefixDescription = getCltNamePrefixDescription();
      ret.nameType = getCltNameType();
      ret.outstandingWarrantIndicator = getCltOutstandingWarrantIndicator();
      ret.prevCaChildrenServIndicator = getCltPrevCaChildrenServIndicator();
      ret.prevOtherDescription = getCltPrevOtherDescription();
      ret.prevRegionalCenterIndicator = getCltPrevRegionalCenterIndicator();
      ret.primaryEthnicityType = getCltPrimaryEthnicityType();

      // Languages
      ret.primaryLanguageType = getCltPrimaryLanguageType();
      ret.secondaryLanguageType = getCltSecondaryLanguageType();

      ret.religionType = getCltReligionType();
      ret.sensitiveHlthInfoOnFileIndicator = getCltSensitiveHlthInfoOnFileIndicator();
      ret.sensitivityIndicator = getCltSensitivityIndicator();
      ret.soc158PlacementCode = getCltSoc158PlacementCode();
      ret.soc158SealedClientIndicator = getCltSoc158SealedClientIndicator();
      ret.socialSecurityNumber = getCltSocialSecurityNumber();
      ret.socialSecurityNumChangedCode = getCltSocialSecurityNumChangedCode();
      ret.suffixTitleDescription = getCltSuffixTitleDescription();
      ret.tribalAncestryClientIndicatorVar = getCltTribalAncestryClientIndicatorVar();
      ret.tribalMembrshpVerifctnIndicatorVar = getCltTribalMembrshpVerifctnIndicatorVar();
      ret.unemployedParentCode = getCltUnemployedParentCode();
      ret.zippyCreatedIndicator = getCltZippyCreatedIndicator();

      ret.setReplicationDate(getCltReplicationDate());
      ret.setReplicationOperation(getCltReplicationOperation());
      ret.setLastUpdatedTime(getCltLastUpdatedTime());

      ret.setClientCounty(getClientCounty());
    }

    // Client Address:
    if (StringUtils.isNotBlank(getClaId())
        && CmsReplicationOperation.D != getClaReplicationOperation()) {
      ReplicatedClientAddress rca = new ReplicatedClientAddress();
      rca.setId(getClaId());
      rca.setAddressType(getClaAddressType());
      rca.setBkInmtId(getClaBkInmtId());
      rca.setEffEndDt(getClaEffectiveEndDate());
      rca.setEffStartDt(getClaEffectiveStartDate());
      rca.setFkAddress(getClaFkAddress());
      rca.setFkClient(getClaFkClient());
      rca.setFkReferral(getClaFkReferral());
      rca.setHomelessInd(getClaHomelessInd());

      rca.setReplicationDate(getClaReplicationDate());
      rca.setReplicationOperation(getClaReplicationOperation());
      rca.setLastUpdatedId(getClaLastUpdatedId());
      rca.setLastUpdatedTime(getClaLastUpdatedTime());
      ret.addClientAddress(rca);

      // Address proper:
      if (StringUtils.isNotBlank(getAdrId())
          && CmsReplicationOperation.D != getAdrReplicationOperation()) {
        ReplicatedAddress adr = new ReplicatedAddress();
        adr.setId(getAdrId());
        adr.setAddressDescription(getAdrAddressDescription());
        adr.setCity(getAdrCity());

        adr.setFrgAdrtB(getAdrFrgAdrtB());
        adr.setGovernmentEntityCd(getAdrGovernmentEntityCd());
        adr.setHeaderAddress(getAdrHeaderAddress());

        adr.setPostDirCd(getAdrPostDirCd());
        adr.setPreDirCd(getAdrPreDirCd());

        // NOTE: no way to figure out phone type from "primary phone". Land line? Cell? dunno.
        adr.setPrimaryExtension(getAdrPrimaryExtension());
        adr.setPrimaryNumber(getAdrPrimaryNumber());

        adr.setEmergencyExtension(getAdrEmergencyExtension());
        adr.setEmergencyNumber(getAdrEmergencyNumber());

        // This is *likely* a cell phone but not guaranteed.
        adr.setMessageExtension(getAdrMessageExtension());
        adr.setMessageNumber(getAdrMessageNumber());

        adr.setState(getAdrState());
        adr.setStateCd(getAdrState());
        adr.setStreetName(getAdrStreetName());
        adr.setStreetNumber(getAdrStreetNumber());
        adr.setStreetSuffixCd(getAdrStreetSuffixCd());
        adr.setUnitDesignationCd(getAdrUnitDesignationCd());
        adr.setUnitNumber(getAdrUnitNumber());
        adr.setZip(getAdrZip());
        adr.setZip4(getAdrZip4());

        adr.setReplicationDate(getAdrReplicationDate());
        adr.setReplicationOperation(getAdrReplicationOperation());
        adr.setLastUpdatedId(getClaLastUpdatedId());
        adr.setLastUpdatedTime(getClaLastUpdatedTime());
        rca.addAddress(adr);
      }
    }

    // Client races
    ret.addClientRace(this.clientEthnicityCode);

    // Safety alerts
    ret.addSafetyAlert(createSafetyAlert());

    // AKA
    ret.addAka(createAka());

    // Open case id
    ret.setOpenCaseId(this.openCaseId);

    map.put(ret.getId(), ret);
    return ret;
  }

  public String getCltAdjudicatedDelinquentIndicator() {
    return cltAdjudicatedDelinquentIndicator;
  }

  public String getCltAdoptionStatusCode() {
    return cltAdoptionStatusCode;
  }

  public String getCltAlienRegistrationNumber() {
    return cltAlienRegistrationNumber;
  }

  public String getCltBirthCity() {
    return cltBirthCity;
  }

  public Short getCltBirthCountryCodeType() {
    return cltBirthCountryCodeType;
  }

  public Date getCltBirthDate() {
    return NeutronDateUtils.freshDate(cltBirthDate);
  }

  public String getCltBirthFacilityName() {
    return cltBirthFacilityName;
  }

  public Short getCltBirthStateCodeType() {
    return cltBirthStateCodeType;
  }

  public String getCltBirthplaceVerifiedIndicator() {
    return cltBirthplaceVerifiedIndicator;
  }

  public void setCltBirthplaceVerifiedIndicator(String cltBirthplaceVerifiedIndicator) {
    this.cltBirthplaceVerifiedIndicator = cltBirthplaceVerifiedIndicator;
  }

  public String getCltChildClientIndicatorVar() {
    return cltChildClientIndicatorVar;
  }

  public String getCltClientIndexNumber() {
    return cltClientIndexNumber;
  }

  public String getCltCommentDescription() {
    return cltCommentDescription;
  }

  public String getCltCommonFirstName() {
    return cltCommonFirstName;
  }

  public String getCltCommonLastName() {
    return cltCommonLastName;
  }

  public String getCltCommonMiddleName() {
    return cltCommonMiddleName;
  }

  public Date getCltConfidentialityActionDate() {
    return NeutronDateUtils.freshDate(cltConfidentialityActionDate);
  }

  public void setCltConfidentialityActionDate(Date cltConfidentialityActionDate) {
    this.cltConfidentialityActionDate = NeutronDateUtils.freshDate(cltConfidentialityActionDate);
  }

  public String getCltConfidentialityInEffectIndicator() {
    return cltConfidentialityInEffectIndicator;
  }

  public void setCltConfidentialityInEffectIndicator(String cltConfidentialityInEffectIndicator) {
    this.cltConfidentialityInEffectIndicator = cltConfidentialityInEffectIndicator;
  }

  public Date getCltCreationDate() {
    return NeutronDateUtils.freshDate(cltCreationDate);
  }

  public void setCltCreationDate(Date cltCreationDate) {
    this.cltCreationDate = NeutronDateUtils.freshDate(cltCreationDate);
  }

  public String getCltCurrCaChildrenServIndicator() {
    return cltCurrCaChildrenServIndicator;
  }

  public void setCltCurrCaChildrenServIndicator(String cltCurrCaChildrenServIndicator) {
    this.cltCurrCaChildrenServIndicator = cltCurrCaChildrenServIndicator;
  }

  public String getCltCurrentlyOtherDescription() {
    return cltCurrentlyOtherDescription;
  }

  public String getCltCurrentlyRegionalCenterIndicator() {
    return cltCurrentlyRegionalCenterIndicator;
  }

  public Date getCltDeathDate() {
    return NeutronDateUtils.freshDate(cltDeathDate);
  }

  public String getCltDeathDateVerifiedIndicator() {
    return cltDeathDateVerifiedIndicator;
  }

  public String getCltDeathPlace() {
    return cltDeathPlace;
  }

  public String getCltDeathReasonText() {
    return cltDeathReasonText;
  }

  public String getCltDriverLicenseNumber() {
    return cltDriverLicenseNumber;
  }

  public Short getCltDriverLicenseStateCodeType() {
    return cltDriverLicenseStateCodeType;
  }

  public String getCltEmailAddress() {
    return cltEmailAddress;
  }

  public String getCltEstimatedDobCode() {
    return cltEstimatedDobCode;
  }

  public String getCltEthUnableToDetReasonCode() {
    return cltEthUnableToDetReasonCode;
  }

  public String getCltGenderCode() {
    return cltGenderCode;
  }

  public String getCltHealthSummaryText() {
    return cltHealthSummaryText;
  }

  public void setCltHealthSummaryText(String cltHealthSummaryText) {
    this.cltHealthSummaryText = cltHealthSummaryText;
  }

  public String getCltHispUnableToDetReasonCode() {
    return cltHispUnableToDetReasonCode;
  }

  public void setCltHispUnableToDetReasonCode(String cltHispUnableToDetReasonCode) {
    this.cltHispUnableToDetReasonCode = cltHispUnableToDetReasonCode;
  }

  public String getCltHispanicOriginCode() {
    return cltHispanicOriginCode;
  }

  public void setCltHispanicOriginCode(String cltHispanicOriginCode) {
    this.cltHispanicOriginCode = cltHispanicOriginCode;
  }

  public String getCltId() {
    return cltId;
  }

  public void setCltId(String cltId) {
    this.cltId = cltId;
  }

  public Short getCltImmigrationCountryCodeType() {
    return cltImmigrationCountryCodeType;
  }

  public void setCltImmigrationCountryCodeType(Short cltImmigrationCountryCodeType) {
    this.cltImmigrationCountryCodeType = cltImmigrationCountryCodeType;
  }

  public Short getCltImmigrationStatusType() {
    return cltImmigrationStatusType;
  }

  public String getCltIncapacitatedParentCode() {
    return cltIncapacitatedParentCode;
  }

  public String getCltIndividualHealthCarePlanIndicator() {
    return cltIndividualHealthCarePlanIndicator;
  }

  public String getCltLimitationOnScpHealthIndicator() {
    return cltLimitationOnScpHealthIndicator;
  }

  public String getCltLiterateCode() {
    return cltLiterateCode;
  }

  public String getCltMaritalCohabitatnHstryIndicatorVar() {
    return cltMaritalCohabitatnHstryIndicatorVar;
  }

  public Short getCltMaritalStatusType() {
    return cltMaritalStatusType;
  }

  public String getCltMilitaryStatusCode() {
    return cltMilitaryStatusCode;
  }

  public Date getCltMotherParentalRightTermDate() {
    return NeutronDateUtils.freshDate(cltMotherParentalRightTermDate);
  }

  public String getCltNamePrefixDescription() {
    return cltNamePrefixDescription;
  }

  public Short getCltNameType() {
    return cltNameType;
  }

  public String getCltOutstandingWarrantIndicator() {
    return cltOutstandingWarrantIndicator;
  }

  public String getCltPrevCaChildrenServIndicator() {
    return cltPrevCaChildrenServIndicator;
  }

  public String getCltPrevOtherDescription() {
    return cltPrevOtherDescription;
  }

  public String getCltPrevRegionalCenterIndicator() {
    return cltPrevRegionalCenterIndicator;
  }

  public void setCltPrevRegionalCenterIndicator(String cltPrevRegionalCenterIndicator) {
    this.cltPrevRegionalCenterIndicator = cltPrevRegionalCenterIndicator;
  }

  public Short getCltPrimaryEthnicityType() {
    return cltPrimaryEthnicityType;
  }

  public void setCltPrimaryEthnicityType(Short cltPrimaryEthnicityType) {
    this.cltPrimaryEthnicityType = cltPrimaryEthnicityType;
  }

  public Short getCltPrimaryLanguageType() {
    return cltPrimaryLanguageType;
  }

  public void setCltPrimaryLanguageType(Short cltPrimaryLanguageType) {
    this.cltPrimaryLanguageType = cltPrimaryLanguageType;
  }

  public Short getCltReligionType() {
    return cltReligionType;
  }

  public void setCltReligionType(Short cltReligionType) {
    this.cltReligionType = cltReligionType;
  }

  public Short getCltSecondaryLanguageType() {
    return cltSecondaryLanguageType;
  }

  public void setCltSecondaryLanguageType(Short cltSecondaryLanguageType) {
    this.cltSecondaryLanguageType = cltSecondaryLanguageType;
  }

  public String getCltSensitiveHlthInfoOnFileIndicator() {
    return cltSensitiveHlthInfoOnFileIndicator;
  }

  public void setCltSensitiveHlthInfoOnFileIndicator(String cltSensitiveHlthInfoOnFileIndicator) {
    this.cltSensitiveHlthInfoOnFileIndicator = cltSensitiveHlthInfoOnFileIndicator;
  }

  public String getCltSensitivityIndicator() {
    return cltSensitivityIndicator;
  }

  public void setCltSensitivityIndicator(String cltSensitivityIndicator) {
    this.cltSensitivityIndicator = cltSensitivityIndicator;
  }

  public String getCltSoc158PlacementCode() {
    return cltSoc158PlacementCode;
  }

  public void setCltSoc158PlacementCode(String cltSoc158PlacementCode) {
    this.cltSoc158PlacementCode = cltSoc158PlacementCode;
  }

  public String getCltSoc158SealedClientIndicator() {
    return cltSoc158SealedClientIndicator;
  }

  public void setCltSoc158SealedClientIndicator(String cltSoc158SealedClientIndicator) {
    this.cltSoc158SealedClientIndicator = cltSoc158SealedClientIndicator;
  }

  public String getCltSocialSecurityNumChangedCode() {
    return cltSocialSecurityNumChangedCode;
  }

  public void setCltSocialSecurityNumChangedCode(String cltSocialSecurityNumChangedCode) {
    this.cltSocialSecurityNumChangedCode = cltSocialSecurityNumChangedCode;
  }

  public String getCltSocialSecurityNumber() {
    return cltSocialSecurityNumber;
  }

  public void setCltSocialSecurityNumber(String cltSocialSecurityNumber) {
    this.cltSocialSecurityNumber = cltSocialSecurityNumber;
  }

  public String getCltSuffixTitleDescription() {
    return cltSuffixTitleDescription;
  }

  public void setCltSuffixTitleDescription(String cltSuffixTitleDescription) {
    this.cltSuffixTitleDescription = cltSuffixTitleDescription;
  }

  public String getCltTribalAncestryClientIndicatorVar() {
    return cltTribalAncestryClientIndicatorVar;
  }

  public void setCltTribalAncestryClientIndicatorVar(String cltTribalAncestryClientIndicatorVar) {
    this.cltTribalAncestryClientIndicatorVar = cltTribalAncestryClientIndicatorVar;
  }

  public String getCltTribalMembrshpVerifctnIndicatorVar() {
    return cltTribalMembrshpVerifctnIndicatorVar;
  }

  public void setCltTribalMembrshpVerifctnIndicatorVar(
      String cltTribalMembrshpVerifctnIndicatorVar) {
    this.cltTribalMembrshpVerifctnIndicatorVar = cltTribalMembrshpVerifctnIndicatorVar;
  }

  public String getCltUnemployedParentCode() {
    return cltUnemployedParentCode;
  }

  public void setCltUnemployedParentCode(String cltUnemployedParentCode) {
    this.cltUnemployedParentCode = cltUnemployedParentCode;
  }

  public String getCltZippyCreatedIndicator() {
    return cltZippyCreatedIndicator;
  }

  public void setCltZippyCreatedIndicator(String cltZippyCreatedIndicator) {
    this.cltZippyCreatedIndicator = cltZippyCreatedIndicator;
  }

  public CmsReplicationOperation getCltReplicationOperation() {
    return cltReplicationOperation;
  }

  public void setCltReplicationOperation(CmsReplicationOperation cltReplicationOperation) {
    this.cltReplicationOperation = cltReplicationOperation;
  }

  public Date getCltReplicationDate() {
    return NeutronDateUtils.freshDate(cltReplicationDate);
  }

  public void setCltReplicationDate(Date cltReplicationDate) {
    this.cltReplicationDate = NeutronDateUtils.freshDate(cltReplicationDate);
  }

  public String getCltLastUpdatedId() {
    return cltLastUpdatedId;
  }

  public void setCltLastUpdatedId(String cltLastUpdatedId) {
    this.cltLastUpdatedId = cltLastUpdatedId;
  }

  public Date getCltLastUpdatedTime() {
    return NeutronDateUtils.freshDate(cltLastUpdatedTime);
  }

  public void setCltLastUpdatedTime(Date cltLastUpdatedTime) {
    this.cltLastUpdatedTime = NeutronDateUtils.freshDate(cltLastUpdatedTime);
  }

  public CmsReplicationOperation getClaReplicationOperation() {
    return claReplicationOperation;
  }

  public void setClaReplicationOperation(CmsReplicationOperation claReplicationOperation) {
    this.claReplicationOperation = claReplicationOperation;
  }

  public Date getClaReplicationDate() {
    return NeutronDateUtils.freshDate(claReplicationDate);
  }

  public void setClaReplicationDate(Date claReplicationDate) {
    this.claReplicationDate = NeutronDateUtils.freshDate(claReplicationDate);
  }

  public String getClaLastUpdatedId() {
    return claLastUpdatedId;
  }

  public void setClaLastUpdatedId(String claLastUpdatedId) {
    this.claLastUpdatedId = claLastUpdatedId;
  }

  public Date getClaLastUpdatedTime() {
    return NeutronDateUtils.freshDate(claLastUpdatedTime);
  }

  public void setClaLastUpdatedTime(Date claLastUpdatedTime) {
    this.claLastUpdatedTime = NeutronDateUtils.freshDate(claLastUpdatedTime);
  }

  public String getClaFkAddress() {
    return claFkAddress;
  }

  public void setClaFkAddress(String claFkAddress) {
    this.claFkAddress = claFkAddress;
  }

  public String getClaFkClient() {
    return claFkClient;
  }

  public void setClaFkClient(String claFkClient) {
    this.claFkClient = claFkClient;
  }

  public String getClaFkReferral() {
    return claFkReferral;
  }

  public void setClaFkReferral(String claFkReferral) {
    this.claFkReferral = claFkReferral;
  }

  public Short getClaAddressType() {
    return claAddressType;
  }

  public void setClaAddressType(Short claAddressType) {
    this.claAddressType = claAddressType;
  }

  public String getClaHomelessInd() {
    return claHomelessInd;
  }

  public void setClaHomelessInd(String claHomelessInd) {
    this.claHomelessInd = claHomelessInd;
  }

  public String getClaBkInmtId() {
    return claBkInmtId;
  }

  public void setClaBkInmtId(String claBkInmtId) {
    this.claBkInmtId = claBkInmtId;
  }

  public Date getClaEffectiveEndDate() {
    return NeutronDateUtils.freshDate(claEffectiveEndDate);
  }

  public void setClaEffectiveEndDate(Date claEffectiveEndDate) {
    this.claEffectiveEndDate = NeutronDateUtils.freshDate(claEffectiveEndDate);
  }

  public Date getClaEffectiveStartDate() {
    return NeutronDateUtils.freshDate(claEffectiveStartDate);
  }

  public void setClaEffectiveStartDate(Date claEffectiveStartDate) {
    this.claEffectiveStartDate = NeutronDateUtils.freshDate(claEffectiveStartDate);
  }

  public String getAdrId() {
    return adrId;
  }

  public void setAdrId(String adrId) {
    this.adrId = adrId;
  }

  public String getAdrCity() {
    return adrCity;
  }

  public void setAdrCity(String adrCity) {
    this.adrCity = adrCity;
  }

  public BigDecimal getAdrEmergencyNumber() {
    return adrEmergencyNumber;
  }

  public void setAdrEmergencyNumber(BigDecimal adrEmergencyNumber) {
    this.adrEmergencyNumber = adrEmergencyNumber;
  }

  public Integer getAdrEmergencyExtension() {
    return adrEmergencyExtension;
  }

  public void setAdrEmergencyExtension(Integer adrEmergencyExtension) {
    this.adrEmergencyExtension = adrEmergencyExtension;
  }

  public String getAdrFrgAdrtB() {
    return adrFrgAdrtB;
  }

  public void setAdrFrgAdrtB(String adrFrgAdrtB) {
    this.adrFrgAdrtB = adrFrgAdrtB;
  }

  public Short getAdrGovernmentEntityCd() {
    return adrGovernmentEntityCd;
  }

  public void setAdrGovernmentEntityCd(Short adrGovernmentEntityCd) {
    this.adrGovernmentEntityCd = adrGovernmentEntityCd;
  }

  public BigDecimal getAdrMessageNumber() {
    return adrMessageNumber;
  }

  public void setAdrMessageNumber(BigDecimal adrMessageNumber) {
    this.adrMessageNumber = adrMessageNumber;
  }

  public Integer getAdrMessageExtension() {
    return adrMessageExtension;
  }

  public void setAdrMessageExtension(Integer adrMessageExtension) {
    this.adrMessageExtension = adrMessageExtension;
  }

  public String getAdrHeaderAddress() {
    return adrHeaderAddress;
  }

  public void setAdrHeaderAddress(String adrHeaderAddress) {
    this.adrHeaderAddress = adrHeaderAddress;
  }

  public BigDecimal getAdrPrimaryNumber() {
    return adrPrimaryNumber;
  }

  public void setAdrPrimaryNumber(BigDecimal adrPrimaryNumber) {
    this.adrPrimaryNumber = adrPrimaryNumber;
  }

  public Integer getAdrPrimaryExtension() {
    return adrPrimaryExtension;
  }

  public Short getAdrState() {
    return adrState;
  }

  public String getAdrStreetName() {
    return adrStreetName;
  }

  public String getAdrStreetNumber() {
    return adrStreetNumber;
  }

  public String getAdrZip() {
    return adrZip;
  }

  public String getAdrAddressDescription() {
    return adrAddressDescription;
  }

  public void setAdrAddressDescription(String adrAddressDescription) {
    this.adrAddressDescription = adrAddressDescription;
  }

  public Short getAdrZip4() {
    return adrZip4;
  }

  public String getAdrPostDirCd() {
    return adrPostDirCd;
  }

  public String getAdrPreDirCd() {
    return adrPreDirCd;
  }

  public Short getAdrStreetSuffixCd() {
    return adrStreetSuffixCd;
  }

  public Short getAdrUnitDesignationCd() {
    return adrUnitDesignationCd;
  }

  public String getAdrUnitNumber() {
    return adrUnitNumber;
  }

  public String getClaId() {
    return claId;
  }

  public Short getClientCounty() {
    return clientCounty;
  }

  public void setClientCounty(Short clientCounty) {
    this.clientCounty = clientCounty;
  }

  public CmsReplicationOperation getAdrReplicationOperation() {
    return adrReplicationOperation;
  }

  public Date getAdrReplicationDate() {
    return NeutronDateUtils.freshDate(adrReplicationDate);
  }

  public Date getLastChange() {
    return NeutronDateUtils.freshDate(lastChange);
  }

  @Override
  public String getNormalizationGroupKey() {
    return this.cltId;
  }

  /**
   * This view (i.e., materialized query table) doesn't have a proper unique key, but a combination
   * of several fields might come close.
   * <ul>
   * <li>"Cook": convert String parameter to strong type</li>
   * <li>"Uncook": convert strong type parameter to String</li>
   * </ul>
   */
  @Override
  public Serializable getPrimaryKey() {
    return null;
  }

  @Override
  public int compare(EsClient o1, EsClient o2) {
    return o1.getCltId().compareTo(o2.getCltId());
  }

  @Override
  public int compareTo(EsClient o) {
    return compare(this, o);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, false);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj, false);
  }

  public Date getCltFatherParentalRightTermDate() {
    return NeutronDateUtils.freshDate(cltFatherParentalRightTermDate);
  }

  public void setClaId(String claId) {
    this.claId = claId;
  }

  public String getClientCountyId() {
    return clientCountyId;
  }

  public String getClientEthnicityId() {
    return clientEthnicityId;
  }

  public Short getClientEthnicityCode() {
    return clientEthnicityCode;
  }

  public String getClientCountyRule() {
    return clientCountyRule;
  }

  public void setClientCountyRule(String clientCountyRule) {
    this.clientCountyRule = clientCountyRule;
  }

  public void setClientCountyId(String clientCountyId) {
    this.clientCountyId = clientCountyId;
  }

  public void setClientEthnicityId(String clientEthnicityId) {
    this.clientEthnicityId = clientEthnicityId;
  }

  public void setClientEthnicityCode(Short clientEthnicityCode) {
    this.clientEthnicityCode = clientEthnicityCode;
  }

  public void setAdrReplicationOperation(CmsReplicationOperation adrReplicationOperation) {
    this.adrReplicationOperation = adrReplicationOperation;
  }

  public String getSafetyAlertId() {
    return safetyAlertId;
  }

  public void setSafetyAlertId(String safetyAlertId) {
    this.safetyAlertId = safetyAlertId;
  }

  public Short getSafetyAlertActivationReasonCode() {
    return safetyAlertActivationReasonCode;
  }

  public void setSafetyAlertActivationReasonCode(Short safetyAlertActivationReasonCode) {
    this.safetyAlertActivationReasonCode = safetyAlertActivationReasonCode;
  }

  public Date getSafetyAlertActivationDate() {
    return NeutronDateUtils.freshDate(safetyAlertActivationDate);
  }

  public void setSafetyAlertActivationDate(Date safetyAlertActivationDate) {
    this.safetyAlertActivationDate = NeutronDateUtils.freshDate(safetyAlertActivationDate);
  }

  public Short getSafetyAlertActivationCountyCode() {
    return safetyAlertActivationCountyCode;
  }

  public void setSafetyAlertActivationCountyCode(Short safetyAlertActivationCountyCode) {
    this.safetyAlertActivationCountyCode = safetyAlertActivationCountyCode;
  }

  public String getSafetyAlertActivationExplanation() {
    return safetyAlertActivationExplanation;
  }

  public void setSafetyAlertActivationExplanation(String safetyAlertActivationExplanation) {
    this.safetyAlertActivationExplanation = safetyAlertActivationExplanation;
  }

  public Date getSafetyAlertDeactivationDate() {
    return NeutronDateUtils.freshDate(safetyAlertDeactivationDate);
  }

  public void setSafetyAlertDeactivationDate(Date safetyAlertDeactivationDate) {
    this.safetyAlertDeactivationDate = NeutronDateUtils.freshDate(safetyAlertDeactivationDate);
  }

  public Short getSafetyAlertDeactivationCountyCode() {
    return safetyAlertDeactivationCountyCode;
  }

  public void setSafetyAlertDeactivationCountyCode(Short safetyAlertDeactivationCountyCode) {
    this.safetyAlertDeactivationCountyCode = safetyAlertDeactivationCountyCode;
  }

  public String getSafetyAlertDeactivationExplanation() {
    return safetyAlertDeactivationExplanation;
  }

  public void setSafetyAlertDeactivationExplanation(String safetyAlertDeactivationExplanation) {
    this.safetyAlertDeactivationExplanation = safetyAlertDeactivationExplanation;
  }

  public String getSafetyAlertLastUpdatedId() {
    return safetyAlertLastUpdatedId;
  }

  public void setSafetyAlertLastUpdatedId(String safetyAlertLastUpdatedId) {
    this.safetyAlertLastUpdatedId = safetyAlertLastUpdatedId;
  }

  public Date getSafetyAlertLastUpdatedTimestamp() {
    return NeutronDateUtils.freshDate(safetyAlertLastUpdatedTimestamp);
  }

  public void setSafetyAlertLastUpdatedTimestamp(Date safetyAlertLastUpdatedTimestamp) {
    this.safetyAlertLastUpdatedTimestamp =
        NeutronDateUtils.freshDate(safetyAlertLastUpdatedTimestamp);
  }

  public CmsReplicationOperation getSafetyAlertLastUpdatedOperation() {
    return safetyAlertLastUpdatedOperation;
  }

  public void setSafetyAlertLastUpdatedOperation(
      CmsReplicationOperation safetyAlertLastUpdatedOperation) {
    this.safetyAlertLastUpdatedOperation = safetyAlertLastUpdatedOperation;
  }

  public Date getSafetyAlertReplicationTimestamp() {
    return NeutronDateUtils.freshDate(safetyAlertReplicationTimestamp);
  }

  public void setSafetyAlertReplicationTimestamp(Date safetyAlertReplicationTimestamp) {
    this.safetyAlertReplicationTimestamp =
        NeutronDateUtils.freshDate(safetyAlertReplicationTimestamp);
  }

  public String getAkaId() {
    return akaId;
  }

  public void setAkaId(String akaId) {
    this.akaId = akaId;
  }

  public String getAkaFirstName() {
    return akaFirstName;
  }

  public void setAkaFirstName(String akaFirstName) {
    this.akaFirstName = akaFirstName;
  }

  public String getAkaLastName() {
    return akaLastName;
  }

  public void setAkaLastName(String akaLastName) {
    this.akaLastName = akaLastName;
  }

  public String getAkaMiddleName() {
    return akaMiddleName;
  }

  public void setAkaMiddleName(String akaMiddleName) {
    this.akaMiddleName = akaMiddleName;
  }

  public String getAkaNamePrefixDescription() {
    return akaNamePrefixDescription;
  }

  public void setAkaNamePrefixDescription(String akaNamePrefixDescription) {
    this.akaNamePrefixDescription = akaNamePrefixDescription;
  }

  public Short getAkaNameType() {
    return akaNameType;
  }

  public void setAkaNameType(Short akaNameType) {
    this.akaNameType = akaNameType;
  }

  public String getAkaSuffixTitleDescription() {
    return akaSuffixTitleDescription;
  }

  public void setAkaSuffixTitleDescription(String akaSuffixTitleDescription) {
    this.akaSuffixTitleDescription = akaSuffixTitleDescription;
  }

  public String getAkaLastUpdatedId() {
    return akaLastUpdatedId;
  }

  public void setAkaLastUpdatedId(String akaLastUpdatedId) {
    this.akaLastUpdatedId = akaLastUpdatedId;
  }

  public Date getAkaLastUpdatedTimestamp() {
    return NeutronDateUtils.freshDate(akaLastUpdatedTimestamp);
  }

  public void setAkaLastUpdatedTimestamp(Date akaLastUpdatedTimestamp) {
    this.akaLastUpdatedTimestamp = NeutronDateUtils.freshDate(akaLastUpdatedTimestamp);
  }

  public CmsReplicationOperation getAkaLastUpdatedOperation() {
    return akaLastUpdatedOperation;
  }

  public void setAkaLastUpdatedOperation(CmsReplicationOperation akaLastUpdatedOperation) {
    this.akaLastUpdatedOperation = akaLastUpdatedOperation;
  }

  public Date getAkaReplicationTimestamp() {
    return NeutronDateUtils.freshDate(akaReplicationTimestamp);
  }

  public void setAkaReplicationTimestamp(Date akaReplicationTimestamp) {
    this.akaReplicationTimestamp = NeutronDateUtils.freshDate(akaReplicationTimestamp);
  }

  public void setLastChange(Date lastChange) {
    this.lastChange = NeutronDateUtils.freshDate(lastChange);
  }

  public void setCltAdjudicatedDelinquentIndicator(String cltAdjudicatedDelinquentIndicator) {
    this.cltAdjudicatedDelinquentIndicator = cltAdjudicatedDelinquentIndicator;
  }

  public void setCltAdoptionStatusCode(String cltAdoptionStatusCode) {
    this.cltAdoptionStatusCode = cltAdoptionStatusCode;
  }

  public void setCltAlienRegistrationNumber(String cltAlienRegistrationNumber) {
    this.cltAlienRegistrationNumber = cltAlienRegistrationNumber;
  }

  public void setCltBirthCity(String cltBirthCity) {
    this.cltBirthCity = cltBirthCity;
  }

  public void setCltBirthCountryCodeType(Short cltBirthCountryCodeType) {
    this.cltBirthCountryCodeType = cltBirthCountryCodeType;
  }

  public void setCltBirthDate(Date cltBirthDate) {
    this.cltBirthDate = NeutronDateUtils.freshDate(cltBirthDate);
  }

  public void setCltBirthFacilityName(String cltBirthFacilityName) {
    this.cltBirthFacilityName = cltBirthFacilityName;
  }

  public void setCltBirthStateCodeType(Short cltBirthStateCodeType) {
    this.cltBirthStateCodeType = cltBirthStateCodeType;
  }

  public void setCltChildClientIndicatorVar(String cltChildClientIndicatorVar) {
    this.cltChildClientIndicatorVar = cltChildClientIndicatorVar;
  }

  public void setCltClientIndexNumber(String cltClientIndexNumber) {
    this.cltClientIndexNumber = cltClientIndexNumber;
  }

  public void setCltCommentDescription(String cltCommentDescription) {
    this.cltCommentDescription = cltCommentDescription;
  }

  public void setCltCommonFirstName(String cltCommonFirstName) {
    this.cltCommonFirstName = cltCommonFirstName;
  }

  public void setCltCommonLastName(String cltCommonLastName) {
    this.cltCommonLastName = cltCommonLastName;
  }

  public void setCltCommonMiddleName(String cltCommonMiddleName) {
    this.cltCommonMiddleName = cltCommonMiddleName;
  }

  public void setCltCurrentlyOtherDescription(String cltCurrentlyOtherDescription) {
    this.cltCurrentlyOtherDescription = cltCurrentlyOtherDescription;
  }

  public void setCltCurrentlyRegionalCenterIndicator(String cltCurrentlyRegionalCenterIndicator) {
    this.cltCurrentlyRegionalCenterIndicator = cltCurrentlyRegionalCenterIndicator;
  }

  public void setCltDeathDate(Date cltDeathDate) {
    this.cltDeathDate = NeutronDateUtils.freshDate(cltDeathDate);
  }

  public void setCltDeathDateVerifiedIndicator(String cltDeathDateVerifiedIndicator) {
    this.cltDeathDateVerifiedIndicator = cltDeathDateVerifiedIndicator;
  }

  public void setCltDeathPlace(String cltDeathPlace) {
    this.cltDeathPlace = cltDeathPlace;
  }

  public void setCltDeathReasonText(String cltDeathReasonText) {
    this.cltDeathReasonText = cltDeathReasonText;
  }

  public void setCltDriverLicenseNumber(String cltDriverLicenseNumber) {
    this.cltDriverLicenseNumber = cltDriverLicenseNumber;
  }

  public void setCltDriverLicenseStateCodeType(Short cltDriverLicenseStateCodeType) {
    this.cltDriverLicenseStateCodeType = cltDriverLicenseStateCodeType;
  }

  public void setCltEmailAddress(String cltEmailAddress) {
    this.cltEmailAddress = cltEmailAddress;
  }

  public void setCltEstimatedDobCode(String cltEstimatedDobCode) {
    this.cltEstimatedDobCode = cltEstimatedDobCode;
  }

  public void setCltEthUnableToDetReasonCode(String cltEthUnableToDetReasonCode) {
    this.cltEthUnableToDetReasonCode = cltEthUnableToDetReasonCode;
  }

  public void setCltFatherParentalRightTermDate(Date cltFatherParentalRightTermDate) {
    this.cltFatherParentalRightTermDate =
        NeutronDateUtils.freshDate(cltFatherParentalRightTermDate);
  }

  public void setCltGenderCode(String cltGenderCode) {
    this.cltGenderCode = cltGenderCode;
  }

  public void setCltImmigrationStatusType(Short cltImmigrationStatusType) {
    this.cltImmigrationStatusType = cltImmigrationStatusType;
  }

  public void setCltIncapacitatedParentCode(String cltIncapacitatedParentCode) {
    this.cltIncapacitatedParentCode = cltIncapacitatedParentCode;
  }

  public void setCltIndividualHealthCarePlanIndicator(String cltIndividualHealthCarePlanIndicator) {
    this.cltIndividualHealthCarePlanIndicator = cltIndividualHealthCarePlanIndicator;
  }

  public void setCltLimitationOnScpHealthIndicator(String cltLimitationOnScpHealthIndicator) {
    this.cltLimitationOnScpHealthIndicator = cltLimitationOnScpHealthIndicator;
  }

  public void setCltLiterateCode(String cltLiterateCode) {
    this.cltLiterateCode = cltLiterateCode;
  }

  public void setCltMaritalCohabitatnHstryIndicatorVar(
      String cltMaritalCohabitatnHstryIndicatorVar) {
    this.cltMaritalCohabitatnHstryIndicatorVar = cltMaritalCohabitatnHstryIndicatorVar;
  }

  public void setCltMaritalStatusType(Short cltMaritalStatusType) {
    this.cltMaritalStatusType = cltMaritalStatusType;
  }

  public void setCltMilitaryStatusCode(String cltMilitaryStatusCode) {
    this.cltMilitaryStatusCode = cltMilitaryStatusCode;
  }

  public void setCltMotherParentalRightTermDate(Date cltMotherParentalRightTermDate) {
    this.cltMotherParentalRightTermDate =
        NeutronDateUtils.freshDate(cltMotherParentalRightTermDate);
  }

  public void setCltNamePrefixDescription(String cltNamePrefixDescription) {
    this.cltNamePrefixDescription = cltNamePrefixDescription;
  }

  public void setCltNameType(Short cltNameType) {
    this.cltNameType = cltNameType;
  }

  public void setCltOutstandingWarrantIndicator(String cltOutstandingWarrantIndicator) {
    this.cltOutstandingWarrantIndicator = cltOutstandingWarrantIndicator;
  }

  public void setCltPrevCaChildrenServIndicator(String cltPrevCaChildrenServIndicator) {
    this.cltPrevCaChildrenServIndicator = cltPrevCaChildrenServIndicator;
  }

  public void setCltPrevOtherDescription(String cltPrevOtherDescription) {
    this.cltPrevOtherDescription = cltPrevOtherDescription;
  }

  public void setAdrReplicationDate(Date adrReplicationDate) {
    this.adrReplicationDate = NeutronDateUtils.freshDate(adrReplicationDate);
  }

  public void setAdrPrimaryExtension(Integer adrPrimaryExtension) {
    this.adrPrimaryExtension = adrPrimaryExtension;
  }

  public void setAdrState(Short adrState) {
    this.adrState = adrState;
  }

  public void setAdrStreetName(String adrStreetName) {
    this.adrStreetName = adrStreetName;
  }

  public void setAdrStreetNumber(String adrStreetNumber) {
    this.adrStreetNumber = adrStreetNumber;
  }

  public void setAdrZip(String adrZip) {
    this.adrZip = adrZip;
  }

  public void setAdrZip4(Short adrZip4) {
    this.adrZip4 = adrZip4;
  }

  public void setAdrPostDirCd(String adrPostDirCd) {
    this.adrPostDirCd = adrPostDirCd;
  }

  public void setAdrPreDirCd(String adrPreDirCd) {
    this.adrPreDirCd = adrPreDirCd;
  }

  public void setAdrStreetSuffixCd(Short adrStreetSuffixCd) {
    this.adrStreetSuffixCd = adrStreetSuffixCd;
  }

  public void setAdrUnitDesignationCd(Short adrUnitDesignationCd) {
    this.adrUnitDesignationCd = adrUnitDesignationCd;
  }

  public void setAdrUnitNumber(String adrUnitNumber) {
    this.adrUnitNumber = adrUnitNumber;
  }

  private ElasticSearchSafetyAlert createSafetyAlert() {
    if (StringUtils.isBlank(this.safetyAlertId)
        || CmsReplicationOperation.D == this.safetyAlertLastUpdatedOperation) {
      return null;
    }

    ElasticSearchSafetyAlert alert = new ElasticSearchSafetyAlert();
    alert.setId(this.safetyAlertId);

    ElasticSearchSafetyAlert.Activation activation = new ElasticSearchSafetyAlert.Activation();
    alert.setActivation(activation);

    activation.setActivationReasonDescription(SystemCodeCache.global()
        .getSystemCodeShortDescription(this.safetyAlertActivationReasonCode));
    activation.setActivationReasonId(this.safetyAlertActivationReasonCode != null
        ? this.safetyAlertActivationReasonCode.toString() : null);

    ElasticSearchSystemCode activationCounty = new ElasticSearchSystemCode();
    activation.setActivationCounty(activationCounty);
    activationCounty.setDescription(SystemCodeCache.global()
        .getSystemCodeShortDescription(this.safetyAlertActivationCountyCode));
    activationCounty.setId(this.safetyAlertActivationCountyCode != null
        ? this.safetyAlertActivationCountyCode.toString() : null);

    activation.setActivationDate(DomainChef.cookDate(this.safetyAlertActivationDate));
    activation.setActivationExplanation(this.safetyAlertActivationExplanation);

    ElasticSearchSafetyAlert.Deactivation deactivation =
        new ElasticSearchSafetyAlert.Deactivation();
    alert.setDeactivation(deactivation);

    ElasticSearchSystemCode deactivationCounty = new ElasticSearchSystemCode();
    deactivation.setDeactivationCounty(deactivationCounty);

    deactivationCounty.setDescription(SystemCodeCache.global()
        .getSystemCodeShortDescription(this.safetyAlertDeactivationCountyCode));
    deactivationCounty.setId(this.safetyAlertDeactivationCountyCode != null
        ? this.safetyAlertDeactivationCountyCode.toString() : null);

    deactivation.setDeactivationDate(DomainChef.cookDate(this.safetyAlertDeactivationDate));
    deactivation.setDeactivationExplanation(this.safetyAlertDeactivationExplanation);

    alert.setLegacyDescriptor(ElasticTransformer.createLegacyDescriptor(this.safetyAlertId,
        this.safetyAlertLastUpdatedTimestamp, LegacyTable.SAFETY_ALERT));

    return alert;
  }

  private ElasticSearchPersonAka createAka() {
    if (StringUtils.isBlank(this.akaId)
        || CmsReplicationOperation.D == this.akaLastUpdatedOperation) {
      return null;
    }

    ElasticSearchPersonAka aka = new ElasticSearchPersonAka();
    aka.setId(this.akaId);

    if (StringUtils.isNotBlank(this.akaFirstName)) {
      aka.setFirstName(this.akaFirstName.trim());
    }

    if (StringUtils.isNotBlank(this.akaLastName)) {
      aka.setLastName(this.akaLastName.trim());
    }

    if (StringUtils.isNotBlank(this.akaMiddleName)) {
      aka.setMiddleName(this.akaMiddleName.trim());
    }

    if (StringUtils.isNotBlank(this.akaNamePrefixDescription)) {
      aka.setPrefix(this.akaNamePrefixDescription.trim());
    }

    if (StringUtils.isNotBlank(this.akaSuffixTitleDescription)) {
      aka.setSuffix(this.akaSuffixTitleDescription.trim());
    }

    if (this.akaNameType != null && this.akaNameType.intValue() != 0) {
      aka.setNameType(SystemCodeCache.global().getSystemCodeShortDescription(this.akaNameType));
    }

    aka.setLegacyDescriptor(ElasticTransformer.createLegacyDescriptor(this.akaId,
        this.akaLastUpdatedTimestamp, LegacyTable.ALIAS_OR_OTHER_CLIENT_NAME));

    return aka;
  }

}
