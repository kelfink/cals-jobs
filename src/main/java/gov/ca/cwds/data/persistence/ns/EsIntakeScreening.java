package gov.ca.cwds.data.persistence.ns;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.data.es.ElasticSearchPersonAddress;
import gov.ca.cwds.data.es.ElasticSearchPersonPhone;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.data.std.ApiPhoneAware.PhoneType;

/**
 * Entity bean for PostgreSQL view, VW_SCREENING_HISTORY.
 * 
 * <p>
 * Implements {@link ApiGroupNormalizer} and converts to {@link IntakeParticipant}.
 * </p>
 * 
 * @author CWDS API Team
 */
@Entity
@Table(name = "VW_SCREENING_HISTORY")
@NamedNativeQueries({
    @NamedNativeQuery(name = "gov.ca.cwds.data.persistence.ns.EsIntakeScreening.findAll",
        query = "SELECT p.\"id\" AS ns_partc_id, p.legacy_id AS cms_legacy_id, vw.* "
            + "FROM {h-schema}VW_SCREENING_HISTORY vw "
            + "JOIN participants p ON p.screening_id = vw.screening_id "
            + "WHERE p.legacy_id IS NOT NULL "
            + "ORDER BY cms_legacy_id, screening_id, ns_partc_id, person_legacy_id, participant_id "
            + "FOR READ ONLY",
        resultClass = EsIntakeScreening.class, readOnly = true),

    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.ns.EsIntakeScreening.findAllUpdatedAfter",
        query = "SELECT p.\"id\" AS ns_partc_id, p.legacy_id AS cms_legacy_id, vw.* "
            + "FROM {h-schema}VW_SCREENING_HISTORY vw "
            + "JOIN PARTICIPANTS p ON p.screening_id = vw.screening_id "
            + "WHERE vw.participant_id IN ( SELECT DISTINCT vw1.participant_id "
            + " FROM {h-schema}VW_SCREENING_HISTORY vw1 WHERE vw1.last_chg > CAST(:after AS TIMESTAMP) "
            + ") AND p.legacy_id IS NOT NULL "
            + "ORDER BY cms_legacy_id, screening_id, ns_partc_id, person_legacy_id, participant_id "
            + "FOR READ ONLY",
        resultClass = EsIntakeScreening.class, readOnly = true),

    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.ns.EsIntakeScreening.findAllUpdatedAfterWithUnlimitedAccess",
        query = "SELECT p.\"id\" AS ns_partc_id, p.legacy_id AS cms_legacy_id, vw.* "
            + "FROM {h-schema}VW_SCREENING_HISTORY vw "
            + "JOIN PARTICIPANTS p ON p.screening_id = vw.screening_id "
            + "WHERE vw.participant_id IN ( SELECT DISTINCT vw1.participant_id "
            + " FROM {h-schema}VW_SCREENING_HISTORY vw1 WHERE vw1.last_chg > CAST(:after AS TIMESTAMP) "
            + ") AND p.legacy_id IS NOT NULL "
            + "ORDER BY cms_legacy_id, screening_id, ns_partc_id, person_legacy_id, participant_id "
            + "FOR READ ONLY",
        resultClass = EsIntakeScreening.class, readOnly = true)})

public class EsIntakeScreening implements PersistentObject, ApiGroupNormalizer<IntakeParticipant> {

  private static final Logger LOGGER = LoggerFactory.getLogger(EsIntakeScreening.class);

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  @Type(type = "timestamp")
  @Column(name = "LAST_CHG", updatable = false)
  private Date lastChange;

  // ================
  // KEYS:
  // ================

  @Id
  @Column(name = "NS_PARTC_ID")
  private String thisParticipantId;

  @Id
  @Column(name = "CMS_LEGACY_ID")
  private String thisLegacyId;

  // ================
  // SCREENING:
  // ================

  @Id
  @Column(name = "SCREENING_ID")
  private String screeningId;

  @Id
  @Column(name = "REFERENCE")
  private String reference;

  @Id
  @Column(name = "STARTED_AT")
  @Type(type = "date")
  private Date startedAt;

  @Id
  @Column(name = "ENDED_AT")
  @Type(type = "date")
  private Date endedAt;

