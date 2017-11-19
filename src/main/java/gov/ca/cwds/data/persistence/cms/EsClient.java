package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;
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
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.Type;

import gov.ca.cwds.data.es.ElasticSearchPersonAka;
import gov.ca.cwds.data.es.ElasticSearchSafetyAlert;
import gov.ca.cwds.data.es.ElasticSearchSystemCode;
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
public class EsClient extends BaseEsClient implements Comparable<EsClient>, Comparator<EsClient> {

  private static final long serialVersionUID = 1L;

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
    ret.setCltSensitivityIndicator(rs.getString("CLT_SENSTV_IND"));
    ret.setCltSoc158SealedClientIndicator(rs.getString("CLT_SOC158_IND"));
    ret.setCltAdjudicatedDelinquentIndicator(rs.getString("CLT_ADJDEL_IND"));
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
