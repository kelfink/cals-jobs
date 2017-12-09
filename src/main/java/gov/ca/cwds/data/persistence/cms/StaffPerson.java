package gov.ca.cwds.data.persistence.cms;

import static gov.ca.cwds.neutron.util.NeutronDateUtils.freshDate;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.Type;

import gov.ca.cwds.data.es.ElasticSearchLegacyDescriptor;
import gov.ca.cwds.data.persistence.cms.rep.CmsReplicatedEntity;
import gov.ca.cwds.data.persistence.cms.rep.EmbeddableCmsReplicatedEntity;
import gov.ca.cwds.data.persistence.cms.rep.EmbeddableCmsReplicatedEntityAware;

/**
 * {@link CmsPersistentObject} representing a StaffPerson.
 * 
 * <p>
 * Note that a staff identifier is a base 62, char(3), not the usual char(10).
 * </p>
 * 
 * @author CWDS API Team
 */
@Entity
@Table(name = "STFPERST")
//@formatter:off
@NamedNativeQuery(name = "gov.ca.cwds.data.persistence.cms.StaffPerson.findAll",
    query = "SELECT "
          + "s.IDENTIFIER, \n"
          + "s.END_DT, \n"
          + "trim(s.FIRST_NM)   AS FIRST_NM, \n"
          + "trim(s.JOB_TL_DSC) AS JOB_TL_DSC, \n"
          + "trim(s.LAST_NM)    AS LAST_NM, \n"
          + "trim(s.MID_INI_NM) AS MID_INI_NM, \n"
          + "trim(s.NMPRFX_DSC) AS NMPRFX_DSC, \n"
          + "s.PHONE_NO, \n"
          + "s.TEL_EXT_NO, \n"
          + "s.START_DT, \n"
          + "trim(s.SUFX_TLDSC) AS SUFX_TLDSC, \n"
          + "s.TLCMTR_IND, \n"
          + "s.LST_UPD_ID, \n"
          + "s.LST_UPD_TS, \n"
          + "s.FKCWS_OFFT, \n"
          + "trim(s.AVLOC_DSC)  AS AVLOC_DSC, \n"
          + "s.SSRS_WKRID, \n"
          + "s.CNTY_SPFCD, \n"
          + "s.DTYWKR_IND, \n"
          + "s.FKCWSADDRT, \n"
          + "trim(s.EMAIL_ADDR) AS EMAIL_ADDR,\n"
          + "s.IBMSNAP_LOGMARKER,\n"
          + "s.IBMSNAP_OPERATION\n"
        + "FROM {h-schema}STFPERST s\n FOR READ ONLY WITH UR ",
    resultClass = StaffPerson.class, readOnly = true)
