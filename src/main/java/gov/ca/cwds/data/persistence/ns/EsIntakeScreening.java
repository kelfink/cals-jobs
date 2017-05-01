package gov.ca.cwds.data.persistence.ns;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.Type;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;

/**
 * Entity bean for PostgreSQL view, VW_SCREENING_HISTORY.
 * 
 * <p>
 * Implements {@link ApiGroupNormalizer} and converts to {@link IntakeScreening}.
 * </p>
 * 
 * @author CWDS API Team
 */
@Entity
@Table(name = "VW_SCREENING_HISTORY")
@NamedNativeQueries({
    @NamedNativeQuery(name = "gov.ca.cwds.data.persistence.ns.EsNsScreeningHistory.findBucketRange",
        query = "SELECT vw.* FROM {h-schema}VW_SCREENING_HISTORY vw "
            + "WHERE vw.SCREENING_ID BETWEEN :min_id AND :max_id "
            + "ORDER BY vw.SCREENING_ID FOR READ ONLY",
        resultClass = EsIntakeScreening.class, readOnly = true),
    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.ns.EsNsScreeningHistory.findAllUpdatedAfter",
        query = "SELECT vw.* FROM {h-schema}VW_SCREENING_HISTORY vw "
            + "WHERE vw.LAST_CHG > CAST(:after AS TIMESTAMP) "
            + "ORDER BY vw.SCREENING_ID FOR READ ONLY ",
        resultClass = EsIntakeScreening.class, readOnly = true)})
public class EsIntakeScreening implements PersistentObject, ApiGroupNormalizer<IntakeScreening> {

  private static final Logger LOGGER = LogManager.getLogger(EsIntakeScreening.class);

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

  @Id
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

  // =============
  // REPORTER:
  // =============

  @Column(name = "RPTR_PRT_ID")
  private String rptrPartcipantId;

  @Column(name = "RPTR_BIRTH_DT")
  private String reporterBirthDt;

  @Column(name = "RPTR_FIRST_NAME")
  private String reporterFirstName;

  @Column(name = "RPTR_LAST_NAME")
  private String reporterLastName;

  @Column(name = "RPTR_GENDER")
  private String reporterGender;

  @Column(name = "RPTR_SSN")
  private String reporterSsn;

  @Column(name = "RPTR_PERSON_ID")
  private String reporterPersonId;

  @Column(name = "RPTR_ROLES")
  private String reporterRoles;

  // =============
  // ALLEGATION:
  // =============

  @Column(name = "ALG_ID")
  private String allegationId;

  @Column(name = "ALLEGATION_TYPES")
  private String allegationTypes;

  @Column(name = "ALG_CRDT")
  private String allegationCrdt;

  @Column(name = "ALG_UPDT")
  private String allegationUpdt;

  @Column(name = "PERPETRATOR_ID")
  private String perpetratorId;

  @Column(name = "VICTIM_ID")
  private String victimId;

  // =============
  // VICTIM:
  // =============

  @Column(name = "VICT_PRT_ID")
  private String victimParticipantId;

  @Column(name = "VICT_BIRTH_DT")
  private String victimBirthDt;

  @Column(name = "VICT_FIRST_NAME")
  private String victimFirstName;

  @Column(name = "VICT_LAST_NAME")
  private String victimLastName;

  @Column(name = "VICT_GENDER")
  private String victimGender;

  @Column(name = "VICT_SSN")
  private String victimSsn;

  @Column(name = "VICT_PERSON_ID")
  private String victimPersonId;

  @Column(name = "VICT_ROLES")
  private String victimRoles;

  // =============
  // PERPETRATOR:
  // =============

  @Column(name = "PERP_PRT_ID")
  private String perpetratorParticipantId;

  @Column(name = "PERP_BIRTH_DT")
  private String perpetratorBirthDt;

  @Column(name = "PERP_FIRST_NAME")
  private String perpetratorFirstName;

  @Column(name = "PERP_LAST_NAME")
  private String perpetratorLastName;

  @Column(name = "PERP_GENDER")
  private String perpetratorGender;

  @Column(name = "PERP_SSN")
  private String perpetratorSsn;

  @Column(name = "PERP_PERSON_ID")
  private String perpetratorPersonId;

  @Column(name = "PERP_ROLES")
  private String perpetratorRoles;

  /**
   * Build an EsNsScreeningHistory from an incoming ResultSet.
   * 
   * @param rs incoming tuple
   * @return a populated EsNsScreeningHistory
   * @throws SQLException if unable to convert types or stream breaks, etc.
   */
  public static EsIntakeScreening produceFromResultSet(ResultSet rs) throws SQLException {
    EsIntakeScreening ret = new EsIntakeScreening();

    // ret.setCltAdjudicatedDelinquentIndicator(rs.getString("CLT_ADJDEL_IND"));

    // ret.setCltReplicationOperation(strToRepOp(rs.getString("CLT_IBMSNAP_OPERATION")));
    // ret.setAdrEmergencyNumber(rs.getBigDecimal("ADR_EMRG_TELNO"));
    // ret.setAdrEmergencyExtension(rs.getInt("ADR_EMRG_EXTNO"));

    return ret;
  }

  @Override
  public Class<IntakeScreening> getReductionClass() {
    return IntakeScreening.class;
  }

  @Override
  public void reduce(Map<Object, IntakeScreening> map) {
    final boolean isClientAdded = map.containsKey(this.screeningId);
    IntakeScreening ret = isClientAdded ? map.get(this.screeningId) : new IntakeScreening();

    if (!isClientAdded) {
      // Populate core client attributes.
      // ret.setAdjudicatedDelinquentIndicator(getCltAdjudicatedDelinquentIndicator());
    }

    // Client Address:
    // if (StringUtils.isNotBlank(getClaId())) {
    // ReplicatedClientAddress rca = new ReplicatedClientAddress();
    // rca.setAddressType(getClaAddressType());
    // ret.addClientAddress(rca);
    //
    // // Address proper:
    // // if (StringUtils.isNotBlank(getAdrId())) {
    // // ReplicatedAddress adr = new ReplicatedAddress();
    // // adr.setAddressDescription(getAdrAddressDescription());
    // // adr.setId(getAdrId());
    // // rca.addAddress(adr);
    // // }
    // }

    map.put(ret.getId(), ret);
  }

  @Override
  public Object getGroupKey() {
    return this.screeningId;
  }

  /**
   * This view (i.e., materialized query table) doesn't have a proper unique key, but a combination
   * of several fields might come close.
   * <ul>
   * <li>"Cook": convert String parameter to strong type</li>
   * <li>"Uncook": convert strong type parameter to String</li>
   * </ul>
   *
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

  public Date getLastChange() {
    return lastChange;
  }

  public void setLastChange(Date lastChange) {
    this.lastChange = lastChange;
  }

}
