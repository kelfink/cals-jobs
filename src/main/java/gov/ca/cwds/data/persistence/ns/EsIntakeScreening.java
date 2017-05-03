package gov.ca.cwds.data.persistence.ns;

import java.io.Serializable;
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

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.rest.api.domain.DomainChef;

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
  private String startedAt;

  @Id
  @Column(name = "ENDED_AT")
  private String endedAt;

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
  private String birthDt;

  @Column(name = "FIRST_NAME")
  private String firstName;

  @Column(name = "LAST_NAME")
  private String lastName;

  @Column(name = "GENDER")
  private String gender;

  @Column(name = "SSN")
  private String ssn;

  @Column(name = "LEGACY_ID")
  private String legacyId;

  @Column(name = "ROLES")
  private String roles;

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

  @Override
  public void reduce(Map<Object, IntakeScreening> map) {
    final boolean isScreeningAdded = map.containsKey(this.screeningId);
    IntakeScreening ret = isScreeningAdded ? map.get(this.screeningId) : new IntakeScreening();

    if (!isScreeningAdded) {
      // Core screening attributes.
      ret.setAdditionalInformation(additionalInformation);
      ret.setAssignee(assignee);
      ret.setCommunicationMethod(communicationMethod);
      ret.setEndedAt(endedAt);
      ret.setId(screeningId);
      ret.setIncidentCounty(incidentCounty);
      ret.setIncidentDate(incidentDate);
      ret.setLocationType(locationType);
      ret.setReference(reference);
      ret.setReportNarrative(reportNarrative);
      ret.setScreeningDecision(screeningDecision);
      ret.setScreeningDecisionDetail(screeningDecisionDetail);
      ret.setScreeningName(screeningName);
    }

    IntakeParticipant partc = ret.getParticipants().containsKey(this.participantId)
        ? ret.getParticipants().get(this.participantId) : new IntakeParticipant();
    partc.setBirthDate(DomainChef.uncookDateString(birthDt));
    partc.setFirstName(firstName);
    partc.setGender(gender);
    partc.setSsn(ssn);
    partc.setLastName(lastName);
    partc.setLegacyId(legacyId);

    // partc.setr
    ret.addParticipant(partc);

    if (flgReporter) {
      // ret.setReporterParticipantId(participantId);
    }

    // Client Address:
    // if (StringUtils.isNotBlank(getClaId())) {
    // ReplicatedClientAddress rca = new ReplicatedClientAddress();
    // rca.setAddressType(getClaAddressType());
    // ret.addClientAddress(rca);
    //
    // // Address proper:
    // // if (StringUtils.isNotBlank(getAdrId())) {
    // // ReplicatedAddress adr = new ReplicatedAddress();
    // // adr.setAddressDescription(getAdrAddressDescription());
    // // adr.setId(getAdrId());
    // // rca.addAddress(adr);
    // // }
    // }

    map.put(ret.getId(), ret);
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