//@formatter:on
public class StaffPerson extends CmsPersistentObject
    implements CmsReplicatedEntity, EmbeddableCmsReplicatedEntityAware {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "IDENTIFIER", length = 3)
  private String id;

  @Type(type = "date")
  @Column(name = "END_DT")
  private Date endDate;

  @Column(name = "FIRST_NM", length = 20, nullable = false)
  @ColumnTransformer(read = "trim(FIRST_NM)")
  private String firstName;

  @Column(name = "JOB_TL_DSC")
  @ColumnTransformer(read = "trim(JOB_TL_DSC)")
  private String jobTitle;

  @Column(name = "LAST_NM", length = 25, nullable = false)
  @ColumnTransformer(read = "trim(LAST_NM)")
  private String lastName;

  @Column(name = "MID_INI_NM")
  @ColumnTransformer(read = "trim(MID_INI_NM)")
  private String middleInitial;

  @Column(name = "NMPRFX_DSC")
  @ColumnTransformer(read = "trim(NMPRFX_DSC)")
  private String namePrefix;

  @Column(name = "PHONE_NO")
  private BigDecimal phoneNumber;

  @Type(type = "integer")
  @Column(name = "TEL_EXT_NO")
  private Integer phoneExt;

  @Type(type = "date")
  @Column(name = "START_DT")
  private Date startDate;

  @Column(name = "SUFX_TLDSC")
  @ColumnTransformer(read = "trim(SUFX_TLDSC)")
  private String nameSuffix;

  @Column(name = "TLCMTR_IND")
  private String telecommuterIndicator;

  @Column(name = "FKCWS_OFFT")
  private String cwsOffice;

  @Column(name = "AVLOC_DSC")
  @ColumnTransformer(read = "trim(AVLOC_DSC)")
  private String availabilityAndLocationDescription;

  @Column(name = "SSRS_WKRID")
  private String ssrsLicensingWorkerId;

  @Column(name = "CNTY_SPFCD")
  private String countyCode;

  @Column(name = "DTYWKR_IND")
  private String dutyWorkerIndicator;

  @Column(name = "FKCWSADDRT")
  private String cwsOfficeAddress;

  @Column(name = "EMAIL_ADDR")
  @ColumnTransformer(read = "trim(EMAIL_ADDR)")
  private String emailAddress;

  private EmbeddableCmsReplicatedEntity embeddableCmsReplicatedEntity;

  /**
   * Default constructor
   * 
   * Required for Hibernate
   */
  public StaffPerson() {
    super();
  }

  /**
   * {@inheritDoc}
   * 
   * @see gov.ca.cwds.data.persistence.PersistentObject#getPrimaryKey()
   */
  @Override
  public String getPrimaryKey() {
    return getId();
  }

  /**
   * @return the id
   */
  @Override
  public String getId() {
    return id;
  }

  /**
   * @return the endDate
   */
  public Date getEndDate() {
    return freshDate(endDate);
  }

  /**
   * @return the firstName
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * @return the jobTitle
   */
  public String getJobTitle() {
    return jobTitle;
  }

  /**
   * @return the lastName
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * @return the middleInitial
   */
  public String getMiddleInitial() {
    return middleInitial;
  }

  /**
   * @return the namePrefix
   */
  public String getNamePrefix() {
    return namePrefix;
  }

  /**
   * @return the phoneNumber
   */
  public BigDecimal getPhoneNumber() {
    return phoneNumber;
  }

  /**
   * @return the phoneExt
   */
  public Integer getPhoneExt() {
    return phoneExt;
  }

  /**
   * @return the startDate
   */
  public Date getStartDate() {
    return freshDate(startDate);
  }

  /**
   * @return the nameSuffix
   */
  public String getNameSuffix() {
    return nameSuffix;
  }

  /**
   * @return the telecommuterIndicator
   */
  public String getTelecommuterIndicator() {
    return telecommuterIndicator;
  }

  /**
   * @return the cwsOffice
   */
  public String getCwsOffice() {
    return cwsOffice;
  }

  /**
   * @return the availabilityAndLocationDescription
   */
  public String getAvailabilityAndLocationDescription() {
    return availabilityAndLocationDescription;
  }

  /**
   * @return the ssrsLicensingWorkerId
   */
  public String getSsrsLicensingWorkerId() {
    return ssrsLicensingWorkerId;
  }

  /**
   * @return the countyCode
   */
  public String getCountyCode() {
    return countyCode;
  }

  /**
   * @return the dutyWorkerIndicator
   */
  public String getDutyWorkerIndicator() {
    return dutyWorkerIndicator;
  }

  /**
   * @return the cwsOfficeAddress
   */
  public String getCwsOfficeAddress() {
    return cwsOfficeAddress;
  }

  /**
   * @return the emailAddress
   */
  public String getEmailAddress() {
    return emailAddress;
  }

  @Override
  public String getLegacyId() {
    return this.id;
  }

  @Override
  public ElasticSearchLegacyDescriptor getLegacyDescriptor() {
    return null;
  }

  @Override
  public EmbeddableCmsReplicatedEntity getReplicatedEntity() {
    return this.embeddableCmsReplicatedEntity;
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, false);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj, false);
  }

}
