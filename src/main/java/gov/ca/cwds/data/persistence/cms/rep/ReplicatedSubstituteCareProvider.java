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
import gov.ca.cwds.data.persistence.cms.BaseSubstituteCareProvider;
import gov.ca.cwds.data.std.ApiGroupNormalizer;

/**
 * {@link PersistentObject} representing a Substitute Care Provider as a
 * {@link CmsReplicatedEntity}.
 * 
 * @author CWDS API Team
 */
@NamedNativeQueries({
    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.cms.rep.ReplicatedSubstituteCareProvider.findBucketRange",
        query = "SELECT x.* FROM {h-schema}SB_PVDRT x "
            + "WHERE x.IDENTIFIER BETWEEN :min_id AND :max_id FOR READ ONLY",
        resultClass = ReplicatedSubstituteCareProvider.class, readOnly = true),
    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.cms.rep.ReplicatedSubstituteCareProvider.findAllUpdatedAfter",
        query = "select z.IDENTIFIER, z.ADD_TEL_NO, z.ADD_EXT_NO, z.YR_INC_AMT, "
            + "z.BIRTH_DT, z.CA_DLIC_NO, z.CITY_NM, z.EDUCATION, z.EMAIL_ADDR, "
            + "z.EMPLYR_NM, z.EMPL_STAT, z.ETH_UD_CD, z.FIRST_NM, z.FRG_ADRT_B, "
            + "z.GENDER_IND, z.HISP_UD_CD, z.HISP_CD, z.IND_TRBC, z.LAST_NM, "
            + "z.LISOWNIND, z.LIS_PER_ID, z.MRTL_STC, z.MID_INI_NM, z.NMPRFX_DSC, "
            + "z.PASSBC_CD, z.PRIM_INC, z.RESOST_IND, z.SEC_INC, z.SS_NO, "
            + "z.STATE_C, z.STREET_NM, z.STREET_NO, z.SUFX_TLDSC, z.ZIP_NO, "
            + "z.ZIP_SFX_NO, z.LST_UPD_ID, z.LST_UPD_TS "
            + ", z.IBMSNAP_OPERATION, z.IBMSNAP_LOGMARKER "
            + "from {h-schema}SB_PVDRT z WHERE z.IBMSNAP_LOGMARKER >= :after for read only ",
        resultClass = ReplicatedSubstituteCareProvider.class),
    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.cms.rep.ReplicatedSubstituteCareProvider.findAllByBucket",
        query = "select z.IDENTIFIER, z.ADD_TEL_NO, z.ADD_EXT_NO, z.YR_INC_AMT, "
            + "z.BIRTH_DT, z.CA_DLIC_NO, z.CITY_NM, z.EDUCATION, z.EMAIL_ADDR, "
            + "z.EMPLYR_NM, z.EMPL_STAT, z.ETH_UD_CD, z.FIRST_NM, z.FRG_ADRT_B, "
            + "z.GENDER_IND, z.HISP_UD_CD, z.HISP_CD, z.IND_TRBC, z.LAST_NM, "
            + "z.LISOWNIND, z.LIS_PER_ID, z.MRTL_STC, z.MID_INI_NM, z.NMPRFX_DSC, "
            + "z.PASSBC_CD, z.PRIM_INC, z.RESOST_IND, z.SEC_INC, z.SS_NO, "
            + "z.STATE_C, z.STREET_NM, z.STREET_NO, z.SUFX_TLDSC, z.ZIP_NO, "
            + "z.ZIP_SFX_NO, z.LST_UPD_ID, z.LST_UPD_TS "
            + ", 'U' as IBMSNAP_OPERATION, z.LST_UPD_TS as IBMSNAP_LOGMARKER "
            + "from ( select mod(y.rn, CAST(:total_buckets AS INTEGER)) + 1 as bucket, y.* "
            + "from ( select row_number() over (order by 1) as rn, x.* "
            + "from {h-schema}SB_PVDRT x ) y ) z where z.bucket = :bucket_num for read only",
        resultClass = ReplicatedSubstituteCareProvider.class)})
@Entity
@Table(name = "SB_PVDRT")
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReplicatedSubstituteCareProvider extends BaseSubstituteCareProvider
    implements CmsReplicatedEntity, ApiGroupNormalizer<ReplicatedSubstituteCareProvider> {

  /**
   * Generated serialization version.
   */
  private static final long serialVersionUID = 6160989831851057517L;

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
  public Class<ReplicatedSubstituteCareProvider> getReductionClass() {
    return (Class<ReplicatedSubstituteCareProvider>) this.getClass();
  }

  @Override
  public void reduce(Map<Object, ReplicatedSubstituteCareProvider> map) {
    // No op.
  }

  @Override
  public Object getGroupKey() {
    return this.getId();
  }

}
