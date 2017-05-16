package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.Type;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.rep.CmsReplicationOperation;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedClient;
import gov.ca.cwds.data.std.ApiGroupNormalizer;

/**
 * Entity bean for Materialized Query Table (MQT), ES_RELATIONSHIP.
 * 
 * <p>
 * Implements {@link ApiGroupNormalizer} and converts to {@link ReplicatedClient}.
 * </p>
 * 
 * @author CWDS API Team
 */
@Entity
@Table(name = "ES_RELATIONSHIP")
@NamedNativeQueries({
    @NamedNativeQuery(name = "gov.ca.cwds.data.persistence.cms.EsRelationship.findBucketRange",
        query = "SELECT x.* FROM {h-schema}ES_RELATIONSHIP x "
            + "WHERE x.CLT_IDENTIFIER BETWEEN :min_id AND :max_id "
            + "ORDER BY x.CLT_IDENTIFIER FOR READ ONLY",
        resultClass = EsRelationship.class, readOnly = true),
    @NamedNativeQuery(name = "gov.ca.cwds.data.persistence.cms.EsRelationship.findAllUpdatedAfter",
        query = "SELECT x.* FROM {h-schema}ES_RELATIONSHIP x "
            + "WHERE x.LAST_CHG > CAST(:after AS TIMESTAMP) "
            + "ORDER BY x.clt_IDENTIFIER FOR READ ONLY ",
        resultClass = EsRelationship.class, readOnly = true)})
public class EsRelationship implements PersistentObject, ApiGroupNormalizer<ReplicatedClient> {

  private static final Logger LOGGER = LogManager.getLogger(EsRelationship.class);

  /**
   * Default.
   */
  private static final long serialVersionUID = 1L;

  @Type(type = "timestamp")
  @Column(name = "LAST_CHG", updatable = false)
  private Date lastChange;

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

  /**
   * Convert IBM replication operation to enum.
   * 
   * @param op replication operation, IUD
   * @return enumerated type
   */
  protected static CmsReplicationOperation strToRepOp(String op) {
    return op != null ? CmsReplicationOperation.valueOf(op) : null;
  }

