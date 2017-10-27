package gov.ca.cwds.data.persistence.ns;

import javax.persistence.Column;

import gov.ca.cwds.data.std.ApiMarker;

public class CommonScreening implements ApiMarker {

  private static final long serialVersionUID = 1L;

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

}
