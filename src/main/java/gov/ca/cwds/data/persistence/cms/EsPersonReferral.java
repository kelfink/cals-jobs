package gov.ca.cwds.data.persistence.cms;

import static gov.ca.cwds.neutron.util.transform.JobTransformUtils.ifNull;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.Type;

import gov.ca.cwds.data.es.ElasticSearchAccessLimitation;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonSocialWorker;
import gov.ca.cwds.data.es.ElasticSearchPersonAllegation;
import gov.ca.cwds.data.es.ElasticSearchPersonNestedPerson;
import gov.ca.cwds.data.es.ElasticSearchPersonReferral;
import gov.ca.cwds.data.es.ElasticSearchPersonReporter;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.jobs.component.AtomSecurity;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.util.JobDateUtil;
import gov.ca.cwds.neutron.util.transform.ElasticTransformer;
import gov.ca.cwds.rest.api.domain.DomainChef;
import gov.ca.cwds.rest.api.domain.cms.LegacyTable;
import gov.ca.cwds.rest.api.domain.cms.SystemCodeCache;

/**
 * Entity bean for view VW_LST_REFERRAL_HIST.
 * 
 * <p>
 * Implements {@link ApiGroupNormalizer} and converts to {@link ReplicatedPersonReferrals}.
 * </p>
 * 
 * @author CWDS API Team
 */
@Entity
@Table(name = "VW_LST_REFERRAL_HIST")
@NamedNativeQuery(name = "gov.ca.cwds.data.persistence.cms.EsPersonReferral.findAllUpdatedAfter",
    query = "SELECT DISTINCT " + EsPersonReferral.COLUMNS
        + " FROM {h-schema}VW_LST_REFERRAL_HIST r "
        + "WHERE (1=1 OR current timestamp < :after) ORDER BY CLIENT_ID,REFERRAL_ID,ALLEGATION_ID,VICTIM_ID WITH UR ",
    resultClass = EsPersonReferral.class, readOnly = true)
@NamedNativeQuery(
    name = "gov.ca.cwds.data.persistence.cms.EsPersonReferral.findAllUpdatedAfterWithUnlimitedAccess",
    query = "SELECT DISTINCT " + EsPersonReferral.COLUMNS
        + " FROM {h-schema}VW_LST_REFERRAL_HIST r " + "WHERE (1=1 OR current timestamp < :after)"
        + "AND r.LIMITED_ACCESS_CODE = 'N' ORDER BY CLIENT_ID,REFERRAL_ID,ALLEGATION_ID,VICTIM_ID WITH UR ",
    resultClass = EsPersonReferral.class, readOnly = true)
@NamedNativeQuery(
    name = "gov.ca.cwds.data.persistence.cms.EsPersonReferral.findAllUpdatedAfterWithLimitedAccess",
    query = "SELECT DISTINCT " + EsPersonReferral.COLUMNS
        + " FROM {h-schema}VW_LST_REFERRAL_HIST r " + "WHERE (1=1 OR current timestamp < :after)"
        + "AND r.LIMITED_ACCESS_CODE != 'N' ORDER BY CLIENT_ID,REFERRAL_ID,ALLEGATION_ID,VICTIM_ID WITH UR ",
    resultClass = EsPersonReferral.class, readOnly = true)
