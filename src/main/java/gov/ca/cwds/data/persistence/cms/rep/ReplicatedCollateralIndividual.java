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

import gov.ca.cwds.data.es.ElasticSearchLegacyDescriptor;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.BaseCollateralIndividual;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.jobs.util.transform.ElasticTransformer;
import gov.ca.cwds.rest.api.domain.cms.LegacyTable;

/**
 * {@link PersistentObject} representing a Collateral Individual as a {@link CmsReplicatedEntity}.
 * 
 * @author CWDS API Team
 */
@NamedNativeQueries({
    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.cms.rep.ReplicatedCollateralIndividual.findBucketRange",
        query = "select z.IDENTIFIER, trim(z.BADGE_NO) BADGE_NO, trim(z.CITY_NM) CITY_NM, trim(z.EMPLYR_NM) EMPLYR_NM, "
            + "z.FAX_NO, trim(z.FIRST_NM) FIRST_NM, z.FRG_ADRT_B, trim(z.LAST_NM) LAST_NM, "
            + "trim(z.MID_INI_NM) MID_INI_NM, trim(z.NMPRFX_DSC) NMPRFX_DSC, z.PRM_TEL_NO, z.PRM_EXT_NO, z.STATE_C, "
            + "trim(z.STREET_NM) STREET_NM, trim(z.STREET_NO) STREET_NO, trim(z.SUFX_TLDSC) SUFX_TLDSC, "
            + "z.ZIP_NO, z.LST_UPD_ID, z.LST_UPD_TS, z.ZIP_SFX_NO, trim(z.COMNT_DSC) COMNT_DSC, "
            + "z.GENDER_CD, z.BIRTH_DT, z.MRTL_STC, trim(z.EMAIL_ADDR) EMAIL_ADDR, "
            + "z.ESTBLSH_CD, z.ESTBLSH_ID, z.RESOST_IND, "
            + "IBMSNAP_OPERATION, z.IBMSNAP_LOGMARKER FROM {h-schema}COLTRL_T z "
            + "WHERE z.IDENTIFIER >= :min_id AND z.IDENTIFIER <= :max_id FOR READ ONLY WITH UR",
        resultClass = ReplicatedCollateralIndividual.class, readOnly = true),
    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.cms.rep.ReplicatedCollateralIndividual.findAllUpdatedAfter",
        query = "select z.IDENTIFIER, trim(z.BADGE_NO) BADGE_NO, trim(z.CITY_NM) CITY_NM, trim(z.EMPLYR_NM) EMPLYR_NM, "
            + "z.FAX_NO, trim(z.FIRST_NM) FIRST_NM, z.FRG_ADRT_B, trim(z.LAST_NM) LAST_NM, "
            + "trim(z.MID_INI_NM) MID_INI_NM, trim(z.NMPRFX_DSC) NMPRFX_DSC, z.PRM_TEL_NO, z.PRM_EXT_NO, z.STATE_C, "
            + "trim(z.STREET_NM) STREET_NM, trim(z.STREET_NO) STREET_NO, trim(z.SUFX_TLDSC) SUFX_TLDSC, "
            + "z.ZIP_NO, z.LST_UPD_ID, z.LST_UPD_TS, z.ZIP_SFX_NO, trim(z.COMNT_DSC) COMNT_DSC, "
            + "z.GENDER_CD, z.BIRTH_DT, z.MRTL_STC, trim(z.EMAIL_ADDR) EMAIL_ADDR, "
            + "z.ESTBLSH_CD, z.ESTBLSH_ID, z.RESOST_IND, "
            + "IBMSNAP_OPERATION, z.IBMSNAP_LOGMARKER FROM {h-schema}COLTRL_T z "
            + "WHERE z.IBMSNAP_LOGMARKER >= :after FOR READ ONLY WITH UR",
        resultClass = ReplicatedCollateralIndividual.class),
    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.cms.rep.ReplicatedCollateralIndividual.findPartitionedBuckets",
        query = "select z.IDENTIFIER, trim(z.BADGE_NO) BADGE_NO, trim(z.CITY_NM) CITY_NM, trim(z.EMPLYR_NM) EMPLYR_NM, "
            + "z.FAX_NO, trim(z.FIRST_NM) FIRST_NM, z.FRG_ADRT_B, trim(z.LAST_NM) LAST_NM, "
            + "trim(z.MID_INI_NM) MID_INI_NM, trim(z.NMPRFX_DSC) NMPRFX_DSC, z.PRM_TEL_NO, z.PRM_EXT_NO, z.STATE_C, "
            + "trim(z.STREET_NM) STREET_NM, trim(z.STREET_NO) STREET_NO, trim(z.SUFX_TLDSC) SUFX_TLDSC, "
            + "z.ZIP_NO, z.LST_UPD_ID, z.LST_UPD_TS, z.ZIP_SFX_NO, trim(z.COMNT_DSC) COMNT_DSC, "
            + "z.GENDER_CD, z.BIRTH_DT, z.MRTL_STC, trim(z.EMAIL_ADDR) EMAIL_ADDR, "
            + "z.ESTBLSH_CD, z.ESTBLSH_ID, z.RESOST_IND, "
            + "IBMSNAP_OPERATION, z.IBMSNAP_LOGMARKER FROM {h-schema}COLTRL_T z "
            + "WHERE z.IDENTIFIER >= :min_id AND z.IDENTIFIER <= :max_id FOR READ ONLY WITH UR",
        resultClass = ReplicatedCollateralIndividual.class)})
@Entity
@Table(name = "COLTRL_T")
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReplicatedCollateralIndividual extends BaseCollateralIndividual
    implements CmsReplicatedEntity, ApiGroupNormalizer<ReplicatedCollateralIndividual> {

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
  public Class<ReplicatedCollateralIndividual> getNormalizationClass() {
    return (Class<ReplicatedCollateralIndividual>) this.getClass();
  }

  @Override
  public ReplicatedCollateralIndividual normalize(Map<Object, ReplicatedCollateralIndividual> map) {
    return null;
  }

  @Override
  public String getNormalizationGroupKey() {
    return this.getId();
  }

  // =======================
  // ApiLegacyAware:
  // =======================

  @Override
  public String getLegacyId() {
    return getId();
  }

  @Override
  public ElasticSearchLegacyDescriptor getLegacyDescriptor() {
    return ElasticTransformer.createLegacyDescriptor(getId(), getLastUpdatedTime(),
        LegacyTable.COLLATERAL_INDIVIDUAL);
  }
}
