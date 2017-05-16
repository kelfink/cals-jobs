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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.Type;

import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonAddress;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonPhone;
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
        query = "SELECT p.\"id\" as ns_partc_id, p.person_id as cms_legacy_id, vw.* "
            + "FROM {h-schema}VW_SCREENING_HISTORY vw "
            + "JOIN participants p ON p.screening_id = vw.screening_id "
            + "ORDER BY ns_partc_id, cms_legacy_id, screening_id, person_legacy_id, participant_id "
            + "FOR READ ONLY",
        resultClass = EsIntakeScreening.class, readOnly = true),
    @NamedNativeQuery(name = "gov.ca.cwds.data.persistence.ns.EsIntakeScreening.findBucketRange",
        query = "SELECT vw.* FROM {h-schema}VW_SCREENING_HISTORY vw "
            + "WHERE vw.SCREENING_ID BETWEEN :min_id AND :max_id "
            + "ORDER BY vw.SCREENING_ID FOR READ ONLY",
        resultClass = EsIntakeScreening.class, readOnly = true),
    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.ns.EsIntakeScreening.findAllUpdatedAfter",
        query = "SELECT p.\"id\" as ns_partc_id, p.person_id as cms_legacy_id, vw.* "
            + "FROM {h-schema}VW_SCREENING_HISTORY vw "
            + "JOIN participants p ON p.screening_id = vw.screening_id "
            + "WHERE vw.LAST_CHG > CAST(:after AS TIMESTAMP) "
            + "ORDER BY ns_partc_id, cms_legacy_id, screening_id, person_legacy_id, participant_id "
            + "FOR READ ONLY",
        resultClass = EsIntakeScreening.class, readOnly = true)})
public class EsIntakeScreening implements PersistentObject, ApiGroupNormalizer<IntakeParticipant> {

  private static final Logger LOGGER = LogManager.getLogger(EsIntakeScreening.class);

  /**
   * Default.
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
  @Type(type = "gov.ca.cwds.data.persistence.ns.StringArrayType")
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
  public Class<IntakeParticipant> getReductionClass() {
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
    } else {
      ret.setId(thisParticipantId);
      ret.setLegacyId(thisLegacyId);
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

  @Override
  public void reduce(Map<Object, IntakeParticipant> map) {
    final String thisPartcId = (String) getGroupKey();

    // Iterate screenings from the perspective of "this" participant.
    // Separate "this" participant from "other" participant.
    // This job stores person documents, not independent screening documents.
    IntakeParticipant thisPartc;

    if (map.containsKey(thisPartcId)) {
      thisPartc = map.get(thisPartcId);
    } else {
      thisPartc = fillParticipant(false);
      map.put(thisPartcId, thisPartc);
    }

    LOGGER.debug("reduce: this partc id: {}, screening id: {}", thisPartcId, screeningId);

    try {
      IntakeScreening s;
      final Map<String, IntakeScreening> mapScreenings = thisPartc.getScreenings();

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

          if (roles != null && roles.length > 0) {
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

          // Synthetic field, not found in legacy. Translate state code.
          // addr.setStateName(stateName);

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
    } catch (RuntimeException e) {
      // Log the offending record.
      LOGGER.error("OOPS! {}", this);
      throw e;
    }

    LOGGER.debug("END");
  }

  @Override
  public Object getGroupKey() {
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

}
