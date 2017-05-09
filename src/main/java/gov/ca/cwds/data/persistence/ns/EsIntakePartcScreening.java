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
import gov.ca.cwds.rest.api.domain.DomainChef;

/**
 * Entity bean for PostgreSQL view, VW_PARTC_SCREENING.
 * 
 * <p>
 * Implements {@link ApiGroupNormalizer} and converts to {@link IntakeParticipant}.
 * </p>
 * 
 * @author CWDS API Team
 */
@Entity
@Table(name = "VW_PARTC_SCREENING")
@NamedNativeQueries({
    @NamedNativeQuery(name = "gov.ca.cwds.data.persistence.ns.EsIntakePartcScreening.findAll",
        query = "SELECT vw.* FROM {h-schema}VW_PARTC_SCREENING vw "
            + "WHERE vw.started_at is not null ORDER BY vw.SCREENING_ID FOR READ ONLY",
        resultClass = EsIntakePartcScreening.class, readOnly = true),
    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.ns.EsIntakePartcScreening.findBucketRange",
        query = "SELECT vw.* FROM {h-schema}VW_PARTC_SCREENING vw "
            + "WHERE vw.SCREENING_ID BETWEEN :min_id AND :max_id "
            + "ORDER BY vw.SCREENING_ID FOR READ ONLY",
        resultClass = EsIntakePartcScreening.class, readOnly = true),
    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.ns.EsIntakePartcScreening.findAllUpdatedAfter",
        query = "SELECT vw.* FROM {h-schema}VW_PARTC_SCREENING vw "
            + "WHERE vw.LAST_CHG > CAST(:after AS TIMESTAMP) "
            + "ORDER BY vw.SCREENING_ID FOR READ ONLY ",
        resultClass = EsIntakePartcScreening.class, readOnly = true)})
public class EsIntakePartcScreening
    implements PersistentObject, ApiGroupNormalizer<IntakeParticipant> {

  private static final Logger LOGGER = LogManager.getLogger(EsIntakePartcScreening.class);

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
  @Column(name = "NS_PARTC_ID")
  private String intakeParticipantId;

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

  @Id
  @Column(name = "CMS_LEGACY_ID")
  private String cmsLegacyId;

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

  @Id
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

  @Id
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
   * @return populated participant object
   */
  protected IntakeScreening fillScreening(IntakeScreening s) {
    IntakeScreening ret = s == null ? new IntakeScreening() : s;
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
    IntakeParticipant p;

    final String keyPartc = (String) getGroupKey();
    if (!map.containsKey(getGroupKey())) {
      p = new IntakeParticipant();
      p.setBirthDate(DomainChef.uncookDateString(birthDt));
      p.setFirstName(firstName);
      p.setGender(gender);
      p.setSsn(ssn);
      p.setLastName(lastName);
      p.setLegacyId(cmsLegacyId);
      p.setId(keyPartc);
      map.put(keyPartc, p);

    } else {
      p = map.get(screeningId);
    }

    IntakeScreening s;
    if (p.getScreenings().containsKey(screeningId)) {
      s = p.getScreenings().get(screeningId);
    } else {
      s = fillScreening();
      s.addParticipant(p);
      p.addScreening(s);
    }

    // final IntakeParticipant worker = p.getAssignedSocialWorker();
    // worker.setLastName(assignee);

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
      // fillScreening(p.getReporter());
    }

    if (StringUtils.isNotBlank(addressId)) {
      final ElasticSearchPersonAddress addr = new ElasticSearchPersonAddress();
      addr.setId(addressId);
      addr.setCity(city);
      addr.setState(state);
      // addr.setStateName(stateName);
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
    return StringUtils.isNotBlank(this.cmsLegacyId) ? this.cmsLegacyId : this.intakeParticipantId;
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
