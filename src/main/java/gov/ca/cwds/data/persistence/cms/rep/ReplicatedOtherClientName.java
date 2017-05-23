package gov.ca.cwds.data.persistence.cms.rep;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.BaseOtherClientName;
import gov.ca.cwds.data.std.ApiGroupNormalizer;

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
public class ReplicatedOtherClientName extends BaseOtherClientName
    implements CmsReplicatedEntity, ApiGroupNormalizer<ReplicatedOtherClientName> {

  /**
   * Default.
   */
  private static final long serialVersionUID = 1L;

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

  @SuppressWarnings("unchecked")
  @Override
  public Class<ReplicatedOtherClientName> getReductionClass() {
    return (Class<ReplicatedOtherClientName>) this.getClass();
  }

  @Override
  public void reduce(Map<Object, ReplicatedOtherClientName> map) {
    // No op.
  }

  @Override
  public Object getGroupKey() {
    return this.getPrimaryKey();
  }

}
