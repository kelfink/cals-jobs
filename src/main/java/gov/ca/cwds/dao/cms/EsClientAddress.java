package gov.ca.cwds.dao.cms;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.Type;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.rep.CmsReplicationOperation;

@Entity
@Table(name = "ES_CLIENT_ADDRESS")
@NamedNativeQuery(name = "gov.ca.cwds.dao.cms.EsClientAddress.findBucketRange",
    query = "SELECT x.* FROM {h-schema}ES_CLIENT_ADDRESS x "
        + "WHERE CLT_IDENTIFIER BETWEEN :min_id AND :max_id ORDER BY CLT_IDENTIFIER, CLA_EFF_STRTDT FOR READ ONLY",
    resultClass = EsClientAddress.class, readOnly = true)
public class EsClientAddress implements Serializable, PersistentObject {

  // ================
  // CLIENT_T:
  // ================

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

  // ================
  // CL_ADDRT:
  // ================

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

  // ================
  // ADDRS_T:
  // ================

  @Id
  @Column(name = "ADR_IDENTIFIER")
  private String adrId;

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

  public String getCltAdjudicatedDelinquentIndicator() {
    return cltAdjudicatedDelinquentIndicator;
  }

  public void setCltAdjudicatedDelinquentIndicator(String cltAdjudicatedDelinquentIndicator) {
    this.cltAdjudicatedDelinquentIndicator = cltAdjudicatedDelinquentIndicator;
  }

  public String getCltAdoptionStatusCode() {
    return cltAdoptionStatusCode;
  }

  public void setCltAdoptionStatusCode(String cltAdoptionStatusCode) {
    this.cltAdoptionStatusCode = cltAdoptionStatusCode;
  }

  public String getCltAlienRegistrationNumber() {
    return cltAlienRegistrationNumber;
  }

  public void setCltAlienRegistrationNumber(String cltAlienRegistrationNumber) {
    this.cltAlienRegistrationNumber = cltAlienRegistrationNumber;
  }

  public String getCltBirthCity() {
    return cltBirthCity;
  }

  public void setCltBirthCity(String cltBirthCity) {
    this.cltBirthCity = cltBirthCity;
  }

  public Short getCltBirthCountryCodeType() {
    return cltBirthCountryCodeType;
  }

  public void setCltBirthCountryCodeType(Short cltBirthCountryCodeType) {
    this.cltBirthCountryCodeType = cltBirthCountryCodeType;
  }

  public Date getCltBirthDate() {
    return cltBirthDate;
  }

  public void setCltBirthDate(Date cltBirthDate) {
    this.cltBirthDate = cltBirthDate;
  }

  public String getCltBirthFacilityName() {
    return cltBirthFacilityName;
  }

  public void setCltBirthFacilityName(String cltBirthFacilityName) {
    this.cltBirthFacilityName = cltBirthFacilityName;
  }

  public Short getCltBirthStateCodeType() {
    return cltBirthStateCodeType;
  }

