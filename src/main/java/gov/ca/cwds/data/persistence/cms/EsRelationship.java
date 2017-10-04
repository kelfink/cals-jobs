package gov.ca.cwds.data.persistence.cms;

import static gov.ca.cwds.jobs.util.transform.JobTransformUtils.ifNull;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.Type;

import gov.ca.cwds.data.es.ElasticSearchPersonRelationship;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.jobs.util.transform.ElasticTransformer;
import gov.ca.cwds.rest.api.domain.cms.LegacyTable;

/**
 * Entity bean for Materialized Query Table (MQT), VW_BI_DIR_RELATION. By request of the Intake
 * team, it only reads relationships from CLN_RELT, for now.
 * 
 * <p>
 * Implements {@link ApiGroupNormalizer} and converts to {@link ReplicatedRelationships}.
 * </p>
 * 
 * @author CWDS API Team
 */
@Entity
@Table(name = "VW_LST_BI_DIR_RELATION")
@NamedNativeQuery(name = "gov.ca.cwds.data.persistence.cms.EsRelationship.findAllUpdatedAfter",
    query = "WITH driver as (\n"
        + " SELECT v1.THIS_LEGACY_ID, v1.RELATED_LEGACY_ID FROM {h-schema}VW_LST_BI_DIR_RELATION v1 \n"
        + "where v1.LAST_CHG > :after \n"
        + ") SELECT v.REVERSE_RELATIONSHIP, v.THIS_LEGACY_ID, v.THIS_SENSITIVITY_IND, \n"
        + "v.THIS_LEGACY_LAST_UPDATED, v.THIS_LEGACY_LAST_UPDATED_ID, v.THIS_FIRST_NAME, v.THIS_LAST_NAME, \n"
        + "v.REL_CODE, v.RELATED_LEGACY_ID, v.RELATED_SENSITIVITY_IND, \n"
        + "v.RELATED_LEGACY_LAST_UPDATED, v.RELATED_LEGACY_LAST_UPDATED_ID, \n"
        + "v.RELATED_FIRST_NAME, v.RELATED_LAST_NAME, v.THIS_IBMSNAP_LOGMARKER, v.THIS_IBMSNAP_OPERATION, \n"
        + "v.RELATED_IBMSNAP_LOGMARKER, v.RELATED_IBMSNAP_OPERATION, v.LAST_CHG \n"
        + "FROM {h-schema}VW_LST_BI_DIR_RELATION v \n"
        + "WHERE v.THIS_LEGACY_ID IN (select d1.THIS_LEGACY_ID from driver d1) \n"
        + "OR v.RELATED_LEGACY_ID IN (select d2.RELATED_LEGACY_ID from driver d2) \n"
        + "ORDER BY THIS_LEGACY_ID, RELATED_LEGACY_ID FOR READ ONLY WITH UR ",
    resultClass = EsRelationship.class, readOnly = true)
@NamedNativeQuery(
    name = "gov.ca.cwds.data.persistence.cms.EsRelationship.findAllUpdatedAfterWithUnlimitedAccess",
    query = "WITH driver as ( \n"
        + " SELECT v1.THIS_LEGACY_ID, v1.RELATED_LEGACY_ID FROM {h-schema}VW_LST_BI_DIR_RELATION v1 \n"
        + "where v1.LAST_CHG > :after \n"
        + ") SELECT v.REVERSE_RELATIONSHIP, v.THIS_LEGACY_ID, v.THIS_SENSITIVITY_IND, \n"
        + "v.THIS_LEGACY_LAST_UPDATED, v.THIS_LEGACY_LAST_UPDATED_ID, v.THIS_FIRST_NAME, v.THIS_LAST_NAME, \n"
        + "v.REL_CODE, v.RELATED_LEGACY_ID, v.RELATED_SENSITIVITY_IND, \n"
        + "v.RELATED_LEGACY_LAST_UPDATED, v.RELATED_LEGACY_LAST_UPDATED_ID, \n"
        + "v.RELATED_FIRST_NAME, v.RELATED_LAST_NAME, v.THIS_IBMSNAP_LOGMARKER, v.THIS_IBMSNAP_OPERATION, \n"
        + "v.RELATED_IBMSNAP_LOGMARKER, v.RELATED_IBMSNAP_OPERATION, v.LAST_CHG \n"
        + "FROM {h-schema}VW_LST_BI_DIR_RELATION v \n"
        + "WHERE (v.THIS_LEGACY_ID IN (select d1.THIS_LEGACY_ID from driver d1) \n"
        + "OR v.RELATED_LEGACY_ID IN (select d2.RELATED_LEGACY_ID from driver d2)) \n"
        + "AND (v.THIS_SENSITIVITY_IND = 'N' AND v.RELATED_SENSITIVITY_IND = 'N') \n"
        + "ORDER BY THIS_LEGACY_ID, RELATED_LEGACY_ID FOR READ ONLY WITH UR ",
    resultClass = EsRelationship.class, readOnly = true)
