package gov.ca.cwds.data.persistence.cms;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import gov.ca.cwds.data.persistence.cms.rep.CmsReplicationOperation;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedClient;

public class EsClientAddressTest {

  private static final String TEST_CLIENT_ID = "abc12340x8";

  private static EsClientAddress emptyTarget;

  @Mock
  private ResultSet rs;

  @BeforeClass
  public static void setupClass() throws Exception {
    emptyTarget = EsClientAddress.extract(Mockito.mock(ResultSet.class));
  }

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    when(rs.first()).thenReturn(true);

    final Short shortZero = Short.valueOf((short) 0);

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
    when(rs.getString("ADR_IDENTIFIER")).thenReturn(TEST_CLIENT_ID);
    when(rs.getString("ADR_POSTDIR_CD")).thenReturn("Y");
    when(rs.getString("ADR_PREDIR_CD")).thenReturn("Y");
    when(rs.getString("ADR_STREET_NM")).thenReturn("Y");
    when(rs.getString("ADR_STREET_NO")).thenReturn("1234");
    when(rs.getString("ADR_UNIT_NO")).thenReturn("ste 210");
    when(rs.getString("ADR_ZIP_NO")).thenReturn("Y");
    when(rs.getString("CLA_BK_INMT_ID")).thenReturn("Y");
    when(rs.getString("CLA_FKADDRS_T")).thenReturn(TEST_CLIENT_ID);
    when(rs.getString("CLA_FKCLIENT_T")).thenReturn(TEST_CLIENT_ID);
    when(rs.getString("CLA_FKREFERL_T")).thenReturn(TEST_CLIENT_ID);
    when(rs.getString("CLA_HOMLES_IND")).thenReturn("Y");
    when(rs.getString("CLA_IBMSNAP_OPERATION")).thenReturn("I");
    when(rs.getString("CLA_IDENTIFIER")).thenReturn(TEST_CLIENT_ID);
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
    when(rs.getString("CLT_IDENTIFIER")).thenReturn(TEST_CLIENT_ID);
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
  }

  private EsClientAddress buildEsClientAddress() {
    final EsClientAddress ret = new EsClientAddress();
    final Short sz = Short.valueOf((short) 0);
    ret.setAdrGovernmentEntityCd(sz);
    ret.setCltBirthCountryCodeType(sz);
    ret.setCltBirthStateCodeType(sz);
    ret.setCltDriverLicenseStateCodeType(sz);
    ret.setCltImmigrationCountryCodeType(sz);
    ret.setCltImmigrationStatusType(sz);
    ret.setCltMaritalStatusType(sz);
    ret.setCltNameType(sz);
    ret.setCltPrimaryEthnicityType(sz);
    ret.setCltPrimaryLanguageType(sz);
    ret.setCltReligionType(sz);
    ret.setCltSecondaryLanguageType(sz);
    ret.setClaAddressType(sz);
    ret.setAdrGovernmentEntityCd(sz);
    ret.setAdrState(sz);
    ret.setAdrZip4(sz);
    ret.setAdrStreetSuffixCd(sz);
    ret.setAdrUnitDesignationCd(sz);
    ret.setAdrEmergencyExtension(0);
    ret.setAdrMessageExtension(0);
    ret.setAdrPrimaryExtension(0);
    ret.setAdrEmergencyNumber(BigDecimal.ZERO);
    ret.setAdrMessageNumber(BigDecimal.ZERO);
    ret.setAdrPrimaryNumber(BigDecimal.ZERO);
    ret.setCltAdjudicatedDelinquentIndicator("Y");
    return ret;
  }

  @Test
  public void type() throws Exception {
    assertThat(EsClientAddress.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    EsClientAddress target = new EsClientAddress();
    assertThat(target, notNullValue());
  }

  @Test
  public void strToRepOp_Args__String() throws Exception {
    String op = null;
    CmsReplicationOperation actual = EsClientAddress.strToRepOp(op);
    CmsReplicationOperation expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void extract_Args__ResultSet() throws Exception {
    final EsClientAddress actual = EsClientAddress.extract(rs);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void extract_Args__ResultSet_read() throws Exception {
    final EsClientAddress actual = EsClientAddress.extract(rs);
    // final EsClientAddress expected = buildEsClientAddress();
    // System.out.println("actual: " + actual);
    // System.out.println("expected: " + expected);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getNormalizationClass_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    final Class<ReplicatedClient> actual = target.getNormalizationClass();
    final Class<ReplicatedClient> expected = ReplicatedClient.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__Map() throws Exception {
    final EsClientAddress target = new EsClientAddress();
    target.setCltId(TEST_CLIENT_ID);
    target.setClaId(TEST_CLIENT_ID);
    target.setAdrId(TEST_CLIENT_ID);

    final Map<Object, ReplicatedClient> map = new HashMap<Object, ReplicatedClient>();
    final ReplicatedClient actual = target.normalize(map);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getNormalizationGroupKey_Args__() throws Exception {
    final EsClientAddress target = new EsClientAddress();
    final Object actual = target.getNormalizationGroupKey();
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    final EsClientAddress target = new EsClientAddress();
    final Serializable actual = target.getPrimaryKey();
    final Serializable expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void hashCode_Args__() throws Exception {
    final EsClientAddress target = new EsClientAddress();
    final int actual = target.hashCode();
    final int expected = 337958661;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void equals_Args__Object() throws Exception {
    final EsClientAddress target = new EsClientAddress();
    Object obj = null;
    final boolean actual = target.equals(obj);
    final boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getCltAdjudicatedDelinquentIndicator_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltAdjudicatedDelinquentIndicator();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltAdjudicatedDelinquentIndicator_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltAdjudicatedDelinquentIndicator = null;
    target.setCltAdjudicatedDelinquentIndicator(cltAdjudicatedDelinquentIndicator);
  }

  @Test
  public void getCltAdoptionStatusCode_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltAdoptionStatusCode();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltAdoptionStatusCode_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltAdoptionStatusCode = null;
    target.setCltAdoptionStatusCode(cltAdoptionStatusCode);
  }

  @Test
  public void getCltAlienRegistrationNumber_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltAlienRegistrationNumber();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltAlienRegistrationNumber_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltAlienRegistrationNumber = null;
    target.setCltAlienRegistrationNumber(cltAlienRegistrationNumber);
  }

  @Test
  public void getCltBirthCity_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltBirthCity();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltBirthCity_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltBirthCity = null;
    target.setCltBirthCity(cltBirthCity);
  }

  @Test
  public void getCltBirthCountryCodeType_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short actual = target.getCltBirthCountryCodeType();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltBirthCountryCodeType_Args__Short() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short cltBirthCountryCodeType = null;
    target.setCltBirthCountryCodeType(cltBirthCountryCodeType);
  }

  @Test
  public void getCltBirthDate_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date actual = target.getCltBirthDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltBirthDate_Args__Date() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date cltBirthDate = mock(Date.class);
    target.setCltBirthDate(cltBirthDate);
  }

  @Test
  public void getCltBirthFacilityName_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltBirthFacilityName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltBirthFacilityName_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltBirthFacilityName = null;
    target.setCltBirthFacilityName(cltBirthFacilityName);
  }

  @Test
  public void getCltBirthStateCodeType_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short actual = target.getCltBirthStateCodeType();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltBirthStateCodeType_Args__Short() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short cltBirthStateCodeType = null;
    target.setCltBirthStateCodeType(cltBirthStateCodeType);
  }

  @Test
  public void getCltBirthplaceVerifiedIndicator_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltBirthplaceVerifiedIndicator();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltBirthplaceVerifiedIndicator_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltBirthplaceVerifiedIndicator = null;
    target.setCltBirthplaceVerifiedIndicator(cltBirthplaceVerifiedIndicator);
  }

  @Test
  public void getCltChildClientIndicatorVar_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltChildClientIndicatorVar();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltChildClientIndicatorVar_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltChildClientIndicatorVar = null;
    target.setCltChildClientIndicatorVar(cltChildClientIndicatorVar);
  }

  @Test
  public void getCltClientIndexNumber_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltClientIndexNumber();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltClientIndexNumber_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltClientIndexNumber = null;
    target.setCltClientIndexNumber(cltClientIndexNumber);
  }

  @Test
  public void getCltCommentDescription_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltCommentDescription();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltCommentDescription_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltCommentDescription = null;
    target.setCltCommentDescription(cltCommentDescription);
  }

  @Test
  public void getCltCommonFirstName_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltCommonFirstName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltCommonFirstName_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltCommonFirstName = null;
    target.setCltCommonFirstName(cltCommonFirstName);
  }

  @Test
  public void getCltCommonLastName_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltCommonLastName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltCommonLastName_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltCommonLastName = null;
    target.setCltCommonLastName(cltCommonLastName);
  }

  @Test
  public void getCltCommonMiddleName_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltCommonMiddleName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltCommonMiddleName_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltCommonMiddleName = null;
    target.setCltCommonMiddleName(cltCommonMiddleName);
  }

  @Test
  public void getCltConfidentialityActionDate_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date actual = target.getCltConfidentialityActionDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltConfidentialityActionDate_Args__Date() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date cltConfidentialityActionDate = mock(Date.class);
    target.setCltConfidentialityActionDate(cltConfidentialityActionDate);
  }

  @Test
  public void getCltConfidentialityInEffectIndicator_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltConfidentialityInEffectIndicator();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltConfidentialityInEffectIndicator_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltConfidentialityInEffectIndicator = null;
    target.setCltConfidentialityInEffectIndicator(cltConfidentialityInEffectIndicator);
  }

  @Test
  public void getCltCreationDate_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date actual = target.getCltCreationDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltCreationDate_Args__Date() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date cltCreationDate = mock(Date.class);
    target.setCltCreationDate(cltCreationDate);
  }

  @Test
  public void getCltCurrCaChildrenServIndicator_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltCurrCaChildrenServIndicator();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltCurrCaChildrenServIndicator_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltCurrCaChildrenServIndicator = null;
    target.setCltCurrCaChildrenServIndicator(cltCurrCaChildrenServIndicator);
  }

  @Test
  public void getCltCurrentlyOtherDescription_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltCurrentlyOtherDescription();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltCurrentlyOtherDescription_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltCurrentlyOtherDescription = null;
    target.setCltCurrentlyOtherDescription(cltCurrentlyOtherDescription);
  }

  @Test
  public void getCltCurrentlyRegionalCenterIndicator_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltCurrentlyRegionalCenterIndicator();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltCurrentlyRegionalCenterIndicator_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltCurrentlyRegionalCenterIndicator = null;
    target.setCltCurrentlyRegionalCenterIndicator(cltCurrentlyRegionalCenterIndicator);
  }

  @Test
  public void getCltDeathDate_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date actual = target.getCltDeathDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltDeathDate_Args__Date() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date cltDeathDate = mock(Date.class);
    target.setCltDeathDate(cltDeathDate);
  }

  @Test
  public void getCltDeathDateVerifiedIndicator_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltDeathDateVerifiedIndicator();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltDeathDateVerifiedIndicator_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltDeathDateVerifiedIndicator = null;
    target.setCltDeathDateVerifiedIndicator(cltDeathDateVerifiedIndicator);
  }

  @Test
  public void getCltDeathPlace_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltDeathPlace();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltDeathPlace_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltDeathPlace = null;
    target.setCltDeathPlace(cltDeathPlace);
  }

  @Test
  public void getCltDeathReasonText_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltDeathReasonText();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltDeathReasonText_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltDeathReasonText = null;
    target.setCltDeathReasonText(cltDeathReasonText);
  }

  @Test
  public void getCltDriverLicenseNumber_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltDriverLicenseNumber();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltDriverLicenseNumber_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltDriverLicenseNumber = null;
    target.setCltDriverLicenseNumber(cltDriverLicenseNumber);
  }

  @Test
  public void getCltDriverLicenseStateCodeType_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short actual = target.getCltDriverLicenseStateCodeType();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltDriverLicenseStateCodeType_Args__Short() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short cltDriverLicenseStateCodeType = null;
    target.setCltDriverLicenseStateCodeType(cltDriverLicenseStateCodeType);
  }

  @Test
  public void getCltEmailAddress_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltEmailAddress();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltEmailAddress_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltEmailAddress = null;
    target.setCltEmailAddress(cltEmailAddress);
  }

  @Test
  public void getCltEstimatedDobCode_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltEstimatedDobCode();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltEstimatedDobCode_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltEstimatedDobCode = null;
    target.setCltEstimatedDobCode(cltEstimatedDobCode);
  }

  @Test
  public void getCltEthUnableToDetReasonCode_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltEthUnableToDetReasonCode();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltEthUnableToDetReasonCode_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltEthUnableToDetReasonCode = null;
    target.setCltEthUnableToDetReasonCode(cltEthUnableToDetReasonCode);
  }

  @Test
  public void getCltFatherParentalRightTermDate_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date actual = target.getCltFatherParentalRightTermDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltFatherParentalRightTermDate_Args__Date() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date cltFatherParentalRightTermDate = mock(Date.class);
    target.setCltFatherParentalRightTermDate(cltFatherParentalRightTermDate);
  }

  @Test
  public void getCltGenderCode_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltGenderCode();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltGenderCode_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltGenderCode = null;
    target.setCltGenderCode(cltGenderCode);
  }

  @Test
  public void getCltHealthSummaryText_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltHealthSummaryText();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltHealthSummaryText_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltHealthSummaryText = null;
    target.setCltHealthSummaryText(cltHealthSummaryText);
  }

  @Test
  public void getCltHispUnableToDetReasonCode_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltHispUnableToDetReasonCode();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltHispUnableToDetReasonCode_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltHispUnableToDetReasonCode = null;
    target.setCltHispUnableToDetReasonCode(cltHispUnableToDetReasonCode);
  }

  @Test
  public void getCltHispanicOriginCode_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltHispanicOriginCode();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltHispanicOriginCode_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltHispanicOriginCode = null;
    target.setCltHispanicOriginCode(cltHispanicOriginCode);
  }

  @Test
  public void getCltId_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltId_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltId = null;
    target.setCltId(cltId);
  }

  @Test
  public void getCltImmigrationCountryCodeType_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short actual = target.getCltImmigrationCountryCodeType();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltImmigrationCountryCodeType_Args__Short() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short cltImmigrationCountryCodeType = null;
    target.setCltImmigrationCountryCodeType(cltImmigrationCountryCodeType);
  }

  @Test
  public void getCltImmigrationStatusType_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short actual = target.getCltImmigrationStatusType();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltImmigrationStatusType_Args__Short() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short cltImmigrationStatusType = null;
    target.setCltImmigrationStatusType(cltImmigrationStatusType);
  }

  @Test
  public void getCltIncapacitatedParentCode_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltIncapacitatedParentCode();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltIncapacitatedParentCode_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltIncapacitatedParentCode = null;
    target.setCltIncapacitatedParentCode(cltIncapacitatedParentCode);
  }

  @Test
  public void getCltIndividualHealthCarePlanIndicator_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltIndividualHealthCarePlanIndicator();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltIndividualHealthCarePlanIndicator_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltIndividualHealthCarePlanIndicator = null;
    target.setCltIndividualHealthCarePlanIndicator(cltIndividualHealthCarePlanIndicator);
  }

  @Test
  public void getCltLimitationOnScpHealthIndicator_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltLimitationOnScpHealthIndicator();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltLimitationOnScpHealthIndicator_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltLimitationOnScpHealthIndicator = null;
    target.setCltLimitationOnScpHealthIndicator(cltLimitationOnScpHealthIndicator);
  }

  @Test
  public void getCltLiterateCode_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltLiterateCode();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltLiterateCode_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltLiterateCode = null;
    target.setCltLiterateCode(cltLiterateCode);
  }

  @Test
  public void getCltMaritalCohabitatnHstryIndicatorVar_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltMaritalCohabitatnHstryIndicatorVar();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltMaritalCohabitatnHstryIndicatorVar_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltMaritalCohabitatnHstryIndicatorVar = null;
    target.setCltMaritalCohabitatnHstryIndicatorVar(cltMaritalCohabitatnHstryIndicatorVar);
  }

  @Test
  public void getCltMaritalStatusType_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short actual = target.getCltMaritalStatusType();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltMaritalStatusType_Args__Short() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short cltMaritalStatusType = null;
    target.setCltMaritalStatusType(cltMaritalStatusType);
  }

  @Test
  public void getCltMilitaryStatusCode_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltMilitaryStatusCode();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltMilitaryStatusCode_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltMilitaryStatusCode = null;
    target.setCltMilitaryStatusCode(cltMilitaryStatusCode);
  }

  @Test
  public void getCltMotherParentalRightTermDate_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date actual = target.getCltMotherParentalRightTermDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltMotherParentalRightTermDate_Args__Date() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date cltMotherParentalRightTermDate = mock(Date.class);
    target.setCltMotherParentalRightTermDate(cltMotherParentalRightTermDate);
  }

  @Test
  public void getCltNamePrefixDescription_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltNamePrefixDescription();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltNamePrefixDescription_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltNamePrefixDescription = null;
    target.setCltNamePrefixDescription(cltNamePrefixDescription);
  }

  @Test
  public void getCltNameType_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short actual = target.getCltNameType();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltNameType_Args__Short() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short cltNameType = null;
    target.setCltNameType(cltNameType);
  }

  @Test
  public void getCltOutstandingWarrantIndicator_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltOutstandingWarrantIndicator();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltOutstandingWarrantIndicator_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltOutstandingWarrantIndicator = null;
    target.setCltOutstandingWarrantIndicator(cltOutstandingWarrantIndicator);
  }

  @Test
  public void getCltPrevCaChildrenServIndicator_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltPrevCaChildrenServIndicator();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltPrevCaChildrenServIndicator_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltPrevCaChildrenServIndicator = null;
    target.setCltPrevCaChildrenServIndicator(cltPrevCaChildrenServIndicator);
  }

  @Test
  public void getCltPrevOtherDescription_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltPrevOtherDescription();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltPrevOtherDescription_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltPrevOtherDescription = null;
    target.setCltPrevOtherDescription(cltPrevOtherDescription);
  }

  @Test
  public void getCltPrevRegionalCenterIndicator_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltPrevRegionalCenterIndicator();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltPrevRegionalCenterIndicator_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltPrevRegionalCenterIndicator = null;
    target.setCltPrevRegionalCenterIndicator(cltPrevRegionalCenterIndicator);
  }

  @Test
  public void getCltPrimaryEthnicityType_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short actual = target.getCltPrimaryEthnicityType();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltPrimaryEthnicityType_Args__Short() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short cltPrimaryEthnicityType = null;
    target.setCltPrimaryEthnicityType(cltPrimaryEthnicityType);
  }

  @Test
  public void getCltPrimaryLanguageType_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short actual = target.getCltPrimaryLanguageType();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltPrimaryLanguageType_Args__Short() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short cltPrimaryLanguageType = null;
    target.setCltPrimaryLanguageType(cltPrimaryLanguageType);
  }

  @Test
  public void getCltReligionType_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short actual = target.getCltReligionType();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltReligionType_Args__Short() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short cltReligionType = null;
    target.setCltReligionType(cltReligionType);
  }

  @Test
  public void getCltSecondaryLanguageType_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short actual = target.getCltSecondaryLanguageType();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltSecondaryLanguageType_Args__Short() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short cltSecondaryLanguageType = null;
    target.setCltSecondaryLanguageType(cltSecondaryLanguageType);
  }

  @Test
  public void getCltSensitiveHlthInfoOnFileIndicator_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltSensitiveHlthInfoOnFileIndicator();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltSensitiveHlthInfoOnFileIndicator_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltSensitiveHlthInfoOnFileIndicator = null;
    target.setCltSensitiveHlthInfoOnFileIndicator(cltSensitiveHlthInfoOnFileIndicator);
  }

  @Test
  public void getCltSensitivityIndicator_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltSensitivityIndicator();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltSensitivityIndicator_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltSensitivityIndicator = null;
    target.setCltSensitivityIndicator(cltSensitivityIndicator);
  }

  @Test
  public void getCltSoc158PlacementCode_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltSoc158PlacementCode();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltSoc158PlacementCode_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltSoc158PlacementCode = null;
    target.setCltSoc158PlacementCode(cltSoc158PlacementCode);
  }

  @Test
  public void getCltSoc158SealedClientIndicator_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltSoc158SealedClientIndicator();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltSoc158SealedClientIndicator_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltSoc158SealedClientIndicator = null;
    target.setCltSoc158SealedClientIndicator(cltSoc158SealedClientIndicator);
  }

  @Test
  public void getCltSocialSecurityNumChangedCode_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltSocialSecurityNumChangedCode();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltSocialSecurityNumChangedCode_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltSocialSecurityNumChangedCode = null;
    target.setCltSocialSecurityNumChangedCode(cltSocialSecurityNumChangedCode);
  }

  @Test
  public void getCltSocialSecurityNumber_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltSocialSecurityNumber();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltSocialSecurityNumber_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltSocialSecurityNumber = null;
    target.setCltSocialSecurityNumber(cltSocialSecurityNumber);
  }

  @Test
  public void getCltSuffixTitleDescription_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltSuffixTitleDescription();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltSuffixTitleDescription_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltSuffixTitleDescription = null;
    target.setCltSuffixTitleDescription(cltSuffixTitleDescription);
  }

  @Test
  public void getCltTribalAncestryClientIndicatorVar_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltTribalAncestryClientIndicatorVar();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltTribalAncestryClientIndicatorVar_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltTribalAncestryClientIndicatorVar = null;
    target.setCltTribalAncestryClientIndicatorVar(cltTribalAncestryClientIndicatorVar);
  }

  @Test
  public void getCltTribalMembrshpVerifctnIndicatorVar_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltTribalMembrshpVerifctnIndicatorVar();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltTribalMembrshpVerifctnIndicatorVar_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltTribalMembrshpVerifctnIndicatorVar = null;
    target.setCltTribalMembrshpVerifctnIndicatorVar(cltTribalMembrshpVerifctnIndicatorVar);
  }

  @Test
  public void getCltUnemployedParentCode_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltUnemployedParentCode();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltUnemployedParentCode_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltUnemployedParentCode = null;
    target.setCltUnemployedParentCode(cltUnemployedParentCode);
  }

  @Test
  public void getCltZippyCreatedIndicator_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltZippyCreatedIndicator();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltZippyCreatedIndicator_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltZippyCreatedIndicator = null;
    target.setCltZippyCreatedIndicator(cltZippyCreatedIndicator);
  }

  @Test
  public void getCltReplicationOperation_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    CmsReplicationOperation actual = target.getCltReplicationOperation();
    CmsReplicationOperation expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltReplicationOperation_Args__CmsReplicationOperation() throws Exception {
    EsClientAddress target = new EsClientAddress();
    CmsReplicationOperation cltReplicationOperation = CmsReplicationOperation.U;
    target.setCltReplicationOperation(cltReplicationOperation);
  }

  @Test
  public void getCltReplicationDate_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date actual = target.getCltReplicationDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltReplicationDate_Args__Date() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date cltReplicationDate = mock(Date.class);
    target.setCltReplicationDate(cltReplicationDate);
  }

  @Test
  public void getCltLastUpdatedId_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getCltLastUpdatedId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltLastUpdatedId_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String cltLastUpdatedId = null;
    target.setCltLastUpdatedId(cltLastUpdatedId);
  }

  @Test
  public void getCltLastUpdatedTime_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date actual = target.getCltLastUpdatedTime();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCltLastUpdatedTime_Args__Date() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date cltLastUpdatedTime = mock(Date.class);
    target.setCltLastUpdatedTime(cltLastUpdatedTime);
  }

  @Test
  public void getClaReplicationOperation_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    CmsReplicationOperation actual = target.getClaReplicationOperation();
    CmsReplicationOperation expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaReplicationOperation_Args__CmsReplicationOperation() throws Exception {
    EsClientAddress target = new EsClientAddress();
    CmsReplicationOperation claReplicationOperation = CmsReplicationOperation.U;
    target.setClaReplicationOperation(claReplicationOperation);
  }

  @Test
  public void getClaReplicationDate_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date actual = target.getClaReplicationDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaReplicationDate_Args__Date() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date claReplicationDate = mock(Date.class);
    target.setClaReplicationDate(claReplicationDate);
  }

  @Test
  public void getClaLastUpdatedId_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getClaLastUpdatedId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaLastUpdatedId_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String claLastUpdatedId = null;
    target.setClaLastUpdatedId(claLastUpdatedId);
  }

  @Test
  public void getClaLastUpdatedTime_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date actual = target.getClaLastUpdatedTime();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaLastUpdatedTime_Args__Date() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date claLastUpdatedTime = mock(Date.class);
    target.setClaLastUpdatedTime(claLastUpdatedTime);
  }

  @Test
  public void getClaFkAddress_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getClaFkAddress();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaFkAddress_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String claFkAddress = null;
    target.setClaFkAddress(claFkAddress);
  }

  @Test
  public void getClaFkClient_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getClaFkClient();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaFkClient_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String claFkClient = null;
    target.setClaFkClient(claFkClient);
  }

  @Test
  public void getClaFkReferral_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getClaFkReferral();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaFkReferral_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String claFkReferral = null;
    target.setClaFkReferral(claFkReferral);
  }

  @Test
  public void getClaAddressType_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short actual = target.getClaAddressType();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaAddressType_Args__Short() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short claAddressType = null;
    target.setClaAddressType(claAddressType);
  }

  @Test
  public void getClaHomelessInd_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getClaHomelessInd();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaHomelessInd_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String claHomelessInd = null;
    target.setClaHomelessInd(claHomelessInd);
  }

  @Test
  public void getClaBkInmtId_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getClaBkInmtId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaBkInmtId_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String claBkInmtId = null;
    target.setClaBkInmtId(claBkInmtId);
  }

  @Test
  public void getClaEffectiveEndDate_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date actual = target.getClaEffectiveEndDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaEffectiveEndDate_Args__Date() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date claEffectiveEndDate = mock(Date.class);
    target.setClaEffectiveEndDate(claEffectiveEndDate);
  }

  @Test
  public void getClaEffectiveStartDate_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date actual = target.getClaEffectiveStartDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaEffectiveStartDate_Args__Date() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date claEffectiveStartDate = mock(Date.class);
    target.setClaEffectiveStartDate(claEffectiveStartDate);
  }

  @Test
  public void getAdrId_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getAdrId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrId_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String adrId = null;
    target.setAdrId(adrId);
  }

  @Test
  public void getAdrCity_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getAdrCity();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrCity_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String adrCity = null;
    target.setAdrCity(adrCity);
  }

  @Test
  public void getAdrEmergencyNumber_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    BigDecimal actual = target.getAdrEmergencyNumber();
    BigDecimal expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrEmergencyNumber_Args__BigDecimal() throws Exception {
    EsClientAddress target = new EsClientAddress();
    BigDecimal adrEmergencyNumber = mock(BigDecimal.class);
    target.setAdrEmergencyNumber(adrEmergencyNumber);
  }

  @Test
  public void getAdrEmergencyExtension_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Integer actual = target.getAdrEmergencyExtension();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrEmergencyExtension_Args__Integer() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Integer adrEmergencyExtension = null;
    target.setAdrEmergencyExtension(adrEmergencyExtension);
  }

  @Test
  public void getAdrFrgAdrtB_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getAdrFrgAdrtB();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrFrgAdrtB_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String adrFrgAdrtB = null;
    target.setAdrFrgAdrtB(adrFrgAdrtB);
  }

  @Test
  public void getAdrGovernmentEntityCd_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short actual = target.getAdrGovernmentEntityCd();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrGovernmentEntityCd_Args__Short() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short adrGovernmentEntityCd = null;
    target.setAdrGovernmentEntityCd(adrGovernmentEntityCd);
  }

  @Test
  public void getAdrMessageNumber_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    BigDecimal actual = target.getAdrMessageNumber();
    BigDecimal expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrMessageNumber_Args__BigDecimal() throws Exception {
    EsClientAddress target = new EsClientAddress();
    BigDecimal adrMessageNumber = mock(BigDecimal.class);
    target.setAdrMessageNumber(adrMessageNumber);
  }

  @Test
  public void getAdrMessageExtension_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Integer actual = target.getAdrMessageExtension();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrMessageExtension_Args__Integer() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Integer adrMessageExtension = null;
    target.setAdrMessageExtension(adrMessageExtension);
  }

  @Test
  public void getAdrHeaderAddress_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getAdrHeaderAddress();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrHeaderAddress_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String adrHeaderAddress = null;
    target.setAdrHeaderAddress(adrHeaderAddress);
  }

  @Test
  public void getAdrPrimaryNumber_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    BigDecimal actual = target.getAdrPrimaryNumber();
    BigDecimal expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrPrimaryNumber_Args__BigDecimal() throws Exception {
    EsClientAddress target = new EsClientAddress();
    BigDecimal adrPrimaryNumber = mock(BigDecimal.class);
    target.setAdrPrimaryNumber(adrPrimaryNumber);
  }

  @Test
  public void getAdrPrimaryExtension_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Integer actual = target.getAdrPrimaryExtension();
    Integer expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrPrimaryExtension_Args__Integer() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Integer adrPrimaryExtension = null;
    target.setAdrPrimaryExtension(adrPrimaryExtension);
  }

  @Test
  public void getAdrState_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short actual = target.getAdrState();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrState_Args__Short() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short adrState = null;
    target.setAdrState(adrState);
  }

  @Test
  public void getAdrStreetName_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getAdrStreetName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrStreetName_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String adrStreetName = null;
    target.setAdrStreetName(adrStreetName);
  }

  @Test
  public void getAdrStreetNumber_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getAdrStreetNumber();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrStreetNumber_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String adrStreetNumber = null;
    target.setAdrStreetNumber(adrStreetNumber);
  }

  @Test
  public void getAdrZip_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getAdrZip();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrZip_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String adrZip = null;
    target.setAdrZip(adrZip);
  }

  @Test
  public void getAdrAddressDescription_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getAdrAddressDescription();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrAddressDescription_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String adrAddressDescription = null;
    target.setAdrAddressDescription(adrAddressDescription);
  }

  @Test
  public void getAdrZip4_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short actual = target.getAdrZip4();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrZip4_Args__Short() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short adrZip4 = null;
    target.setAdrZip4(adrZip4);
  }

  @Test
  public void getAdrPostDirCd_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getAdrPostDirCd();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrPostDirCd_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String adrPostDirCd = null;
    target.setAdrPostDirCd(adrPostDirCd);
  }

  @Test
  public void getAdrPreDirCd_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getAdrPreDirCd();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrPreDirCd_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String adrPreDirCd = null;
    target.setAdrPreDirCd(adrPreDirCd);
  }

  @Test
  public void getAdrStreetSuffixCd_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short actual = target.getAdrStreetSuffixCd();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrStreetSuffixCd_Args__Short() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short adrStreetSuffixCd = null;
    target.setAdrStreetSuffixCd(adrStreetSuffixCd);
  }

  @Test
  public void getAdrUnitDesignationCd_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short actual = target.getAdrUnitDesignationCd();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrUnitDesignationCd_Args__Short() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Short adrUnitDesignationCd = null;
    target.setAdrUnitDesignationCd(adrUnitDesignationCd);
  }

  @Test
  public void getAdrUnitNumber_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getAdrUnitNumber();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrUnitNumber_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String adrUnitNumber = null;
    target.setAdrUnitNumber(adrUnitNumber);
  }

  @Test
  public void getClaId_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String actual = target.getClaId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setClaId_Args__String() throws Exception {
    EsClientAddress target = new EsClientAddress();
    String claId = null;
    target.setClaId(claId);
  }

  @Test
  public void getAdrReplicationOperation_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    CmsReplicationOperation actual = target.getAdrReplicationOperation();
    CmsReplicationOperation expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrReplicationOperation_Args__CmsReplicationOperation() throws Exception {
    EsClientAddress target = new EsClientAddress();
    CmsReplicationOperation adrReplicationOperation = CmsReplicationOperation.U;
    target.setAdrReplicationOperation(adrReplicationOperation);
  }

  @Test
  public void getAdrReplicationDate_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date actual = target.getAdrReplicationDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAdrReplicationDate_Args__Date() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date adrReplicationDate = mock(Date.class);
    target.setAdrReplicationDate(adrReplicationDate);
  }

  @Test
  public void getLastChange_Args__() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date actual = target.getLastChange();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLastChange_Args__Date() throws Exception {
    EsClientAddress target = new EsClientAddress();
    Date lastChange = mock(Date.class);
    target.setLastChange(lastChange);
  }

}
