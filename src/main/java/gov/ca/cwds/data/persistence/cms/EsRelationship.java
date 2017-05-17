package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.Type;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;

/**
 * Entity bean for Materialized Query Table (MQT), VW_RELATIONSHIP.
 * 
 * <p>
 * Implements {@link ApiGroupNormalizer} and converts to {@link ReplicatedRelationships}.
 * </p>
 * 
 * @author CWDS API Team
 */
@Entity
@Table(name = "VW_RELATIONSHIP")
@NamedNativeQueries({
    @NamedNativeQuery(name = "gov.ca.cwds.data.persistence.cms.EsRelationship.findAllUpdatedAfter",
        query = "SELECT v.* FROM {h-schema}VW_RELATIONSHIP v "
            + "WHERE v.LAST_CHG > CAST(:after AS TIMESTAMP) "
            + "ORDER BY THIS_LEGACY_ID, RELATED_LEGACY_ID, THIS_LEGACY_TABLE, RELATED_LEGACY_TABLE "
            + "FOR READ ONLY ",
        resultClass = EsRelationship.class, readOnly = true)})
public class EsRelationship
    implements PersistentObject, ApiGroupNormalizer<ReplicatedRelationships> {

  /**
   * Default.
   */
  private static final long serialVersionUID = 1L;

  // TODO: may required an additional id column for uniqueness, like THIRD_ID.

  @Id
  @Column(name = "THIS_LEGACY_TABLE")
  private String thisLegacyTable;

  @Id
  @Column(name = "RELATED_LEGACY_TABLE")
  private String relatedLegacyTable;

  @Id
  @Column(name = "THIS_LEGACY_ID")
  private String thisLegacyId;

  @Column(name = "THIS_FIRST_NAME")
  private String thisFirstName;

  @Column(name = "THIS_LAST_NAME")
  private String thisLastName;

  @Id
  @Type(type = "short")
  @Column(name = "REL_CODE")
  private Short relCode;

  @Id
  @Column(name = "RELATED_LEGACY_ID")
  private String relatedLegacyId;

  @Column(name = "RELATED_FIRST_NAME")
  private String relatedFirstName;

  @Column(name = "RELATED_LAST_NAME")
  private String relatedLastName;

  // TODO: add replication columns when available.
  // Needed to delete ES documents.

  // @Enumerated(EnumType.STRING)
  // @Column(name = "CLT_IBMSNAP_OPERATION", updatable = false)
  // private CmsReplicationOperation cltReplicationOperation;

  // @Type(type = "timestamp")
  // @Column(name = "CLT_IBMSNAP_LOGMARKER", updatable = false)
  // private Date cltReplicationDate;

  /**
   * Build an EsRelationship from an incoming ResultSet.
   * 
   * @param rs incoming tuple
   * @return a populated EsRelationship
   * @throws SQLException if unable to convert types or stream breaks, etc.
   */
  public static EsRelationship produceFromResultSet(ResultSet rs) throws SQLException {
    EsRelationship ret = new EsRelationship();

    ret.setThisLegacyTable(rs.getString("THIS_LEGACY_TABLE"));
    ret.setRelatedLegacyTable(rs.getString("RELATED_LEGACY_TABLE"));
    ret.setThisLegacyId(rs.getString("THIS_LEGACY_ID"));
    ret.setThisFirstName(rs.getString("THIS_FIRST_NAME"));
    ret.setThisLastName(rs.getString("THIS_LAST_NAME"));
    ret.setRelCode(rs.getShort("REL_CODE"));
    ret.setRelatedLegacyId(rs.getString("RELATED_LEGACY_ID"));
    ret.setRelatedFirstName(rs.getString("RELATED_FIRST_NAME"));
    ret.setRelatedLastName(rs.getString("RELATED_LAST_NAME"));

    return ret;
  }

  @Override
  public Class<ReplicatedRelationships> getReductionClass() {
    return ReplicatedRelationships.class;
  }

  @Override
  public void reduce(Map<Object, ReplicatedRelationships> map) {
    final boolean isClientAdded = map.containsKey(this.thisLegacyId);
    final ReplicatedRelationships ret =
        isClientAdded ? map.get(this.thisLegacyId) : new ReplicatedRelationships(this.thisLegacyId);



    map.put(ret.getId(), ret);
  }

  @Override
  public Object getGroupKey() {
    return this.thisLegacyId;
  }

  /**
   * This view (i.e., materialized query table) doesn't have a proper unique key, but a combination
   * of several fields might come close.
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

  public String getThisLegacyTable() {
    return thisLegacyTable;
  }

  public void setThisLegacyTable(String thisLegacyTable) {
    this.thisLegacyTable = thisLegacyTable;
  }

  public String getRelatedLegacyTable() {
    return relatedLegacyTable;
  }

  public void setRelatedLegacyTable(String relatedLegacyTable) {
    this.relatedLegacyTable = relatedLegacyTable;
  }

  public String getThisLegacyId() {
    return thisLegacyId;
  }

  public void setThisLegacyId(String thisLegacyId) {
    this.thisLegacyId = thisLegacyId;
  }

  public String getThisFirstName() {
    return thisFirstName;
  }

  public void setThisFirstName(String thisFirstName) {
    this.thisFirstName = thisFirstName;
  }

  public String getThisLastName() {
    return thisLastName;
  }

  public void setThisLastName(String thisLastName) {
    this.thisLastName = thisLastName;
  }

  public Short getRelCode() {
    return relCode;
  }

  public void setRelCode(Short relCode) {
    this.relCode = relCode;
  }

  public String getRelatedLegacyId() {
    return relatedLegacyId;
  }

  public void setRelatedLegacyId(String relatedLegacyId) {
    this.relatedLegacyId = relatedLegacyId;
  }

  public String getRelatedFirstName() {
    return relatedFirstName;
  }

  public void setRelatedFirstName(String relatedFirstName) {
    this.relatedFirstName = relatedFirstName;
  }

  public String getRelatedLastName() {
    return relatedLastName;
  }

  public void setRelatedLastName(String relatedLastName) {
    this.relatedLastName = relatedLastName;
  }

}