public class EsPersonReferral
    implements PersistentObject, ApiGroupNormalizer<ReplicatedPersonReferrals>,
    Comparable<EsPersonReferral>, Comparator<EsPersonReferral> {

  public static final String COLUMNS =
      "r.CLIENT_ID,r.CLIENT_SENSITIVITY_IND,r.REFERRAL_ID,r.START_DATE,r.END_DATE,r.REFERRAL_RESPONSE_TYPE,r.LIMITED_ACCESS_CODE,r.LIMITED_ACCESS_DATE,r.LIMITED_ACCESS_DESCRIPTION,r.LIMITED_ACCESS_GOVERNMENT_ENT,r.REFERRAL_LAST_UPDATED,r.REPORTER_ID,r.REPORTER_FIRST_NM,r.REPORTER_LAST_NM,r.REPORTER_LAST_UPDATED,r.WORKER_ID,r.WORKER_FIRST_NM,r.WORKER_LAST_NM,r.WORKER_LAST_UPDATED,r.PERPETRATOR_ID,r.PERPETRATOR_SENSITIVITY_IND,r.PERPETRATOR_FIRST_NM,r.PERPETRATOR_LAST_NM,r.PERPETRATOR_LAST_UPDATED,r.VICTIM_ID,r.VICTIM_SENSITIVITY_IND,r.VICTIM_FIRST_NM,r.VICTIM_LAST_NM,r.VICTIM_LAST_UPDATED,r.REFERRAL_COUNTY,r.ALLEGATION_ID,r.ALLEGATION_DISPOSITION,r.ALLEGATION_TYPE,r.ALLEGATION_LAST_UPDATED,r.RCT_IBMSNAP_LOGMARKER,r.RCT_IBMSNAP_OPERATION,r.RFL_IBMSNAP_LOGMARKER,r.RFL_IBMSNAP_OPERATION,r.STP_IBMSNAP_LOGMARKER,r.STP_IBMSNAP_OPERATION,r.RPT_IBMSNAP_LOGMARKER,r.RPT_IBMSNAP_OPERATION,r.ALG_IBMSNAP_LOGMARKER,r.ALG_IBMSNAP_OPERATION,r.CLP_IBMSNAP_LOGMARKER,r.CLP_IBMSNAP_OPERATION,r.CLV_IBMSNAP_LOGMARKER,r.CLV_IBMSNAP_OPERATION,r.LAST_CHG";

  private static final long serialVersionUID = -2265057057202257108L;

  private static final String COLUMN_REFERRAL_ID = "REFERRAL_ID";

  private static FlightPlan opts; // WARNING: not a good idea. Try another way

  @Type(type = "timestamp")
  @Column(name = "LAST_CHG", updatable = false)
  private Date lastChange;

  // ===================
  // KEYS:
  // ===================

  @Id
  @Column(name = "CLIENT_ID")
  private String clientId;

  @Transient
  private transient String clientSensitivity;

  @Id
  @Column(name = COLUMN_REFERRAL_ID)
  private String referralId;

  // ===================
  // REFERRAL:
  // ===================

  @Column(name = "START_DATE")
  @Type(type = "date")
  private Date startDate;

  @Column(name = "END_DATE")
  @Type(type = "date")
  private Date endDate;

  @Column(name = "REFERRAL_RESPONSE_TYPE")
  @Type(type = "integer")
  private Integer referralResponseType;

  @Column(name = "REFERRAL_COUNTY")
  @Type(type = "integer")
  private Integer county;

  @Column(name = "REFERRAL_LAST_UPDATED")
  @Type(type = "timestamp")
  private Date referralLastUpdated;

  // =================
  // REPORTER:
  // =================

  @Column(name = "REPORTER_ID")
  private String reporterId;

  @Column(name = "REPORTER_FIRST_NM")
  @ColumnTransformer(read = "trim(REPORTER_FIRST_NM)")
  private String reporterFirstName;

  @Column(name = "REPORTER_LAST_NM")
  @ColumnTransformer(read = "trim(REPORTER_LAST_NM)")
  private String reporterLastName;

  @Column(name = "REPORTER_LAST_UPDATED")
  @Type(type = "timestamp")
  private Date reporterLastUpdated;

  // =================
  // SOCIAL WORKER:
  // =================

  @Column(name = "WORKER_ID")
  private String workerId;

  @Column(name = "WORKER_FIRST_NM")
  @ColumnTransformer(read = "trim(WORKER_FIRST_NM)")
  private String workerFirstName;

  @Column(name = "WORKER_LAST_NM")
  @ColumnTransformer(read = "trim(WORKER_LAST_NM)")
  private String workerLastName;

  @Column(name = "WORKER_LAST_UPDATED")
  @Type(type = "timestamp")
  private Date workerLastUpdated;

  // =============
  // ALLEGATION:
  // =============

  @Id
  @Column(name = "ALLEGATION_ID")
  private String allegationId;

  @Column(name = "ALLEGATION_DISPOSITION")
  @Type(type = "integer")
  private Integer allegationDisposition;

  @Column(name = "ALLEGATION_TYPE")
  @Type(type = "integer")
  private Integer allegationType;

  @Column(name = "ALLEGATION_LAST_UPDATED")
  @Type(type = "timestamp")
  private Date allegationLastUpdated;

  // =============
  // VICTIM:
  // =============

  @Id
  @Column(name = "VICTIM_ID")
  private String victimId;

  @Column(name = "VICTIM_FIRST_NM")
  @ColumnTransformer(read = "trim(VICTIM_FIRST_NM)")
  private String victimFirstName;

  @Column(name = "VICTIM_LAST_NM")
  @ColumnTransformer(read = "trim(VICTIM_LAST_NM)")
  private String victimLastName;

  @Column(name = "VICTIM_LAST_UPDATED")
  @Type(type = "timestamp")
  private Date victimLastUpdated;

  @Column(name = "VICTIM_SENSITIVITY_IND")
  private String victimSensitivityIndicator;

  // =====================
  // ACCESS LIMITATION:
  // =====================

  @Column(name = "LIMITED_ACCESS_CODE")
  private String limitedAccessCode;

  @Column(name = "LIMITED_ACCESS_DATE")
  @Type(type = "date")
  private Date limitedAccessDate;

  @Column(name = "LIMITED_ACCESS_DESCRIPTION")
  @ColumnTransformer(read = "trim(LIMITED_ACCESS_DESCRIPTION)")
  private String limitedAccessDescription;

  @Column(name = "LIMITED_ACCESS_GOVERNMENT_ENT")
  @Type(type = "integer")
  private Integer limitedAccessGovernmentEntityId;

  // =============
  // PERPETRATOR:
  // =============

  @Column(name = "PERPETRATOR_ID")
  private String perpetratorId;

  @Column(name = "PERPETRATOR_FIRST_NM")
  @ColumnTransformer(read = "trim(PERPETRATOR_FIRST_NM)")
  private String perpetratorFirstName;

  @Column(name = "PERPETRATOR_LAST_NM")
  @ColumnTransformer(read = "trim(PERPETRATOR_LAST_NM)")
  private String perpetratorLastName;

  @Column(name = "PERPETRATOR_LAST_UPDATED")
  @Type(type = "timestamp")
  private Date perpetratorLastUpdated;

  @Column(name = "PERPETRATOR_SENSITIVITY_IND")
  private String perpetratorSensitivityIndicator;

  public EsPersonReferral() {
    // Default no-op.
  }

  public EsPersonReferral(final ResultSet rs) throws SQLException {
    this.referralId = ifNull(rs.getString(COLUMN_REFERRAL_ID));
    this.startDate = rs.getDate("START_DATE");
    this.endDate = rs.getDate("END_DATE");
    this.referralResponseType = rs.getInt("REFERRAL_RESPONSE_TYPE");

    this.limitedAccessCode = ifNull(rs.getString("LIMITED_ACCESS_CODE"));
    this.limitedAccessDate = rs.getDate("LIMITED_ACCESS_DATE");
    this.limitedAccessDescription = ifNull(rs.getString("LIMITED_ACCESS_DESCRIPTION"));
    this.limitedAccessGovernmentEntityId = rs.getInt("LIMITED_ACCESS_GOVERNMENT_ENT");
    this.referralLastUpdated = rs.getTimestamp("REFERRAL_LAST_UPDATED");

    this.allegationId = ifNull(rs.getString("ALLEGATION_ID"));
    this.allegationType = rs.getInt("ALLEGATION_TYPE");
    this.allegationDisposition = rs.getInt("ALLEGATION_DISPOSITION");
    this.allegationLastUpdated = rs.getTimestamp("ALLEGATION_LAST_UPDATED");

    this.perpetratorId = ifNull(rs.getString("PERPETRATOR_ID"));
    this.perpetratorFirstName = ifNull(rs.getString("PERPETRATOR_FIRST_NM"));
    this.perpetratorLastName = ifNull(rs.getString("PERPETRATOR_LAST_NM"));
    this.perpetratorLastUpdated = rs.getTimestamp("PERPETRATOR_LAST_UPDATED");
    this.perpetratorSensitivityIndicator = rs.getString("PERPETRATOR_SENSITIVITY_IND");

    this.reporterId = ifNull(rs.getString("REPORTER_ID"));
    this.reporterFirstName = ifNull(rs.getString("REPORTER_FIRST_NM"));
    this.reporterLastName = ifNull(rs.getString("REPORTER_LAST_NM"));
    this.reporterLastUpdated = rs.getTimestamp("REPORTER_LAST_UPDATED");

    this.victimId = ifNull(rs.getString("VICTIM_ID"));
    this.victimFirstName = ifNull(rs.getString("VICTIM_FIRST_NM"));
    this.victimLastName = ifNull(rs.getString("VICTIM_LAST_NM"));
    this.victimLastUpdated = rs.getTimestamp("VICTIM_LAST_UPDATED");
    this.victimSensitivityIndicator = rs.getString("VICTIM_SENSITIVITY_IND");

    this.workerId = ifNull(rs.getString("WORKER_ID"));
    this.workerFirstName = ifNull(rs.getString("WORKER_FIRST_NM"));
    this.workerLastName = ifNull(rs.getString("WORKER_LAST_NM"));
    this.workerLastUpdated = rs.getTimestamp("WORKER_LAST_UPDATED");

    this.county = rs.getInt("REFERRAL_COUNTY");
    this.lastChange = rs.getDate("LAST_CHG");
  }

  /**
   * Extract allegation.
   * 
   * @param rs allegation result set
   * @return allegation side of referral
   * @throws SQLException database error
   */
  public static EsPersonReferral extractAllegation(final ResultSet rs) throws SQLException {
    final EsPersonReferral ret = new EsPersonReferral();

    ret.referralId = ifNull(rs.getString(COLUMN_REFERRAL_ID));
    ret.allegationId = ifNull(rs.getString("ALLEGATION_ID"));
    ret.allegationType = rs.getInt("ALLEGATION_TYPE");
    ret.allegationDisposition = rs.getInt("ALLEGATION_DISPOSITION");
    ret.allegationLastUpdated = rs.getTimestamp("ALLEGATION_LAST_UPDATED");

    ret.perpetratorId = ifNull(rs.getString("PERPETRATOR_ID"));
    ret.perpetratorFirstName = ifNull(rs.getString("PERPETRATOR_FIRST_NM"));
    ret.perpetratorLastName = ifNull(rs.getString("PERPETRATOR_LAST_NM"));
    ret.perpetratorLastUpdated = rs.getTimestamp("PERPETRATOR_LAST_UPDATED");
    ret.perpetratorSensitivityIndicator = rs.getString("PERPETRATOR_SENSITIVITY_IND");

    ret.victimId = ifNull(rs.getString("VICTIM_ID"));
    ret.victimFirstName = ifNull(rs.getString("VICTIM_FIRST_NM"));
    ret.victimLastName = ifNull(rs.getString("VICTIM_LAST_NM"));
    ret.victimLastUpdated = rs.getTimestamp("VICTIM_LAST_UPDATED");
    ret.victimSensitivityIndicator = rs.getString("VICTIM_SENSITIVITY_IND");

    return ret;
  }

  /**
   * Extract referral.
   * 
   * <p>
   * IBM strongly recommends retrieving column results by position and not by column name.
   * </p>
   * 
   * @param rs referral result set
   * @return parent referral element
   * @throws SQLException on DB error
   */
  public static EsPersonReferral extractReferral(final ResultSet rs) throws SQLException {
    final EsPersonReferral ret = new EsPersonReferral();

    ret.referralId = ifNull(rs.getString(COLUMN_REFERRAL_ID));
    ret.startDate = rs.getDate("START_DATE");
    ret.endDate = rs.getDate("END_DATE");
    ret.referralResponseType = rs.getInt("REFERRAL_RESPONSE_TYPE");

    ret.limitedAccessCode = ifNull(rs.getString("LIMITED_ACCESS_CODE"));
    ret.limitedAccessDate = rs.getDate("LIMITED_ACCESS_DATE");
    ret.limitedAccessDescription = ifNull(rs.getString("LIMITED_ACCESS_DESCRIPTION"));
    ret.limitedAccessGovernmentEntityId = rs.getInt("LIMITED_ACCESS_GOVERNMENT_ENT");
    ret.referralLastUpdated = rs.getTimestamp("REFERRAL_LAST_UPDATED");

    ret.reporterId = ifNull(rs.getString("REPORTER_ID"));
    ret.reporterFirstName = ifNull(rs.getString("REPORTER_FIRST_NM"));
    ret.reporterLastName = ifNull(rs.getString("REPORTER_LAST_NM"));
    ret.reporterLastUpdated = rs.getTimestamp("REPORTER_LAST_UPDATED");

    ret.workerId = ifNull(rs.getString("WORKER_ID"));
    ret.workerFirstName = ifNull(rs.getString("WORKER_FIRST_NM"));
    ret.workerLastName = ifNull(rs.getString("WORKER_LAST_NM"));
    ret.workerLastUpdated = rs.getTimestamp("WORKER_LAST_UPDATED");

    ret.county = rs.getInt("REFERRAL_COUNTY");
    ret.lastChange = rs.getDate("LAST_CHG");

    return ret;
  }

  @Override
  public Class<ReplicatedPersonReferrals> getNormalizationClass() {
    return ReplicatedPersonReferrals.class;
  }

  /**
   * Merge common Referral fields into this Allegation before normalizing. Very silly, yes, but it
   * works with existing code without refactoring.
   * 
   * @param clientId target client id
   * @param ref parent referral to merge
   */
  public void mergeClientReferralInfo(String clientId, EsPersonReferral ref) {
    this.clientId = clientId;
    this.referralId = ref.referralId;
    this.county = ref.county;
    this.startDate = ref.startDate;
    this.endDate = ref.endDate;
    this.referralResponseType = ref.referralResponseType;
    this.referralLastUpdated = ref.referralLastUpdated;
    this.limitedAccessCode = ref.limitedAccessCode;
    this.limitedAccessDate = ref.limitedAccessDate;
    this.limitedAccessDescription = ifNull(ref.limitedAccessDescription);
    this.limitedAccessGovernmentEntityId = ref.limitedAccessGovernmentEntityId;
    this.reporterFirstName = ifNull(ref.reporterFirstName);
    this.reporterId = ifNull(ref.reporterId);
    this.reporterLastName = ifNull(ref.reporterLastName);
    this.reporterLastUpdated = ref.reporterLastUpdated;
    this.workerFirstName = ifNull(ref.workerFirstName);
    this.workerId = ref.workerId;
    this.workerLastName = ifNull(ref.workerLastName);
    this.workerLastUpdated = ref.workerLastUpdated;
  }

  private ElasticSearchPersonReporter makeReporter() {
    final ElasticSearchPersonReporter ret = new ElasticSearchPersonReporter();
    ret.setId(this.reporterId);
    ret.setLegacyClientId(this.reporterId);
    ret.setFirstName(ifNull(this.reporterFirstName));
    ret.setLastName(ifNull(this.reporterLastName));
    ret.setLegacyDescriptor(ElasticTransformer.createLegacyDescriptor(this.reporterId,
        this.reporterLastUpdated, LegacyTable.REPORTER));
    return ret;
  }

  private ElasticSearchPersonSocialWorker makeAssignedWorker() {
    final ElasticSearchPersonSocialWorker ret = new ElasticSearchPersonSocialWorker();
    ret.setId(this.workerId);
    ret.setLegacyClientId(this.workerId);
    ret.setFirstName(ifNull(this.workerFirstName));
    ret.setLastName(ifNull(this.workerLastName));
    ret.setLegacyDescriptor(ElasticTransformer.createLegacyDescriptor(this.workerId,
        this.workerLastUpdated, LegacyTable.STAFF_PERSON));
    return ret;
  }

  private ElasticSearchAccessLimitation makeAccessLimitation() {
    final ElasticSearchAccessLimitation ret = new ElasticSearchAccessLimitation();
    ret.setLimitedAccessCode(this.limitedAccessCode);
    ret.setLimitedAccessDate(DomainChef.cookDate(this.limitedAccessDate));
    ret.setLimitedAccessDescription(ifNull(this.limitedAccessDescription));
    ret.setLimitedAccessGovernmentEntityId(this.limitedAccessGovernmentEntityId == null ? null
        : this.limitedAccessGovernmentEntityId.toString());
    ret.setLimitedAccessGovernmentEntityName(SystemCodeCache.global()
        .getSystemCodeShortDescription(this.limitedAccessGovernmentEntityId));
    return ret;
  }

  private ElasticSearchPersonAllegation makeAllegation() {
    final ElasticSearchPersonAllegation ret = new ElasticSearchPersonAllegation();
    ret.setId(this.allegationId);
    ret.setLegacyId(this.allegationId);
    ret.setAllegationDescription(
        SystemCodeCache.global().getSystemCodeShortDescription(this.allegationType));
    ret.setDispositionId(
        this.allegationDisposition == null ? null : this.allegationDisposition.toString());
    ret.setDispositionDescription(
        SystemCodeCache.global().getSystemCodeShortDescription(this.allegationDisposition));
    ret.setLegacyDescriptor(ElasticTransformer.createLegacyDescriptor(this.allegationId,
        this.allegationLastUpdated, LegacyTable.ALLEGATION));
    return ret;
  }

  private ElasticSearchPersonNestedPerson makePerpetrator() {
    final ElasticSearchPersonNestedPerson perpetrator = new ElasticSearchPersonNestedPerson();
    perpetrator.setId(this.perpetratorId);
    perpetrator.setFirstName(ifNull(this.perpetratorFirstName));
    perpetrator.setLastName(ifNull(this.perpetratorLastName));
    perpetrator.setLegacyDescriptor(ElasticTransformer.createLegacyDescriptor(this.perpetratorId,
        this.perpetratorLastUpdated, LegacyTable.CLIENT));
    perpetrator.setSensitivityIndicator(perpetratorSensitivityIndicator);
    return perpetrator;
  }

  private ElasticSearchPersonNestedPerson makeVictim() {
    final ElasticSearchPersonNestedPerson victim = new ElasticSearchPersonNestedPerson();
    victim.setId(this.victimId);
    victim.setFirstName(ifNull(this.victimFirstName));
    victim.setLastName(ifNull(this.victimLastName));
    victim.setLegacyDescriptor(ElasticTransformer.createLegacyDescriptor(this.victimId,
        this.victimLastUpdated, LegacyTable.CLIENT));
    victim.setSensitivityIndicator(victimSensitivityIndicator);
    return victim;
  }

  @Override
  public ReplicatedPersonReferrals normalize(Map<Object, ReplicatedPersonReferrals> map) {
    ReplicatedPersonReferrals ret = map.get(this.clientId);
    if (ret == null) {
      ret = new ReplicatedPersonReferrals(this.clientId);
      map.put(this.clientId, ret);
    }

    final boolean isNewReferral = !ret.hasReferral(this.referralId);
    final ElasticSearchPersonReferral r =
        !isNewReferral ? ret.getReferral(referralId) : new ElasticSearchPersonReferral();

    if (isNewReferral) {
      r.setId(this.referralId);
      r.setLegacyId(this.referralId);
      r.setLegacyLastUpdated(DomainChef.cookStrictTimestamp(this.referralLastUpdated));
      r.setStartDate(DomainChef.cookDate(this.startDate));
      r.setEndDate(DomainChef.cookDate(this.endDate));
      r.setCountyId(this.county == null ? null : this.county.toString());
      r.setCountyName(SystemCodeCache.global().getSystemCodeShortDescription(this.county));
      r.setResponseTimeId(
          this.referralResponseType == null ? null : this.referralResponseType.toString());
      r.setResponseTime(
          SystemCodeCache.global().getSystemCodeShortDescription(this.referralResponseType));
      r.setLegacyDescriptor(ElasticTransformer.createLegacyDescriptor(this.referralId,
          this.referralLastUpdated, LegacyTable.REFERRAL));

      r.setReporter(makeReporter());
      r.setAssignedSocialWorker(makeAssignedWorker());
      r.setAccessLimitation(makeAccessLimitation());
    }

    // A referral may have multiple allegations.
    final ElasticSearchPersonAllegation allegation = makeAllegation();

    if (AtomSecurity.isNotSealedSensitive(opts, perpetratorSensitivityIndicator)) {
      allegation.setPerpetrator(makePerpetrator());

      // NOTE: #148091785: deprecated person fields.
      allegation.setPerpetratorId(this.perpetratorId);
      allegation.setPerpetratorLegacyClientId(this.perpetratorId);
      allegation.setPerpetratorFirstName(ifNull(this.perpetratorFirstName));
      allegation.setPerpetratorLastName(ifNull(this.perpetratorLastName));
    }

    if (AtomSecurity.isNotSealedSensitive(opts, victimSensitivityIndicator)) {
      allegation.setVictim(makeVictim());

      // NOTE: #148091785: deprecated person fields.
      allegation.setVictimId(this.victimId);
      allegation.setVictimLegacyClientId(this.victimId);
      allegation.setVictimFirstName(ifNull(this.victimFirstName));
      allegation.setVictimLastName(ifNull(this.victimLastName));
    }

    ret.addReferral(r, allegation);
    return ret;
  }

  @Override
  public String getNormalizationGroupKey() {
    return this.clientId;
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
    return JobDateUtil.freshDate(lastChange);
  }

  /**
   * Setter for composite "last change", the latest time that any associated record was created or
   * updated.
   * 
   * @param lastChange last change date
   */
  public void setLastChange(Date lastChange) {
    this.lastChange = JobDateUtil.freshDate(lastChange);
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getReferralId() {
    return referralId;
  }

  public void setReferralId(String referralId) {
    this.referralId = referralId;
  }

  public Date getStartDate() {
    return JobDateUtil.freshDate(startDate);
  }

  public void setStartDate(Date startDate) {
    this.startDate = JobDateUtil.freshDate(startDate);
  }

  public Date getEndDate() {
    return JobDateUtil.freshDate(endDate);
  }

  public void setEndDate(Date endDate) {
    this.endDate = JobDateUtil.freshDate(endDate);
  }

  public Integer getReferralResponseType() {
    return referralResponseType;
  }

  public void setReferralResponseType(Integer referralResponseType) {
    this.referralResponseType = referralResponseType;
  }

  public Integer getCounty() {
    return county;
  }

  public void setCounty(Integer county) {
    this.county = county;
  }

  public String getReporterId() {
    return reporterId;
  }

  public void setReporterId(String reporterId) {
    this.reporterId = reporterId;
  }

  public String getReporterFirstName() {
    return reporterFirstName;
  }

  public void setReporterFirstName(String reporterFirstName) {
    this.reporterFirstName = reporterFirstName;
  }

  public String getReporterLastName() {
    return reporterLastName;
  }

  public void setReporterLastName(String reporterLastName) {
    this.reporterLastName = reporterLastName;
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

  public String getAllegationId() {
    return allegationId;
  }

  public void setAllegationId(String allegationId) {
    this.allegationId = allegationId;
  }

  public Integer getAllegationDisposition() {
    return allegationDisposition;
  }

  public void setAllegationDisposition(Integer allegationDisposition) {
    this.allegationDisposition = allegationDisposition;
  }

  public Integer getAllegationType() {
    return allegationType;
  }

  public void setAllegationType(Integer allegationType) {
    this.allegationType = allegationType;
  }

  public String getVictimId() {
    return victimId;
  }

  public void setVictimId(String victimId) {
    this.victimId = victimId;
  }

  public String getVictimFirstName() {
    return victimFirstName;
  }

  public void setVictimFirstName(String victimFirstName) {
    this.victimFirstName = victimFirstName;
  }

  public String getVictimLastName() {
    return victimLastName;
  }

  public void setVictimLastName(String victimLastName) {
    this.victimLastName = victimLastName;
  }

  public String getPerpetratorId() {
    return perpetratorId;
  }

  public void setPerpetratorId(String perpetratorId) {
    this.perpetratorId = perpetratorId;
  }

  public String getPerpetratorFirstName() {
    return perpetratorFirstName;
  }

  public void setPerpetratorFirstName(String perpetratorFirstName) {
    this.perpetratorFirstName = perpetratorFirstName;
  }

  public String getPerpetratorLastName() {
    return perpetratorLastName;
  }

  public void setPerpetratorLastName(String perpetratorLastName) {
    this.perpetratorLastName = perpetratorLastName;
  }

  public String getLimitedAccessCode() {
    return limitedAccessCode;
  }

  public void setLimitedAccessCode(String limitedAccessCode) {
    this.limitedAccessCode = limitedAccessCode;
  }

  public Date getLimitedAccessDate() {
    return JobDateUtil.freshDate(limitedAccessDate);
  }

  public void setLimitedAccessDate(Date limitedAccessDate) {
    this.limitedAccessDate = JobDateUtil.freshDate(limitedAccessDate);
  }

  public String getLimitedAccessDescription() {
    return limitedAccessDescription;
  }

  public void setLimitedAccessDescription(String limitedAccessDescription) {
    this.limitedAccessDescription = limitedAccessDescription;
  }

  public Integer getLimitedAccessGovernmentEntityId() {
    return limitedAccessGovernmentEntityId;
  }

  public void setLimitedAccessGovernmentEntityId(Integer limitedAccessGovernmentEntityId) {
    this.limitedAccessGovernmentEntityId = limitedAccessGovernmentEntityId;
  }

  public Date getReferralLastUpdated() {
    return JobDateUtil.freshDate(referralLastUpdated);
  }

  public void setReferralLastUpdated(Date referralLastUpdated) {
    this.referralLastUpdated = JobDateUtil.freshDate(referralLastUpdated);
  }

  public Date getReporterLastUpdated() {
    return JobDateUtil.freshDate(reporterLastUpdated);
  }

  public void setReporterLastUpdated(Date reporterLastUpdated) {
    this.reporterLastUpdated = JobDateUtil.freshDate(reporterLastUpdated);
  }

  public Date getWorkerLastUpdated() {
    return JobDateUtil.freshDate(workerLastUpdated);
  }

  public void setWorkerLastUpdated(Date workerLastUpdated) {
    this.workerLastUpdated = JobDateUtil.freshDate(workerLastUpdated);
  }

  public Date getAllegationLastUpdated() {
    return JobDateUtil.freshDate(allegationLastUpdated);
  }

  public void setAllegationLastUpdated(Date allegationLastUpdated) {
    this.allegationLastUpdated = JobDateUtil.freshDate(allegationLastUpdated);
  }

  public Date getVictimLastUpdated() {
    return JobDateUtil.freshDate(victimLastUpdated);
  }

  public void setVictimLastUpdated(Date victimLastUpdated) {
    this.victimLastUpdated = JobDateUtil.freshDate(victimLastUpdated);
  }

  public Date getPerpetratorLastUpdated() {
    return JobDateUtil.freshDate(perpetratorLastUpdated);
  }

  public void setPerpetratorLastUpdated(Date perpetratorLastUpdated) {
    this.perpetratorLastUpdated = JobDateUtil.freshDate(perpetratorLastUpdated);
  }

  public String getVictimSensitivityIndicator() {
    return victimSensitivityIndicator;
  }

  public void setVictimSensitivityIndicator(String victimSensitivityIndicator) {
    this.victimSensitivityIndicator = victimSensitivityIndicator;
  }

  public String getPerpetratorSensitivityIndicator() {
    return perpetratorSensitivityIndicator;
  }

  public void setPerpetratorSensitivityIndicator(String perpetratorSensitivityIndicator) {
    this.perpetratorSensitivityIndicator = perpetratorSensitivityIndicator;
  }

  public String getClientSensitivity() {
    return clientSensitivity;
  }

  public void setClientSensitivity(String clientSensitivity) {
    this.clientSensitivity = clientSensitivity;
  }

  /**
   * WARNING: Bad idea. Inject or access by other means.
   * 
   * @param opts job options
   */
  public static void setOpts(FlightPlan opts) {
    EsPersonReferral.opts = opts;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE, false);
  }

  @Override
  public int compare(EsPersonReferral o1, EsPersonReferral o2) {
    return o1.getClientId().compareTo(o2.getClientId());
  }

  @Override
  public int compareTo(EsPersonReferral o) {
    return compare(this, o);
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
