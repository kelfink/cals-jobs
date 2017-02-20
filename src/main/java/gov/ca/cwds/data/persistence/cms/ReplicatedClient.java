package gov.ca.cwds.data.persistence.cms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import gov.ca.cwds.dao.cms.CmsReplicatedEntity;
import gov.ca.cwds.dao.cms.CmsReplicationOperation;
import gov.ca.cwds.data.CmsSystemCodeDeserializer;
import gov.ca.cwds.data.SystemCodeSerializer;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiLanguageAware;
import gov.ca.cwds.data.std.ApiMultipleLanguagesAware;
import gov.ca.cwds.data.std.ApiPersonAware;

/**
 * {@link PersistentObject} representing a Client as a {@link CmsReplicatedEntity}.
 * 
 * @author CWDS API Team
 */
@NamedNativeQueries({
    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.cms.ReplicatedClient.findAllUpdatedAfter",
        query = "select z.IDENTIFIER, z.ADPTN_STCD, z.ALN_REG_NO, z.BIRTH_DT, "
            + "trim(z.BR_FAC_NM) as BR_FAC_NM, z.B_STATE_C, z.B_CNTRY_C, z.CHLD_CLT_B, "
            + "trim(z.COM_FST_NM) as COM_FST_NM, trim(z.COM_LST_NM) as COM_LST_NM, "
            + "trim(z.COM_MID_NM) as COM_MID_NM, z.CONF_EFIND, z.CONF_ACTDT, z.CREATN_DT, "
            + "z.DEATH_DT, trim(z.DTH_RN_TXT) as DTH_RN_TXT, trim(z.DRV_LIC_NO) as DRV_LIC_NO, "
            + "z.D_STATE_C, z.GENDER_CD, z.I_CNTRY_C, z.IMGT_STC, z.INCAPC_CD, "
            + "z.LITRATE_CD, z.MAR_HIST_B, z.MRTL_STC, z.MILT_STACD, z.NMPRFX_DSC, "
            + "z.NAME_TPC, z.OUTWRT_IND, z.P_ETHNCTYC, z.P_LANG_TPC, z.RLGN_TPC, "
            + "z.S_LANG_TC, z.SENSTV_IND, z.SNTV_HLIND, z.SS_NO, z.SSN_CHG_CD, "
            + "trim(z.SUFX_TLDSC) as SUFX_TLDSC, z.UNEMPLY_CD, z.LST_UPD_ID, z.LST_UPD_TS, "
            + "trim(z.COMMNT_DSC) as COMMNT_DSC, z.EST_DOB_CD, z.BP_VER_IND, z.HISP_CD, "
            + "z.CURRCA_IND, z.CURREG_IND, z.COTH_DESC, z.PREVCA_IND, z.PREREG_IND, "
            + "trim(z.POTH_DESC) as POTH_DESC, z.HCARE_IND, z.LIMIT_IND, "
            + "trim(z.BIRTH_CITY) as BIRTH_CITY, trim(z.HEALTH_TXT) as HEALTH_TXT, "
            + "z.MTERM_DT, z.FTERM_DT, z.ZIPPY_IND, trim(z.DEATH_PLC) as DEATH_PLC, "
            + "z.TR_MBVRT_B, z.TRBA_CLT_B, z.SOC158_IND, z.DTH_DT_IND, "
            + "trim(z.EMAIL_ADDR) as EMAIL_ADDR, z.ADJDEL_IND, z.ETH_UD_CD, "
            + "z.HISP_UD_CD, z.SOCPLC_CD, z.CL_INDX_NO, z.IBMSNAP_OPERATION, z.IBMSNAP_LOGMARKER "
            + "from {h-schema}CLIENT_T z WHERE z.IBMSNAP_LOGMARKER >= :after FOR READ ONLY",
        resultClass = ReplicatedClient.class),
    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.cms.ReplicatedClient.findPartitionedBuckets",
        query = "select z.IDENTIFIER, z.ADPTN_STCD, z.ALN_REG_NO, z.BIRTH_DT, "
            + "trim(z.BR_FAC_NM) as BR_FAC_NM, z.B_STATE_C, z.B_CNTRY_C, z.CHLD_CLT_B, "
            + "trim(z.COM_FST_NM) as COM_FST_NM, trim(z.COM_LST_NM) as COM_LST_NM, "
            + "trim(z.COM_MID_NM) as COM_MID_NM, z.CONF_EFIND, z.CONF_ACTDT, z.CREATN_DT, "
            + "z.DEATH_DT, trim(z.DTH_RN_TXT) as DTH_RN_TXT, trim(z.DRV_LIC_NO) as DRV_LIC_NO, "
            + "z.D_STATE_C, z.GENDER_CD, z.I_CNTRY_C, z.IMGT_STC, z.INCAPC_CD, "
            + "z.LITRATE_CD, z.MAR_HIST_B, z.MRTL_STC, z.MILT_STACD, z.NMPRFX_DSC, "
            + "z.NAME_TPC, z.OUTWRT_IND, z.P_ETHNCTYC, z.P_LANG_TPC, z.RLGN_TPC, "
            + "z.S_LANG_TC, z.SENSTV_IND, z.SNTV_HLIND, z.SS_NO, z.SSN_CHG_CD, "
            + "trim(z.SUFX_TLDSC) as SUFX_TLDSC, z.UNEMPLY_CD, z.LST_UPD_ID, z.LST_UPD_TS, "
            + "trim(z.COMMNT_DSC) as COMMNT_DSC, z.EST_DOB_CD, z.BP_VER_IND, z.HISP_CD, "
            + "z.CURRCA_IND, z.CURREG_IND, z.COTH_DESC, z.PREVCA_IND, z.PREREG_IND, "
            + "trim(z.POTH_DESC) as POTH_DESC, z.HCARE_IND, z.LIMIT_IND, "
            + "trim(z.BIRTH_CITY) as BIRTH_CITY, trim(z.HEALTH_TXT) as HEALTH_TXT, "
            + "z.MTERM_DT, z.FTERM_DT, z.ZIPPY_IND, trim(z.DEATH_PLC) as DEATH_PLC, "
            + "z.TR_MBVRT_B, z.TRBA_CLT_B, z.SOC158_IND, z.DTH_DT_IND, "
            + "trim(z.EMAIL_ADDR) as EMAIL_ADDR, z.ADJDEL_IND, z.ETH_UD_CD, "
            + "z.HISP_UD_CD, z.SOCPLC_CD, z.CL_INDX_NO "
            + ", 'U' as IBMSNAP_OPERATION, z.LST_UPD_TS as IBMSNAP_LOGMARKER "
            + "from ( select mod(y.rn, CAST(:total_buckets AS INTEGER)) + 1 as bucket, y.* "
            + "from ( select row_number() over (order by 1) as rn, x.* "
            + "from ( select c.* from {h-schema}CLIENT_T c "
            + "where c.SOC158_IND ='N' and c.SENSTV_IND = 'N' "
            + "AND c.IDENTIFIER >= :min_id and c.IDENTIFIER < :max_id "
            + ") x ) y ) z where z.bucket = :bucket_num for read only",
        resultClass = Client.class)})