  public void setCltBirthStateCodeType(Short cltBirthStateCodeType) {
    this.cltBirthStateCodeType = cltBirthStateCodeType;
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

  public void setCltChildClientIndicatorVar(String cltChildClientIndicatorVar) {
    this.cltChildClientIndicatorVar = cltChildClientIndicatorVar;
  }

  public String getCltClientIndexNumber() {
    return cltClientIndexNumber;
  }

  public void setCltClientIndexNumber(String cltClientIndexNumber) {
    this.cltClientIndexNumber = cltClientIndexNumber;
  }

  public String getCltCommentDescription() {
    return cltCommentDescription;
  }

  public void setCltCommentDescription(String cltCommentDescription) {
    this.cltCommentDescription = cltCommentDescription;
  }

  public String getCltCommonFirstName() {
    return cltCommonFirstName;
  }

  public void setCltCommonFirstName(String cltCommonFirstName) {
    this.cltCommonFirstName = cltCommonFirstName;
  }

  public String getCltCommonLastName() {
    return cltCommonLastName;
  }

  public void setCltCommonLastName(String cltCommonLastName) {
    this.cltCommonLastName = cltCommonLastName;
  }

  public String getCltCommonMiddleName() {
    return cltCommonMiddleName;
  }

  public void setCltCommonMiddleName(String cltCommonMiddleName) {
    this.cltCommonMiddleName = cltCommonMiddleName;
  }

  public Date getCltConfidentialityActionDate() {
    return cltConfidentialityActionDate;
  }

  public void setCltConfidentialityActionDate(Date cltConfidentialityActionDate) {
    this.cltConfidentialityActionDate = cltConfidentialityActionDate;
  }

  public String getCltConfidentialityInEffectIndicator() {
    return cltConfidentialityInEffectIndicator;
  }

  public void setCltConfidentialityInEffectIndicator(String cltConfidentialityInEffectIndicator) {
    this.cltConfidentialityInEffectIndicator = cltConfidentialityInEffectIndicator;
  }

  public Date getCltCreationDate() {
    return cltCreationDate;
  }

  public void setCltCreationDate(Date cltCreationDate) {
    this.cltCreationDate = cltCreationDate;
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

  public void setCltCurrentlyOtherDescription(String cltCurrentlyOtherDescription) {
    this.cltCurrentlyOtherDescription = cltCurrentlyOtherDescription;
  }

  public String getCltCurrentlyRegionalCenterIndicator() {
    return cltCurrentlyRegionalCenterIndicator;
  }

  public void setCltCurrentlyRegionalCenterIndicator(String cltCurrentlyRegionalCenterIndicator) {
    this.cltCurrentlyRegionalCenterIndicator = cltCurrentlyRegionalCenterIndicator;
  }

  public Date getCltDeathDate() {
    return cltDeathDate;
  }

  public void setCltDeathDate(Date cltDeathDate) {
    this.cltDeathDate = cltDeathDate;
  }

  public String getCltDeathDateVerifiedIndicator() {
    return cltDeathDateVerifiedIndicator;
  }

  public void setCltDeathDateVerifiedIndicator(String cltDeathDateVerifiedIndicator) {
    this.cltDeathDateVerifiedIndicator = cltDeathDateVerifiedIndicator;
  }

  public String getCltDeathPlace() {
    return cltDeathPlace;
  }

  public void setCltDeathPlace(String cltDeathPlace) {
    this.cltDeathPlace = cltDeathPlace;
  }

  public String getCltDeathReasonText() {
    return cltDeathReasonText;
  }

  public void setCltDeathReasonText(String cltDeathReasonText) {
    this.cltDeathReasonText = cltDeathReasonText;
  }

  public String getCltDriverLicenseNumber() {
    return cltDriverLicenseNumber;
  }

  public void setCltDriverLicenseNumber(String cltDriverLicenseNumber) {
    this.cltDriverLicenseNumber = cltDriverLicenseNumber;
  }

  public Short getCltDriverLicenseStateCodeType() {
    return cltDriverLicenseStateCodeType;
  }

  public void setCltDriverLicenseStateCodeType(Short cltDriverLicenseStateCodeType) {
    this.cltDriverLicenseStateCodeType = cltDriverLicenseStateCodeType;
  }

  public String getCltEmailAddress() {
    return cltEmailAddress;
  }

  public void setCltEmailAddress(String cltEmailAddress) {
    this.cltEmailAddress = cltEmailAddress;
  }

  public String getCltEstimatedDobCode() {
    return cltEstimatedDobCode;
  }

  public void setCltEstimatedDobCode(String cltEstimatedDobCode) {
    this.cltEstimatedDobCode = cltEstimatedDobCode;
  }

  public String getCltEthUnableToDetReasonCode() {
    return cltEthUnableToDetReasonCode;
  }

  public void setCltEthUnableToDetReasonCode(String cltEthUnableToDetReasonCode) {
    this.cltEthUnableToDetReasonCode = cltEthUnableToDetReasonCode;
  }

  public Date getCltFatherParentalRightTermDate() {
    return cltFatherParentalRightTermDate;
  }

  public void setCltFatherParentalRightTermDate(Date cltFatherParentalRightTermDate) {
    this.cltFatherParentalRightTermDate = cltFatherParentalRightTermDate;
  }

  public String getCltGenderCode() {
    return cltGenderCode;
  }

  public void setCltGenderCode(String cltGenderCode) {
    this.cltGenderCode = cltGenderCode;
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

  public void setCltImmigrationStatusType(Short cltImmigrationStatusType) {
    this.cltImmigrationStatusType = cltImmigrationStatusType;
  }

  public String getCltIncapacitatedParentCode() {
    return cltIncapacitatedParentCode;
  }

  public void setCltIncapacitatedParentCode(String cltIncapacitatedParentCode) {
    this.cltIncapacitatedParentCode = cltIncapacitatedParentCode;
  }

  public String getCltIndividualHealthCarePlanIndicator() {
    return cltIndividualHealthCarePlanIndicator;
  }

  public void setCltIndividualHealthCarePlanIndicator(String cltIndividualHealthCarePlanIndicator) {
    this.cltIndividualHealthCarePlanIndicator = cltIndividualHealthCarePlanIndicator;
  }

  public String getCltLimitationOnScpHealthIndicator() {
    return cltLimitationOnScpHealthIndicator;
  }

  public void setCltLimitationOnScpHealthIndicator(String cltLimitationOnScpHealthIndicator) {
    this.cltLimitationOnScpHealthIndicator = cltLimitationOnScpHealthIndicator;
  }

  public String getCltLiterateCode() {
    return cltLiterateCode;
  }

  public void setCltLiterateCode(String cltLiterateCode) {
    this.cltLiterateCode = cltLiterateCode;
  }

  public String getCltMaritalCohabitatnHstryIndicatorVar() {
    return cltMaritalCohabitatnHstryIndicatorVar;
  }

  public void setCltMaritalCohabitatnHstryIndicatorVar(
      String cltMaritalCohabitatnHstryIndicatorVar) {
    this.cltMaritalCohabitatnHstryIndicatorVar = cltMaritalCohabitatnHstryIndicatorVar;
  }

  public Short getCltMaritalStatusType() {
    return cltMaritalStatusType;
  }

  public void setCltMaritalStatusType(Short cltMaritalStatusType) {
    this.cltMaritalStatusType = cltMaritalStatusType;
  }

  public String getCltMilitaryStatusCode() {
    return cltMilitaryStatusCode;
  }

  public void setCltMilitaryStatusCode(String cltMilitaryStatusCode) {
    this.cltMilitaryStatusCode = cltMilitaryStatusCode;
  }

  public Date getCltMotherParentalRightTermDate() {
    return cltMotherParentalRightTermDate;
  }

  public void setCltMotherParentalRightTermDate(Date cltMotherParentalRightTermDate) {
    this.cltMotherParentalRightTermDate = cltMotherParentalRightTermDate;
  }

  public String getCltNamePrefixDescription() {
    return cltNamePrefixDescription;
  }

  public void setCltNamePrefixDescription(String cltNamePrefixDescription) {
    this.cltNamePrefixDescription = cltNamePrefixDescription;
  }

  public Short getCltNameType() {
    return cltNameType;
  }

  public void setCltNameType(Short cltNameType) {
    this.cltNameType = cltNameType;
  }

  public String getCltOutstandingWarrantIndicator() {
    return cltOutstandingWarrantIndicator;
  }

  public void setCltOutstandingWarrantIndicator(String cltOutstandingWarrantIndicator) {
    this.cltOutstandingWarrantIndicator = cltOutstandingWarrantIndicator;
  }

  public String getCltPrevCaChildrenServIndicator() {
    return cltPrevCaChildrenServIndicator;
  }

  public void setCltPrevCaChildrenServIndicator(String cltPrevCaChildrenServIndicator) {
    this.cltPrevCaChildrenServIndicator = cltPrevCaChildrenServIndicator;
  }

  public String getCltPrevOtherDescription() {
    return cltPrevOtherDescription;
  }

  public void setCltPrevOtherDescription(String cltPrevOtherDescription) {
    this.cltPrevOtherDescription = cltPrevOtherDescription;
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
    return cltReplicationDate;
  }

  public void setCltReplicationDate(Date cltReplicationDate) {
    this.cltReplicationDate = cltReplicationDate;
  }

  public String getCltLastUpdatedId() {
    return cltLastUpdatedId;
  }

  public void setCltLastUpdatedId(String cltLastUpdatedId) {
    this.cltLastUpdatedId = cltLastUpdatedId;
  }

  public Date getCltLastUpdatedTime() {
    return cltLastUpdatedTime;
  }

  public void setCltLastUpdatedTime(Date cltLastUpdatedTime) {
    this.cltLastUpdatedTime = cltLastUpdatedTime;
  }

  public CmsReplicationOperation getClaReplicationOperation() {
    return claReplicationOperation;
  }

  public void setClaReplicationOperation(CmsReplicationOperation claReplicationOperation) {
    this.claReplicationOperation = claReplicationOperation;
  }

  public Date getClaReplicationDate() {
    return claReplicationDate;
  }

  public void setClaReplicationDate(Date claReplicationDate) {
    this.claReplicationDate = claReplicationDate;
  }

  public String getClaLastUpdatedId() {
    return claLastUpdatedId;
  }

  public void setClaLastUpdatedId(String claLastUpdatedId) {
    this.claLastUpdatedId = claLastUpdatedId;
  }

  public Date getClaLastUpdatedTime() {
    return claLastUpdatedTime;
  }

  public void setClaLastUpdatedTime(Date claLastUpdatedTime) {
    this.claLastUpdatedTime = claLastUpdatedTime;
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
    return claEffectiveEndDate;
  }

  public void setClaEffectiveEndDate(Date claEffectiveEndDate) {
    this.claEffectiveEndDate = claEffectiveEndDate;
  }

  public Date getClaEffectiveStartDate() {
    return claEffectiveStartDate;
  }

  public void setClaEffectiveStartDate(Date claEffectiveStartDate) {
    this.claEffectiveStartDate = claEffectiveStartDate;
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

  public void setAdrPrimaryExtension(Integer adrPrimaryExtension) {
    this.adrPrimaryExtension = adrPrimaryExtension;
  }

  public Short getAdrState() {
    return adrState;
  }

  public void setAdrState(Short adrState) {
    this.adrState = adrState;
  }

  public String getAdrStreetName() {
    return adrStreetName;
  }

  public void setAdrStreetName(String adrStreetName) {
    this.adrStreetName = adrStreetName;
  }

  public String getAdrStreetNumber() {
    return adrStreetNumber;
  }

  public void setAdrStreetNumber(String adrStreetNumber) {
    this.adrStreetNumber = adrStreetNumber;
  }

  public String getAdrZip() {
    return adrZip;
  }

  public void setAdrZip(String adrZip) {
    this.adrZip = adrZip;
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

  public void setAdrZip4(Short adrZip4) {
    this.adrZip4 = adrZip4;
  }

  public String getAdrPostDirCd() {
    return adrPostDirCd;
  }

  public void setAdrPostDirCd(String adrPostDirCd) {
    this.adrPostDirCd = adrPostDirCd;
  }

  public String getAdrPreDirCd() {
    return adrPreDirCd;
  }

  public void setAdrPreDirCd(String adrPreDirCd) {
    this.adrPreDirCd = adrPreDirCd;
  }

  public Short getAdrStreetSuffixCd() {
    return adrStreetSuffixCd;
  }

  public void setAdrStreetSuffixCd(Short adrStreetSuffixCd) {
    this.adrStreetSuffixCd = adrStreetSuffixCd;
  }

  public Short getAdrUnitDesignationCd() {
    return adrUnitDesignationCd;
  }

  public void setAdrUnitDesignationCd(Short adrUnitDesignationCd) {
    this.adrUnitDesignationCd = adrUnitDesignationCd;
  }

  public String getAdrUnitNumber() {
    return adrUnitNumber;
  }

  public void setAdrUnitNumber(String adrUnitNumber) {
    this.adrUnitNumber = adrUnitNumber;
  }

  @Override
  public Serializable getPrimaryKey() {
    return null;
  }

}