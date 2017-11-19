package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.NamedNativeQuery;

import gov.ca.cwds.data.persistence.cms.rep.CmsReplicationOperation;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedAddress;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedClient;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedClientAddress;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.neutron.util.NeutronDateUtils;

/**
 * Entity bean for view VW_LST_CLIENT_ADDRESS.
 * 
 * <p>
 * Implements {@link ApiGroupNormalizer} and converts to {@link ReplicatedClient}.
 * </p>
 * 
 * NOTE: #145240149: find ALL client/address recs affected by changes.
 *
 * REFRESH TABLE cwsrsq.ES_REL_CLN_RELT_CLIENT ;
 * 
 * @author CWDS API Team
 */
@Entity
@Table(name = "VW_LST_CLIENT_ADDRESS")
@NamedNativeQuery(name = "gov.ca.cwds.data.persistence.cms.EsClientAddress.findAllUpdatedAfter",
    query = "SELECT x.* FROM {h-schema}VW_LST_CLIENT_ADDRESS x WHERE x.CLT_IDENTIFIER IN ( "
        + "SELECT x1.CLT_IDENTIFIER FROM {h-schema}VW_LST_CLIENT_ADDRESS x1 "
        + "WHERE x1.LAST_CHG > :after " + ") ORDER BY CLT_IDENTIFIER FOR READ ONLY WITH UR ",
    resultClass = EsClientAddress.class, readOnly = true)

@NamedNativeQuery(
    name = "gov.ca.cwds.data.persistence.cms.EsClientAddress.findAllUpdatedAfterWithUnlimitedAccess",
    query = "SELECT x.* FROM {h-schema}VW_LST_CLIENT_ADDRESS x WHERE x.CLT_IDENTIFIER IN ( "
        + "SELECT x1.CLT_IDENTIFIER FROM {h-schema}VW_LST_CLIENT_ADDRESS x1 "
        + "WHERE x1.LAST_CHG > :after "
        + ") AND x.CLT_SENSTV_IND = 'N' ORDER BY CLT_IDENTIFIER FOR READ ONLY WITH UR",
    resultClass = EsClientAddress.class, readOnly = true)

@NamedNativeQuery(
    name = "gov.ca.cwds.data.persistence.cms.EsClientAddress.findAllUpdatedAfterWithLimitedAccess",
    query = "SELECT x.* FROM {h-schema}VW_LST_CLIENT_ADDRESS x WHERE x.CLT_IDENTIFIER IN ( "
        + "SELECT x1.CLT_IDENTIFIER FROM {h-schema}VW_LST_CLIENT_ADDRESS x1 "
        + "WHERE x1.LAST_CHG > :after "
        + ") AND x.CLT_SENSTV_IND != 'N' ORDER BY CLT_IDENTIFIER FOR READ ONLY WITH UR ",
    resultClass = EsClientAddress.class, readOnly = true)
public class EsClientAddress extends BaseEsClient
    implements Comparable<EsClientAddress>, Comparator<EsClientAddress> {

  private static final long serialVersionUID = 1L;

  /**
   * Build an EsClientAddress from the incoming ResultSet.
   * 
   * @param rs incoming tuple
   * @return a populated EsClientAddress
   * @throws SQLException if unable to convert types or stream breaks, etc.
   */
  public static EsClientAddress extract(final ResultSet rs) throws SQLException {
    final EsClientAddress ret = new EsClientAddress();
    BaseEsClient.extract(ret, rs);
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

    map.put(ret.getId(), ret);
    return ret;
  }

  public Date getCltFatherParentalRightTermDate() {
    return NeutronDateUtils.freshDate(cltFatherParentalRightTermDate);
  }

  public void setClaId(String claId) {
    this.claId = claId;
  }

  public void setAdrReplicationOperation(CmsReplicationOperation adrReplicationOperation) {
    this.adrReplicationOperation = adrReplicationOperation;
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
  public int compare(EsClientAddress o1, EsClientAddress o2) {
    return o1.getCltId().compareTo(o2.getCltId());
  }

  @Override
  public int compareTo(EsClientAddress o) {
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

}
