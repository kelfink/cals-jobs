package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.data.es.ElasticSearchAccessLimitation;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonSocialWorker;
import gov.ca.cwds.data.es.ElasticSearchPersonAllegation;
import gov.ca.cwds.data.es.ElasticSearchPersonNestedPerson;
import gov.ca.cwds.data.es.ElasticSearchPersonReferral;
import gov.ca.cwds.data.es.ElasticSearchPersonReporter;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.data.std.ApiObjectIdentity;
import gov.ca.cwds.jobs.util.transform.ElasticTransformer;
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
@NamedNativeQueries({@NamedNativeQuery(
    name = "gov.ca.cwds.data.persistence.cms.EsPersonReferral.findAllUpdatedAfter",
    query = "SELECT r.* FROM {h-schema}VW_LST_REFERRAL_HIST r WHERE r.CLIENT_ID IN ( "
        + "SELECT r1.CLIENT_ID FROM {h-schema}VW_LST_REFERRAL_HIST r1 "
        + "WHERE r1.LAST_CHG > CAST(:after AS TIMESTAMP) "
        + ") ORDER BY CLIENT_ID FOR READ ONLY WITH UR ",
    resultClass = EsPersonReferral.class, readOnly = true),

    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.cms.EsPersonReferral.findAllUpdatedAfterWithUnlimitedAccess",
        query = "SELECT r.* FROM {h-schema}VW_LST_REFERRAL_HIST r WHERE r.CLIENT_ID IN ( "
            + "SELECT r1.CLIENT_ID FROM {h-schema}VW_LST_REFERRAL_HIST r1 "
            + "WHERE r1.LAST_CHG > CAST(:after AS TIMESTAMP) "
            + ") AND r.LIMITED_ACCESS_CODE = 'N'  ORDER BY CLIENT_ID FOR READ ONLY WITH UR ",
        resultClass = EsPersonReferral.class, readOnly = true),

    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.cms.EsPersonReferral.findAllUpdatedAfterWithLimitedAccess",
        query = "SELECT r.* FROM {h-schema}VW_LST_REFERRAL_HIST r WHERE r.CLIENT_ID IN ( "
            + "SELECT r1.CLIENT_ID FROM {h-schema}VW_LST_REFERRAL_HIST r1 "
            + "WHERE r1.LAST_CHG > CAST(:after AS TIMESTAMP) "
            + ") AND r.LIMITED_ACCESS_CODE != 'N'  ORDER BY CLIENT_ID FOR READ ONLY WITH UR ",
        resultClass = EsPersonReferral.class, readOnly = true)})
