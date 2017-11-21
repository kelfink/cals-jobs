package gov.ca.cwds.data.persistence.cms;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.data.persistence.cms.rep.CmsReplicationOperation;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedClient;
import gov.ca.cwds.jobs.Goddard;

public class EsClientTest extends Goddard {

  EsClientPerson target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    target = new EsClientPerson();
    target.setCltId(DEFAULT_CLIENT_ID);
    target.setSafetyAlertId(DEFAULT_CLIENT_ID);
    target.setAkaId(DEFAULT_CLIENT_ID);

    final Short shortZero = Short.valueOf((short) 0);
    when(rs.first()).thenReturn(true);
    when(rs.getShort("ADR_GVR_ENTC")).thenReturn(shortZero);
    when(rs.getShort("ADR_ST_SFX_C")).thenReturn(shortZero);
    when(rs.getShort("ADR_STATE_C")).thenReturn(shortZero);
    when(rs.getShort("ADR_UNT_DSGC")).thenReturn(shortZero);
    when(rs.getShort("ADR_ZIP_SFX_NO")).thenReturn(shortZero);
    when(rs.getShort("CLA_ADDR_TPC")).thenReturn(shortZero);
    when(rs.getShort("CLT_B_CNTRY_C")).thenReturn(shortZero);
    when(rs.getShort("CLT_B_STATE_C")).thenReturn(shortZero);
    when(rs.getShort("CLT_D_STATE_C")).thenReturn(shortZero);
    when(rs.getShort("CLT_I_CNTRY_C")).thenReturn(shortZero);
    when(rs.getShort("CLT_IMGT_STC")).thenReturn(shortZero);
    when(rs.getShort("CLT_MRTL_STC")).thenReturn(shortZero);
    when(rs.getShort("CLT_NAME_TPC")).thenReturn(shortZero);
    when(rs.getShort("CLT_P_ETHNCTYC")).thenReturn(shortZero);
    when(rs.getShort("CLT_P_LANG_TPC")).thenReturn(shortZero);
    when(rs.getShort("CLT_RLGN_TPC")).thenReturn(shortZero);
    when(rs.getShort("CLT_S_LANG_TC")).thenReturn(shortZero);

    when(rs.getBigDecimal("ADR_EMRG_TELNO")).thenReturn(BigDecimal.ZERO);
    when(rs.getBigDecimal("ADR_MSG_TEL_NO")).thenReturn(BigDecimal.ZERO);
    when(rs.getBigDecimal("ADR_PRM_TEL_NO")).thenReturn(BigDecimal.ZERO);

    when(rs.getInt("ADR_EMRG_EXTNO")).thenReturn(0);
    when(rs.getInt("ADR_MSG_EXT_NO")).thenReturn(0);
    when(rs.getInt("ADR_PRM_EXT_NO")).thenReturn(0);

    when(rs.getString("ADR_ADDR_DSC")).thenReturn("somewhere");
    when(rs.getString("ADR_CITY_NM")).thenReturn("Windelsberg");
    when(rs.getString("ADR_FRG_ADRT_B")).thenReturn("Y");
    when(rs.getString("ADR_HEADER_ADR")).thenReturn("Y");
    when(rs.getString("ADR_IBMSNAP_OPERATION")).thenReturn("I");
    when(rs.getString("ADR_IDENTIFIER")).thenReturn(DEFAULT_CLIENT_ID);
    when(rs.getString("ADR_POSTDIR_CD")).thenReturn("Y");
    when(rs.getString("ADR_PREDIR_CD")).thenReturn("Y");
    when(rs.getString("ADR_STREET_NM")).thenReturn("Y");
    when(rs.getString("ADR_STREET_NO")).thenReturn("1234");
    when(rs.getString("ADR_UNIT_NO")).thenReturn("ste 210");
    when(rs.getString("ADR_ZIP_NO")).thenReturn("Y");

    when(rs.getString("CLA_BK_INMT_ID")).thenReturn("Y");
    when(rs.getString("CLA_FKADDRS_T")).thenReturn(DEFAULT_CLIENT_ID);
    when(rs.getString("CLA_FKCLIENT_T")).thenReturn(DEFAULT_CLIENT_ID);
    when(rs.getString("CLA_FKREFERL_T")).thenReturn(DEFAULT_CLIENT_ID);
    when(rs.getString("CLA_HOMLES_IND")).thenReturn("Y");
    when(rs.getString("CLA_IBMSNAP_OPERATION")).thenReturn("I");
    when(rs.getString("CLA_IDENTIFIER")).thenReturn(DEFAULT_CLIENT_ID);
    when(rs.getString("CLA_LST_UPD_ID")).thenReturn("Y");
    when(rs.getString("CLT_ADJDEL_IND")).thenReturn("Y");
    when(rs.getString("CLT_ADPTN_STCD")).thenReturn("Y");
    when(rs.getString("CLT_ALN_REG_NO")).thenReturn("Y");
    when(rs.getString("CLT_BIRTH_CITY")).thenReturn("Y");
    when(rs.getString("CLT_BP_VER_IND")).thenReturn("Y");
    when(rs.getString("CLT_BR_FAC_NM")).thenReturn("Y");
    when(rs.getString("CLT_CHLD_CLT_B")).thenReturn("Y");
    when(rs.getString("CLT_CL_INDX_NO")).thenReturn("Y");
    when(rs.getString("CLT_COM_FST_NM")).thenReturn("Y");
    when(rs.getString("CLT_COM_LST_NM")).thenReturn("Y");
    when(rs.getString("CLT_COM_MID_NM")).thenReturn("Y");
    when(rs.getString("CLT_COMMNT_DSC")).thenReturn("Y");
    when(rs.getString("CLT_CONF_EFIND")).thenReturn("Y");
    when(rs.getString("CLT_COTH_DESC")).thenReturn("Y");
    when(rs.getString("CLT_CURRCA_IND")).thenReturn("Y");
    when(rs.getString("CLT_CURREG_IND")).thenReturn("Y");
    when(rs.getString("CLT_DEATH_PLC")).thenReturn("Y");
    when(rs.getString("CLT_DRV_LIC_NO")).thenReturn("Y");
    when(rs.getString("CLT_DTH_DT_IND")).thenReturn("Y");
    when(rs.getString("CLT_DTH_RN_TXT")).thenReturn("Y");
    when(rs.getString("CLT_EMAIL_ADDR")).thenReturn("Y");
    when(rs.getString("CLT_EST_DOB_CD")).thenReturn("Y");
    when(rs.getString("CLT_ETH_UD_CD")).thenReturn("Y");
    when(rs.getString("CLT_GENDER_CD")).thenReturn("Y");
    when(rs.getString("CLT_HCARE_IND")).thenReturn("Y");
    when(rs.getString("CLT_HEALTH_TXT")).thenReturn("Y");
    when(rs.getString("CLT_HISP_CD")).thenReturn("Y");
    when(rs.getString("CLT_HISP_UD_CD")).thenReturn("Y");
    when(rs.getString("CLT_IBMSNAP_OPERATION")).thenReturn("U");
    when(rs.getString("CLT_IDENTIFIER")).thenReturn(DEFAULT_CLIENT_ID);
    when(rs.getString("CLT_INCAPC_CD")).thenReturn("Y");
    when(rs.getString("CLT_LIMIT_IND")).thenReturn("Y");
    when(rs.getString("CLT_LITRATE_CD")).thenReturn("Y");
    when(rs.getString("CLT_LST_UPD_ID")).thenReturn("Y");
    when(rs.getString("CLT_MAR_HIST_B")).thenReturn("Y");
    when(rs.getString("CLT_MILT_STACD")).thenReturn("Y");
    when(rs.getString("CLT_NMPRFX_DSC")).thenReturn("Y");
    when(rs.getString("CLT_OUTWRT_IND")).thenReturn("Y");
    when(rs.getString("CLT_POTH_DESC")).thenReturn("Y");
    when(rs.getString("CLT_PREREG_IND")).thenReturn("Y");
    when(rs.getString("CLT_PREVCA_IND")).thenReturn("Y");
    when(rs.getString("CLT_SENSTV_IND")).thenReturn("Y");
    when(rs.getString("CLT_SNTV_HLIND")).thenReturn("Y");
    when(rs.getString("CLT_SOC158_IND")).thenReturn("Y");
    when(rs.getString("CLT_SOCPLC_CD")).thenReturn("Y");
    when(rs.getString("CLT_SS_NO")).thenReturn("Y");
    when(rs.getString("CLT_SSN_CHG_CD")).thenReturn("Y");
    when(rs.getString("CLT_SUFX_TLDSC")).thenReturn("Y");
    when(rs.getString("CLT_TR_MBVRT_B")).thenReturn("Y");
    when(rs.getString("CLT_TRBA_CLT_B")).thenReturn("Y");
    when(rs.getString("CLT_UNEMPLY_CD")).thenReturn("Y");
    when(rs.getString("CLT_ZIPPY_IND")).thenReturn("Y");

