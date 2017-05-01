package gov.ca.cwds.data.persistence.ns;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiPersonAware;

/**
 * JSON object for Intake Screenings.
 * 
 * @author CWDS API Team
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "screenings")
public class IntakeScreening implements PersistentObject, ApiPersonAware {

  @Id
  // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "screenings_id_seq")
  // @SequenceGenerator(name = "screenings_id_seq", sequenceName = "screenings_id_seq",
  // allocationSize = 10)
  @Column(name = "SCREENING_ID")
  private String id;

  @Column(name = "REFERENCE")
  private String reference;

  @Column(name = "STARTED_AT")
  private String startedAt;

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

  private IntakeParticipant reporter = new IntakeParticipant();

  // @OneToOne(cascade = CascadeType.ALL)
  // @JoinColumn(name = "contact_address_id")
  // private Address contactAddress;

  // @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "screening")
  // private Set<Participant> participants = new HashSet<>(0);

  /**
   * Default constructor
   * 
   * Required for Hibernate
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

  // /**
  // * Constructor
  // *
  // * @param reference The reference
  // * @param endedAt The endedAt date
  // * @param incidentCounty The incident county
  // * @param incidentDate The incident date
  // * @param locationType The location type
  // * @param communicationMethod The communication method
  // * @param name The name of the screening
  // * @param responseTime The response time
  // * @param screeningDecision The screening decision
  // * @param startedAt The started at date
  // * @param narrative The narrative
  // * @param contactAddress The contact address
  // * @param participants The list of participants
  // */
  // public IntakeScreening(String reference, Date endedAt, String incidentCounty, Date
  // incidentDate,
  // String locationType, String communicationMethod, String name, String responseTime,
  // String screeningDecision, Date startedAt, String narrative, Address contactAddress,
  // Set<Participant> participants) {
  // super();
  //
  // this.reference = reference;
  // this.endedAt = endedAt;
  // this.incidentCounty = incidentCounty;
  // this.incidentDate = incidentDate;
  // this.locationType = locationType;
  // this.communicationMethod = communicationMethod;
  // this.name = name;
  // this.responseTime = responseTime;
  // this.screeningDecision = screeningDecision;
  // this.startedAt = startedAt;
  // this.narrative = narrative;
  // this.contactAddress = contactAddress;
  //
  // if (participants != null && !participants.isEmpty()) {
  // this.participants.addAll(participants);
  // }
  // }

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
   * @return the id
   */
  public String getId() {
    return id;
  }

  @Override
  public Date getBirthDate() {
    return reporter.getBirthDate();
  }

  @Override
  public String getFirstName() {
    return reporter.getFirstName();
  }

  @Override
  public String getGender() {
    return reporter.getGender();
  }

  @Override
  public String getLastName() {
    return reporter.getLastName();
  }

  @Override
  public String getMiddleName() {
    return null;
  }

  @Override
  public String getNameSuffix() {
    return null;
  }

  @Override
  public String getSsn() {
    return reporter.getSsn();
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setReporterParticipantId(String id) {
    reporter.setId(id);
  }

  public void setFirstName(String firstName) {
    reporter.setFirstName(firstName);
  }

  public void setLastName(String lastName) {
    reporter.setLastName(lastName);
  }

  public void setBirthDate(Date birthDate) {
    reporter.setBirthDate(birthDate);
  }

  public void setGender(String gender) {
    reporter.setGender(gender);
  }

  public void setSsn(String ssn) {
    reporter.setSsn(ssn);
  }

  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public String getStartedAt() {
    return startedAt;
  }

  public void setStartedAt(String startedAt) {
    this.startedAt = startedAt;
  }

  public String getEndedAt() {
    return endedAt;
  }

  public void setEndedAt(String endedAt) {
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

  public IntakeParticipant getReporter() {
    return reporter;
  }

  public void setReporter(IntakeParticipant reporter) {
    this.reporter = reporter;
  }

}
