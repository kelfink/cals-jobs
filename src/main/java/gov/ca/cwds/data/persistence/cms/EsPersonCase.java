package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.Type;

import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonCase;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonChild;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonParent;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonSocialWorker;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.rest.api.domain.DomainChef;

/**
 * Entity bean for Materialized Query Table (MQT), ES_CASE_HIST.
 * 
 * <p>
 * Implements {@link ApiGroupNormalizer} and converts to {@link ReplicatedPersonCases}.
 * </p>
 * 
 * @author CWDS API Team
 */
public abstract class EsPersonCase
    implements PersistentObject, ApiGroupNormalizer<ReplicatedPersonCases> {

  private static final long serialVersionUID = 2896950873299112269L;

  private static final Logger LOGGER = LogManager.getLogger(EsPersonCase.class);

  // ================
  // CASE:
  // ================

  @Column(name = "START_DATE")
  @Type(type = "date")
  private Date startDate;

  @Column(name = "END_DATE")
  @Type(type = "date")
  private Date endDate;

  @Column(name = "COUNTY")
  @Type(type = "integer")
  private Integer county;

  @Column(name = "SERVICE_COMP")
  @Type(type = "integer")
  private Integer serviceComponent;

  @Type(type = "timestamp")
  @Column(name = "CASE_LAST_UPDATED", updatable = false)
  private Date caseLastUpdated;

  // ==============
  // FOCUS CHILD:
  // ==============

  @Column(name = "FOCUS_CHLD_FIRST_NM")
  private String focusChildFirstName;

  @Column(name = "FOCUS_CHLD_LAST_NM")
  private String focusChildLastName;

  @Type(type = "timestamp")
  @Column(name = "FOCUS_CHILD_LAST_UPDATED", updatable = false)
  private Date focusChildLastUpdated;

  // ==============
  // SOCIAL WORKER:
  // ==============

  @Column(name = "WORKER_ID")
  private String workerId;

  @Column(name = "WORKER_FIRST_NM")
  private String workerFirstName;

  @Column(name = "WORKER_LAST_NM")
  private String workerLastName;

  @Type(type = "timestamp")
  @Column(name = "WORKER_LAST_UPDATED", updatable = false)
  private Date workerLastUpdated;

  // =============
  // PARENT:
  // =============

  @Column(name = "PARENT_FIRST_NM")
  private String parentFirstName;

  @Column(name = "PARENT_LAST_NM")
  private String parentLastName;

  @Column(name = "PARENT_RELATIONSHIP")
  @Type(type = "integer")
  private Integer parentRelationship;

  @Type(type = "timestamp")
  @Column(name = "PARENT_LAST_UPDATED", updatable = false)
  private Date parentLastUpdated;

  @Column(name = "PARENT_SOURCE_TABLE")
  private String parentSourceTable;

  // =============
  // REDUCE:
  // =============

  @Override
  public Class<ReplicatedPersonCases> getNormalizationClass() {
    return ReplicatedPersonCases.class;
  }

  @Override
  public ReplicatedPersonCases normalize(Map<Object, ReplicatedPersonCases> map) {
    String groupId = (String) getNormalizationGroupKey();
    ReplicatedPersonCases cases = map.get(groupId);
    if (cases == null) {
      cases = new ReplicatedPersonCases(groupId);
      map.put(groupId, cases);
    }

    ElasticSearchPersonCase esPersonCase = new ElasticSearchPersonCase();

    //
    // Case
    //
    esPersonCase.setId(getCaseId());
    esPersonCase.setLegacyId(getCaseId());
    esPersonCase.setStartDate(DomainChef.cookDate(this.startDate));
    esPersonCase.setEndDate(DomainChef.cookDate(this.endDate));
    esPersonCase.setLegacyLastUpdated(DomainChef.cookStrictTimestamp(this.caseLastUpdated));
    esPersonCase
        .setCountyName(ElasticSearchPerson.getSystemCodes().getCodeShortDescription(this.county));
    esPersonCase.setServiceComponent(
        ElasticSearchPerson.getSystemCodes().getCodeShortDescription(this.serviceComponent));

    //
    // Child
    //
    ElasticSearchPersonChild child = new ElasticSearchPersonChild();
    child.setId(getFocusChildId());
    child.setLegacyClientId(getFocusChildId());
    child.setFirstName(this.focusChildFirstName);
    child.setLastName(this.focusChildLastName);
    child.setLegacyLastUpdated(DomainChef.cookStrictTimestamp(this.focusChildLastUpdated));
    esPersonCase.setFocusChild(child);

    //
    // Assigned Worker
    //
    ElasticSearchPersonSocialWorker assignedWorker = new ElasticSearchPersonSocialWorker();
    assignedWorker.setId(this.workerId);
    assignedWorker.setLegacyClientId(this.workerId);
    assignedWorker.setFirstName(this.workerFirstName);
    assignedWorker.setLastName(this.workerLastName);
    assignedWorker.setLegacyLastUpdated(DomainChef.cookStrictTimestamp(this.workerLastUpdated));
    esPersonCase.setAssignedSocialWorker(assignedWorker);

    //
    // A Case may have more than one parents
    //
    ElasticSearchPersonParent parent = new ElasticSearchPersonParent();
    parent.setId(getParentId());
    parent.setLegacyClientId(getParentId());
    parent.setFirstName(this.parentFirstName);
    parent.setLastName(this.parentLastName);
    parent.setLegacyLastUpdated(DomainChef.cookStrictTimestamp(this.parentLastUpdated));
    parent.setLegacySourceTable(this.parentSourceTable);
    parent.setRelationship(
        ElasticSearchPerson.getSystemCodes().getCodeShortDescription(this.parentRelationship));

    cases.addCase(esPersonCase, parent);
    return cases;
  }

  @Override
  public Serializable getPrimaryKey() {
    return null;
  }

  public abstract String getCaseId();

  public abstract void setCaseId(String caseId);

  public abstract String getFocusChildId();

  public abstract String getParentId();

  public abstract void setParentId(String parentId);

  public abstract void setFocusChildId(String focusChildId);

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public Integer getCounty() {
    return county;
  }

  public void setCounty(Integer county) {
    this.county = county;
  }

  public String getFocusChildFirstName() {
    return focusChildFirstName;
  }

  public void setFocusChildFirstName(String focusChildFirstName) {
    this.focusChildFirstName = focusChildFirstName;
  }

  public String getFocusChildLastName() {
    return focusChildLastName;
  }

  public void setFocusChildLastName(String focusChildLastName) {
    this.focusChildLastName = focusChildLastName;
  }

  public Integer getServiceComponent() {
    return serviceComponent;
  }

  public void setServiceComponent(Integer serviceComponent) {
    this.serviceComponent = serviceComponent;
  }

  public String getWorkerId() {
    return workerId;
  }

  public void setWorkerId(String workerId) {
    this.workerId = workerId;
  }

  public String getWorkerFirstName() {
    return workerFirstName;
  }

  public void setWorkerFirstName(String workerFirstName) {
    this.workerFirstName = workerFirstName;
  }

  public String getWorkerLastName() {
    return workerLastName;
  }

  public void setWorkerLastName(String workerLastName) {
    this.workerLastName = workerLastName;
  }

  public String getParentFirstName() {
    return parentFirstName;
  }

  public void setParentFirstName(String parentFirstName) {
    this.parentFirstName = parentFirstName;
  }

  public String getParentLastName() {
    return parentLastName;
  }

  public void setParentLastName(String parentLastName) {
    this.parentLastName = parentLastName;
  }

  public Integer getParentRelationship() {
    return parentRelationship;
  }

  public void setParentRelationship(Integer parentRelationship) {
    this.parentRelationship = parentRelationship;
  }

  public Date getCaseLastUpdated() {
    return caseLastUpdated;
  }

  public void setCaseLastUpdated(Date caseLastUpdated) {
    this.caseLastUpdated = caseLastUpdated;
  }

  public Date getFocusChildLastUpdated() {
    return focusChildLastUpdated;
  }

  public void setFocusChildLastUpdated(Date focusChildLastUpdated) {
    this.focusChildLastUpdated = focusChildLastUpdated;
  }

  public Date getWorkerLastUpdated() {
    return workerLastUpdated;
  }

  public void setWorkerLastUpdated(Date workerLastUpdated) {
    this.workerLastUpdated = workerLastUpdated;
  }

  public Date getParentLastUpdated() {
    return parentLastUpdated;
  }

  public void setParentLastUpdated(Date parentLastUpdated) {
    this.parentLastUpdated = parentLastUpdated;
  }

  public String getParentSourceTable() {
    return parentSourceTable;
  }

  public void setParentSourceTable(String parentSourceTable) {
    this.parentSourceTable = parentSourceTable;
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

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