    when(rs.getString("CLA_IDENTIFIER")).thenReturn(DEFAULT_CLIENT_ID);
    when(rs.getString("ADR_IDENTIFIER")).thenReturn(DEFAULT_CLIENT_ID);
    when(rs.getString("SAL_THIRD_ID")).thenReturn(DEFAULT_CLIENT_ID);

    when(rs.getShort("ONM_NAME_TPC")).thenReturn(shortZero);
    when(rs.getShort("SAL_ACTV_GEC")).thenReturn(shortZero);
    when(rs.getShort("SAL_ACTV_RNC")).thenReturn(shortZero);
    when(rs.getShort("SAL_DACT_GEC")).thenReturn(shortZero);

    when(rs.getString("ONM_IBMSNAP_OPERATION")).thenReturn("U");
    when(rs.getString("SAL_IBMSNAP_OPERATION")).thenReturn("U");

    when(rs.getString("ONM_FIRST_NM")).thenReturn("David");
    when(rs.getString("ONM_LAST_NM")).thenReturn("Smith");
    when(rs.getString("ONM_LST_UPD_ID")).thenReturn("0X3");
    when(rs.getString("ONM_MIDDLE_NM")).thenReturn("X");
    when(rs.getString("ONM_NMPRFX_DSC")).thenReturn("X");
    when(rs.getString("ONM_SUFX_TLDSC")).thenReturn("phd");
    when(rs.getString("ONM_THIRD_ID")).thenReturn(DEFAULT_CLIENT_ID);

    when(rs.getString("SAL_ACTV_TXT")).thenReturn("X");
    when(rs.getString("SAL_DACT_TXT")).thenReturn("X");
    when(rs.getString("SAL_LST_UPD_ID")).thenReturn("3fa");
    when(rs.getString("SAL_THIRD_ID")).thenReturn(DEFAULT_CLIENT_ID);

