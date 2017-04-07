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

import org.hibernate.annotations.Type;

import gov.ca.cwds.data.persistence.cms.rep.CmsReplicationOperation;

@Entity
@Table(name = "ES_CLIENT_ADDRESS")
public class EsClientAddress implements Serializable {

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


}