  /**
   * Build an EsRelationship from an incoming ResultSet.
   * 
   * @param rs incoming tuple
   * @return a populated EsRelationship
   * @throws SQLException if unable to convert types or stream breaks, etc.
   */
  public static EsRelationship produceFromResultSet(ResultSet rs) throws SQLException {
    EsRelationship ret = new EsRelationship();

    ret.setCltAdjudicatedDelinquentIndicator(rs.getString("CLT_ADJDEL_IND"));
    ret.setCltAdoptionStatusCode(rs.getString("CLT_ADPTN_STCD"));
    ret.setCltAlienRegistrationNumber(rs.getString("CLT_ALN_REG_NO"));
    ret.setCltBirthCity(rs.getString("CLT_BIRTH_CITY"));
    ret.setCltBirthCountryCodeType(rs.getShort("CLT_B_CNTRY_C"));
    ret.setCltBirthDate(rs.getDate("CLT_BIRTH_DT"));
    ret.setCltBirthFacilityName(rs.getString("CLT_BR_FAC_NM"));
    ret.setCltBirthStateCodeType(rs.getShort("CLT_B_STATE_C"));
    ret.setCltBirthplaceVerifiedIndicator(rs.getString("CLT_BP_VER_IND"));
    ret.setCltChildClientIndicatorVar(rs.getString("CLT_CHLD_CLT_B"));
    ret.setCltClientIndexNumber(rs.getString("CLT_CL_INDX_NO"));
    ret.setCltCommentDescription(rs.getString("CLT_COMMNT_DSC"));
    ret.setCltCommonFirstName(rs.getString("CLT_COM_FST_NM"));
    ret.setCltCommonLastName(rs.getString("CLT_COM_LST_NM"));
    ret.setCltCommonMiddleName(rs.getString("CLT_COM_MID_NM"));
    ret.setCltConfidentialityActionDate(rs.getDate("CLT_CONF_ACTDT"));
    ret.setCltConfidentialityInEffectIndicator(rs.getString("CLT_CONF_EFIND"));
    ret.setCltCreationDate(rs.getDate("CLT_CREATN_DT"));
    ret.setCltCurrCaChildrenServIndicator(rs.getString("CLT_CURRCA_IND"));
    ret.setCltCurrentlyOtherDescription(rs.getString("CLT_COTH_DESC"));
    ret.setCltCurrentlyRegionalCenterIndicator(rs.getString("CLT_CURREG_IND"));
    ret.setCltDeathDate(rs.getDate("CLT_DEATH_DT"));
    ret.setCltDeathDateVerifiedIndicator(rs.getString("CLT_DTH_DT_IND"));
    ret.setCltDeathPlace(rs.getString("CLT_DEATH_PLC"));
    ret.setCltDeathReasonText(rs.getString("CLT_DTH_RN_TXT"));
    ret.setCltDriverLicenseNumber(rs.getString("CLT_DRV_LIC_NO"));
    ret.setCltDriverLicenseStateCodeType(rs.getShort("CLT_D_STATE_C"));
    ret.setCltEmailAddress(rs.getString("CLT_EMAIL_ADDR"));
    ret.setCltEstimatedDobCode(rs.getString("CLT_EST_DOB_CD"));
    ret.setCltEthUnableToDetReasonCode(rs.getString("CLT_ETH_UD_CD"));
    ret.setCltFatherParentalRightTermDate(rs.getDate("CLT_FTERM_DT"));
    ret.setCltGenderCode(rs.getString("CLT_GENDER_CD"));
    ret.setCltHealthSummaryText(rs.getString("CLT_HEALTH_TXT"));
    ret.setCltHispUnableToDetReasonCode(rs.getString("CLT_HISP_UD_CD"));
    ret.setCltHispanicOriginCode(rs.getString("CLT_HISP_CD"));
    ret.setCltId(rs.getString("CLT_IDENTIFIER"));
    ret.setCltImmigrationCountryCodeType(rs.getShort("CLT_I_CNTRY_C"));
    ret.setCltImmigrationStatusType(rs.getShort("CLT_IMGT_STC"));
    ret.setCltIncapacitatedParentCode(rs.getString("CLT_INCAPC_CD"));
    ret.setCltIndividualHealthCarePlanIndicator(rs.getString("CLT_HCARE_IND"));
    ret.setCltLimitationOnScpHealthIndicator(rs.getString("CLT_LIMIT_IND"));
    ret.setCltLiterateCode(rs.getString("CLT_LITRATE_CD"));
    ret.setCltMaritalCohabitatnHstryIndicatorVar(rs.getString("CLT_MAR_HIST_B"));
    ret.setCltMaritalStatusType(rs.getShort("CLT_MRTL_STC"));
    ret.setCltMilitaryStatusCode(rs.getString("CLT_MILT_STACD"));
    ret.setCltMotherParentalRightTermDate(rs.getDate("CLT_MTERM_DT"));
    ret.setCltNamePrefixDescription(rs.getString("CLT_NMPRFX_DSC"));
    ret.setCltNameType(rs.getShort("CLT_NAME_TPC"));
    ret.setCltOutstandingWarrantIndicator(rs.getString("CLT_OUTWRT_IND"));
    ret.setCltPrevCaChildrenServIndicator(rs.getString("CLT_PREVCA_IND"));
    ret.setCltPrevOtherDescription(rs.getString("CLT_POTH_DESC"));
    ret.setCltPrevRegionalCenterIndicator(rs.getString("CLT_PREREG_IND"));
    ret.setCltPrimaryEthnicityType(rs.getShort("CLT_P_ETHNCTYC"));
    ret.setCltPrimaryLanguageType(rs.getShort("CLT_P_LANG_TPC"));
    ret.setCltReligionType(rs.getShort("CLT_RLGN_TPC"));
    ret.setCltSecondaryLanguageType(rs.getShort("CLT_S_LANG_TC"));
    ret.setCltSensitiveHlthInfoOnFileIndicator(rs.getString("CLT_SNTV_HLIND"));
    ret.setCltSensitivityIndicator(rs.getString("CLT_SENSTV_IND"));
    ret.setCltSoc158PlacementCode(rs.getString("CLT_SOCPLC_CD"));
    ret.setCltSoc158SealedClientIndicator(rs.getString("CLT_SOC158_IND"));
    ret.setCltSocialSecurityNumChangedCode(rs.getString("CLT_SSN_CHG_CD"));
    ret.setCltSocialSecurityNumber(rs.getString("CLT_SS_NO"));
    ret.setCltSuffixTitleDescription(rs.getString("CLT_SUFX_TLDSC"));
    ret.setCltTribalAncestryClientIndicatorVar(rs.getString("CLT_TRBA_CLT_B"));
    ret.setCltTribalMembrshpVerifctnIndicatorVar(rs.getString("CLT_TR_MBVRT_B"));
    ret.setCltUnemployedParentCode(rs.getString("CLT_UNEMPLY_CD"));
    ret.setCltZippyCreatedIndicator(rs.getString("CLT_ZIPPY_IND"));
    ret.setCltReplicationOperation(strToRepOp(rs.getString("CLT_IBMSNAP_OPERATION")));
    ret.setCltReplicationDate(rs.getDate("CLT_IBMSNAP_LOGMARKER"));
    ret.setCltLastUpdatedId(rs.getString("CLT_LST_UPD_ID"));
    ret.setCltLastUpdatedTime(rs.getDate("CLT_LST_UPD_TS"));

    return ret;
  }