// How does one inherit in hibernate to account for replication tables??
// Hibernate demands a "discriminator column", but that's nonsense 'cuz no such column exists!
// We want to reuse column definitions for maintainability, NOT actually join or extend tables!
// Hibernate doesn't understand our intent.
// This class represents the REPLICATION version of the SAME table.
// Only known solution is to make a base class of Client with all columns as a MappedSuperclass,
// then change both Client and ReplicatedClient to extend that base class. Very cheesy solution.
// Spending too much time "managing the framework."

@Entity
// @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// @DiscriminatorOptions()
@Table(name = "CLIENT_T")
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
// public class ReplicatedClient extends Client implements CmsReplicatedEntity {
public class ReplicatedClient extends CmsPersistentObject
    implements ApiPersonAware, ApiMultipleLanguagesAware, CmsReplicatedEntity {

  private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

  /**
   * Base serialization version. Increment by class version.
   */
  private static final long serialVersionUID = 1L;

  @Column(name = "ADJDEL_IND")
  private String adjudicatedDelinquentIndicator;

  @Column(name = "ADPTN_STCD")
  private String adoptionStatusCode;

  @Column(name = "ALN_REG_NO")
  private String alienRegistrationNumber;

  @Column(name = "BIRTH_CITY")
  private String birthCity;

  @SystemCodeSerializer(logical = true, description = true)
  @JsonDeserialize(using = CmsSystemCodeDeserializer.class)
  @Type(type = "short")
  @Column(name = "B_CNTRY_C")
  private Short birthCountryCodeType;

  @Type(type = "date")
  @Column(name = "BIRTH_DT")
  private Date birthDate;

  @Column(name = "BR_FAC_NM")
  private String birthFacilityName;

  @SystemCodeSerializer(logical = true, description = true)
  @JsonDeserialize(using = CmsSystemCodeDeserializer.class)
  @Type(type = "short")
  @Column(name = "B_STATE_C")
  private Short birthStateCodeType;

  @Column(name = "BP_VER_IND")
  private String birthplaceVerifiedIndicator;

  @Column(name = "CHLD_CLT_B")
  private String childClientIndicatorVar;

  @Column(name = "CL_INDX_NO")
  private String clientIndexNumber;

  @Column(name = "COMMNT_DSC")
  private String commentDescription;

  @Column(name = "COM_FST_NM")
  private String commonFirstName;

  @Column(name = "COM_LST_NM")
  private String commonLastName;

  @Column(name = "COM_MID_NM")
  private String commonMiddleName;

  @Type(type = "date")
  @Column(name = "CONF_ACTDT")
  private Date confidentialityActionDate;

  @Column(name = "CONF_EFIND")
  private String confidentialityInEffectIndicator;

  @Type(type = "date")
  @Column(name = "CREATN_DT")
  private Date creationDate;

  @Column(name = "CURRCA_IND")
  private String currCaChildrenServIndicator;

  @Column(name = "COTH_DESC")
  private String currentlyOtherDescription;

  @Column(name = "CURREG_IND")
  private String currentlyRegionalCenterIndicator;

  @Type(type = "date")
  @Column(name = "DEATH_DT")
  private Date deathDate;

  @Column(name = "DTH_DT_IND")
  private String deathDateVerifiedIndicator;

  @Column(name = "DEATH_PLC")
  private String deathPlace;

  @Column(name = "DTH_RN_TXT")
  private String deathReasonText;

  @Column(name = "DRV_LIC_NO")
  private String driverLicenseNumber;

  @SystemCodeSerializer(logical = true, description = true)
  @JsonDeserialize(using = CmsSystemCodeDeserializer.class)
  @Type(type = "short")
  @Column(name = "D_STATE_C")
  private Short driverLicenseStateCodeType;

  @Column(name = "EMAIL_ADDR")
  private String emailAddress;

  @Column(name = "EST_DOB_CD")
  private String estimatedDobCode;

  @Column(name = "ETH_UD_CD")
  private String ethUnableToDetReasonCode;

  @Type(type = "date")
  @Column(name = "FTERM_DT")
  private Date fatherParentalRightTermDate;

  @Column(name = "GENDER_CD")
  private String genderCode;

  @Column(name = "HEALTH_TXT")
  private String healthSummaryText;

  @Column(name = "HISP_UD_CD")
  private String hispUnableToDetReasonCode;

  @Column(name = "HISP_CD")
  private String hispanicOriginCode;

  @Id
  @Column(name = "IDENTIFIER", length = CMS_ID_LEN)
  private String id;

  @SystemCodeSerializer(logical = true, description = true)
  @JsonDeserialize(using = CmsSystemCodeDeserializer.class)
  @Type(type = "short")
  @Column(name = "I_CNTRY_C")
  private Short immigrationCountryCodeType;

  @SystemCodeSerializer(logical = true, description = true)
  @JsonDeserialize(using = CmsSystemCodeDeserializer.class)
  @Type(type = "short")
  @Column(name = "IMGT_STC")
  private Short immigrationStatusType;

  @Column(name = "INCAPC_CD")
  private String incapacitatedParentCode;

  @Column(name = "HCARE_IND")
  private String individualHealthCarePlanIndicator;

  @Column(name = "LIMIT_IND")
  private String limitationOnScpHealthIndicator;

  @Column(name = "LITRATE_CD")
  private String literateCode;

  @Column(name = "MAR_HIST_B")
  private String maritalCohabitatnHstryIndicatorVar;

  @SystemCodeSerializer(logical = true, description = true)
  @JsonDeserialize(using = CmsSystemCodeDeserializer.class)
  @Type(type = "short")
  @Column(name = "MRTL_STC")
  private Short maritalStatusType;

  @Column(name = "MILT_STACD")
  private String militaryStatusCode;

  @Type(type = "date")
  @Column(name = "MTERM_DT")
  private Date motherParentalRightTermDate;

  @Column(name = "NMPRFX_DSC")
  private String namePrefixDescription;

  @SystemCodeSerializer(logical = true, description = true)
  @JsonDeserialize(using = CmsSystemCodeDeserializer.class)
  @Type(type = "short")
  @Column(name = "NAME_TPC")
  private Short nameType;

  @Column(name = "OUTWRT_IND")
  private String outstandingWarrantIndicator;

  @Column(name = "PREVCA_IND")
  private String prevCaChildrenServIndicator;

  @Column(name = "POTH_DESC")
  private String prevOtherDescription;

  @Column(name = "PREREG_IND")
  private String prevRegionalCenterIndicator;

  @SystemCodeSerializer(logical = true, description = true)
  @JsonDeserialize(using = CmsSystemCodeDeserializer.class)
  @Type(type = "short")
  @Column(name = "P_ETHNCTYC")
  private Short primaryEthnicityType;

  @SystemCodeSerializer(logical = true, description = true)
  @JsonDeserialize(using = CmsSystemCodeDeserializer.class)
  @Type(type = "short")
  @Column(name = "P_LANG_TPC")
  private Short primaryLanguageType;

  @SystemCodeSerializer(logical = true, description = true)
  @JsonDeserialize(using = CmsSystemCodeDeserializer.class)
  @Type(type = "short")
  @Column(name = "RLGN_TPC")
  private Short religionType;

  @SystemCodeSerializer(logical = true, description = true)
  @JsonDeserialize(using = CmsSystemCodeDeserializer.class)
  @Type(type = "short")
  @Column(name = "S_LANG_TC")
  private Short secondaryLanguageType;

  @Column(name = "SNTV_HLIND")
  private String sensitiveHlthInfoOnFileIndicator;

  @Column(name = "SENSTV_IND")
  private String sensitivityIndicator;

  @Column(name = "SOCPLC_CD")
  private String soc158PlacementCode;

  @Column(name = "SOC158_IND")
  private String soc158SealedClientIndicator;

  @Column(name = "SSN_CHG_CD")
  private String socialSecurityNumChangedCode;

  @Column(name = "SS_NO")
  private String socialSecurityNumber;

  @Column(name = "SUFX_TLDSC")
  private String suffixTitleDescription;

  @Column(name = "TRBA_CLT_B")
  private String tribalAncestryClientIndicatorVar;

  @Column(name = "TR_MBVRT_B")
  private String tribalMembrshpVerifctnIndicatorVar;

  @Column(name = "UNEMPLY_CD")
  private String unemployedParentCode;

  @Column(name = "ZIPPY_IND")
  private String zippyCreatedIndicator;

  @Enumerated(EnumType.STRING)
  @Column(name = "IBMSNAP_OPERATION", updatable = false)
  private CmsReplicationOperation replicationOperation;

  @Type(type = "timestamp")
  @Column(name = "IBMSNAP_LOGMARKER", updatable = false)
  private Date replicationDate;

  @Override
  public CmsReplicationOperation getReplicationOperation() {
    return replicationOperation;
  }

  @Override
  public void setReplicationOperation(CmsReplicationOperation replicationOperation) {
    this.replicationOperation = replicationOperation;
  }

  @Override
  public Date getReplicationDate() {
    return replicationDate;
  }

  @Override
  public void setReplicationDate(Date replicationDate) {
    this.replicationDate = replicationDate;
  }

  /**
   * {@inheritDoc}
   * 
   * @see gov.ca.cwds.data.persistence.PersistentObject#getPrimaryKey()
   */
  @Override
  public String getPrimaryKey() {
    return getId();
  }

  /**
   * @return the adjudicatedDelinquentIndicator
   */
  public String getAdjudicatedDelinquentIndicator() {
    return StringUtils.trimToEmpty(adjudicatedDelinquentIndicator);
  }

  /**
   * @return the adoptionStatusCode
   */
  public String getAdoptionStatusCode() {
    return StringUtils.trimToEmpty(adoptionStatusCode);
  }

  /**
   * @return the alienRegistrationNumber
   */
  public String getAlienRegistrationNumber() {
    return StringUtils.trimToEmpty(alienRegistrationNumber);
  }

  /**
   * @return the birthCity
   */
  public String getBirthCity() {
    return StringUtils.trimToEmpty(birthCity);
  }

  /**
   * @return the birthCountryCodeType
   */
  public Short getBirthCountryCodeType() {
    return birthCountryCodeType;
  }

  /**
   * @return the birthDate
   */
  @Override
  public Date getBirthDate() {
    return birthDate;
  }

  /**
   * @return the birthFacilityName
   */
  public String getBirthFacilityName() {
    return StringUtils.trimToEmpty(birthFacilityName);
  }

  /**
   * @return the birthStateCodeType
   */
  public Short getBirthStateCodeType() {
    return birthStateCodeType;
  }

  /**
   * @return the birthplaceVerifiedIndicator
   */
  public String getBirthplaceVerifiedIndicator() {
    return StringUtils.trimToEmpty(birthplaceVerifiedIndicator);
  }

  /**
   * @return the childClientIndicatorVar
   */
  public String getChildClientIndicatorVar() {
    return StringUtils.trimToEmpty(childClientIndicatorVar);
  }

  /**
   * @return the clientIndexNumber
   */
  public String getClientIndexNumber() {
    return StringUtils.trimToEmpty(clientIndexNumber);
  }

  /**
   * @return the commentDescription
   */
  public String getCommentDescription() {
    return StringUtils.trimToEmpty(commentDescription);
  }

  /**
   * @return the commonFirstName
   */
  public String getCommonFirstName() {
    return StringUtils.trimToEmpty(commonFirstName);
  }

  /**
   * @return the commonLastName
   */
  public String getCommonLastName() {
    return StringUtils.trimToEmpty(commonLastName);
  }

  /**
   * @return the commonMiddleName
   */
  public String getCommonMiddleName() {
    return StringUtils.trimToEmpty(commonMiddleName);
  }

  /**
   * @return the confidentialityActionDate
   */
  public Date getConfidentialityActionDate() {
    return confidentialityActionDate;
  }

  /**
   * @return the confidentialityInEffectIndicator
   */
  public String getConfidentialityInEffectIndicator() {
    return StringUtils.trimToEmpty(confidentialityInEffectIndicator);
  }

  /**
   * @return the creationDate
   */
  public Date getCreationDate() {
    return creationDate;
  }

  /**
   * @return the currCaChildrenServIndicator
   */
  public String getCurrCaChildrenServIndicator() {
    return StringUtils.trimToEmpty(currCaChildrenServIndicator);
  }

  /**
   * @return the currentlyOtherDescription
   */
  public String getCurrentlyOtherDescription() {
    return StringUtils.trimToEmpty(currentlyOtherDescription);
  }

  /**
   * @return the currentlyRegionalCenterIndicator
   */
  public String getCurrentlyRegionalCenterIndicator() {
    return StringUtils.trimToEmpty(currentlyRegionalCenterIndicator);
  }

  /**
   * @return the deathDate
   */
  public Date getDeathDate() {
    return deathDate;
  }

  /**
   * @return the deathDateVerifiedIndicator
   */
  public String getDeathDateVerifiedIndicator() {
    return StringUtils.trimToEmpty(deathDateVerifiedIndicator);
  }

  /**
   * @return the deathPlace
   */
  public String getDeathPlace() {
    return StringUtils.trimToEmpty(deathPlace);
  }

  /**
   * @return the deathReasonText
   */
  public String getDeathReasonText() {
    return StringUtils.trimToEmpty(deathReasonText);
  }

  /**
   * @return the driverLicenseNumber
   */
  public String getDriverLicenseNumber() {
    return StringUtils.trimToEmpty(driverLicenseNumber);
  }

  /**
   * @return the driverLicenseStateCodeType
   */
  public Short getDriverLicenseStateCodeType() {
    return driverLicenseStateCodeType;
  }

  /**
   * @return the emailAddress
   */
  public String getEmailAddress() {
    return StringUtils.trimToEmpty(emailAddress);
  }

  /**
   * @return the estimatedDobCode
   */
  public String getEstimatedDobCode() {
    return StringUtils.trimToEmpty(estimatedDobCode);
  }

  /**
   * @return the ethUnableToDetReasonCode
   */
  public String getEthUnableToDetReasonCode() {
    return StringUtils.trimToEmpty(ethUnableToDetReasonCode);
  }

  /**
   * @return the fatherParentalRightTermDate
   */
  public Date getFatherParentalRightTermDate() {
    return fatherParentalRightTermDate;
  }

  /**
   * @return the genderCode
   */
  public String getGenderCode() {
    return StringUtils.trimToEmpty(genderCode);
  }

  /**
   * @return the healthSummaryText
   */
  public String getHealthSummaryText() {
    return StringUtils.trimToEmpty(healthSummaryText);
  }

  /**
   * @return the hispUnableToDetReasonCode
   */
  public String getHispUnableToDetReasonCode() {
    return StringUtils.trimToEmpty(hispUnableToDetReasonCode);
  }

  /**
   * @return the hispanicOriginCode
   */
  public String getHispanicOriginCode() {
    return StringUtils.trimToEmpty(hispanicOriginCode);
  }

  /**
   * @return the id
   */
  public String getId() {
    return StringUtils.trimToEmpty(id);
  }

  /**
   * @return the immigrationCountryCodeType
   */
  public Short getImmigrationCountryCodeType() {
    return immigrationCountryCodeType;
  }

  /**
   * @return the immigrationStatusType
   */
  public Short getImmigrationStatusType() {
    return immigrationStatusType;
  }

  /**
   * @return the incapacitatedParentCode
   */
  public String getIncapacitatedParentCode() {
    return StringUtils.trimToEmpty(incapacitatedParentCode);
  }

  /**
   * @return the individualHealthCarePlanIndicator
   */
  public String getIndividualHealthCarePlanIndicator() {
    return StringUtils.trimToEmpty(individualHealthCarePlanIndicator);
  }

  /**
   * @return the limitationOnScpHealthIndicator
   */
  public String getLimitationOnScpHealthIndicator() {
    return StringUtils.trimToEmpty(limitationOnScpHealthIndicator);
  }

  /**
   * @return the literateCode
   */
  public String getLiterateCode() {
    return StringUtils.trimToEmpty(literateCode);
  }

  /**
   * @return the maritalCohabitatnHstryIndicatorVar
   */
  public String getMaritalCohabitatnHstryIndicatorVar() {
    return StringUtils.trimToEmpty(maritalCohabitatnHstryIndicatorVar);
  }

  /**
   * @return the maritalStatusType
   */
  public Short getMaritalStatusType() {
    return maritalStatusType;
  }

  /**
   * @return the militaryStatusCode
   */
  public String getMilitaryStatusCode() {
    return StringUtils.trimToEmpty(militaryStatusCode);
  }

  /**
   * @return the motherParentalRightTermDate
   */
  public Date getMotherParentalRightTermDate() {
    return motherParentalRightTermDate;
  }

  /**
   * @return the namePrefixDescription
   */
  public String getNamePrefixDescription() {
    return StringUtils.trimToEmpty(namePrefixDescription);
  }

  /**
   * @return the nameType
   */
  public Short getNameType() {
    return nameType;
  }

  /**
   * @return the outstandingWarrantIndicator
   */
  public String getOutstandingWarrantIndicator() {
    return StringUtils.trimToEmpty(outstandingWarrantIndicator);
  }

  /**
   * @return the prevCaChildrenServIndicator
   */
  public String getPrevCaChildrenServIndicator() {
    return StringUtils.trimToEmpty(prevCaChildrenServIndicator);
  }

  /**
   * @return the prevOtherDescription
   */
  public String getPrevOtherDescription() {
    return StringUtils.trimToEmpty(prevOtherDescription);
  }

  /**
   * @return the prevRegionalCenterIndicator
   */
  public String getPrevRegionalCenterIndicator() {
    return StringUtils.trimToEmpty(prevRegionalCenterIndicator);
  }

  /**
   * @return the primaryEthnicityType
   */
  public Short getPrimaryEthnicityType() {
    return primaryEthnicityType;
  }

  /**
   * @return the primaryLanguageType
   */
  public Short getPrimaryLanguageType() {
    return primaryLanguageType;
  }

  /**
   * @return the religionType
   */
  public Short getReligionType() {
    return religionType;
  }

  /**
   * @return the secondaryLanguageType
   */
  public Short getSecondaryLanguageType() {
    return secondaryLanguageType;
  }

  /**
   * @return the sensitiveHlthInfoOnFileIndicator
   */
  public String getSensitiveHlthInfoOnFileIndicator() {
    return StringUtils.trimToEmpty(sensitiveHlthInfoOnFileIndicator);
  }

  /**
   * @return the sensitivityIndicator
   */
  public String getSensitivityIndicator() {
    return StringUtils.trimToEmpty(sensitivityIndicator);
  }

  /**
   * @return the soc158PlacementCode
   */
  public String getSoc158PlacementCode() {
    return StringUtils.trimToEmpty(soc158PlacementCode);
  }

  /**
   * @return the soc158SealedClientIndicator
   */
  public String getSoc158SealedClientIndicator() {
    return StringUtils.trimToEmpty(soc158SealedClientIndicator);
  }

  /**
   * @return the socialSecurityNumChangedCode
   */
  public String getSocialSecurityNumChangedCode() {
    return StringUtils.trimToEmpty(socialSecurityNumChangedCode);
  }

  /**
   * @return the socialSecurityNumber
   */
  public String getSocialSecurityNumber() {
    return StringUtils.trimToEmpty(socialSecurityNumber);
  }

  /**
   * @return the suffixTitleDescription
   */
  public String getSuffixTitleDescription() {
    return StringUtils.trimToEmpty(suffixTitleDescription);
  }

  /**
   * @return the tribalAncestryClientIndicatorVar
   */
  public String getTribalAncestryClientIndicatorVar() {
    return StringUtils.trimToEmpty(tribalAncestryClientIndicatorVar);
  }

  /**
   * @return the tribalMembrshpVerifctnIndicatorVar
   */
  public String getTribalMembrshpVerifctnIndicatorVar() {
    return StringUtils.trimToEmpty(tribalMembrshpVerifctnIndicatorVar);
  }

  /**
   * @return the unemployedParentCode
   */
  public String getUnemployedParentCode() {
    return StringUtils.trimToEmpty(unemployedParentCode);
  }

  /**
   * @return the zippyCreatedIndicator
   */
  public String getZippyCreatedIndicator() {
    return StringUtils.trimToEmpty(zippyCreatedIndicator);
  }

  // ==================
  // IPersonAware:
  // ==================

  @JsonIgnore
  @Override
  public String getMiddleName() {
    return this.commonMiddleName;
  }

  @JsonIgnore
  @Override
  public String getFirstName() {
    return this.commonFirstName;
  }

  @JsonIgnore
  @Override
  public String getLastName() {
    return this.commonLastName;
  }

  @JsonIgnore
  @Override
  public String getGender() {
    return this.genderCode;
  }

  @JsonIgnore
  @Override
  public String getSsn() {
    return this.socialSecurityNumber;
  }

  @JsonIgnore
  @Override
  public String getNameSuffix() {
    return this.suffixTitleDescription;
  }

  // =========================
  // IMultipleLanguagesAware:
  // =========================

  @Override
  @JsonIgnore
  public ApiLanguageAware[] getLanguages() {

    List<ApiLanguageAware> languages = new ArrayList<>();
    if (this.primaryLanguageType != null && this.primaryLanguageType != 0) {
      languages.add(new ApiLanguageAware() {
        @Override
        public Integer getLanguageSysId() {
          return primaryLanguageType.intValue();
        }
      });
    }

    if (this.secondaryLanguageType != null && this.secondaryLanguageType != 0) {
      LOGGER.info("secondaryLanguageType={}", secondaryLanguageType);
      languages.add(new ApiLanguageAware() {
        @Override
        public Integer getLanguageSysId() {
          return secondaryLanguageType.intValue();
        }
      });
    }

    return languages.toArray(new ApiLanguageAware[0]);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, false);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj, false);
  }

  @Override
  public String toString() {
    return "Client [adjudicatedDelinquentIndicator=" + adjudicatedDelinquentIndicator
        + ", adoptionStatusCode=" + adoptionStatusCode + ", alienRegistrationNumber="
        + alienRegistrationNumber + ", birthCity=" + birthCity + ", birthCountryCodeType="
        + birthCountryCodeType + ", birthDate=" + birthDate + ", birthFacilityName="
        + birthFacilityName + ", birthStateCodeType=" + birthStateCodeType
        + ", birthplaceVerifiedIndicator=" + birthplaceVerifiedIndicator
        + ", childClientIndicatorVar=" + childClientIndicatorVar + ", clientIndexNumber="
        + clientIndexNumber + ", commentDescription=" + commentDescription + ", commonFirstName="
        + commonFirstName + ", commonLastName=" + commonLastName + ", commonMiddleName="
        + commonMiddleName + ", confidentialityActionDate=" + confidentialityActionDate
        + ", confidentialityInEffectIndicator=" + confidentialityInEffectIndicator
        + ", creationDate=" + creationDate + ", currCaChildrenServIndicator="
        + currCaChildrenServIndicator + ", currentlyOtherDescription=" + currentlyOtherDescription
        + ", currentlyRegionalCenterIndicator=" + currentlyRegionalCenterIndicator + ", deathDate="
        + deathDate + ", deathDateVerifiedIndicator=" + deathDateVerifiedIndicator + ", deathPlace="
        + deathPlace + ", deathReasonText=" + deathReasonText + ", driverLicenseNumber="
        + driverLicenseNumber + ", driverLicenseStateCodeType=" + driverLicenseStateCodeType
        + ", emailAddress=" + emailAddress + ", estimatedDobCode=" + estimatedDobCode
        + ", ethUnableToDetReasonCode=" + ethUnableToDetReasonCode
        + ", fatherParentalRightTermDate=" + fatherParentalRightTermDate + ", genderCode="
        + genderCode + ", healthSummaryText=" + healthSummaryText + ", hispUnableToDetReasonCode="
        + hispUnableToDetReasonCode + ", hispanicOriginCode=" + hispanicOriginCode + ", id=" + id
        + ", immigrationCountryCodeType=" + immigrationCountryCodeType + ", immigrationStatusType="
        + immigrationStatusType + ", incapacitatedParentCode=" + incapacitatedParentCode
        + ", individualHealthCarePlanIndicator=" + individualHealthCarePlanIndicator
        + ", limitationOnScpHealthIndicator=" + limitationOnScpHealthIndicator + ", literateCode="
        + literateCode + ", maritalCohabitatnHstryIndicatorVar="
        + maritalCohabitatnHstryIndicatorVar + ", maritalStatusType=" + maritalStatusType
        + ", militaryStatusCode=" + militaryStatusCode + ", motherParentalRightTermDate="
        + motherParentalRightTermDate + ", namePrefixDescription=" + namePrefixDescription
        + ", nameType=" + nameType + ", outstandingWarrantIndicator=" + outstandingWarrantIndicator
        + ", prevCaChildrenServIndicator=" + prevCaChildrenServIndicator + ", prevOtherDescription="
        + prevOtherDescription + ", prevRegionalCenterIndicator=" + prevRegionalCenterIndicator
        + ", primaryEthnicityType=" + primaryEthnicityType + ", primaryLanguageType="
        + primaryLanguageType + ", religionType=" + religionType + ", secondaryLanguageType="
        + secondaryLanguageType + ", sensitiveHlthInfoOnFileIndicator="
        + sensitiveHlthInfoOnFileIndicator + ", sensitivityIndicator=" + sensitivityIndicator
        + ", soc158PlacementCode=" + soc158PlacementCode + ", soc158SealedClientIndicator="
        + soc158SealedClientIndicator + ", socialSecurityNumChangedCode="
        + socialSecurityNumChangedCode + ", socialSecurityNumber=" + socialSecurityNumber
        + ", suffixTitleDescription=" + suffixTitleDescription
        + ", tribalAncestryClientIndicatorVar=" + tribalAncestryClientIndicatorVar
        + ", tribalMembrshpVerifctnIndicatorVar=" + tribalMembrshpVerifctnIndicatorVar
        + ", unemployedParentCode=" + unemployedParentCode + ", zippyCreatedIndicator="
        + zippyCreatedIndicator + "]";
  }

}

