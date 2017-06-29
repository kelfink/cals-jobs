package gov.ca.cwds.data.persistence.cms.rep;

import static gov.ca.cwds.jobs.util.transform.JobTransformUtils.ifNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchLegacyDescriptor;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonAka;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.BaseOtherClientName;
import gov.ca.cwds.data.persistence.cms.ReplicatedAkas;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.jobs.util.jdbc.RowMapper;
import gov.ca.cwds.jobs.util.transform.ElasticTransformer;
import gov.ca.cwds.rest.api.domain.cms.LegacyTable;

/**
 * {@link PersistentObject} representing an Other Client Name as a {@link CmsReplicatedEntity}.
 * 
 * @author CWDS API Team
 */
@NamedNativeQueries({
    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherClientName.findBucketRange",
        query = "SELECT x.* FROM {h-schema}OCL_NM_T x "
            + "WHERE x.THIRD_ID BETWEEN :min_id AND :max_id FOR READ ONLY",
        resultClass = ReplicatedOtherClientName.class, readOnly = true),
    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherClientName.findAllUpdatedAfter",
        query = "select z.THIRD_ID, z.FIRST_NM, z.LAST_NM, z.MIDDLE_NM, z.NMPRFX_DSC, "
            + "z.NAME_TPC, z.SUFX_TLDSC, z.LST_UPD_ID, z.LST_UPD_TS, z.FKCLIENT_T "
            + ", z.IBMSNAP_OPERATION, z.IBMSNAP_LOGMARKER "
            + "from {h-schema}OCL_NM_T z WHERE z.IBMSNAP_LOGMARKER >= :after for read only ",
        resultClass = ReplicatedOtherClientName.class),
    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherClientName.findPartitionedBuckets",
        query = "select z.THIRD_ID, z.FIRST_NM, z.LAST_NM, z.MIDDLE_NM, z.NMPRFX_DSC, "
            + "z.NAME_TPC, z.SUFX_TLDSC, z.LST_UPD_ID, z.LST_UPD_TS, z.FKCLIENT_T "
            + ", 'U' as IBMSNAP_OPERATION, z.LST_UPD_TS as IBMSNAP_LOGMARKER "
            + "from ( select mod(y.rn, CAST(:total_buckets AS INTEGER)) + 1 as bucket, y.* "
            + "from ( select row_number() over (order by 1) as rn, x.* "
            + "from {h-schema}OCL_NM_T x WHERE x.FKCLIENT_T >= :min_id and x.FKCLIENT_T < :max_id "
            + ") y ) z where z.bucket = :bucket_num for read only",
        resultClass = ReplicatedOtherClientName.class)})
@Entity
@Table(name = "OCL_NM_T")
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReplicatedOtherClientName extends BaseOtherClientName implements CmsReplicatedEntity,
    ApiGroupNormalizer<ReplicatedAkas>, RowMapper<ReplicatedOtherClientName> {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LogManager.getLogger(ReplicatedOtherClientName.class);

  @Enumerated(EnumType.STRING)
  @Column(name = "IBMSNAP_OPERATION", updatable = false)
  private CmsReplicationOperation replicationOperation;

  @Type(type = "timestamp")
  @Column(name = "IBMSNAP_LOGMARKER", updatable = false)
  private Date replicationDate;

  // =======================
  // CmsReplicatedEntity:
  // =======================

  @Override
  public CmsReplicationOperation getReplicationOperation() {
    return replicationOperation;
  }

  /**
   * Build a ReplicatedOtherClientName from an incoming ResultSet.
   * 
   * @param rs incoming tuple
   * @return a populated EsRelationship
   * @throws SQLException if unable to convert types or stream breaks, etc.
   */
  public static ReplicatedOtherClientName mapRowToBean(ResultSet rs) throws SQLException {
    ReplicatedOtherClientName ret = new ReplicatedOtherClientName();

    ret.setClientId(rs.getString("FKCLIENT_T"));
    ret.setThirdId(rs.getString("THIRD_ID"));

    ret.firstName = ifNull(rs.getString("FIRST_NM"));
    ret.middleName = ifNull(rs.getString("MIDDLE_NM"));
    ret.lastName = ifNull(rs.getString("LAST_NM"));
    ret.nameType = rs.getShort("NAME_TPC");
    ret.namePrefixDescription = ifNull(rs.getString("NMPRFX_DSC"));
    ret.suffixTitleDescription = ifNull(rs.getString("SUFX_TLDSC"));

    ret.setLastUpdatedId(rs.getString("LST_UPD_ID"));
    ret.setLastUpdatedTime(rs.getDate("LST_UPD_TS"));

    return ret;
  }

  @Override
  public ReplicatedOtherClientName mapRow(ResultSet rs) throws SQLException {
    return ReplicatedOtherClientName.mapRowToBean(rs);
  }

  @Override
  public void setReplicationOperation(CmsReplicationOperation replicationOperation) {
    this.replicationOperation = replicationOperation;
  }

  @Override
  public Date getReplicationDate() {
    return replicationDate;
  }

  @Override
  public void setReplicationDate(Date replicationDate) {
    this.replicationDate = replicationDate;
  }

  // =======================
  // ApiGroupNormalizer:
  // =======================

  @Override
  public Class<ReplicatedAkas> getNormalizationClass() {
    return ReplicatedAkas.class;
  }

  @Override
  public Object getNormalizationGroupKey() {
    return this.getPrimaryKey();
  }

  @Override
  public ReplicatedAkas normalize(Map<Object, ReplicatedAkas> map) {
    final boolean isClientAdded = map.containsKey(this.clientId);
    final ReplicatedAkas ret =
        isClientAdded ? map.get(this.clientId) : new ReplicatedAkas(this.clientId);

    final ElasticSearchPersonAka aka = new ElasticSearchPersonAka();
    ret.addAka(aka);

    if (StringUtils.isNotBlank(this.firstName)) {
      aka.setFirstName(this.firstName.trim());
    }

    if (StringUtils.isNotBlank(this.lastName)) {
      aka.setLastName(this.lastName.trim());
    }

    if (StringUtils.isNotBlank(this.middleName)) {
      aka.setMiddleName(this.middleName.trim());
    }

    if (StringUtils.isNotBlank(this.namePrefixDescription)) {
      aka.setPrefix(this.namePrefixDescription.trim());
    }

    if (StringUtils.isNotBlank(this.suffixTitleDescription)) {
      aka.setSuffix(this.suffixTitleDescription.trim());
    }

    if (this.nameType != null && this.nameType.intValue() != 0) {
      aka.setNameType(ElasticSearchPerson.getSystemCodes().lookup(this.nameType).getShortDsc());
    }

    aka.setLegacyDescriptor(getLegacyDescriptor());

    map.put(ret.getId(), ret);
    return ret;
  }

  // =======================
  // ApiLegacyAware:
  // =======================

  @Override
  public String getLegacyId() {
    return getId();
  }

  @Override
  public String getId() {
    return getThirdId();
  }

  @Override
  public ElasticSearchLegacyDescriptor getLegacyDescriptor() {
    return ElasticTransformer.createLegacyDescriptor(getId(), getReplicationDate(),
        LegacyTable.ALIAS_OR_OTHER_CLIENT_NAME);
  }

  // =======================
  // SETTERS:
  // =======================

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  public void setNamePrefixDescription(String namePrefixDescription) {
    this.namePrefixDescription = namePrefixDescription;
  }

  public void setNameType(Short nameType) {
    this.nameType = nameType;
  }

  public void setSuffixTitleDescription(String suffixTitleDescription) {
    this.suffixTitleDescription = suffixTitleDescription;
  }

}