  @Column(name = "REFERRAL_ID")
  private String referralId;

  @Column(name = "INCIDENT_DATE")
  private String incidentDate;

  @Column(name = "LOCATION_TYPE")
  private String locationType;

  @Column(name = "COMMUNICATION_METHOD")
  private String communicationMethod;

  @Column(name = "SCREENING_NAME")
  private String screeningName;

  @Column(name = "SCREENING_DECISION")
  private String screeningDecision;

  @Column(name = "INCIDENT_COUNTY")
  private String incidentCounty;

  @Column(name = "REPORT_NARRATIVE")
  private String reportNarrative;

  @Column(name = "ASSIGNEE")
  private String assignee;

  @Column(name = "ADDITIONAL_INFORMATION")
  private String additionalInformation;

  @Column(name = "SCREENING_DECISION_DETAIL")
  private String screeningDecisionDetail;

  // ==============
  // PARTICIPANT:
  // ==============

  @Id
  @Column(name = "PARTICIPANT_ID")
  private String otherParticipantId;

  @Column(name = "PERSON_LEGACY_ID")
  private String otherLegacyId;

  @Column(name = "BIRTH_DT")
  private Date birthDt;

  @Column(name = "FIRST_NAME")
  private String firstName;

  @Column(name = "LAST_NAME")
  private String lastName;

  @Column(name = "GENDER")
  private String gender;

  @Column(name = "SSN")
  private String ssn;

  @Column(name = "ROLES")
  @Type(type = "gov.ca.cwds.jobs.util.jdbc.StringArrayType")
  private String[] roles;

  @Column(name = "IS_REPORTER")
  @ColumnTransformer(read = "IS_REPORTER IS NOT NULL")
  private boolean flgReporter;

  @Column(name = "IS_PERPETRATOR")
  @ColumnTransformer(read = "IS_PERPETRATOR IS NOT NULL")
  private boolean flgPerpetrator;

  @Column(name = "IS_VICTIM")
  @ColumnTransformer(read = "IS_VICTIM IS NOT NULL")
  private boolean flgVictim;

  // =============
  // ALLEGATION:
  // =============

  @Id
  @Column(name = "ALLEGATION_ID")
  private String allegationId;

  @Column(name = "ALLEGATION_TYPES")
  private String allegationTypes;

  // =============
  // ADDRESS:
  // =============

  @Column(name = "ADDRESS_ID")
  private String addressId;

  @Column(name = "ADDRESS_TYPE")
  private String addressType;

  @Column(name = "STREET_ADDRESS")
  private String streetAddress;

  @Column(name = "CITY")
  private String city;

  @Column(name = "STATE")
  private String state;

  @Column(name = "ZIP")
  private String zip;

  // =============
  // PHONE:
  // =============

  @Column(name = "PHONE_NUMBER_ID")
  private String phoneNumberId;

  @Column(name = "PHONE_NUMBER")
  private String phoneNumber;

  @Column(name = "PHONE_TYPE")
  private String phoneType;

  // =============
  // REDUCE:
  // =============

  @Override
  public Class<IntakeParticipant> getNormalizationClass() {
    return IntakeParticipant.class;
  }

  /**
   * Populate a participant object or create a new one.
   * 
   * @param p participant object to populate or null to create a new one
   * @param isOther is this another participant? (i.e., not "this" participant)
   * @return populated participant object
   */
  protected IntakeParticipant fillParticipant(IntakeParticipant p, boolean isOther) {
    IntakeParticipant ret = p == null ? new IntakeParticipant() : p;
    ret.setBirthDate(birthDt);
    ret.setFirstName(firstName);
    ret.setGender(gender);
    ret.setSsn(ssn);
    ret.setLastName(lastName);

    if (isOther) {
      ret.setId(otherParticipantId);
      ret.setLegacyId(otherLegacyId);
      //
      // TODO - update when legacy last updated and table name are available
      //
      // ret.setLegacyLastUpdated(otherLegacyLastUpdated);
      // ret.setLegacyTable(otherLegacyTable);
    } else {
      ret.setId(thisParticipantId);
      ret.setLegacyId(thisLegacyId);
      //
      // TODO - update when legacy last updated and table name are available
      //
      // ret.setLegacyLastUpdated(thisLegacyLastUpdated);
      // ret.setLegacyTable(thisLegacyTable);
    }

    return ret;
  }

