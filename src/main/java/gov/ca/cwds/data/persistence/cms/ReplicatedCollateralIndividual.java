package gov.ca.cwds.data.persistence.cms;

import java.util.Date;

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

import gov.ca.cwds.dao.cms.CmsReplicatedEntity;
import gov.ca.cwds.dao.cms.CmsReplicationOperation;
import gov.ca.cwds.data.persistence.PersistentObject;

/**
 * {@link PersistentObject} representing an Attorney as a {@link CmsReplicatedEntity}.
 * 
 * @author CWDS API Team
 */
@NamedNativeQueries({
    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.cms.ReplicatedCollateralIndividual.findAllUpdatedAfter",
        query = "select z.IDENTIFIER, z.BADGE_NO, z.CITY_NM, z.EMPLYR_NM, z.FAX_NO, "
            + "z.FIRST_NM, z.FRG_ADRT_B, z.LAST_NM, z.MID_INI_NM, z.NMPRFX_DSC, "
            + "z.PRM_TEL_NO, z.PRM_EXT_NO, z.STATE_C, z.STREET_NM, z.STREET_NO, "
            + "z.SUFX_TLDSC, z.ZIP_NO, z.LST_UPD_ID, z.LST_UPD_TS, z.ZIP_SFX_NO, "
            + "z.COMNT_DSC, z.GENDER_CD, z.BIRTH_DT, z.MRTL_STC, z.EMAIL_ADDR, "
            + "z.ESTBLSH_CD, z.ESTBLSH_ID, z.RESOST_IND "
            + ", z.IBMSNAP_OPERATION, z.IBMSNAP_LOGMARKER "
            + "from {h-schema}COLTRL_T z WHERE z.IBMSNAP_LOGMARKER >= :after for read only ",
        resultClass = ReplicatedCollateralIndividual.class),
    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.cms.ReplicatedCollateralIndividual.findPartitionedBuckets",
        query = "select z.IDENTIFIER, z.BADGE_NO, z.CITY_NM, z.EMPLYR_NM, z.FAX_NO, "
            + "z.FIRST_NM, z.FRG_ADRT_B, z.LAST_NM, z.MID_INI_NM, z.NMPRFX_DSC, "
            + "z.PRM_TEL_NO, z.PRM_EXT_NO, z.STATE_C, z.STREET_NM, z.STREET_NO, "
            + "z.SUFX_TLDSC, z.ZIP_NO, z.LST_UPD_ID, z.LST_UPD_TS, z.ZIP_SFX_NO, "
            + "z.COMNT_DSC, z.GENDER_CD, z.BIRTH_DT, z.MRTL_STC, z.EMAIL_ADDR, "
            + "z.ESTBLSH_CD, z.ESTBLSH_ID, z.RESOST_IND "
            + ", 'U' as IBMSNAP_OPERATION, z.LST_UPD_TS as IBMSNAP_LOGMARKER "
            + "from ( select mod(y.rn, CAST(:total_buckets AS INTEGER)) + 1 as bucket, y.* "
            + "from ( select row_number() over (order by 1) as rn, x.* "
            + "from {h-schema}COLTRL_T x WHERE x.IDENTIFIER >= :min_id and x.IDENTIFIER < :max_id "
            + ") y ) z where z.bucket = :bucket_num for read only",
        resultClass = ReplicatedCollateralIndividual.class)})
@Entity
@Table(name = "REPTR_T")
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReplicatedCollateralIndividual extends BaseCollateralIndividual
    implements CmsReplicatedEntity {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Enumerated(EnumType.STRING)
  @Column(name = "IBMSNAP_OPERATION", updatable = false)
  private CmsReplicationOperation replicationOperation;

  @Type(type = "timestamp")
  @Column(name = "IBMSNAP_LOGMARKER", updatable = false)
  private Date replicationDate;

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

}
