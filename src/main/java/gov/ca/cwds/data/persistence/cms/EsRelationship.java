package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.Type;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.rep.CmsReplicationOperation;
import gov.ca.cwds.data.std.ApiGroupNormalizer;

/**
 * Entity bean for Materialized Query Table (MQT), ES_RELATIONSHIP.
 * 
 * <p>
 * Implements {@link ApiGroupNormalizer} and converts to {@link ReplicatedRelationship}.
 * </p>
 * 
 * @author CWDS API Team
 */
@Entity
@Table(name = "ES_RELATIONSHIP")
@NamedNativeQueries({
    @NamedNativeQuery(name = "gov.ca.cwds.data.persistence.cms.EsRelationship.findAllUpdatedAfter",
        query = "SELECT x.* FROM {h-schema}ES_RELATIONSHIP x "
            + "WHERE x.LAST_CHG > CAST(:after AS TIMESTAMP) "
            + "ORDER BY x.clt_IDENTIFIER FOR READ ONLY ",
        resultClass = EsRelationship.class, readOnly = true)})
public class EsRelationship implements PersistentObject, ApiGroupNormalizer<ReplicatedRelationship> {

  // private static final Logger LOGGER = LogManager.getLogger(EsRelationship.class);

  /**
   * Default.
   */
  private static final long serialVersionUID = 1L;

  @Column(name = "CLT_ADPTN_STCD")
  private String cltAdoptionStatusCode;

  @Type(type = "short")
  @Column(name = "CLT_B_CNTRY_C")
  private Short cltBirthCountryCodeType;

  @Type(type = "date")
  @Column(name = "CLT_BIRTH_DT")
  private Date cltBirthDate;


  @Enumerated(EnumType.STRING)
  @Column(name = "CLT_IBMSNAP_OPERATION", updatable = false)
  private CmsReplicationOperation cltReplicationOperation;

  @Type(type = "timestamp")
  @Column(name = "CLT_IBMSNAP_LOGMARKER", updatable = false)
  private Date cltReplicationDate;

  @Column(name = "CLT_LST_UPD_ID")
  private String cltLastUpdatedId;

  @Type(type = "timestamp")
  @Column(name = "CLT_LST_UPD_TS")
  private Date cltLastUpdatedTime;

  /**
   * Convert IBM replication operation to enum.
   * 
   * @param op replication operation, IUD
   * @return enumerated type
   */
  protected static CmsReplicationOperation strToRepOp(String op) {
    return op != null ? CmsReplicationOperation.valueOf(op) : null;
  }

  /**
   * Build an EsRelationship from an incoming ResultSet.
   * 
   * @param rs incoming tuple
   * @return a populated EsRelationship
   * @throws SQLException if unable to convert types or stream breaks, etc.
   */
  public static EsRelationship produceFromResultSet(ResultSet rs) throws SQLException {
    EsRelationship ret = new EsRelationship();

    // ret.setCltAdoptionStatusCode(rs.getString("CLT_ADPTN_STCD"));
    // ret.setCltBirthCountryCodeType(rs.getShort("CLT_B_CNTRY_C"));
    // ret.setCltBirthDate(rs.getDate("CLT_BIRTH_DT"));

    return ret;
  }

  @Override
  public Class<ReplicatedRelationship> getReductionClass() {
    return ReplicatedRelationship.class;
  }

  @Override
  public void reduce(Map<Object, ReplicatedRelationship> map) {
    // final boolean isClientAdded = map.containsKey(this.cltId);
    // Relationship ret = isClientAdded ? map.get(this.cltId) : new Relationship();
    //
    // map.put(ret.getId(), ret);
  }

  @Override
  public Object getGroupKey() {
    // return this.cltId;
    return null;
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

}