  @Override
  public Class<ReplicatedClient> getReductionClass() {
    return ReplicatedClient.class;
  }

  @Override
  public void reduce(Map<Object, ReplicatedClient> map) {
    final boolean isClientAdded = map.containsKey(this.cltId);
    ReplicatedClient ret = isClientAdded ? map.get(this.cltId) : new ReplicatedClient();

    if (!isClientAdded) {
      // Populate core client attributes.
      ret.setAdjudicatedDelinquentIndicator(getCltAdjudicatedDelinquentIndicator());
      ret.setAdoptionStatusCode(getCltAdoptionStatusCode());
      ret.setAlienRegistrationNumber(getCltAlienRegistrationNumber());
      ret.setBirthCity(getCltBirthCity());
      ret.setBirthCountryCodeType(getCltBirthCountryCodeType());
      ret.setBirthDate(getCltBirthDate());
      ret.setBirthFacilityName(getCltBirthFacilityName());
      ret.setBirthplaceVerifiedIndicator(getCltBirthplaceVerifiedIndicator());
      ret.setBirthStateCodeType(getCltBirthStateCodeType());
      ret.setChildClientIndicatorVar(getCltChildClientIndicatorVar());
      ret.setClientIndexNumber(getCltClientIndexNumber());
      ret.setCommentDescription(getCltCommentDescription());
      ret.setCommonFirstName(getCltCommonFirstName());
      ret.setCommonLastName(getCltCommonLastName());
      ret.setCommonMiddleName(getCltCommonMiddleName());
      ret.setConfidentialityActionDate(getCltConfidentialityActionDate());
      ret.setConfidentialityInEffectIndicator(getCltConfidentialityInEffectIndicator());
      ret.setCreationDate(getCltCreationDate());
      ret.setCurrCaChildrenServIndicator(getCltCurrCaChildrenServIndicator());
      ret.setCurrentlyOtherDescription(getCltCurrentlyOtherDescription());
      ret.setCurrentlyRegionalCenterIndicator(getCltCurrentlyRegionalCenterIndicator());
      ret.setDeathDate(getCltDeathDate());
      ret.setDeathDateVerifiedIndicator(getCltDeathDateVerifiedIndicator());
      ret.setDeathPlace(getCltDeathPlace());
      ret.setDeathReasonText(getCltDeathReasonText());
      ret.setDriverLicenseNumber(getCltDriverLicenseNumber());
      ret.setDriverLicenseStateCodeType(getCltDriverLicenseStateCodeType());
      ret.setEmailAddress(getCltEmailAddress());
      ret.setEstimatedDobCode(getCltEstimatedDobCode());
      ret.setEthUnableToDetReasonCode(getCltEthUnableToDetReasonCode());
      ret.setFatherParentalRightTermDate(getCltFatherParentalRightTermDate());
      ret.setCommonFirstName(getCltCommonFirstName());
      ret.setGenderCode(getCltGenderCode());
      ret.setHealthSummaryText(getCltHealthSummaryText());
      ret.setHispanicOriginCode(getCltHispanicOriginCode());
      ret.setHispUnableToDetReasonCode(getCltHispUnableToDetReasonCode());
      ret.setId(getCltId());
      ret.setImmigrationCountryCodeType(getCltImmigrationCountryCodeType());
      ret.setImmigrationStatusType(getCltImmigrationStatusType());
      ret.setIncapacitatedParentCode(getCltIncapacitatedParentCode());
      ret.setIndividualHealthCarePlanIndicator(getCltIndividualHealthCarePlanIndicator());
      ret.setCommonLastName(getCltCommonLastName());
      ret.setLimitationOnScpHealthIndicator(getCltLimitationOnScpHealthIndicator());
      ret.setLiterateCode(getCltLiterateCode());
      ret.setMaritalCohabitatnHstryIndicatorVar(getCltMaritalCohabitatnHstryIndicatorVar());
      ret.setMaritalStatusType(getCltMaritalStatusType());
      ret.setCommonMiddleName(getCltCommonMiddleName());
      ret.setMilitaryStatusCode(getCltMilitaryStatusCode());
      ret.setMotherParentalRightTermDate(getCltMotherParentalRightTermDate());
      ret.setNamePrefixDescription(getCltNamePrefixDescription());
      ret.setNameType(getCltNameType());
      ret.setOutstandingWarrantIndicator(getCltOutstandingWarrantIndicator());
      ret.setPrevCaChildrenServIndicator(getCltPrevCaChildrenServIndicator());
      ret.setPrevOtherDescription(getCltPrevOtherDescription());
      ret.setPrevRegionalCenterIndicator(getCltPrevRegionalCenterIndicator());
      ret.setPrimaryEthnicityType(getCltPrimaryEthnicityType());
      ret.setPrimaryLanguageType(getCltPrimaryLanguageType());
      ret.setReligionType(getCltReligionType());
      ret.setSecondaryLanguageType(getCltSecondaryLanguageType());
      ret.setSensitiveHlthInfoOnFileIndicator(getCltSensitiveHlthInfoOnFileIndicator());
      ret.setSensitivityIndicator(getCltSensitivityIndicator());
      ret.setSoc158PlacementCode(getCltSoc158PlacementCode());
      ret.setSoc158SealedClientIndicator(getCltSoc158SealedClientIndicator());
      ret.setSocialSecurityNumber(getCltSocialSecurityNumber());
      ret.setSocialSecurityNumChangedCode(getCltSocialSecurityNumChangedCode());
      ret.setSuffixTitleDescription(getCltSuffixTitleDescription());
      ret.setTribalAncestryClientIndicatorVar(getCltTribalAncestryClientIndicatorVar());
      ret.setTribalMembrshpVerifctnIndicatorVar(getCltTribalMembrshpVerifctnIndicatorVar());
      ret.setUnemployedParentCode(getCltUnemployedParentCode());
      ret.setZippyCreatedIndicator(getCltZippyCreatedIndicator());
    }

    map.put(ret.getId(), ret);
  }

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

  @Override
  public Object getGroupKey() {
    return this.cltId;
  }

  /**
   * This view (i.e., materialized query table) doesn't have a proper unique key, but a combination
   * of several fields might come close.
   * <ul>
   * <li>"Cook": convert String parameter to strong type</li>
   * <li>"Uncook": convert strong type parameter to String</li>
   * </ul>
   *
   */
  @Override
  public Serializable getPrimaryKey() {
    return null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public final int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, false);
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public final boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj, false);
  }

  public Date getLastChange() {
    return lastChange;
  }

  public void setLastChange(Date lastChange) {
    this.lastChange = lastChange;
  }

}