public class EsPersonReferral extends ApiObjectIdentity
    implements PersistentObject, ApiGroupNormalizer<ReplicatedPersonReferrals> {

  private static final Logger LOGGER = LoggerFactory.getLogger(EsPersonReferral.class);

  private static final long serialVersionUID = -2265057057202257108L;

  @Type(type = "timestamp")
  @Column(name = "LAST_CHG", updatable = false)
  private Date lastChange;

  // ================
  // KEYS:
  // ================

  @Id
  @Column(name = "CLIENT_ID")
  private String clientId;

  @Id
  @Column(name = "REFERRAL_ID")
  private String referralId;

  // ================
  // REFERRAL:
  // ================

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

  // ==============
  // REPORTER:
  // ==============

  @Column(name = "REPORTER_ID")
  private String reporterId;

  @Column(name = "REPORTER_FIRST_NM")
  private String reporterFirstName;

  @Column(name = "REPORTER_LAST_NM")
  private String reporterLastName;

  @Column(name = "REPORTER_LAST_UPDATED")
  @Type(type = "timestamp")
  private Date reporterLastUpdated;

  // ==============
  // SOCIAL WORKER:
  // ==============

  @Column(name = "WORKER_ID")
  private String workerId;

  @Column(name = "WORKER_FIRST_NM")
  private String workerFirstName;

  @Column(name = "WORKER_LAST_NM")
  private String workerLastName;

  @Column(name = "WORKER_LAST_UPDATED")
  @Type(type = "timestamp")
  private Date workerLastUpdated;

  // =============
  // ALLEGATION:
  // =============

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

  @Column(name = "VICTIM_ID")
  private String victimId;

  @Column(name = "VICTIM_FIRST_NM")
  private String victimFirstName;

  @Column(name = "VICTIM_LAST_NM")
  private String victimLastName;

  @Column(name = "VICTIM_LAST_UPDATED")
  @Type(type = "timestamp")
  private Date victimLastUpdated;

  @Column(name = "VICTIM_SENSITIVITY_IND")
  private String victimSensitivityIndicator;

  // ==================
  // ACCESS LIMITATION:
  // ==================

  @Column(name = "LIMITED_ACCESS_CODE")
  private String limitedAccessCode;

  @Column(name = "LIMITED_ACCESS_DATE")
  @Type(type = "date")
  private Date limitedAccessDate;

  @Column(name = "LIMITED_ACCESS_DESCRIPTION")
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
  private String perpetratorFirstName;

  @Column(name = "PERPETRATOR_LAST_NM")
  private String perpetratorLastName;

  @Column(name = "PERPETRATOR_LAST_UPDATED")
  @Type(type = "timestamp")
  private Date perpetratorLastUpdated;

  @Column(name = "PERPETRATOR_SENSITIVITY_IND")
  private String perpetratorSensitivityIndicator;

  // =============
  // REDUCE:
  // =============

  public static enum COLUMN_TYPE {

    CHAR,

    SMALLINT,

    /**
     * 2005-11-08
     */
    DATE,

    /**
     * 2017-08-04 14:39:17.021616
     */
    TIMESTAMP
  }

  public static Date uncookStrictTimestampString(String s) {
    String trimTimestamp = StringUtils.trim(s);
    if (StringUtils.isNotEmpty(trimTimestamp)) {
      try {
        final Timestamp ts = Timestamp.valueOf(s);
        return new Date(ts.getTime());
      } catch (Exception e) {
        throw e;
      }
    }
    return null;
  }

  static Date parseTimestamp(String s) {
    return uncookStrictTimestampString(s);
  }

  static Date parseDate(String s) {
    return DomainChef.uncookDateString(s);
  }

  static Integer parseInteger(String s) {
    return StringUtils.isNotBlank(s) ? Integer.parseInt(s) : null;
  }

  public enum COLUMN_POSITION {

    CLIENT_ID(COLUMN_TYPE.CHAR, (c, r) -> {
      r.setClientId(c);
    }),

    /**
     * TODO: missing column.
     */
    CLIENT_SENSITIVITY_IND(COLUMN_TYPE.CHAR),

    REFERRAL_ID(COLUMN_TYPE.CHAR, (c, r) -> {
      r.setReferralId(c);
    }),

    ALLEGATION_ID(COLUMN_TYPE.CHAR, (c, r) -> {
      r.setAllegationId(c);
    }),

    VICTIM_ID(COLUMN_TYPE.CHAR, (c, r) -> {
      r.setVictimId(c);
    }),

    PERPETRATOR_ID(COLUMN_TYPE.CHAR, (c, r) -> {
      r.setPerpetratorId(c);
    }),

    REPORTER_ID(COLUMN_TYPE.CHAR, (c, r) -> {
      r.setReporterId(c);
    }),

    WORKER_ID(COLUMN_TYPE.CHAR, (c, r) -> {
      r.setWorkerId(c);
    }),

    START_DATE(COLUMN_TYPE.DATE, null, (c, r) -> {
      r.setStartDate(c);
    }),

    END_DATE(COLUMN_TYPE.DATE, null, (c, r) -> {
      r.setEndDate(c);
    }),

    REFERRAL_RESPONSE_TYPE(COLUMN_TYPE.SMALLINT, (c, r) -> {
      r.setReferralResponseType(parseInteger(c));
    }),

    LIMITED_ACCESS_CODE(COLUMN_TYPE.CHAR, (c, r) -> {
      r.setLimitedAccessCode(c);
    }),

    LIMITED_ACCESS_DATE(COLUMN_TYPE.DATE, null, (c, r) -> {
      r.setLimitedAccessDate(c);
    }),

    LIMITED_ACCESS_DESCRIPTION(COLUMN_TYPE.CHAR, (c, r) -> {
      r.setLimitedAccessDescription(c);
    }),

    LIMITED_ACCESS_GOVERNMENT_ENT(COLUMN_TYPE.SMALLINT, (c, r) -> {
      r.setLimitedAccessGovernmentEntityId(parseInteger(c));
    }),

    REFERRAL_LAST_UPDATED(COLUMN_TYPE.TIMESTAMP, null, (c, r) -> {
      r.setReferralLastUpdated(c);
    }),

    REPORTER_FIRST_NM(COLUMN_TYPE.CHAR, (c, r) -> {
      r.setReporterFirstName(c);
    }),

    REPORTER_LAST_NM(COLUMN_TYPE.CHAR, (c, r) -> {
      r.setReporterLastName(c);
    }),

    REPORTER_LAST_UPDATED(COLUMN_TYPE.TIMESTAMP, null, (c, r) -> {
      r.setReporterLastUpdated(c);
    }),

    WORKER_FIRST_NM(COLUMN_TYPE.CHAR, (c, r) -> {
      r.setWorkerFirstName(c);
    }),

    WORKER_LAST_NM(COLUMN_TYPE.CHAR, (c, r) -> {
      r.setWorkerLastName(c);
    }),

    WORKER_LAST_UPDATED(COLUMN_TYPE.TIMESTAMP, null, (c, r) -> {
      r.setWorkerLastUpdated(c);
    }),

    VICTIM_SENSITIVITY_IND(COLUMN_TYPE.CHAR, (c, r) -> {
      r.setVictimSensitivityIndicator(c);
    }),

    VICTIM_FIRST_NM(COLUMN_TYPE.CHAR, (c, r) -> {
      r.setVictimFirstName(c);
    }),

    VICTIM_LAST_NM(COLUMN_TYPE.CHAR, (c, r) -> {
      r.setVictimLastName(c);
    }),

    VICTIM_LAST_UPDATED(COLUMN_TYPE.TIMESTAMP, null, (c, r) -> {
      r.setVictimLastUpdated(c);
    }),

    PERPETRATOR_SENSITIVITY_IND(COLUMN_TYPE.CHAR, (c, r) -> {
      r.setPerpetratorSensitivityIndicator(c);
    }),

    PERPETRATOR_FIRST_NM(COLUMN_TYPE.CHAR, (c, r) -> {
      r.setPerpetratorFirstName(c);
    }),

    PERPETRATOR_LAST_NM(COLUMN_TYPE.CHAR, (c, r) -> {
      r.setPerpetratorLastName(c);
    }),

    PERPETRATOR_LAST_UPDATED(COLUMN_TYPE.TIMESTAMP, null, (c, r) -> {
      r.setPerpetratorLastUpdated(c);
    }),

    /**
     * Not in MQT but will be needed for authorization.
     */
    REFERRAL_COUNTY(COLUMN_TYPE.SMALLINT),

    ALLEGATION_DISPOSITION(COLUMN_TYPE.SMALLINT, (c, r) -> {
      r.setAllegationDisposition(parseInteger(c));
    }),

    ALLEGATION_TYPE(COLUMN_TYPE.SMALLINT, (c, r) -> {
      r.setAllegationType(parseInteger(c));
    }),

    ALLEGATION_LAST_UPDATED(COLUMN_TYPE.TIMESTAMP, null, (c, r) -> {
      r.setAllegationLastUpdated(c);
    }),

    /**
     * In MQT but not in ES person document.
     */
    RFL_IBMSNAP_LOGMARKER(COLUMN_TYPE.TIMESTAMP),

    /**
     * In MQT but not in ES person document.
     */
    RFL_IBMSNAP_OPERATION(COLUMN_TYPE.CHAR),

    /**
     * In MQT but not in ES person document.
     */
    STP_IBMSNAP_LOGMARKER(COLUMN_TYPE.TIMESTAMP),

    /**
     * In MQT but not in ES person document.
     */
    STP_IBMSNAP_OPERATION(COLUMN_TYPE.CHAR),

    /**
     * In MQT but not in ES person document.
     */
    RPT_IBMSNAP_LOGMARKER(COLUMN_TYPE.TIMESTAMP),

    /**
     * In MQT but not in ES person document.
     */
    RPT_IBMSNAP_OPERATION(COLUMN_TYPE.CHAR),

    /**
     * In MQT but not in ES person document.
     */
    ALG_IBMSNAP_LOGMARKER(COLUMN_TYPE.TIMESTAMP),

    /**
     * In MQT but not in ES person document.
     */
    ALG_IBMSNAP_OPERATION(COLUMN_TYPE.CHAR),

    /**
     * In MQT but not in ES person document.
     */
    CLV_IBMSNAP_LOGMARKER(COLUMN_TYPE.TIMESTAMP),

    /**
     * In MQT but not in ES person document.
     */
    CLV_IBMSNAP_OPERATION(COLUMN_TYPE.CHAR),

    /**
     * In MQT but not in ES person document.
     */
    CLP_IBMSNAP_LOGMARKER(COLUMN_TYPE.TIMESTAMP),

    /**
     * In MQT but not in ES person document.
     */
    CLP_IBMSNAP_OPERATION(COLUMN_TYPE.CHAR);

    private final COLUMN_TYPE type;

    private final BiConsumer<String, EsPersonReferral> handler;

    private final BiConsumer<Date, EsPersonReferral> dateHandler;

    COLUMN_POSITION(COLUMN_TYPE type) {
      this.type = type;
      this.handler = (c, r) -> {
        LOGGER.debug("No handler for column: {}, value: {}", this.name(), c);
      };
      this.dateHandler = (c, r) -> {
        LOGGER.debug("DEFAULT DATE HANDLER. column: {}", this.name());
      };
    }

    COLUMN_POSITION(COLUMN_TYPE type, BiConsumer<String, EsPersonReferral> handler) {
      this.type = type;
      this.handler = handler;
      this.dateHandler = (c, r) -> {
        LOGGER.debug("DEFAULT DATE HANDLER. column: {}", this.name());
      };
    }

    COLUMN_POSITION(COLUMN_TYPE type, BiConsumer<String, EsPersonReferral> handler,
        BiConsumer<Date, EsPersonReferral> dateHandler) {
      this.type = type;
      this.handler = handler;
      this.dateHandler = dateHandler;
    }

    private final void handle(String c, EsPersonReferral ref) {
      try {
        if (StringUtils.isNotBlank(c)) {
          if (this.type == COLUMN_TYPE.TIMESTAMP && this.dateHandler != null) {
            this.dateHandler.accept(parseTimestamp(c), ref);
          } else if (this.type == COLUMN_TYPE.DATE && this.dateHandler != null) {
            this.dateHandler.accept(parseDate(c), ref);
          } else if (this.handler != null) {
            this.handler.accept(c, ref);
          }
        }
      } catch (Exception e) {
        LOGGER.error("FAILED TO PARSE! column: {}, value: {}", this.name(), c);
        throw e;
      }
    }

    private static final void handleColumn(int pos, String col, EsPersonReferral ref) {
      COLUMN_POSITION.values()[pos].handle(col, ref);
    }

    public static final EsPersonReferral parse(String line) {
      EsPersonReferral ret = new EsPersonReferral();

      int pos = 0;
      for (String c : line.split("\t")) {
        COLUMN_POSITION.handleColumn(pos++, c, ret);
      }

      return ret;
    }

  }

  public static EsPersonReferral parseLine(String line) {
    return EsPersonReferral.COLUMN_POSITION.parse(line);
  }

  @Override
  public Class<ReplicatedPersonReferrals> getNormalizationClass() {
    return ReplicatedPersonReferrals.class;
  }

  @Override
  public ReplicatedPersonReferrals normalize(Map<Object, ReplicatedPersonReferrals> map) {
    ReplicatedPersonReferrals referrals = map.get(this.clientId);
    if (referrals == null) {
      referrals = new ReplicatedPersonReferrals(this.clientId);
      map.put(this.clientId, referrals);
    }

    ElasticSearchPersonReferral r = new ElasticSearchPersonReferral();

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

    //
    // Reporter
    //
    ElasticSearchPersonReporter reporter = new ElasticSearchPersonReporter();
    reporter.setId(this.reporterId);
    reporter.setLegacyClientId(this.reporterId);
    reporter.setFirstName(this.reporterFirstName);
    reporter.setLastName(this.reporterLastName);
    reporter.setLegacyDescriptor(ElasticTransformer.createLegacyDescriptor(this.reporterId,
        this.reporterLastUpdated, LegacyTable.REPORTER));
    r.setReporter(reporter);

    //
    // Assigned Worker
    //
    ElasticSearchPersonSocialWorker assignedWorker = new ElasticSearchPersonSocialWorker();
    assignedWorker.setId(this.workerId);
    assignedWorker.setLegacyClientId(this.workerId);
    assignedWorker.setFirstName(this.workerFirstName);
    assignedWorker.setLastName(this.workerLastName);
    assignedWorker.setLegacyDescriptor(ElasticTransformer.createLegacyDescriptor(this.workerId,
        this.workerLastUpdated, LegacyTable.STAFF_PERSON));
    r.setAssignedSocialWorker(assignedWorker);

    //
    // Access Limitation
    //
    ElasticSearchAccessLimitation accessLimit = new ElasticSearchAccessLimitation();
    accessLimit.setLimitedAccessCode(this.limitedAccessCode);
    accessLimit.setLimitedAccessDate(DomainChef.cookDate(this.limitedAccessDate));
    accessLimit.setLimitedAccessDescription(this.limitedAccessDescription);
    accessLimit.setLimitedAccessGovernmentEntityId(this.limitedAccessGovernmentEntityId == null
        ? null : this.limitedAccessGovernmentEntityId.toString());
    accessLimit.setLimitedAccessGovernmentEntityName(SystemCodeCache.global()
        .getSystemCodeShortDescription(this.limitedAccessGovernmentEntityId));
    r.setAccessLimitation(accessLimit);

    //
    // A referral may have more than one allegations
    //
    ElasticSearchPersonAllegation allegation = new ElasticSearchPersonAllegation();
    allegation.setId(this.allegationId);
    allegation.setLegacyId(this.allegationId);
    allegation.setAllegationDescription(
        SystemCodeCache.global().getSystemCodeShortDescription(this.allegationType));
    allegation.setDispositionId(
        this.allegationDisposition == null ? null : this.allegationDisposition.toString());
    allegation.setDispositionDescription(
        SystemCodeCache.global().getSystemCodeShortDescription(this.allegationDisposition));
    allegation.setLegacyDescriptor(ElasticTransformer.createLegacyDescriptor(this.allegationId,
        this.allegationLastUpdated, LegacyTable.ALLEGATION));

    ElasticSearchPersonNestedPerson perpetrator = new ElasticSearchPersonNestedPerson();
    perpetrator.setId(this.perpetratorId);
    perpetrator.setFirstName(this.perpetratorFirstName);
    perpetrator.setLastName(this.perpetratorLastName);
    perpetrator.setLegacyDescriptor(ElasticTransformer.createLegacyDescriptor(this.perpetratorId,
        this.perpetratorLastUpdated, LegacyTable.CLIENT));
    allegation.setPerpetrator(perpetrator);

    allegation.setPerpetratorId(this.perpetratorId);
    allegation.setPerpetratorLegacyClientId(this.perpetratorId);
    allegation.setPerpetratorFirstName(this.perpetratorFirstName);
    allegation.setPerpetratorLastName(this.perpetratorLastName);

    ElasticSearchPersonNestedPerson victim = new ElasticSearchPersonNestedPerson();
    victim.setId(this.victimId);
    victim.setFirstName(this.victimFirstName);
    victim.setLastName(this.victimLastName);
    victim.setLegacyDescriptor(ElasticTransformer.createLegacyDescriptor(this.victimId,
        this.victimLastUpdated, LegacyTable.CLIENT));
    allegation.setVictim(victim);

    allegation.setVictimId(this.victimId);
    allegation.setVictimLegacyClientId(this.victimId);
    allegation.setVictimFirstName(this.victimFirstName);
    allegation.setVictimLastName(this.victimLastName);

    referrals.addReferral(r, allegation);
    return referrals;
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
    return limitedAccessDate;
  }

  public void setLimitedAccessDate(Date limitedAccessDate) {
    this.limitedAccessDate = limitedAccessDate;
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
    return referralLastUpdated;
  }

  public void setReferralLastUpdated(Date referralLastUpdated) {
    this.referralLastUpdated = referralLastUpdated;
  }

  public Date getReporterLastUpdated() {
    return reporterLastUpdated;
  }

  public void setReporterLastUpdated(Date reporterLastUpdated) {
    this.reporterLastUpdated = reporterLastUpdated;
  }

  public Date getWorkerLastUpdated() {
    return workerLastUpdated;
  }

  public void setWorkerLastUpdated(Date workerLastUpdated) {
    this.workerLastUpdated = workerLastUpdated;
  }

  public Date getAllegationLastUpdated() {
    return allegationLastUpdated;
  }

  public void setAllegationLastUpdated(Date allegationLastUpdated) {
    this.allegationLastUpdated = allegationLastUpdated;
  }

  public Date getVictimLastUpdated() {
    return victimLastUpdated;
  }

  public void setVictimLastUpdated(Date victimLastUpdated) {
    this.victimLastUpdated = victimLastUpdated;
  }

  public Date getPerpetratorLastUpdated() {
    return perpetratorLastUpdated;
  }

  public void setPerpetratorLastUpdated(Date perpetratorLastUpdated) {
    this.perpetratorLastUpdated = perpetratorLastUpdated;
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

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE, false);
  }

  public static void main(String[] args) {
    Path pathIn = Paths.get(args[0]);
    try (Stream<String> lines = Files.lines(pathIn)) {
      // Maintain file order by client, referral.
      final List<EsPersonReferral> results =
          lines.sequential().map(EsPersonReferral::parseLine).collect(Collectors.toList());
      results.stream().forEach(t -> {
        LOGGER.info("Raw Referral: {}", t);
      });
    } catch (Exception e) {
      LOGGER.error("Oops!", e);
    }
  }

}
