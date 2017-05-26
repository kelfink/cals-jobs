package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.Type;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;

/**
 * Entity bean for Materialized Query Table (MQT), ES_CASE_HIST.
 * 
 * <p>
 * Implements {@link ApiGroupNormalizer} and converts to {@link ReplicatedPersonCase}.
 * </p>
 * 
 * @author CWDS API Team
 */
@Entity
@Table(name = "ES_CASE_HIST")
@NamedNativeQueries({
    @NamedNativeQuery(name = "gov.ca.cwds.data.persistence.cms.EsCaseReferral.findAllUpdatedAfter",
        query = "------------------------TODO-----------------------",
        resultClass = EsPersonCase.class, readOnly = true)})
public class EsPersonCase implements PersistentObject, ApiGroupNormalizer<ReplicatedPersonCases> {

  private static final long serialVersionUID = -3777509530463773550L;

  private static final Logger LOGGER = LogManager.getLogger(EsPersonCase.class);

  @Type(type = "timestamp")
  @Column(name = "LAST_CHG", updatable = false)
  private Date lastChange;

  // ================
  // CASE:
  // ================

  @Id
  @Column(name = "CASE_ID")
  private String caseId;

  @Column(name = "START_DATE")
  @Type(type = "date")
  private Date startDate;

  @Column(name = "END_DATE")
  @Type(type = "date")
  private Date endDate;

  @Column(name = "COUNTY")
  private String county;

  // ==============
  // FOCUS CHILD:
  // ==============

  @Column(name = "CHILD_ID")
  private String focusChildId;

  @Column(name = "FOCUS_CHLD_FIRST_NM")
  private String focusChildFirstName;

  @Column(name = "FOCUS_CHLD_LAST_NM")
  private String focusChildLastName;

  @Column(name = "SERVICE_COMP")
  private String serviceComponent;

  // ==============
  // SOCIAL WORKER:
  // ==============

  @Column(name = "WORKER_ID")
  private String workerId;

  @Column(name = "WORKER_FIRST_NM")
  private String workerFirstName;

  @Column(name = "WORKER_LAST_NM")
  private String workerLastName;

  // =============
  // PARENT:
  // =============

  @Column(name = "PARENT_ID")
  private String parentId;

  @Column(name = "PARENT_FIRST_NM")
  private String parentFirstName;

  @Column(name = "PARENT_LAST_NM")
  private String parentLastName;

  // =============
  // REDUCE:
  // =============

  @Override
  public Class<ReplicatedPersonCases> getReductionClass() {
    return ReplicatedPersonCases.class;
  }

  @Override
  public void reduce(Map<Object, ReplicatedPersonCases> map) {
    ReplicatedPersonCases cases = map.get(this.caseId);
    if (cases == null) {
      cases = new ReplicatedPersonCases(this.caseId);
      map.put(this.caseId, cases);
    }

    // TODO
  }

  @Override
  public Object getGroupKey() {
    return this.caseId;
  }

  @Override
  public Serializable getPrimaryKey() {
    return null;
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

  public String getCaseId() {
    return caseId;
  }

  public void setCaseId(String caseId) {
    this.caseId = caseId;
  }

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

  public String getCounty() {
    return county;
  }

  public void setCounty(String county) {
    this.county = county;
  }

  public String getFocusChildId() {
    return focusChildId;
  }

  public void setFocusChildId(String focusChildId) {
    this.focusChildId = focusChildId;
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

  public String getServiceComponent() {
    return serviceComponent;
  }

  public void setServiceComponent(String serviceComponent) {
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

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
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