public class EsRelationship
    implements PersistentObject, ApiGroupNormalizer<ReplicatedRelationships> {

  private static final Map<Short, CmsRelationship> mapRelationCodes = new ConcurrentHashMap<>();

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  @Id
  @Type(type = "boolean")
  @Column(name = "REVERSE_RELATIONSHIP")
  private Boolean reverseRelationship;

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

  @Column(name = "THIS_LEGACY_LAST_UPDATED")
  @Type(type = "date")
  private Date thisLegacyLastUpdated;

  @Column(name = "RELATED_LEGACY_LAST_UPDATED")
  @Type(type = "date")
  private Date relatedLegacyLastUpdated;

  // NOTE: add replication columns when available.
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
  public static EsRelationship mapRow(ResultSet rs) throws SQLException {
    final EsRelationship ret = new EsRelationship();

    ret.setReverseRelationship(rs.getBoolean("REVERSE_RELATIONSHIP"));
    ret.setThisLegacyId(ifNull(rs.getString("THIS_LEGACY_ID")));
    ret.setThisFirstName(ifNull(rs.getString("THIS_FIRST_NAME")));
    ret.setThisLastName(ifNull(rs.getString("THIS_LAST_NAME")));
    ret.setRelCode(rs.getShort("REL_CODE"));
    ret.setRelatedLegacyId(ifNull(rs.getString("RELATED_LEGACY_ID")));
    ret.setRelatedFirstName(ifNull(rs.getString("RELATED_FIRST_NAME")));
    ret.setRelatedLastName(ifNull(rs.getString("RELATED_LAST_NAME")));
    ret.setThisLegacyLastUpdated(rs.getDate("THIS_LEGACY_LAST_UPDATED"));
    ret.setRelatedLegacyLastUpdated(rs.getDate("RELATED_LEGACY_LAST_UPDATED"));

    return ret;
  }

  @Override
  public Class<ReplicatedRelationships> getNormalizationClass() {
    return ReplicatedRelationships.class;
  }

  /**
   * Parse bi-directional relationships and add to appropriate side.
   * 
   * @param rel relationship to modify
   */
  protected void parseBiDirectionalRelationship(final ElasticSearchPersonRelationship rel) {
    if (this.relCode != null && this.relCode.intValue() != 0) {

      CmsRelationship relationship;
      if (mapRelationCodes.containsKey(relCode)) {
        relationship = mapRelationCodes.get(relCode);
      } else {
        relationship = new CmsRelationship(relCode);
        mapRelationCodes.put(relCode, relationship);
      }

      // Reverse relationship direction.
      if (getReverseRelationship() == null || getReverseRelationship().booleanValue()) {
        rel.setIndexedPersonRelationship(relationship.secondaryRel);
        rel.setRelatedPersonRelationship(relationship.primaryRel);
      } else {
        rel.setIndexedPersonRelationship(relationship.primaryRel);
        rel.setRelatedPersonRelationship(relationship.secondaryRel);
      }

      // Context remains the same.
      rel.setRelationshipContext(relationship.relContext);
    }
  }

  /**
   * Implementation notes: Only reading from CLN_RELT, for the moment. Intake will set field
   * "related_person_id" from <strong>PostgreSQL</strong>, NOT from DB2.
   */
  @Override
  public ReplicatedRelationships normalize(Map<Object, ReplicatedRelationships> map) {
    final boolean isClientAdded = map.containsKey(this.thisLegacyId);
    final ReplicatedRelationships ret =
        isClientAdded ? map.get(this.thisLegacyId) : new ReplicatedRelationships(this.thisLegacyId);

    final ElasticSearchPersonRelationship rel = new ElasticSearchPersonRelationship();
    ret.addRelation(rel);

    if (StringUtils.isNotBlank(this.relatedFirstName)) {
      rel.setRelatedPersonFirstName(this.relatedFirstName.trim());
    }

    if (StringUtils.isNotBlank(this.relatedLastName)) {
      rel.setRelatedPersonLastName(this.relatedLastName.trim());
    }

    if (StringUtils.isNotBlank(this.relatedLegacyId)) {
      rel.setRelatedPersonLegacyId(this.relatedLegacyId.trim());
    }

    rel.setLegacyDescriptor(ElasticTransformer.createLegacyDescriptor(this.relatedLegacyId,
        this.relatedLegacyLastUpdated, LegacyTable.CLIENT));

    parseBiDirectionalRelationship(rel);
    map.put(ret.getId(), ret);
    return ret;
  }

  @Override
  public String getNormalizationGroupKey() {
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

  public Boolean getReverseRelationship() {
    return reverseRelationship;
  }

  public void setReverseRelationship(Boolean reverseRelationship) {
    this.reverseRelationship = reverseRelationship;
  }

  public Date getThisLegacyLastUpdated() {
    return thisLegacyLastUpdated;
  }

  public void setThisLegacyLastUpdated(Date thisLegacyLastUpdated) {
    this.thisLegacyLastUpdated = thisLegacyLastUpdated;
  }

  public Date getRelatedLegacyLastUpdated() {
    return relatedLegacyLastUpdated;
  }

  public void setRelatedLegacyLastUpdated(Date relatedLegacyLastUpdated) {
    this.relatedLegacyLastUpdated = relatedLegacyLastUpdated;
  }
}