  /**
   * Create a new participant and populate it from this object.
   * 
   * @param isOther is this another participant? (i.e., not "this" participant)
   * @return a new participant, populated from this object
   */
  protected IntakeParticipant fillParticipant(boolean isOther) {
    return fillParticipant(null, isOther);
  }

  /**
   * Populate a screening object or create a new one.
   * 
   * @param s screening object to populate or null to create a new one
   * @return populated screening object
   */
  protected IntakeScreening fillScreening(IntakeScreening s) {
    IntakeScreening ret = s == null ? new IntakeScreening() : s;

    ret.setId(screeningId);
    ret.setReferralId(referralId);
    ret.setAdditionalInformation(additionalInformation);
    ret.setAssignee(assignee);
    ret.setCommunicationMethod(communicationMethod);
    ret.setIncidentCounty(incidentCounty);
    ret.setIncidentDate(incidentDate);
    ret.setLocationType(locationType);
    ret.setReference(reference);
    ret.setReportNarrative(reportNarrative);
    ret.setScreeningDecision(screeningDecision);
    ret.setScreeningDecisionDetail(screeningDecisionDetail);
    ret.setScreeningName(screeningName);

    if (endedAt != null) {
      ret.setEndedAt(new Date(endedAt.getTime()));
    }

    if (startedAt != null) {
      ret.setStartedAt(new Date(startedAt.getTime()));
    }

    return ret;
  }

  /**
   * @return a new Screening object
   */
  protected IntakeScreening fillScreening() {
    return fillScreening(null);
  }

  /**
   * Iterate screenings from the perspective of "this" participant. Separate "this" participant from
   * "other" participant. This job stores person documents in ES, not disconnected or independent
   * screening documents.
   */
  @Override
  public IntakeParticipant normalize(Map<Object, IntakeParticipant> map) {
    final String thisPartcId = getNormalizationGroupKey();

    IntakeParticipant ret;

    if (map.containsKey(thisPartcId)) {
      ret = map.get(thisPartcId);
    } else {
      ret = fillParticipant(false);
      map.put(thisPartcId, ret);
    }

    try {
      IntakeScreening s;
      final Map<String, IntakeScreening> mapScreenings = ret.getScreenings();

      if (!mapScreenings.containsKey(screeningId)) {
        s = fillScreening();
        mapScreenings.put(s.getId(), s); // iterating screenings for *this* participant.

        final IntakeParticipant worker = s.getSocialWorker();
        worker.setLastName(assignee);
      } else {
        s = mapScreenings.get(screeningId);
      }

      // Other participant.
      final boolean hasOtherPartc = isNotBlank(otherParticipantId);
      IntakeParticipant otherPartc = null;

      if (hasOtherPartc) {
        if (s.getParticipants().containsKey(otherParticipantId)) {
          otherPartc = s.getParticipants().get(otherParticipantId);
        } else {
          otherPartc = fillParticipant(true);
          s.addParticipant(otherPartc);

          if (!ArrayUtils.isEmpty(roles)) {
            for (String role : roles) {
              s.addParticipantRole(otherPartc.getId(), role);
            }
          }
        }
      }

      IntakeAllegation alg;
      if (s.getAllegations().containsKey(allegationId)) {
        alg = s.getAllegations().get(allegationId);
      } else {
        alg = new IntakeAllegation();
        alg.setId(allegationId);
        s.addAllegation(alg);
      }

      if (hasOtherPartc) {
        if (flgPerpetrator) {
          alg.setPerpetrator(otherPartc);
        }

        if (flgVictim) {
          alg.setVictim(otherPartc);
        }

        if (flgReporter) {
          fillParticipant(s.getReporter(), otherPartc.getId().equals(thisPartcId));
        }

        if (isNotBlank(addressId)) {
          final ElasticSearchPersonAddress addr = new ElasticSearchPersonAddress();
          addr.setId(addressId);
          addr.setCity(city);
          addr.setState(state);

          // Synthetic, composite field, "state_name", not found in legacy.
          addr.setStreetAddress(streetAddress);
          addr.setType(addressType);
          addr.setZip(zip);
          otherPartc.addAddress(addr);
        }

        if (isNotBlank(phoneNumberId)) {
          final ElasticSearchPersonPhone ph = new ElasticSearchPersonPhone();
          ph.setId(phoneNumberId);
          ph.setPhoneNumber(phoneNumber);

          if (isNotBlank(phoneType)) {
            ph.setPhoneType(PhoneType.valueOf(phoneType));
          }

          otherPartc.addPhone(ph);
        }
      }
    } catch (Exception e) {
      // Log the offending record.
      LOGGER.error("OOPS! {}", this);
      throw e;
    }

    return ret;
  }

