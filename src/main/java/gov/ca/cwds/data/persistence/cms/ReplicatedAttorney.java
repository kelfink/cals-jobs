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
@NamedNativeQueries({@NamedNativeQuery(
    name = "gov.ca.cwds.data.persistence.cms.ReplicatedAttorney.findAllUpdatedAfter",
    query = "select z.IDENTIFIER, z.CITY_NM, z.CWATRY_IND, z.FAX_NO, z.FIRST_NM, "
        + "z.GVR_ENTC, z.LANG_TPC, z.LAST_NM, z.MSG_EXT_NO, z.MSG_TEL_NO, "
        + "z.MID_INI_NM, z.NMPRFX_DSC, z.POSTIL_DSC, z.PRM_EXT_NO, z.PRM_TEL_NO, "
        + "z.STATE_C, z.STREET_NM, z.STREET_NO, z.SUFX_TLDSC, z.ZIP_NO, z.LST_UPD_ID, "
        + "z.LST_UPD_TS, z.BUSNSS_NM, z.ZIP_SFX_NO, z.END_DT, z.ARCASS_IND, z.EMAIL_ADDR "
        + ", z.IBMSNAP_OPERATION, z.IBMSNAP_LOGMARKER "
        + "from {h-schema}ATTRNY_T z WHERE z.IBMSNAP_LOGMARKER >= :after FOR READ ONLY",
    resultClass = ReplicatedAttorney.class)})
@Entity
@Table(name = "ATTRNY_T")
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReplicatedAttorney extends Attorney implements CmsReplicatedEntity {

  /**
   * 
   */
  private static final long serialVersionUID = 6160989831851057517L;

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
