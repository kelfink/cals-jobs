package gov.ca.cwds.data.persistence.cms;

import static gov.ca.cwds.jobs.transform.JobTransformUtils.ifNull;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.Type;

import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonRelationship;
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
        query = "SELECT v.* FROM {h-schema}VW_RELATIONSHIP v WHERE v.THIS_LEGACY_ID IN ("
            + " SELECT v1.THIS_LEGACY_ID FROM {h-schema}VW_RELATIONSHIP v1 "
            + "WHERE v1.LAST_CHG > CAST(:after AS TIMESTAMP) "
            + ") ORDER BY THIS_LEGACY_ID, RELATED_LEGACY_ID, THIS_LEGACY_TABLE, RELATED_LEGACY_TABLE "
            + "FOR READ ONLY ",
        resultClass = EsRelationship.class, readOnly = true)})
public class EsRelationship
    implements PersistentObject, ApiGroupNormalizer<ReplicatedRelationships> {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LogManager.getLogger(EsRelationship.class);

  private static final Pattern RGX_RELATIONSHIP =
      Pattern.compile("([A-Za-z0-9 _-]+)[/]?([A-Za-z0-9 _-]+)?\\s*(\\([A-Za-z0-9 _-]+\\))?");

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
  public static EsRelationship mapRow(ResultSet rs) throws SQLException {
    EsRelationship ret = new EsRelationship();

    ret.setThisLegacyTable(ifNull(rs.getString("THIS_LEGACY_TABLE")));
    ret.setRelatedLegacyTable(ifNull(rs.getString("RELATED_LEGACY_TABLE")));
    ret.setThisLegacyId(ifNull(rs.getString("THIS_LEGACY_ID")));
    ret.setThisFirstName(ifNull(rs.getString("THIS_FIRST_NAME")));
    ret.setThisLastName(ifNull(rs.getString("THIS_LAST_NAME")));
    ret.setRelCode(rs.getShort("REL_CODE"));
    ret.setRelatedLegacyId(ifNull(rs.getString("RELATED_LEGACY_ID")));
    ret.setRelatedFirstName(ifNull(rs.getString("RELATED_FIRST_NAME")));
    ret.setRelatedLastName(ifNull(rs.getString("RELATED_LAST_NAME")));

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

    ElasticSearchPersonRelationship rel = new ElasticSearchPersonRelationship();
    ret.addRelation(rel);
    rel.setRelatedPersonFirstName(this.relatedFirstName.trim());
    rel.setRelatedPersonLastName(this.relatedLastName.trim());
    rel.setRelatedPersonLegacyId(this.relatedLegacyId);
    rel.setRelatedPersonLegacySourceTable(this.thisLegacyTable.trim());

    // Intake will set field "related_person_id" from **Postgres**, NOT from DB2.
    // rel.setRelatedPersonId(this.); // NOSONAR

    if (this.relCode != null && this.relCode.intValue() != 0) {
      final CmsSystemCode code = ElasticSearchPerson.getSystemCodes().lookup(this.relCode);
      final String wholeRel = code.getShortDsc();
      String primaryRel = "";
      String secondaryRel = "";
      String relContext = "";

      final Matcher m = RGX_RELATIONSHIP.matcher(wholeRel);
      if (m.matches()) {
        for (int i = 0; i <= m.groupCount(); i++) {
          final String s = m.group(i);
          switch (i) {
            case 1:
              primaryRel = s;
              break;

            case 2:
              secondaryRel = s;
              break;

            case 3:
              relContext =
                  StringUtils.isNotBlank(s) ? s.replaceAll("\\(", "").replaceAll("\\)", "") : "";
              break;

            default:
              break;
          }
        }

        rel.setIndexedPersonRelationship(primaryRel);
        rel.setRelatedPersonRelationship(secondaryRel);
        rel.setRelationshipContext(relContext);

        final String priRel = primaryRel;
        final String secRel = primaryRel;

        // Only log if trace is enabled.
        LOGGER.trace("primaryRel={}, secondaryRel={}", () -> priRel, () -> secRel);

      } else {
        LOGGER.trace("NO MATCH!! rel={}", () -> wholeRel);
      }
    }

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

  @Override
  public String toString() {
    return "EsRelationship [thisLegacyTable=" + thisLegacyTable + ", relatedLegacyTable="
        + relatedLegacyTable + ", thisLegacyId=" + thisLegacyId + ", thisFirstName=" + thisFirstName
        + ", thisLastName=" + thisLastName + ", relCode=" + relCode + ", relatedLegacyId="
        + relatedLegacyId + ", relatedFirstName=" + relatedFirstName + ", relatedLastName="
        + relatedLastName + "]";
  }

}
