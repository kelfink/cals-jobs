package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

import gov.ca.cwds.data.es.ElasticSearchAccessLimitation;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonSocialWorker;
import gov.ca.cwds.data.es.ElasticSearchPersonCase;
import gov.ca.cwds.data.es.ElasticSearchPersonChild;
import gov.ca.cwds.data.es.ElasticSearchPersonParent;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.rep.EmbeddableAccessLimitation;
import gov.ca.cwds.data.persistence.cms.rep.EmbeddableStaffWorker;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.neutron.util.NeutronDateUtils;
import gov.ca.cwds.neutron.util.transform.ElasticTransformer;
import gov.ca.cwds.rest.api.domain.DomainChef;
import gov.ca.cwds.rest.api.domain.cms.LegacyTable;
import gov.ca.cwds.rest.api.domain.cms.SystemCodeCache;

/**
 * Entity bean for Materialized Query Table (MQT), MQT_CASE_HIST.
 * 
 * <p>
 * Implements {@link ApiGroupNormalizer} and converts to {@link ReplicatedPersonCases}.
 * </p>
 * 
 * @author CWDS API Team
 */
@MappedSuperclass
public abstract class EsPersonCase
    implements PersistentObject, ApiGroupNormalizer<ReplicatedPersonCases> {

  private static final long serialVersionUID = 2896950873299112269L;

  // =================
  // ID:
  // =================
  @Id
  @Column(name = "CASE_ID")
  private String caseId;

  @Id
  @Column(name = "FOCUS_CHILD_ID")
  private String focusChildId;

  @Id
  @Column(name = "PARENT_ID")
  private String parentId;

  // ===================
  // CASE:
  // ===================

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

  // =================
  // FOCUS CHILD:
  // =================

  @Column(name = "FOCUS_CHLD_FIRST_NM")
  private String focusChildFirstName;

  @Column(name = "FOCUS_CHLD_LAST_NM")
  private String focusChildLastName;

  @Type(type = "timestamp")
  @Column(name = "FOCUS_CHILD_LAST_UPDATED", updatable = false)
  private Date focusChildLastUpdated;

  @Column(name = "FOCUS_CHILD_SENSITIVITY_IND")
  private String focusChildSensitivityIndicator;

  // =================
  // SOCIAL WORKER:
  // =================

  private EmbeddableStaffWorker worker = new EmbeddableStaffWorker();

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

  @Column(name = "PARENT_SENSITIVITY_IND")
  private String parentSensitivityIndicator;

  // =====================
  // ACCESS LIMITATION:
  // =====================

  private EmbeddableAccessLimitation accessLimitation = new EmbeddableAccessLimitation();

  /**
   * Default constructor.
   */
  public EsPersonCase() {
    // Default, no-arg constructor
  }

  public String getCaseId() {
    return caseId;
  }

  public void setCaseId(String caseId) {
    this.caseId = caseId;
  }

  public String getFocusChildId() {
    return focusChildId;
  }

  public void setFocusChildId(String focusChildId) {
    this.focusChildId = focusChildId;
  }

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public Date getStartDate() {
    return NeutronDateUtils.freshDate(startDate);
  }

  public void setStartDate(Date startDate) {
    this.startDate = NeutronDateUtils.freshDate(startDate);
  }

  public Date getEndDate() {
    return NeutronDateUtils.freshDate(endDate);
  }

  public void setEndDate(Date endDate) {
    this.endDate = NeutronDateUtils.freshDate(endDate);
  }

  public Integer getCounty() {
    return county;
  }

  public void setCounty(Integer county) {
    this.county = county;
  }

  public Integer getServiceComponent() {
    return serviceComponent;
  }

  public void setServiceComponent(Integer serviceComponent) {
    this.serviceComponent = serviceComponent;
  }

  public Date getCaseLastUpdated() {
    return NeutronDateUtils.freshDate(caseLastUpdated);
  }

  public void setCaseLastUpdated(Date caseLastUpdated) {
    this.caseLastUpdated = NeutronDateUtils.freshDate(caseLastUpdated);
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

  public Date getFocusChildLastUpdated() {
    return NeutronDateUtils.freshDate(focusChildLastUpdated);
  }

  public void setFocusChildLastUpdated(Date focusChildLastUpdated) {
    this.focusChildLastUpdated = NeutronDateUtils.freshDate(focusChildLastUpdated);
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

  public Date getParentLastUpdated() {
    return NeutronDateUtils.freshDate(parentLastUpdated);
  }

  public void setParentLastUpdated(Date parentLastUpdated) {
    this.parentLastUpdated = NeutronDateUtils.freshDate(parentLastUpdated);
  }

  public String getParentSourceTable() {
    return parentSourceTable;
  }

  public void setParentSourceTable(String parentSourceTable) {
    this.parentSourceTable = parentSourceTable;
  }

  public String getFocusChildSensitivityIndicator() {
    return focusChildSensitivityIndicator;
  }

  public void setFocusChildSensitivityIndicator(String focusChildSensitivityIndicator) {
    this.focusChildSensitivityIndicator = focusChildSensitivityIndicator;
  }

  public String getParentSensitivityIndicator() {
    return parentSensitivityIndicator;
  }

  public void setParentSensitivityIndicator(String parentSensitivityIndicator) {
    this.parentSensitivityIndicator = parentSensitivityIndicator;
  }

  @Override
  public Class<ReplicatedPersonCases> getNormalizationClass() {
    return ReplicatedPersonCases.class;
  }

  @Override
  public Serializable getPrimaryKey() {
    return null;
  }

  @Override
  public ReplicatedPersonCases normalize(Map<Object, ReplicatedPersonCases> map) {
    String groupId = (String) getNormalizationGroupKey();
    ReplicatedPersonCases cases = map.get(groupId);
    if (cases == null) {
      cases = new ReplicatedPersonCases(groupId);
      map.put(groupId, cases);
    }

    final ElasticSearchPersonCase esPersonCase = new ElasticSearchPersonCase();

    //
    // Case:
    //
    esPersonCase.setId(this.caseId);
    esPersonCase.setLegacyId(this.caseId);
    esPersonCase.setLegacyLastUpdated(DomainChef.cookStrictTimestamp(this.caseLastUpdated));
    esPersonCase.setStartDate(DomainChef.cookDate(this.startDate));
    esPersonCase.setEndDate(DomainChef.cookDate(this.endDate));
    esPersonCase.setCountyId(this.county == null ? null : this.county.toString());
    esPersonCase.setCountyName(SystemCodeCache.global().getSystemCodeShortDescription(this.county));
    esPersonCase.setServiceComponentId(
        this.serviceComponent == null ? null : this.serviceComponent.toString());
    esPersonCase.setServiceComponent(
        SystemCodeCache.global().getSystemCodeShortDescription(this.serviceComponent));
    esPersonCase.setLegacyDescriptor(ElasticTransformer.createLegacyDescriptor(this.caseId,
        this.caseLastUpdated, LegacyTable.CASE));

    //
    // Child:
    //
    final ElasticSearchPersonChild child = new ElasticSearchPersonChild();
    child.setId(this.focusChildId);
    child.setLegacyClientId(this.focusChildId);
    child.setLegacyLastUpdated(DomainChef.cookStrictTimestamp(this.focusChildLastUpdated));
    child.setFirstName(this.focusChildFirstName);
    child.setLastName(this.focusChildLastName);
    child.setLegacyDescriptor(ElasticTransformer.createLegacyDescriptor(this.focusChildId,
        this.focusChildLastUpdated, LegacyTable.CLIENT));
    child.setSensitivityIndicator(this.focusChildSensitivityIndicator);
    esPersonCase.setFocusChild(child);

    //
    // Assigned Worker:
    //
    final ElasticSearchPersonSocialWorker assignedWorker = new ElasticSearchPersonSocialWorker();
    assignedWorker.setId(this.worker.getWorkerId());
    assignedWorker.setLegacyClientId(this.worker.getWorkerId());
    assignedWorker
        .setLegacyLastUpdated(DomainChef.cookStrictTimestamp(this.worker.getWorkerLastUpdated()));
    assignedWorker.setFirstName(this.worker.getWorkerFirstName());
    assignedWorker.setLastName(this.worker.getWorkerLastName());
    assignedWorker.setLegacyDescriptor(ElasticTransformer.createLegacyDescriptor(
        this.worker.getWorkerId(), this.worker.getWorkerLastUpdated(), LegacyTable.STAFF_PERSON));
    esPersonCase.setAssignedSocialWorker(assignedWorker);

    //
    // Access Limitation:
    //
    final ElasticSearchAccessLimitation accessLimit = new ElasticSearchAccessLimitation();
    accessLimit.setLimitedAccessCode(this.accessLimitation.getLimitedAccessCode());
    accessLimit
        .setLimitedAccessDate(DomainChef.cookDate(this.accessLimitation.getLimitedAccessDate()));
    accessLimit.setLimitedAccessDescription(this.accessLimitation.getLimitedAccessDescription());
    accessLimit.setLimitedAccessGovernmentEntityId(
        this.accessLimitation.getLimitedAccessGovernmentEntityId() == null ? null
            : this.accessLimitation.getLimitedAccessGovernmentEntityId().toString());
    accessLimit.setLimitedAccessGovernmentEntityName(SystemCodeCache.global()
        .getSystemCodeShortDescription(this.accessLimitation.getLimitedAccessGovernmentEntityId()));
    esPersonCase.setAccessLimitation(accessLimit);

    //
    // A Case may have more than one parents:
    //
    final ElasticSearchPersonParent parent = new ElasticSearchPersonParent();
    parent.setId(this.parentId);
    parent.setLegacyClientId(getParentId());
    parent.setLegacyLastUpdated(DomainChef.cookStrictTimestamp(this.parentLastUpdated));
    parent.setLegacySourceTable(this.parentSourceTable);
    parent.setFirstName(this.parentFirstName);
    parent.setLastName(this.parentLastName);
    parent.setRelationship(
        SystemCodeCache.global().getSystemCodeShortDescription(this.parentRelationship));
    parent.setLegacyDescriptor(ElasticTransformer.createLegacyDescriptor(this.parentId,
        this.parentLastUpdated, LegacyTable.CLIENT));
    parent.setSensitivityIndicator(this.parentSensitivityIndicator);

    cases.addCase(esPersonCase, parent);
    return cases;
  }

  public EmbeddableStaffWorker getWorker() {
    return worker;
  }

  public void setWorker(EmbeddableStaffWorker worker) {
    this.worker = worker;
  }

  public EmbeddableAccessLimitation getAccessLimitation() {
    return accessLimitation;
  }

  public void setAccessLimitation(EmbeddableAccessLimitation accessLimitation) {
    this.accessLimitation = accessLimitation;
  }

  public String getLimitedAccessCode() {
    return accessLimitation.getLimitedAccessCode();
  }

  public void setLimitedAccessCode(String limitedAccessCode) {
    accessLimitation.setLimitedAccessCode(limitedAccessCode);
  }

  public Date getLimitedAccessDate() {
    return accessLimitation.getLimitedAccessDate();
  }

  public void setLimitedAccessDate(Date limitedAccessDate) {
    accessLimitation.setLimitedAccessDate(limitedAccessDate);
  }

  public String getLimitedAccessDescription() {
    return accessLimitation.getLimitedAccessDescription();
  }

  public void setLimitedAccessDescription(String limitedAccessDescription) {
    accessLimitation.setLimitedAccessDescription(limitedAccessDescription);
  }

  public Integer getLimitedAccessGovernmentEntityId() {
    return accessLimitation.getLimitedAccessGovernmentEntityId();
  }

  public void setLimitedAccessGovernmentEntityId(Integer limitedAccessGovernmentEntityId) {
    accessLimitation.setLimitedAccessGovernmentEntityId(limitedAccessGovernmentEntityId);
  }

  public String getWorkerId() {
    return worker.getWorkerId();
  }

  public void setWorkerId(String workerId) {
    worker.setWorkerId(workerId);
  }

  public String getWorkerFirstName() {
    return worker.getWorkerFirstName();
  }

  public void setWorkerFirstName(String workerFirstName) {
    worker.setWorkerFirstName(workerFirstName);
  }

  public String getWorkerLastName() {
    return worker.getWorkerLastName();
  }

  public void setWorkerLastName(String workerLastName) {
    worker.setWorkerLastName(workerLastName);
  }

  public Date getWorkerLastUpdated() {
    return worker.getWorkerLastUpdated();
  }

  public void setWorkerLastUpdated(Date workerLastUpdated) {
    worker.setWorkerLastUpdated(workerLastUpdated);
  }

  @Override
  public String toString() {
    return worker.toString();
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
