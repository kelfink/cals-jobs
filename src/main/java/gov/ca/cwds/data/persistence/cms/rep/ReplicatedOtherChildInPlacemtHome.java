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
import gov.ca.cwds.data.persistence.cms.BaseOtherChildInPlacemtHome;
import gov.ca.cwds.data.std.ApiGroupNormalizer;

/**
 * {@link PersistentObject} representing an Other Child In Placement Home as a
 * {@link CmsReplicatedEntity}.
 * 
 * @author CWDS API Team
 */
@NamedNativeQueries({
    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherChildInPlacemtHome.findBucketRange",
        query = "SELECT x.IDENTIFIER, x.BIRTH_DT, x.GENDER_CD, x.OTHCHLD_NM, "
            + "x.LST_UPD_ID, x.LST_UPD_TS, x.FKPLC_HM_T, x.YR_INC_AMT "
            + ", x.IBMSNAP_OPERATION, x.IBMSNAP_LOGMARKER FROM {h-schema}OTH_KIDT x "
            + "WHERE x.IDENTIFIER BETWEEN :min_id AND :max_id ORDER BY x.IDENTIFIER FOR READ ONLY",
        resultClass = ReplicatedOtherChildInPlacemtHome.class, readOnly = true),
    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherChildInPlacemtHome.findAllUpdatedAfter",
        query = "SELECT z.IDENTIFIER, z.BIRTH_DT, z.GENDER_CD, z.OTHCHLD_NM, "
            + "z.LST_UPD_ID, z.LST_UPD_TS, z.FKPLC_HM_T, z.YR_INC_AMT "
            + ", z.IBMSNAP_OPERATION, z.IBMSNAP_LOGMARKER "
            + "FROM {h-schema}OTH_KIDT z WHERE z.IBMSNAP_LOGMARKER >= :after FOR READ ONLY ",
        resultClass = ReplicatedOtherChildInPlacemtHome.class),
    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherChildInPlacemtHome.findAllByBucket",
        query = "SELECT z.IDENTIFIER, z.BIRTH_DT, z.GENDER_CD, z.OTHCHLD_NM, "
            + "z.LST_UPD_ID, z.LST_UPD_TS, z.FKPLC_HM_T, z.YR_INC_AMT "
            + ", 'U' as IBMSNAP_OPERATION, z.LST_UPD_TS as IBMSNAP_LOGMARKER "
            + "from ( select mod(y.rn, CAST(:total_buckets AS INTEGER)) + 1 as bucket, y.* "
            + "from ( select row_number() over (order by 1) as rn, x.* "
            + "from {h-schema}OTH_KIDT x ) y ) z where z.bucket = :bucket_num FOR READ ONLY",
        resultClass = ReplicatedOtherChildInPlacemtHome.class)})
@Entity
@Table(name = "OTH_KIDT")
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReplicatedOtherChildInPlacemtHome extends BaseOtherChildInPlacemtHome
    implements CmsReplicatedEntity, ApiGroupNormalizer<ReplicatedOtherChildInPlacemtHome> {

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
  public Class<ReplicatedOtherChildInPlacemtHome> getReductionClass() {
    return (Class<ReplicatedOtherChildInPlacemtHome>) this.getClass();
  }

  @Override
  public void reduce(Map<Object, ReplicatedOtherChildInPlacemtHome> map) {
    // No op.
  }

  @Override
  public Object getGroupKey() {
    return this.getId();
  }

}
