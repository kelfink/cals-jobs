package gov.ca.cwds.generic.data.persistence.cms.rep;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import gov.ca.cwds.data.es.ElasticSearchLegacyDescriptor;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.BaseSubstituteCareProvider;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.generic.jobs.util.transform.ElasticTransformer;
import gov.ca.cwds.rest.api.domain.cms.LegacyTable;
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

/**
 * {@link PersistentObject} representing a Substitute Care Provider as a {@link CmsReplicatedEntity}
 * .
 *
 * @author CWDS API Team
 */
@NamedNativeQueries({
    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.cms.rep.ReplicatedSubstituteCareProvider.findBucketRange",
        query = "select z.IDENTIFIER, z.ADD_TEL_NO, z.ADD_EXT_NO, z.YR_INC_AMT, "
            + "z.BIRTH_DT, trim(z.CA_DLIC_NO) CA_DLIC_NO, trim(z.CITY_NM) CITY_NM, z.EDUCATION, trim(z.EMAIL_ADDR) EMAIL_ADDR, "
            + "trim(z.EMPLYR_NM) EMPLYR_NM, z.EMPL_STAT, z.ETH_UD_CD, trim(z.FIRST_NM) FIRST_NM, z.FRG_ADRT_B, "
            + "z.GENDER_IND, z.HISP_UD_CD, z.HISP_CD, z.IND_TRBC, trim(z.LAST_NM) LAST_NM, "
            + "z.LISOWNIND, trim(z.LIS_PER_ID) LIS_PER_ID, z.MRTL_STC, trim(z.MID_INI_NM) MID_INI_NM, trim(z.NMPRFX_DSC) NMPRFX_DSC, "
            + "z.PASSBC_CD, z.PRIM_INC, z.RESOST_IND, z.SEC_INC, trim(z.SS_NO) SS_NO, z.STATE_C, "
            + "trim(z.STREET_NM) STREET_NM, trim(z.STREET_NO) STREET_NO, trim(z.SUFX_TLDSC) SUFX_TLDSC, z.ZIP_NO, "
            + "z.ZIP_SFX_NO, z.LST_UPD_ID, z.LST_UPD_TS, z.IBMSNAP_OPERATION, z.IBMSNAP_LOGMARKER "
            + "from {h-schema}SB_PVDRT z WHERE z.IDENTIFIER > :min_id AND z.IDENTIFIER < :max_id FOR READ ONLY WITH UR",
        resultClass = ReplicatedSubstituteCareProvider.class, readOnly = true),
    @NamedNativeQuery(
        name = "gov.ca.cwds.data.persistence.cms.rep.ReplicatedSubstituteCareProvider.findAllUpdatedAfter",
        query = "select z.IDENTIFIER, z.ADD_TEL_NO, z.ADD_EXT_NO, z.YR_INC_AMT, "
            + "z.BIRTH_DT, trim(z.CA_DLIC_NO) CA_DLIC_NO, trim(z.CITY_NM) CITY_NM, z.EDUCATION, trim(z.EMAIL_ADDR) EMAIL_ADDR, "
            + "trim(z.EMPLYR_NM) EMPLYR_NM, z.EMPL_STAT, z.ETH_UD_CD, trim(z.FIRST_NM) FIRST_NM, z.FRG_ADRT_B, "
            + "z.GENDER_IND, z.HISP_UD_CD, z.HISP_CD, z.IND_TRBC, trim(z.LAST_NM) LAST_NM, "
            + "z.LISOWNIND, trim(z.LIS_PER_ID) LIS_PER_ID, z.MRTL_STC, trim(z.MID_INI_NM) MID_INI_NM, trim(z.NMPRFX_DSC) NMPRFX_DSC, "
            + "z.PASSBC_CD, z.PRIM_INC, z.RESOST_IND, z.SEC_INC, trim(z.SS_NO) SS_NO, z.STATE_C, "
            + "trim(z.STREET_NM) STREET_NM, trim(z.STREET_NO) STREET_NO, trim(z.SUFX_TLDSC) SUFX_TLDSC, z.ZIP_NO, "
            + "z.ZIP_SFX_NO, z.LST_UPD_ID, z.LST_UPD_TS, z.IBMSNAP_OPERATION, z.IBMSNAP_LOGMARKER "
            + "from {h-schema}SB_PVDRT z WHERE z.IBMSNAP_LOGMARKER >= :after FOR READ ONLY WITH UR ",
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
  public Class<ReplicatedSubstituteCareProvider> getNormalizationClass() {
    return (Class<ReplicatedSubstituteCareProvider>) this.getClass();
  }

  @Override
  public ReplicatedSubstituteCareProvider normalize(
      Map<Object, ReplicatedSubstituteCareProvider> map) {
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
        LegacyTable.SUBSTITUTE_CARE_PROVIDER);
  }
}
