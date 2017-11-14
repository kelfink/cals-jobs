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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gov.ca.cwds.dao.ApiMultiplePersonAware;
import gov.ca.cwds.dao.ApiScreeningAware;
import gov.ca.cwds.data.es.ElasticSearchPersonAny;
import gov.ca.cwds.data.es.ElasticSearchPersonScreening;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiPersonAware;
import gov.ca.cwds.neutron.util.NeutronDateUtils;

/**
 * NS Persistence class for Intake Screenings.
 * 
 * @author CWDS API Team
 */
@SuppressWarnings("serial")
public class IntakeScreening extends CommonScreening
    implements PersistentObject, ApiMultiplePersonAware, ApiScreeningAware {

  private static final Set<String> EMPTY_SET_STRING = new LinkedHashSet<>();

  @Id
  @Column(name = "SCREENING_ID")
  private String id;

  @Column(name = "REFERRAL_ID")
  private String referralId;

  @Column(name = "REFERENCE")
  private String reference;

  @Column(name = "STARTED_AT")
  private Date startedAt;

  @Column(name = "ENDED_AT")
  private Date endedAt;

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
    ret.setCountyName(getIncidentCounty());
    ret.setDecision(getScreeningDecision());
    ret.setEndDate(endedAt);
    ret.setStartDate(startedAt);
    ret.setResponseTime(getScreeningDecisionDetail()); // Intake field name should change.

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
    return NeutronDateUtils.freshDate(startedAt);
  }

  public void setStartedAt(Date startedAt) {
    this.startedAt = NeutronDateUtils.freshDate(startedAt);
  }

  public Date getEndedAt() {
    return NeutronDateUtils.freshDate(endedAt);
  }

  public void setEndedAt(Date endedAt) {
    this.endedAt = NeutronDateUtils.freshDate(endedAt);
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
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE, true);
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
