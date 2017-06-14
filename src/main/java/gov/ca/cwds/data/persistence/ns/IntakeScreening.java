package gov.ca.cwds.data.persistence.ns;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gov.ca.cwds.dao.ApiMultiplePersonAware;
import gov.ca.cwds.dao.ApiScreeningAware;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonAny;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonScreening;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.ns.IntakeParticipant.EsPersonType;
import gov.ca.cwds.data.std.ApiPersonAware;

/**
 * NS Persistence class for Intake Screenings.
 * 
 * @author CWDS API Team
 */
@SuppressWarnings("serial")
// @Entity
// @Table(name = "screenings")
public class IntakeScreening
    implements PersistentObject, ApiMultiplePersonAware, ApiScreeningAware {

  private static final Set<String> EMPTY_SET_STRING = new LinkedHashSet<>();

  @Id
  // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "screenings_id_seq")
  // @SequenceGenerator(name = "screenings_id_seq", sequenceName = "screenings_id_seq",
  // allocationSize = 10)
  @Column(name = "SCREENING_ID")
  private String id;

  @Column(name = "REFERRAL_ID")
  private String referralId;

  @Column(name = "REFERENCE")
  private String reference;

  @Column(name = "STARTED_AT")
  // @Type(type = "timestamp")
  private Date startedAt;

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

  @JsonIgnore
  private Map<String, IntakeParticipant> participants = new LinkedHashMap<>();

  @JsonIgnore
  private Map<String, Set<String>> participantRoles = new LinkedHashMap<>();

  @JsonIgnore
  private Map<String, IntakeAllegation> allegations = new LinkedHashMap<>();

  @JsonIgnore
  private IntakeParticipant socialWorker = new IntakeParticipant();

  @JsonIgnore
  private IntakeParticipant reporter = new IntakeParticipant();

  /**
   * Default constructor, required for Hibernate.
   */
  public IntakeScreening() {
    super();
  }

  /**
   * Constructor
   * 
   * @param reference The reference
   */
  public IntakeScreening(String reference) {
    this.reference = reference;
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
   * Convert this Intake screening object to an Elasticsearch screening element.
   * 
   * @return ES screening document object
   */
  public ElasticSearchPersonScreening toEsScreening() {
    ElasticSearchPersonScreening ret = new ElasticSearchPersonScreening();

    ret.setId(id);
    ret.setReferralId(referralId);
    ret.setCountyName(incidentCounty);
    ret.setDecision(screeningDecision);
    ret.setEndDate(endedAt);
    ret.setStartDate(startedAt);
    ret.setResponseTime(screeningDecisionDetail); // Intake field name should change.

    ret.getReporter().setFirstName(getReporter().getFirstName());
    ret.getReporter().setLastName(getReporter().getLastName());
    ret.getReporter().setId(getReporter().getId());
    ret.getReporter().setLegacyClientId(getReporter().getLegacyId());

    ret.getAssignedSocialWorker().setFirstName(getSocialWorker().getFirstName());
    ret.getAssignedSocialWorker().setId(getSocialWorker().getId());
    ret.getAssignedSocialWorker().setLastName(getSocialWorker().getLastName());
    ret.getAssignedSocialWorker().setLegacyClientId(getSocialWorker().getLegacyId());

    for (IntakeAllegation alg : this.allegations.values()) {
      ret.getAllegations().add(alg.toEsAllegation());
    }

    for (IntakeParticipant p : this.participants.values()) {
      ret.getAllPeople().add((ElasticSearchPersonAny) p.toEsPerson(EsPersonType.ALL, this));
    }

    return ret;
  }

  @Override
  public ApiPersonAware[] getPersons() {
    return getParticipants().values().toArray(new ApiPersonAware[0]);
  }

  @Override
  public ElasticSearchPersonScreening[] getEsScreenings() {
    List<ElasticSearchPersonScreening> esScreenings = new ArrayList<>();
    esScreenings.add(toEsScreening());
    return esScreenings.toArray(new ElasticSearchPersonScreening[0]);
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public IntakeParticipant getSocialWorker() {
    return socialWorker;
  }

  public void setSocialWorker(IntakeParticipant assignedSocialWorker) {
    this.socialWorker = assignedSocialWorker;
  }

  public void addParticipant(IntakeParticipant prt) {
    this.participants.put(prt.getId(), prt);
  }

  public void addAllegation(IntakeAllegation alg) {
    this.allegations.put(alg.getId(), alg);
  }

  public void addParticipantRole(String partcId, String role) {
    Set<String> roles;
    if (this.participantRoles.containsKey(partcId)) {
      roles = this.participantRoles.get(partcId);
    } else {
      roles = new LinkedHashSet<>();
      this.participantRoles.put(partcId, roles);
    }

    roles.add(role);
  }

  public Set<String> findParticipantRoles(String partcId) {
    return this.participantRoles.containsKey(partcId) ? this.participantRoles.get(partcId)
        : EMPTY_SET_STRING;
  }

  public Map<String, IntakeParticipant> getParticipants() {
    return participants;
  }

  public Map<String, IntakeAllegation> getAllegations() {
    return allegations;
  }

  public IntakeParticipant getReporter() {
    return reporter;
  }

  public void setReporter(IntakeParticipant reporter) {
    this.reporter = reporter;
  }

  public Map<String, Set<String>> getParticipantRoles() {
    return participantRoles;
  }

  public String getReferralId() {
    return referralId;
  }

  public void setReferralId(String referralId) {
    this.referralId = referralId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result =
        prime * result + ((additionalInformation == null) ? 0 : additionalInformation.hashCode());
    result = prime * result + ((allegations == null) ? 0 : allegations.hashCode());
    result = prime * result + ((assignee == null) ? 0 : assignee.hashCode());
    result = prime * result + ((communicationMethod == null) ? 0 : communicationMethod.hashCode());
    result = prime * result + ((endedAt == null) ? 0 : endedAt.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((incidentCounty == null) ? 0 : incidentCounty.hashCode());
    result = prime * result + ((incidentDate == null) ? 0 : incidentDate.hashCode());
    result = prime * result + ((locationType == null) ? 0 : locationType.hashCode());
    result = prime * result + ((participantRoles == null) ? 0 : participantRoles.hashCode());
    result = prime * result + ((participants == null) ? 0 : participants.hashCode());
    result = prime * result + ((reference == null) ? 0 : reference.hashCode());
    result = prime * result + ((referralId == null) ? 0 : referralId.hashCode());
    result = prime * result + ((reportNarrative == null) ? 0 : reportNarrative.hashCode());
    result = prime * result + ((reporter == null) ? 0 : reporter.hashCode());
    result = prime * result + ((screeningDecision == null) ? 0 : screeningDecision.hashCode());
    result = prime * result
        + ((screeningDecisionDetail == null) ? 0 : screeningDecisionDetail.hashCode());
    result = prime * result + ((screeningName == null) ? 0 : screeningName.hashCode());
    result = prime * result + ((socialWorker == null) ? 0 : socialWorker.hashCode());
    result = prime * result + ((startedAt == null) ? 0 : startedAt.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    IntakeScreening other = (IntakeScreening) obj;
    if (additionalInformation == null) {
      if (other.additionalInformation != null)
        return false;
    } else if (!additionalInformation.equals(other.additionalInformation))
      return false;
    if (allegations == null) {
      if (other.allegations != null)
        return false;
    } else if (!allegations.equals(other.allegations))
      return false;
    if (assignee == null) {
      if (other.assignee != null)
        return false;
    } else if (!assignee.equals(other.assignee))
      return false;
    if (communicationMethod == null) {
      if (other.communicationMethod != null)
        return false;
    } else if (!communicationMethod.equals(other.communicationMethod))
      return false;
    if (endedAt == null) {
      if (other.endedAt != null)
        return false;
    } else if (!endedAt.equals(other.endedAt))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (incidentCounty == null) {
      if (other.incidentCounty != null)
        return false;
    } else if (!incidentCounty.equals(other.incidentCounty))
      return false;
    if (incidentDate == null) {
      if (other.incidentDate != null)
        return false;
    } else if (!incidentDate.equals(other.incidentDate))
      return false;
    if (locationType == null) {
      if (other.locationType != null)
        return false;
    } else if (!locationType.equals(other.locationType))
      return false;
    if (participantRoles == null) {
      if (other.participantRoles != null)
        return false;
    } else if (!participantRoles.equals(other.participantRoles))
      return false;
    if (participants == null) {
      if (other.participants != null)
        return false;
    } else if (!participants.equals(other.participants))
      return false;
    if (reference == null) {
      if (other.reference != null)
        return false;
    } else if (!reference.equals(other.reference))
      return false;
    if (referralId == null) {
      if (other.referralId != null)
        return false;
    } else if (!referralId.equals(other.referralId))
      return false;
    if (reportNarrative == null) {
      if (other.reportNarrative != null)
        return false;
    } else if (!reportNarrative.equals(other.reportNarrative))
      return false;
    if (reporter == null) {
      if (other.reporter != null)
        return false;
    } else if (!reporter.equals(other.reporter))
      return false;
    if (screeningDecision == null) {
      if (other.screeningDecision != null)
        return false;
    } else if (!screeningDecision.equals(other.screeningDecision))
      return false;
    if (screeningDecisionDetail == null) {
      if (other.screeningDecisionDetail != null)
        return false;
    } else if (!screeningDecisionDetail.equals(other.screeningDecisionDetail))
      return false;
    if (screeningName == null) {
      if (other.screeningName != null)
        return false;
    } else if (!screeningName.equals(other.screeningName))
      return false;
    if (socialWorker == null) {
      if (other.socialWorker != null)
        return false;
    } else if (!socialWorker.equals(other.socialWorker))
      return false;
    if (startedAt == null) {
      if (other.startedAt != null)
        return false;
    } else if (!startedAt.equals(other.startedAt))
      return false;
    return true;
  }

}