    final long time = new Date().getTime();
    when(rs.getTimestamp("ONM_IBMSNAP_LOGMARKER")).thenReturn(new Timestamp(time));
    when(rs.getTimestamp("SAL_IBMSNAP_LOGMARKER")).thenReturn(new Timestamp(time));
    when(rs.getTimestamp("ONM_LST_UPD_TS")).thenReturn(new Timestamp(time));
    when(rs.getTimestamp("SAL_LST_UPD_TS")).thenReturn(new Timestamp(time));
  }

  @Test
  public void type() throws Exception {
    assertThat(EsClientPerson.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void extract_Args__ResultSet() throws Exception {
    final EsClientPerson actual = EsClientPerson.extract(rs);
    assertThat(actual, is(notNullValue()));
  }

  @Test(expected = SQLException.class)
  public void extract_Args__ResultSet_T__SQLException() throws Exception {
    when(rs.getString(any(String.class))).thenThrow(SQLException.class);
    EsClientPerson.extract(rs);
  }

  @Test
  public void getNormalizationClass_Args__() throws Exception {
    Class<ReplicatedClient> actual = target.getNormalizationClass();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void normalize_Args__Map() throws Exception {
    target.setAdrId(DEFAULT_CLIENT_ID);
    target.setClaId(DEFAULT_CLIENT_ID);
    target.setAdrReplicationOperation(CmsReplicationOperation.U);
    target.setClaReplicationOperation(CmsReplicationOperation.U);

    target.setAkaFirstName("fred");
    target.setAkaLastName("meyer");
    target.setAkaMiddleName("xavier");
    target.setAkaNamePrefixDescription("mr");
    target.setAkaNameType((short) 1313);
    target.setAkaReplicationTimestamp(new Date());
    target.setAkaLastUpdatedTimestamp(new Timestamp(new Date().getTime()));
    target.setAkaSuffixTitleDescription("phd");
    target.setAkaLastUpdatedOperation(CmsReplicationOperation.I);
    target.setAkaLastUpdatedId("0x5");

    final Map<Object, ReplicatedClient> map = new HashMap<Object, ReplicatedClient>();
    final ReplicatedClient rep = new ReplicatedClient();
    rep.setId(DEFAULT_CLIENT_ID);

    final ReplicatedClient actual = target.normalize(map);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getCltAdjudicatedDelinquentIndicator_Args__() throws Exception {
    final String actual = target.getCltAdjudicatedDelinquentIndicator();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltAdoptionStatusCode_Args__() throws Exception {
    final String actual = target.getCltAdoptionStatusCode();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltAlienRegistrationNumber_Args__() throws Exception {
    final String actual = target.getCltAlienRegistrationNumber();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltBirthCity_Args__() throws Exception {
    final String actual = target.getCltBirthCity();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltBirthCountryCodeType_Args__() throws Exception {
    Short actual = target.getCltBirthCountryCodeType();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltBirthDate_Args__() throws Exception {
    Date actual = target.getCltBirthDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltBirthFacilityName_Args__() throws Exception {
    final String actual = target.getCltBirthFacilityName();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltBirthStateCodeType_Args__() throws Exception {
    Short actual = target.getCltBirthStateCodeType();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltBirthplaceVerifiedIndicator_Args__() throws Exception {
    final String actual = target.getCltBirthplaceVerifiedIndicator();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltBirthplaceVerifiedIndicator_Args__String() throws Exception {
    String cltBirthplaceVerifiedIndicator = null;
    target.setCltBirthplaceVerifiedIndicator(cltBirthplaceVerifiedIndicator);
  }

  @Test
  public void getCltChildClientIndicatorVar_Args__() throws Exception {
    final String actual = target.getCltChildClientIndicatorVar();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltClientIndexNumber_Args__() throws Exception {
    final String actual = target.getCltClientIndexNumber();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltCommentDescription_Args__() throws Exception {
    final String actual = target.getCltCommentDescription();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltCommonFirstName_Args__() throws Exception {
    final String actual = target.getCltCommonFirstName();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltCommonLastName_Args__() throws Exception {
    final String actual = target.getCltCommonLastName();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltCommonMiddleName_Args__() throws Exception {
    final String actual = target.getCltCommonMiddleName();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltConfidentialityActionDate_Args__() throws Exception {
    Date actual = target.getCltConfidentialityActionDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltConfidentialityActionDate_Args__Date() throws Exception {
    Date cltConfidentialityActionDate = mock(Date.class);
    target.setCltConfidentialityActionDate(cltConfidentialityActionDate);
  }

  @Test
  public void getCltConfidentialityInEffectIndicator_Args__() throws Exception {
    final String actual = target.getCltConfidentialityInEffectIndicator();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltConfidentialityInEffectIndicator_Args__String() throws Exception {
    String cltConfidentialityInEffectIndicator = null;
    target.setCltConfidentialityInEffectIndicator(cltConfidentialityInEffectIndicator);
  }

  @Test
  public void getCltCreationDate_Args__() throws Exception {
    Date actual = target.getCltCreationDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltCreationDate_Args__Date() throws Exception {
    Date cltCreationDate = mock(Date.class);
    target.setCltCreationDate(cltCreationDate);
  }

  @Test
  public void getCltCurrCaChildrenServIndicator_Args__() throws Exception {
    final String actual = target.getCltCurrCaChildrenServIndicator();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltCurrCaChildrenServIndicator_Args__String() throws Exception {
    String cltCurrCaChildrenServIndicator = null;
    target.setCltCurrCaChildrenServIndicator(cltCurrCaChildrenServIndicator);
  }

  @Test
  public void getCltCurrentlyOtherDescription_Args__() throws Exception {
    final String actual = target.getCltCurrentlyOtherDescription();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltCurrentlyRegionalCenterIndicator_Args__() throws Exception {
    final String actual = target.getCltCurrentlyRegionalCenterIndicator();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltDeathDate_Args__() throws Exception {
    Date actual = target.getCltDeathDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltDeathDateVerifiedIndicator_Args__() throws Exception {
    final String actual = target.getCltDeathDateVerifiedIndicator();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltDeathPlace_Args__() throws Exception {
    final String actual = target.getCltDeathPlace();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltDeathReasonText_Args__() throws Exception {
    final String actual = target.getCltDeathReasonText();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltDriverLicenseNumber_Args__() throws Exception {
    final String actual = target.getCltDriverLicenseNumber();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltDriverLicenseStateCodeType_Args__() throws Exception {
    Short actual = target.getCltDriverLicenseStateCodeType();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltEmailAddress_Args__() throws Exception {
    final String actual = target.getCltEmailAddress();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltEstimatedDobCode_Args__() throws Exception {
    final String actual = target.getCltEstimatedDobCode();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltEthUnableToDetReasonCode_Args__() throws Exception {
    final String actual = target.getCltEthUnableToDetReasonCode();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltGenderCode_Args__() throws Exception {
    final String actual = target.getCltGenderCode();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltHealthSummaryText_Args__() throws Exception {
    final String actual = target.getCltHealthSummaryText();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltHealthSummaryText_Args__String() throws Exception {
    String cltHealthSummaryText = null;
    target.setCltHealthSummaryText(cltHealthSummaryText);
  }

  @Test
  public void getCltHispUnableToDetReasonCode_Args__() throws Exception {
    final String actual = target.getCltHispUnableToDetReasonCode();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltHispUnableToDetReasonCode_Args__String() throws Exception {
    String cltHispUnableToDetReasonCode = null;
    target.setCltHispUnableToDetReasonCode(cltHispUnableToDetReasonCode);
  }

  @Test
  public void getCltHispanicOriginCode_Args__() throws Exception {
    final String actual = target.getCltHispanicOriginCode();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltHispanicOriginCode_Args__String() throws Exception {
    String cltHispanicOriginCode = null;
    target.setCltHispanicOriginCode(cltHispanicOriginCode);
  }

  @Test
  public void getCltId_Args__() throws Exception {
    final String actual = target.getCltId();
    final String expected = DEFAULT_CLIENT_ID;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltId_Args__String() throws Exception {
    String cltId = null;
    target.setCltId(cltId);
  }

  @Test
  public void getCltImmigrationCountryCodeType_Args__() throws Exception {
    Short actual = target.getCltImmigrationCountryCodeType();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltImmigrationCountryCodeType_Args__Short() throws Exception {
    Short cltImmigrationCountryCodeType = null;
    target.setCltImmigrationCountryCodeType(cltImmigrationCountryCodeType);
  }

  @Test
  public void getCltImmigrationStatusType_Args__() throws Exception {
    Short actual = target.getCltImmigrationStatusType();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltIncapacitatedParentCode_Args__() throws Exception {
    final String actual = target.getCltIncapacitatedParentCode();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltIndividualHealthCarePlanIndicator_Args__() throws Exception {
    final String actual = target.getCltIndividualHealthCarePlanIndicator();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltLimitationOnScpHealthIndicator_Args__() throws Exception {
    final String actual = target.getCltLimitationOnScpHealthIndicator();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltLiterateCode_Args__() throws Exception {
    final String actual = target.getCltLiterateCode();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltMaritalCohabitatnHstryIndicatorVar_Args__() throws Exception {
    final String actual = target.getCltMaritalCohabitatnHstryIndicatorVar();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltMaritalStatusType_Args__() throws Exception {
    Short actual = target.getCltMaritalStatusType();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltMilitaryStatusCode_Args__() throws Exception {
    final String actual = target.getCltMilitaryStatusCode();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltMotherParentalRightTermDate_Args__() throws Exception {
    Date actual = target.getCltMotherParentalRightTermDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltNamePrefixDescription_Args__() throws Exception {
    final String actual = target.getCltNamePrefixDescription();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltNameType_Args__() throws Exception {
    Short actual = target.getCltNameType();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltOutstandingWarrantIndicator_Args__() throws Exception {
    final String actual = target.getCltOutstandingWarrantIndicator();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltPrevCaChildrenServIndicator_Args__() throws Exception {
    final String actual = target.getCltPrevCaChildrenServIndicator();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltPrevOtherDescription_Args__() throws Exception {
    final String actual = target.getCltPrevOtherDescription();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltPrevRegionalCenterIndicator_Args__() throws Exception {
    final String actual = target.getCltPrevRegionalCenterIndicator();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltPrevRegionalCenterIndicator_Args__String() throws Exception {
    String cltPrevRegionalCenterIndicator = null;
    target.setCltPrevRegionalCenterIndicator(cltPrevRegionalCenterIndicator);
  }

  @Test
  public void getCltPrimaryEthnicityType_Args__() throws Exception {
    Short actual = target.getCltPrimaryEthnicityType();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltPrimaryEthnicityType_Args__Short() throws Exception {
    Short cltPrimaryEthnicityType = null;
    target.setCltPrimaryEthnicityType(cltPrimaryEthnicityType);
  }

  @Test
  public void getCltPrimaryLanguageType_Args__() throws Exception {
    Short actual = target.getCltPrimaryLanguageType();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltPrimaryLanguageType_Args__Short() throws Exception {
    Short cltPrimaryLanguageType = null;
    target.setCltPrimaryLanguageType(cltPrimaryLanguageType);
  }

  @Test
  public void getCltReligionType_Args__() throws Exception {
    Short actual = target.getCltReligionType();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltReligionType_Args__Short() throws Exception {
    Short cltReligionType = null;
    target.setCltReligionType(cltReligionType);
  }

  @Test
  public void getCltSecondaryLanguageType_Args__() throws Exception {
    Short actual = target.getCltSecondaryLanguageType();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltSecondaryLanguageType_Args__Short() throws Exception {
    Short cltSecondaryLanguageType = null;
    target.setCltSecondaryLanguageType(cltSecondaryLanguageType);
  }

  @Test
  public void getCltSensitiveHlthInfoOnFileIndicator_Args__() throws Exception {
    final String actual = target.getCltSensitiveHlthInfoOnFileIndicator();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltSensitiveHlthInfoOnFileIndicator_Args__String() throws Exception {
    String cltSensitiveHlthInfoOnFileIndicator = null;
    target.setCltSensitiveHlthInfoOnFileIndicator(cltSensitiveHlthInfoOnFileIndicator);
  }

  @Test
  public void getCltSensitivityIndicator_Args__() throws Exception {
    final String actual = target.getCltSensitivityIndicator();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltSensitivityIndicator_Args__String() throws Exception {
    String cltSensitivityIndicator = null;
    target.setCltSensitivityIndicator(cltSensitivityIndicator);
  }

  @Test
  public void getCltSoc158PlacementCode_Args__() throws Exception {
    final String actual = target.getCltSoc158PlacementCode();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltSoc158PlacementCode_Args__String() throws Exception {
    String cltSoc158PlacementCode = null;
    target.setCltSoc158PlacementCode(cltSoc158PlacementCode);
  }

  @Test
  public void getCltSoc158SealedClientIndicator_Args__() throws Exception {
    final String actual = target.getCltSoc158SealedClientIndicator();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltSoc158SealedClientIndicator_Args__String() throws Exception {
    String cltSoc158SealedClientIndicator = null;
    target.setCltSoc158SealedClientIndicator(cltSoc158SealedClientIndicator);
  }

  @Test
  public void getCltSocialSecurityNumChangedCode_Args__() throws Exception {
    final String actual = target.getCltSocialSecurityNumChangedCode();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltSocialSecurityNumChangedCode_Args__String() throws Exception {
    String cltSocialSecurityNumChangedCode = null;
    target.setCltSocialSecurityNumChangedCode(cltSocialSecurityNumChangedCode);
  }

  @Test
  public void getCltSocialSecurityNumber_Args__() throws Exception {
    final String actual = target.getCltSocialSecurityNumber();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltSocialSecurityNumber_Args__String() throws Exception {
    String cltSocialSecurityNumber = null;
    target.setCltSocialSecurityNumber(cltSocialSecurityNumber);
  }

  @Test
  public void getCltSuffixTitleDescription_Args__() throws Exception {
    final String actual = target.getCltSuffixTitleDescription();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltSuffixTitleDescription_Args__String() throws Exception {
    String cltSuffixTitleDescription = null;
    target.setCltSuffixTitleDescription(cltSuffixTitleDescription);
  }

  @Test
  public void getCltTribalAncestryClientIndicatorVar_Args__() throws Exception {
    final String actual = target.getCltTribalAncestryClientIndicatorVar();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltTribalAncestryClientIndicatorVar_Args__String() throws Exception {
    String cltTribalAncestryClientIndicatorVar = null;
    target.setCltTribalAncestryClientIndicatorVar(cltTribalAncestryClientIndicatorVar);
  }

  @Test
  public void getCltTribalMembrshpVerifctnIndicatorVar_Args__() throws Exception {
    final String actual = target.getCltTribalMembrshpVerifctnIndicatorVar();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltTribalMembrshpVerifctnIndicatorVar_Args__String() throws Exception {
    String cltTribalMembrshpVerifctnIndicatorVar = null;
    target.setCltTribalMembrshpVerifctnIndicatorVar(cltTribalMembrshpVerifctnIndicatorVar);
  }

  @Test
  public void getCltUnemployedParentCode_Args__() throws Exception {
    final String actual = target.getCltUnemployedParentCode();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltUnemployedParentCode_Args__String() throws Exception {
    String cltUnemployedParentCode = null;
    target.setCltUnemployedParentCode(cltUnemployedParentCode);
  }

  @Test
  public void getCltZippyCreatedIndicator_Args__() throws Exception {
    final String actual = target.getCltZippyCreatedIndicator();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltZippyCreatedIndicator_Args__String() throws Exception {
    String cltZippyCreatedIndicator = null;
    target.setCltZippyCreatedIndicator(cltZippyCreatedIndicator);
  }

  @Test
  public void getCltReplicationOperation_Args__() throws Exception {
    CmsReplicationOperation actual = target.getCltReplicationOperation();
    CmsReplicationOperation expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltReplicationOperation_Args__CmsReplicationOperation() throws Exception {
    CmsReplicationOperation cltReplicationOperation = CmsReplicationOperation.U;
    target.setCltReplicationOperation(cltReplicationOperation);
  }

  @Test
  public void getCltReplicationDate_Args__() throws Exception {
    Date actual = target.getCltReplicationDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltReplicationDate_Args__Date() throws Exception {
    Date cltReplicationDate = mock(Date.class);
    target.setCltReplicationDate(cltReplicationDate);
  }

  @Test
  public void getCltLastUpdatedId_Args__() throws Exception {
    final String actual = target.getCltLastUpdatedId();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltLastUpdatedId_Args__String() throws Exception {
    String cltLastUpdatedId = null;
    target.setCltLastUpdatedId(cltLastUpdatedId);
  }

  @Test
  public void getCltLastUpdatedTime_Args__() throws Exception {
    Date actual = target.getCltLastUpdatedTime();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltLastUpdatedTime_Args__Date() throws Exception {
    Date cltLastUpdatedTime = mock(Date.class);
    target.setCltLastUpdatedTime(cltLastUpdatedTime);
  }

  @Test
  public void getClaReplicationOperation_Args__() throws Exception {
    CmsReplicationOperation actual = target.getClaReplicationOperation();
    CmsReplicationOperation expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaReplicationOperation_Args__CmsReplicationOperation() throws Exception {
    CmsReplicationOperation claReplicationOperation = CmsReplicationOperation.I;
    target.setClaReplicationOperation(claReplicationOperation);
  }

  @Test
  public void getClaReplicationDate_Args__() throws Exception {
    Date actual = target.getClaReplicationDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaReplicationDate_Args__Date() throws Exception {
    Date claReplicationDate = mock(Date.class);
    target.setClaReplicationDate(claReplicationDate);
  }

  @Test
  public void getClaLastUpdatedId_Args__() throws Exception {
    final String actual = target.getClaLastUpdatedId();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaLastUpdatedId_Args__String() throws Exception {
    String claLastUpdatedId = null;
    target.setClaLastUpdatedId(claLastUpdatedId);
  }

  @Test
  public void getClaLastUpdatedTime_Args__() throws Exception {
    Date actual = target.getClaLastUpdatedTime();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaLastUpdatedTime_Args__Date() throws Exception {
    Date claLastUpdatedTime = mock(Date.class);
    target.setClaLastUpdatedTime(claLastUpdatedTime);
  }

  @Test
  public void getClaFkAddress_Args__() throws Exception {
    final String actual = target.getClaFkAddress();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaFkAddress_Args__String() throws Exception {
    String claFkAddress = null;
    target.setClaFkAddress(claFkAddress);
  }

  @Test
  public void getClaFkClient_Args__() throws Exception {
    final String actual = target.getClaFkClient();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaFkClient_Args__String() throws Exception {
    String claFkClient = null;
    target.setClaFkClient(claFkClient);
  }

  @Test
  public void getClaFkReferral_Args__() throws Exception {
    final String actual = target.getClaFkReferral();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaFkReferral_Args__String() throws Exception {
    String claFkReferral = null;
    target.setClaFkReferral(claFkReferral);
  }

  @Test
  public void getClaAddressType_Args__() throws Exception {
    Short actual = target.getClaAddressType();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaAddressType_Args__Short() throws Exception {
    Short claAddressType = null;
    target.setClaAddressType(claAddressType);
  }

  @Test
  public void getClaHomelessInd_Args__() throws Exception {
    final String actual = target.getClaHomelessInd();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaHomelessInd_Args__String() throws Exception {
    String claHomelessInd = null;
    target.setClaHomelessInd(claHomelessInd);
  }

  @Test
  public void getClaBkInmtId_Args__() throws Exception {
    final String actual = target.getClaBkInmtId();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaBkInmtId_Args__String() throws Exception {
    String claBkInmtId = null;
    target.setClaBkInmtId(claBkInmtId);
  }

  @Test
  public void getClaEffectiveEndDate_Args__() throws Exception {
    Date actual = target.getClaEffectiveEndDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaEffectiveEndDate_Args__Date() throws Exception {
    Date claEffectiveEndDate = mock(Date.class);
    target.setClaEffectiveEndDate(claEffectiveEndDate);
  }

  @Test
  public void getClaEffectiveStartDate_Args__() throws Exception {
    Date actual = target.getClaEffectiveStartDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaEffectiveStartDate_Args__Date() throws Exception {
    Date claEffectiveStartDate = mock(Date.class);
    target.setClaEffectiveStartDate(claEffectiveStartDate);
  }

  @Test
  public void getAdrId_Args__() throws Exception {
    final String actual = target.getAdrId();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrId_Args__String() throws Exception {
    String adrId = null;
    target.setAdrId(adrId);
  }

  @Test
  public void getAdrCity_Args__() throws Exception {
    final String actual = target.getAdrCity();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrCity_Args__String() throws Exception {
    String adrCity = null;
    target.setAdrCity(adrCity);
  }

  @Test
  public void getAdrEmergencyNumber_Args__() throws Exception {
    BigDecimal actual = target.getAdrEmergencyNumber();
    BigDecimal expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrEmergencyNumber_Args__BigDecimal() throws Exception {
    BigDecimal adrEmergencyNumber = mock(BigDecimal.class);
    target.setAdrEmergencyNumber(adrEmergencyNumber);
  }

  @Test
  public void getAdrEmergencyExtension_Args__() throws Exception {
    Integer actual = target.getAdrEmergencyExtension();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrEmergencyExtension_Args__Integer() throws Exception {
    Integer adrEmergencyExtension = null;
    target.setAdrEmergencyExtension(adrEmergencyExtension);
  }

  @Test
  public void getAdrFrgAdrtB_Args__() throws Exception {
    final String actual = target.getAdrFrgAdrtB();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrFrgAdrtB_Args__String() throws Exception {
    String adrFrgAdrtB = null;
    target.setAdrFrgAdrtB(adrFrgAdrtB);
  }

  @Test
  public void getAdrGovernmentEntityCd_Args__() throws Exception {
    Short actual = target.getAdrGovernmentEntityCd();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrGovernmentEntityCd_Args__Short() throws Exception {
    Short adrGovernmentEntityCd = null;
    target.setAdrGovernmentEntityCd(adrGovernmentEntityCd);
  }

  @Test
  public void getAdrMessageNumber_Args__() throws Exception {
    BigDecimal actual = target.getAdrMessageNumber();
    BigDecimal expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrMessageNumber_Args__BigDecimal() throws Exception {
    BigDecimal adrMessageNumber = mock(BigDecimal.class);
    target.setAdrMessageNumber(adrMessageNumber);
  }

  @Test
  public void getAdrMessageExtension_Args__() throws Exception {
    Integer actual = target.getAdrMessageExtension();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrMessageExtension_Args__Integer() throws Exception {
    Integer adrMessageExtension = null;
    target.setAdrMessageExtension(adrMessageExtension);
  }

  @Test
  public void getAdrHeaderAddress_Args__() throws Exception {
    final String actual = target.getAdrHeaderAddress();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrHeaderAddress_Args__String() throws Exception {
    String adrHeaderAddress = null;
    target.setAdrHeaderAddress(adrHeaderAddress);
  }

  @Test
  public void getAdrPrimaryNumber_Args__() throws Exception {
    BigDecimal actual = target.getAdrPrimaryNumber();
    BigDecimal expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrPrimaryNumber_Args__BigDecimal() throws Exception {
    BigDecimal adrPrimaryNumber = mock(BigDecimal.class);
    target.setAdrPrimaryNumber(adrPrimaryNumber);
  }

  @Test
  public void getAdrPrimaryExtension_Args__() throws Exception {
    Integer actual = target.getAdrPrimaryExtension();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getAdrState_Args__() throws Exception {
    Short actual = target.getAdrState();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getAdrStreetName_Args__() throws Exception {
    final String actual = target.getAdrStreetName();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getAdrStreetNumber_Args__() throws Exception {
    final String actual = target.getAdrStreetNumber();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getAdrZip_Args__() throws Exception {
    final String actual = target.getAdrZip();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getAdrAddressDescription_Args__() throws Exception {
    final String actual = target.getAdrAddressDescription();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrAddressDescription_Args__String() throws Exception {
    String adrAddressDescription = null;
    target.setAdrAddressDescription(adrAddressDescription);
  }

  @Test
  public void getAdrZip4_Args__() throws Exception {
    Short actual = target.getAdrZip4();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getAdrPostDirCd_Args__() throws Exception {
    final String actual = target.getAdrPostDirCd();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getAdrPreDirCd_Args__() throws Exception {
    final String actual = target.getAdrPreDirCd();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getAdrStreetSuffixCd_Args__() throws Exception {
    Short actual = target.getAdrStreetSuffixCd();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getAdrUnitDesignationCd_Args__() throws Exception {
    Short actual = target.getAdrUnitDesignationCd();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getAdrUnitNumber_Args__() throws Exception {
    final String actual = target.getAdrUnitNumber();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getClaId_Args__() throws Exception {
    final String actual = target.getClaId();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getClientCounty_Args__() throws Exception {
    Short actual = target.getClientCounty();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClientCounty_Args__Short() throws Exception {
    Short clientCounty = null;
    target.setClientCounty(clientCounty);
  }

  @Test
  public void getAdrReplicationOperation_Args__() throws Exception {
    CmsReplicationOperation actual = target.getAdrReplicationOperation();
    CmsReplicationOperation expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getAdrReplicationDate_Args__() throws Exception {
    Date actual = target.getAdrReplicationDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLastChange_Args__() throws Exception {
    Date actual = target.getLastChange();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getNormalizationGroupKey_Args__() throws Exception {
    final String actual = target.getNormalizationGroupKey();
    final String expected = DEFAULT_CLIENT_ID;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    Serializable actual = target.getPrimaryKey();
    Serializable expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void compare_Args__EsClient__EsClient() throws Exception {
    EsClientPerson o1 = new EsClientPerson();
    o1.setCltId(DEFAULT_CLIENT_ID);
    EsClientPerson o2 = new EsClientPerson();
    o2.setCltId(DEFAULT_CLIENT_ID);

    int actual = target.compare(o1, o2);
    int expected = 0;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void compareTo_Args__EsClient() throws Exception {
    EsClientPerson o = new EsClientPerson();
    o.setCltId(DEFAULT_CLIENT_ID);

    int actual = target.compareTo(o);
    int expected = 0;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void hashCode_Args__() throws Exception {
    int actual = target.hashCode();
    assertThat(actual, is(not(0)));
  }

  @Test
  public void equals_Args__Object() throws Exception {
    Object obj = null;
    boolean actual = target.equals(obj);
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltFatherParentalRightTermDate_Args__() throws Exception {
    Date actual = target.getCltFatherParentalRightTermDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaId_Args__String() throws Exception {
    String claId = null;
    target.setClaId(claId);
  }

  @Test
  public void getClientCountyId_Args__() throws Exception {
    final String actual = target.getClientCountyId();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getClientEthnicityId_Args__() throws Exception {
    final String actual = target.getClientEthnicityId();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getClientEthnicityCode_Args__() throws Exception {
    Short actual = target.getClientEthnicityCode();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getClientCountyRule_Args__() throws Exception {
    final String actual = target.getClientCountyRule();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClientCountyRule_Args__String() throws Exception {
    String clientCountyRule = null;
    target.setClientCountyRule(clientCountyRule);
  }

  @Test
  public void setClientCountyId_Args__String() throws Exception {
    String clientCountyId = null;
    target.setClientCountyId(clientCountyId);
  }

  @Test
  public void setClientEthnicityId_Args__String() throws Exception {
    String clientEthnicityId = null;
    target.setClientEthnicityId(clientEthnicityId);
  }

  @Test
  public void setClientEthnicityCode_Args__Short() throws Exception {
    Short clientEthnicityCode = null;
    target.setClientEthnicityCode(clientEthnicityCode);
  }

  @Test
  public void setAdrReplicationOperation_Args__CmsReplicationOperation() throws Exception {
    CmsReplicationOperation adrReplicationOperation = CmsReplicationOperation.U;
    target.setAdrReplicationOperation(adrReplicationOperation);
  }

  @Test
  public void getSafetyAlertId_Args__() throws Exception {
    final String actual = target.getSafetyAlertId();
    final String expected = DEFAULT_CLIENT_ID;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setSafetyAlertId_Args__String() throws Exception {
    String safetyAlertId = null;
    target.setSafetyAlertId(safetyAlertId);
  }

  @Test
  public void getSafetyAlertActivationReasonCode_Args__() throws Exception {
    Short actual = target.getSafetyAlertActivationReasonCode();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setSafetyAlertActivationReasonCode_Args__Short() throws Exception {
    Short safetyAlertActivationReasonCode = null;
    target.setSafetyAlertActivationReasonCode(safetyAlertActivationReasonCode);
  }

  @Test
  public void getSafetyAlertActivationDate_Args__() throws Exception {
    Date actual = target.getSafetyAlertActivationDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setSafetyAlertActivationDate_Args__Date() throws Exception {
    Date safetyAlertActivationDate = mock(Date.class);
    target.setSafetyAlertActivationDate(safetyAlertActivationDate);
  }

  @Test
  public void getSafetyAlertActivationCountyCode_Args__() throws Exception {
    Short actual = target.getSafetyAlertActivationCountyCode();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setSafetyAlertActivationCountyCode_Args__Short() throws Exception {
    Short safetyAlertActivationCountyCode = null;
    target.setSafetyAlertActivationCountyCode(safetyAlertActivationCountyCode);
  }

  @Test
  public void getSafetyAlertActivationExplanation_Args__() throws Exception {
    final String actual = target.getSafetyAlertActivationExplanation();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setSafetyAlertActivationExplanation_Args__String() throws Exception {
    String safetyAlertActivationExplanation = null;
    target.setSafetyAlertActivationExplanation(safetyAlertActivationExplanation);
  }

  @Test
  public void getSafetyAlertDeactivationDate_Args__() throws Exception {
    Date actual = target.getSafetyAlertDeactivationDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setSafetyAlertDeactivationDate_Args__Date() throws Exception {
    Date safetyAlertDeactivationDate = mock(Date.class);
    target.setSafetyAlertDeactivationDate(safetyAlertDeactivationDate);
  }

  @Test
  public void getSafetyAlertDeactivationCountyCode_Args__() throws Exception {
    Short actual = target.getSafetyAlertDeactivationCountyCode();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setSafetyAlertDeactivationCountyCode_Args__Short() throws Exception {
    Short safetyAlertDeactivationCountyCode = null;
    target.setSafetyAlertDeactivationCountyCode(safetyAlertDeactivationCountyCode);
  }

  @Test
  public void getSafetyAlertDeactivationExplanation_Args__() throws Exception {
    final String actual = target.getSafetyAlertDeactivationExplanation();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setSafetyAlertDeactivationExplanation_Args__String() throws Exception {
    String safetyAlertDeactivationExplanation = null;
    target.setSafetyAlertDeactivationExplanation(safetyAlertDeactivationExplanation);
  }

  @Test
  public void getSafetyAlertLastUpdatedId_Args__() throws Exception {
    final String actual = target.getSafetyAlertLastUpdatedId();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setSafetyAlertLastUpdatedId_Args__String() throws Exception {
    String safetyAlertLastUpdatedId = null;
    target.setSafetyAlertLastUpdatedId(safetyAlertLastUpdatedId);
  }

  @Test
  public void getSafetyAlertLastUpdatedTimestamp_Args__() throws Exception {
    Date actual = target.getSafetyAlertLastUpdatedTimestamp();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setSafetyAlertLastUpdatedTimestamp_Args__Date() throws Exception {
    Date safetyAlertLastUpdatedTimestamp = mock(Date.class);
    target.setSafetyAlertLastUpdatedTimestamp(safetyAlertLastUpdatedTimestamp);
  }

  @Test
  public void getSafetyAlertLastUpdatedOperation_Args__() throws Exception {
    CmsReplicationOperation actual = target.getSafetyAlertLastUpdatedOperation();
    CmsReplicationOperation expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setSafetyAlertLastUpdatedOperation_Args__CmsReplicationOperation() throws Exception {
    CmsReplicationOperation safetyAlertLastUpdatedOperation = CmsReplicationOperation.U;
    target.setSafetyAlertLastUpdatedOperation(safetyAlertLastUpdatedOperation);
  }

  @Test
  public void getSafetyAlertReplicationTimestamp_Args__() throws Exception {
    Date actual = target.getSafetyAlertReplicationTimestamp();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setSafetyAlertReplicationTimestamp_Args__Date() throws Exception {
    Date safetyAlertReplicationTimestamp = new Date();
    target.setSafetyAlertReplicationTimestamp(safetyAlertReplicationTimestamp);
  }

  @Test
  public void getAkaId_Args__() throws Exception {
    final String actual = target.getAkaId();
    final String expected = DEFAULT_CLIENT_ID;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAkaId_Args__String() throws Exception {
    String akaId = null;
    target.setAkaId(akaId);
  }

  @Test
  public void getAkaFirstName_Args__() throws Exception {
    target.setAkaFirstName("fred");
    final String actual = target.getAkaFirstName();
    final String expected = "fred";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAkaFirstName_Args__String() throws Exception {
    String akaFirstName = null;
    target.setAkaFirstName(akaFirstName);
  }

  @Test
  public void getAkaLastName_Args__() throws Exception {
    final String actual = target.getAkaLastName();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAkaLastName_Args__String() throws Exception {
    String akaLastName = null;
    target.setAkaLastName(akaLastName);
  }

  @Test
  public void getAkaMiddleName_Args__() throws Exception {
    final String actual = target.getAkaMiddleName();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAkaMiddleName_Args__String() throws Exception {
    String akaMiddleName = null;
    target.setAkaMiddleName(akaMiddleName);
  }

  @Test
  public void getAkaNamePrefixDescription_Args__() throws Exception {
    final String actual = target.getAkaNamePrefixDescription();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAkaNamePrefixDescription_Args__String() throws Exception {
    String akaNamePrefixDescription = null;
    target.setAkaNamePrefixDescription(akaNamePrefixDescription);
  }

  @Test
  public void getAkaNameType_Args__() throws Exception {
    Short actual = target.getAkaNameType();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAkaNameType_Args__Short() throws Exception {
    Short akaNameType = null;
    target.setAkaNameType(akaNameType);
  }

  @Test
  public void getAkaSuffixTitleDescription_Args__() throws Exception {
    final String actual = target.getAkaSuffixTitleDescription();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAkaSuffixTitleDescription_Args__String() throws Exception {
    String akaSuffixTitleDescription = null;
    target.setAkaSuffixTitleDescription(akaSuffixTitleDescription);
  }

  @Test
  public void getAkaLastUpdatedId_Args__() throws Exception {
    final String actual = target.getAkaLastUpdatedId();
    final String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAkaLastUpdatedId_Args__String() throws Exception {
    String akaLastUpdatedId = null;
    target.setAkaLastUpdatedId(akaLastUpdatedId);
  }

  @Test
  public void getAkaLastUpdatedTimestamp_Args__() throws Exception {
    Date actual = target.getAkaLastUpdatedTimestamp();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAkaLastUpdatedTimestamp_Args__Date() throws Exception {
    Date akaLastUpdatedTimestamp = mock(Date.class);
    target.setAkaLastUpdatedTimestamp(akaLastUpdatedTimestamp);
  }

  @Test
  public void getAkaLastUpdatedOperation_Args__() throws Exception {
    CmsReplicationOperation actual = target.getAkaLastUpdatedOperation();
    CmsReplicationOperation expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAkaLastUpdatedOperation_Args__CmsReplicationOperation() throws Exception {
    CmsReplicationOperation akaLastUpdatedOperation = CmsReplicationOperation.U;
    target.setAkaLastUpdatedOperation(akaLastUpdatedOperation);
  }

  @Test
  public void getAkaReplicationTimestamp_Args__() throws Exception {
    Date actual = target.getAkaReplicationTimestamp();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAkaReplicationTimestamp_Args__Date() throws Exception {
    Date akaReplicationTimestamp = mock(Date.class);
    target.setAkaReplicationTimestamp(akaReplicationTimestamp);
  }

  @Test
  public void setLastChange_Args__Date() throws Exception {
    Date lastChange = mock(Date.class);
    target.setLastChange(lastChange);
  }

  @Test
  public void setCltAdjudicatedDelinquentIndicator_Args__String() throws Exception {
    String cltAdjudicatedDelinquentIndicator = null;
    target.setCltAdjudicatedDelinquentIndicator(cltAdjudicatedDelinquentIndicator);
  }

  @Test
  public void setCltAdoptionStatusCode_Args__String() throws Exception {
    String cltAdoptionStatusCode = null;
    target.setCltAdoptionStatusCode(cltAdoptionStatusCode);
  }

  @Test
  public void setCltAlienRegistrationNumber_Args__String() throws Exception {
    String cltAlienRegistrationNumber = null;
    target.setCltAlienRegistrationNumber(cltAlienRegistrationNumber);
  }

  @Test
  public void setCltBirthCity_Args__String() throws Exception {
    String cltBirthCity = null;
    target.setCltBirthCity(cltBirthCity);
  }

  @Test
  public void setCltBirthCountryCodeType_Args__Short() throws Exception {
    Short cltBirthCountryCodeType = null;
    target.setCltBirthCountryCodeType(cltBirthCountryCodeType);
  }

  @Test
  public void setCltBirthDate_Args__Date() throws Exception {
    Date cltBirthDate = mock(Date.class);
    target.setCltBirthDate(cltBirthDate);
  }

  @Test
  public void setCltBirthFacilityName_Args__String() throws Exception {
    String cltBirthFacilityName = null;
    target.setCltBirthFacilityName(cltBirthFacilityName);
  }

  @Test
  public void setCltBirthStateCodeType_Args__Short() throws Exception {
    Short cltBirthStateCodeType = null;
    target.setCltBirthStateCodeType(cltBirthStateCodeType);
  }

  @Test
  public void setCltChildClientIndicatorVar_Args__String() throws Exception {
    String cltChildClientIndicatorVar = null;
    target.setCltChildClientIndicatorVar(cltChildClientIndicatorVar);
  }

  @Test
  public void setCltClientIndexNumber_Args__String() throws Exception {
    String cltClientIndexNumber = null;
    target.setCltClientIndexNumber(cltClientIndexNumber);
  }

  @Test
  public void setCltCommentDescription_Args__String() throws Exception {
    String cltCommentDescription = null;
    target.setCltCommentDescription(cltCommentDescription);
  }

  @Test
  public void setCltCommonFirstName_Args__String() throws Exception {
    String cltCommonFirstName = null;
    target.setCltCommonFirstName(cltCommonFirstName);
  }

  @Test
  public void setCltCommonLastName_Args__String() throws Exception {
    String cltCommonLastName = null;
    target.setCltCommonLastName(cltCommonLastName);
  }

  @Test
  public void setCltCommonMiddleName_Args__String() throws Exception {
    String cltCommonMiddleName = null;
    target.setCltCommonMiddleName(cltCommonMiddleName);
  }

  @Test
  public void setCltCurrentlyOtherDescription_Args__String() throws Exception {
    String cltCurrentlyOtherDescription = null;
    target.setCltCurrentlyOtherDescription(cltCurrentlyOtherDescription);
  }

  @Test
  public void setCltCurrentlyRegionalCenterIndicator_Args__String() throws Exception {
    String cltCurrentlyRegionalCenterIndicator = null;
    target.setCltCurrentlyRegionalCenterIndicator(cltCurrentlyRegionalCenterIndicator);
  }

  @Test
  public void setCltDeathDate_Args__Date() throws Exception {
    Date cltDeathDate = mock(Date.class);
    target.setCltDeathDate(cltDeathDate);
  }

  @Test
  public void setCltDeathDateVerifiedIndicator_Args__String() throws Exception {
    String cltDeathDateVerifiedIndicator = null;
    target.setCltDeathDateVerifiedIndicator(cltDeathDateVerifiedIndicator);
  }

  @Test
  public void setCltDeathPlace_Args__String() throws Exception {
    String cltDeathPlace = null;
    target.setCltDeathPlace(cltDeathPlace);
  }

  @Test
  public void setCltDeathReasonText_Args__String() throws Exception {
    String cltDeathReasonText = null;
    target.setCltDeathReasonText(cltDeathReasonText);
  }

  @Test
  public void setCltDriverLicenseNumber_Args__String() throws Exception {
    String cltDriverLicenseNumber = null;
    target.setCltDriverLicenseNumber(cltDriverLicenseNumber);
  }

  @Test
  public void setCltDriverLicenseStateCodeType_Args__Short() throws Exception {
    Short cltDriverLicenseStateCodeType = null;
    target.setCltDriverLicenseStateCodeType(cltDriverLicenseStateCodeType);
  }

  @Test
  public void setCltEmailAddress_Args__String() throws Exception {
    String cltEmailAddress = null;
    target.setCltEmailAddress(cltEmailAddress);
  }

  @Test
  public void setCltEstimatedDobCode_Args__String() throws Exception {
    String cltEstimatedDobCode = null;
    target.setCltEstimatedDobCode(cltEstimatedDobCode);
  }

  @Test
  public void setCltEthUnableToDetReasonCode_Args__String() throws Exception {
    String cltEthUnableToDetReasonCode = null;
    target.setCltEthUnableToDetReasonCode(cltEthUnableToDetReasonCode);
  }

  @Test
  public void setCltFatherParentalRightTermDate_Args__Date() throws Exception {
    Date cltFatherParentalRightTermDate = mock(Date.class);
    target.setCltFatherParentalRightTermDate(cltFatherParentalRightTermDate);
  }

  @Test
  public void setCltGenderCode_Args__String() throws Exception {
    String cltGenderCode = null;
    target.setCltGenderCode(cltGenderCode);
  }

  @Test
  public void setCltImmigrationStatusType_Args__Short() throws Exception {
    Short cltImmigrationStatusType = null;
    target.setCltImmigrationStatusType(cltImmigrationStatusType);
  }

  @Test
  public void setCltIncapacitatedParentCode_Args__String() throws Exception {
    String cltIncapacitatedParentCode = null;
    target.setCltIncapacitatedParentCode(cltIncapacitatedParentCode);
  }

  @Test
  public void setCltIndividualHealthCarePlanIndicator_Args__String() throws Exception {
    String cltIndividualHealthCarePlanIndicator = null;
    target.setCltIndividualHealthCarePlanIndicator(cltIndividualHealthCarePlanIndicator);
  }

  @Test
  public void setCltLimitationOnScpHealthIndicator_Args__String() throws Exception {
    String cltLimitationOnScpHealthIndicator = null;
    target.setCltLimitationOnScpHealthIndicator(cltLimitationOnScpHealthIndicator);
  }

  @Test
  public void setCltLiterateCode_Args__String() throws Exception {
    String cltLiterateCode = null;
    target.setCltLiterateCode(cltLiterateCode);
  }

  @Test
  public void setCltMaritalCohabitatnHstryIndicatorVar_Args__String() throws Exception {
    String cltMaritalCohabitatnHstryIndicatorVar = null;
    target.setCltMaritalCohabitatnHstryIndicatorVar(cltMaritalCohabitatnHstryIndicatorVar);
  }

  @Test
  public void setCltMaritalStatusType_Args__Short() throws Exception {
    Short cltMaritalStatusType = null;
    target.setCltMaritalStatusType(cltMaritalStatusType);
  }

  @Test
  public void setCltMilitaryStatusCode_Args__String() throws Exception {
    String cltMilitaryStatusCode = null;
    target.setCltMilitaryStatusCode(cltMilitaryStatusCode);
  }

  @Test
  public void setCltMotherParentalRightTermDate_Args__Date() throws Exception {
    Date cltMotherParentalRightTermDate = mock(Date.class);
    target.setCltMotherParentalRightTermDate(cltMotherParentalRightTermDate);
  }

  @Test
  public void setCltNamePrefixDescription_Args__String() throws Exception {
    String cltNamePrefixDescription = null;
    target.setCltNamePrefixDescription(cltNamePrefixDescription);
  }

  @Test
  public void setCltNameType_Args__Short() throws Exception {
    Short cltNameType = null;
    target.setCltNameType(cltNameType);
  }

  @Test
  public void setCltOutstandingWarrantIndicator_Args__String() throws Exception {
    String cltOutstandingWarrantIndicator = null;
    target.setCltOutstandingWarrantIndicator(cltOutstandingWarrantIndicator);
  }

  @Test
  public void setCltPrevCaChildrenServIndicator_Args__String() throws Exception {
    String cltPrevCaChildrenServIndicator = null;
    target.setCltPrevCaChildrenServIndicator(cltPrevCaChildrenServIndicator);
  }

  @Test
  public void setCltPrevOtherDescription_Args__String() throws Exception {
    String cltPrevOtherDescription = null;
    target.setCltPrevOtherDescription(cltPrevOtherDescription);
  }

  @Test
  public void setAdrReplicationDate_Args__Date() throws Exception {
    Date adrReplicationDate = mock(Date.class);
    target.setAdrReplicationDate(adrReplicationDate);
  }

  @Test
  public void setAdrPrimaryExtension_Args__Integer() throws Exception {
    Integer adrPrimaryExtension = null;
    target.setAdrPrimaryExtension(adrPrimaryExtension);
  }

  @Test
  public void setAdrState_Args__Short() throws Exception {
    Short adrState = null;
    target.setAdrState(adrState);
  }

  @Test
  public void setAdrStreetName_Args__String() throws Exception {
    String adrStreetName = null;
    target.setAdrStreetName(adrStreetName);
  }

  @Test
  public void setAdrStreetNumber_Args__String() throws Exception {
    String adrStreetNumber = null;
    target.setAdrStreetNumber(adrStreetNumber);
  }

  @Test
  public void setAdrZip_Args__String() throws Exception {
    String adrZip = null;
    target.setAdrZip(adrZip);
  }

  @Test
  public void setAdrZip4_Args__Short() throws Exception {
    Short adrZip4 = null;
    target.setAdrZip4(adrZip4);
  }

  @Test
  public void setAdrPostDirCd_Args__String() throws Exception {
    String adrPostDirCd = null;
    target.setAdrPostDirCd(adrPostDirCd);
  }

  @Test
  public void setAdrPreDirCd_Args__String() throws Exception {
    String adrPreDirCd = null;
    target.setAdrPreDirCd(adrPreDirCd);
  }

  @Test
  public void setAdrStreetSuffixCd_Args__Short() throws Exception {
    Short adrStreetSuffixCd = null;
    target.setAdrStreetSuffixCd(adrStreetSuffixCd);
  }

  @Test
  public void setAdrUnitDesignationCd_Args__Short() throws Exception {
    Short adrUnitDesignationCd = null;
    target.setAdrUnitDesignationCd(adrUnitDesignationCd);
  }

  @Test
  public void setAdrUnitNumber_Args__String() throws Exception {
    String adrUnitNumber = null;
    target.setAdrUnitNumber(adrUnitNumber);
  }

}
