package gov.ca.cwds.data.persistence.ns;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
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
 * Implements {@link ApiGroupNormalizer} and converts to {@link IntakeScreening}.
 * </p>
 * 
 * @author CWDS API Team
 */
@Entity
@Table(name = "VW_SCREENING_HISTORY")
@NamedNativeQueries({
    @NamedNativeQuery(name = "gov.ca.cwds.data.persistence.ns.EsIntakeScreening.findAll",
        query = "SELECT vw.* FROM {h-schema}VW_SCREENING_HISTORY vw "
            + "WHERE vw.started_at is not null ORDER BY vw.SCREENING_ID FOR READ ONLY",
        resultClass = EsIntakeScreening.class, readOnly = true),
    @NamedNativeQuery(name = "gov.ca.cwds.data.persistence.ns.EsIntakeScreening.findBucketRange",
        query = "SELECT vw.* FROM {h-schema}VW_SCREENING_HISTORY vw "
            + "WHERE vw.SCREENING_ID BETWEEN :min_id AND :max_id "
            + "ORDER BY vw.SCREENING_ID FOR READ ONLY",
        resultClass = EsIntakeScreening.class, readOnly = true),
    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.ns.EsIntakeScreening.findAllUpdatedAfter",
        query = "SELECT vw.* FROM {h-schema}VW_SCREENING_HISTORY vw "
            + "WHERE vw.LAST_CHG > CAST(:after AS TIMESTAMP) "
            + "ORDER BY vw.SCREENING_ID FOR READ ONLY ",
        resultClass = EsIntakeScreening.class, readOnly = true)})
public class EsIntakeScreening implements PersistentObject, ApiGroupNormalizer<IntakeScreening> {

  private static final Logger LOGGER = LogManager.getLogger(EsIntakeScreening.class);

  /**
   * Default.
   */
  private static final long serialVersionUID = 1L;

  @Type(type = "timestamp")
  @Column(name = "LAST_CHG", updatable = false)
  private Date lastChange;

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
  // @Type(type = "timestamp")
  private Date startedAt;

  @Id
  @Column(name = "ENDED_AT")
  // @Type(type = "timestamp")
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
  private String participantId;

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

  @Column(name = "PERSON_LEGACY_ID")
  private String personLegacyId;

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
  public Class<IntakeScreening> getReductionClass() {
    return IntakeScreening.class;
  }

  /**
   * Populate a participant object or create a new one.
   * 
   * @param p participant object to populate or null to create a new one
   * @return populated participant object
   */
  protected IntakeParticipant fillParticipant(IntakeParticipant p) {
    IntakeParticipant ret = p == null ? new IntakeParticipant() : p;
    ret.setBirthDate(birthDt);
    ret.setFirstName(firstName);
    ret.setGender(gender);
    ret.setSsn(ssn);
    ret.setLastName(lastName);
    ret.setLegacyId(personLegacyId);
    ret.setId(participantId);
    return ret;
  }

  /**
   * Create a new participant and populate it from this object.
   * 
   * @return a new participant, populated from this object
   */
  protected IntakeParticipant fillParticipant() {
    return fillParticipant(null);
  }

  @Override
  public void reduce(Map<Object, IntakeScreening> map) {
    IntakeScreening s;

    if (!map.containsKey(screeningId)) {
      s = new IntakeScreening();
      s.setAdditionalInformation(additionalInformation);
      s.setAssignee(assignee);
      s.setCommunicationMethod(communicationMethod);
      s.setEndedAt(endedAt);
      s.setId(screeningId);
      s.setIncidentCounty(incidentCounty);
      s.setIncidentDate(incidentDate);
      s.setLocationType(locationType);
      s.setReference(reference);
      s.setReportNarrative(reportNarrative);
      s.setScreeningDecision(screeningDecision);
      s.setScreeningDecisionDetail(screeningDecisionDetail);
      s.setScreeningName(screeningName);
      map.put(s.getId(), s);

      final IntakeParticipant worker = s.getSocialWorker();
      worker.setLastName(assignee);
    } else {
      s = map.get(screeningId);
    }

    IntakeParticipant p;
    if (s.getParticipants().containsKey(participantId)) {
      p = s.getParticipants().get(participantId);
    } else {
      p = fillParticipant();
      s.addParticipant(p);
      p.addScreening(s);

      for (String role : roles) {
        s.addParticipantRole(p.getIntakeId(), role);
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

    if (flgPerpetrator) {
      alg.setPerpetrator(p);
    }

    if (flgVictim) {
      alg.setVictim(p);
    }

    if (flgReporter) {
      fillParticipant(s.getReporter());
    }

    if (StringUtils.isNotBlank(addressId)) {
      final ElasticSearchPersonAddress addr = new ElasticSearchPersonAddress();
      addr.setId(addressId);
      addr.setCity(city);
      addr.setState(state);
      // addr.setStateName(stateName); // Not found in legacy.
      addr.setStreetAddress(streetAddress);
      addr.setType(addressType);
      addr.setZip(zip);
      p.addAddress(addr);
    }

    if (StringUtils.isNotBlank(phoneNumberId)) {
      final ElasticSearchPersonPhone ph = new ElasticSearchPersonPhone();
      ph.setId(phoneNumberId);
      ph.setPhoneNumber(phoneNumber);

      if (StringUtils.isNotBlank(phoneType)) {
        ph.setPhoneType(PhoneType.valueOf(phoneType));
      }

      p.addPhone(ph);
    }

  }

  @Override
  public Object getGroupKey() {
    return this.screeningId;
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

}