  @Override
  public String getNormalizationGroupKey() {
    return isNotBlank(thisLegacyId) ? thisLegacyId : thisParticipantId;
  }

  /**
   * This view lacks a proper unique key, but a combination of several fields comes close.
   * <ul>
   * <li>"Cook": convert String parameter to strong type</li>
   * <li>"Uncook": convert strong type parameter to String</li>
   * </ul>
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

  /**
   * Getter for composite "last change", the latest time that any associated record was created or
   * updated.
   * 
   * @return last change date
   */
  public Date getLastChange() {
    return lastChange;
  }

  /**
   * Setter for composite "last change", the latest time that any associated record was created or
   * updated.
   * 
   * @param lastChange last change date
   */
  public void setLastChange(Date lastChange) {
    this.lastChange = lastChange;
  }

  @Override
  public String toString() {
    return "EsIntakeScreening [lastChange=" + lastChange + ", thisParticipantId="
        + thisParticipantId + ", thisLegacyId=" + thisLegacyId + ", screeningId=" + screeningId
        + ", reference=" + reference + ", startedAt=" + startedAt + ", endedAt=" + endedAt
        + ", incidentDate=" + incidentDate + ", locationType=" + locationType
        + ", communicationMethod=" + communicationMethod + ", screeningName=" + screeningName
        + ", screeningDecision=" + screeningDecision + ", incidentCounty=" + incidentCounty
        + ", reportNarrative=" + reportNarrative + ", assignee=" + assignee
        + ", additionalInformation=" + additionalInformation + ", screeningDecisionDetail="
        + screeningDecisionDetail + ", otherParticipantId=" + otherParticipantId
        + ", otherLegacyId=" + otherLegacyId + ", birthDt=" + birthDt + ", firstName=" + firstName
        + ", lastName=" + lastName + ", gender=" + gender + ", ssn=" + ssn + ", roles="
        + Arrays.toString(roles) + ", flgReporter=" + flgReporter + ", flgPerpetrator="
        + flgPerpetrator + ", flgVictim=" + flgVictim + ", allegationId=" + allegationId
        + ", allegationTypes=" + allegationTypes + ", addressId=" + addressId + ", addressType="
        + addressType + ", streetAddress=" + streetAddress + ", city=" + city + ", state=" + state
        + ", zip=" + zip + ", phoneNumberId=" + phoneNumberId + ", phoneNumber=" + phoneNumber
        + ", phoneType=" + phoneType + "]";
  }

  public String getThisParticipantId() {
    return thisParticipantId;
  }

  public void setThisParticipantId(String thisParticipantId) {
    this.thisParticipantId = thisParticipantId;
  }

  public String getThisLegacyId() {
    return thisLegacyId;
  }

  public void setThisLegacyId(String thisLegacyId) {
    this.thisLegacyId = thisLegacyId;
  }

  public String getScreeningId() {
    return screeningId;
  }

  public void setScreeningId(String screeningId) {
    this.screeningId = screeningId;
  }

  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public Date getStartedAt() {
    return startedAt;
  }

  public void setStartedAt(Date startedAt) {
    this.startedAt = startedAt;
  }

  public Date getEndedAt() {
    return endedAt;
  }

  public void setEndedAt(Date endedAt) {
    this.endedAt = endedAt;
  }

  public String getReferralId() {
    return referralId;
  }

  public void setReferralId(String referralId) {
    this.referralId = referralId;
  }

  public String getIncidentDate() {
    return incidentDate;
  }

  public void setIncidentDate(String incidentDate) {
    this.incidentDate = incidentDate;
  }

  public String getLocationType() {
    return locationType;
  }

  public void setLocationType(String locationType) {
    this.locationType = locationType;
  }

  public String getCommunicationMethod() {
    return communicationMethod;
  }

  public void setCommunicationMethod(String communicationMethod) {
    this.communicationMethod = communicationMethod;
  }

  public String getScreeningName() {
    return screeningName;
  }

  public void setScreeningName(String screeningName) {
    this.screeningName = screeningName;
  }

  public String getScreeningDecision() {
    return screeningDecision;
  }

  public void setScreeningDecision(String screeningDecision) {
    this.screeningDecision = screeningDecision;
  }

  public String getIncidentCounty() {
    return incidentCounty;
  }

  public void setIncidentCounty(String incidentCounty) {
    this.incidentCounty = incidentCounty;
  }

  public String getReportNarrative() {
    return reportNarrative;
  }

  public void setReportNarrative(String reportNarrative) {
    this.reportNarrative = reportNarrative;
  }

  public String getAssignee() {
    return assignee;
  }

  public void setAssignee(String assignee) {
    this.assignee = assignee;
  }

  public String getAdditionalInformation() {
    return additionalInformation;
  }

  public void setAdditionalInformation(String additionalInformation) {
    this.additionalInformation = additionalInformation;
  }

  public String getScreeningDecisionDetail() {
    return screeningDecisionDetail;
  }

  public void setScreeningDecisionDetail(String screeningDecisionDetail) {
    this.screeningDecisionDetail = screeningDecisionDetail;
  }

  public String getOtherParticipantId() {
    return otherParticipantId;
  }

  public void setOtherParticipantId(String otherParticipantId) {
    this.otherParticipantId = otherParticipantId;
  }

  public String getOtherLegacyId() {
    return otherLegacyId;
  }

  public void setOtherLegacyId(String otherLegacyId) {
    this.otherLegacyId = otherLegacyId;
  }

  public Date getBirthDt() {
    return birthDt;
  }

  public void setBirthDt(Date birthDt) {
    this.birthDt = birthDt;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getSsn() {
    return ssn;
  }

  public void setSsn(String ssn) {
    this.ssn = ssn;
  }

  public String[] getRoles() {
    return roles;
  }

  public void setRoles(String[] roles) {
    this.roles = roles;
  }

  public boolean isFlgReporter() {
    return flgReporter;
  }

  public void setFlgReporter(boolean flgReporter) {
    this.flgReporter = flgReporter;
  }

  public boolean isFlgPerpetrator() {
    return flgPerpetrator;
  }

  public void setFlgPerpetrator(boolean flgPerpetrator) {
    this.flgPerpetrator = flgPerpetrator;
  }

  public boolean isFlgVictim() {
    return flgVictim;
  }

  public void setFlgVictim(boolean flgVictim) {
    this.flgVictim = flgVictim;
  }

  public String getAllegationId() {
    return allegationId;
  }

  public void setAllegationId(String allegationId) {
    this.allegationId = allegationId;
  }

  public String getAllegationTypes() {
    return allegationTypes;
  }

  public void setAllegationTypes(String allegationTypes) {
    this.allegationTypes = allegationTypes;
  }

  public String getAddressId() {
    return addressId;
  }

  public void setAddressId(String addressId) {
    this.addressId = addressId;
  }

  public String getAddressType() {
    return addressType;
  }

  public void setAddressType(String addressType) {
    this.addressType = addressType;
  }

  public String getStreetAddress() {
    return streetAddress;
  }

  public void setStreetAddress(String streetAddress) {
    this.streetAddress = streetAddress;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  public String getPhoneNumberId() {
    return phoneNumberId;
  }

  public void setPhoneNumberId(String phoneNumberId) {
    this.phoneNumberId = phoneNumberId;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getPhoneType() {
    return phoneType;
  }

  public void setPhoneType(String phoneType) {
    this.phoneType = phoneType;
  }

}
