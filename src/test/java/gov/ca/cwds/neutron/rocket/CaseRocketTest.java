package gov.ca.cwds.neutron.rocket;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import gov.ca.cwds.dao.cms.ReplicatedClientDao;
import gov.ca.cwds.dao.cms.ReplicatedPersonCasesDao;
import gov.ca.cwds.dao.cms.StaffPersonDao;
import gov.ca.cwds.data.persistence.cms.EsCaseRelatedPerson;
import gov.ca.cwds.data.persistence.cms.EsPersonCase;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonCases;
import gov.ca.cwds.jobs.Goddard;
import gov.ca.cwds.jobs.exception.NeutronException;

public class CaseRocketTest extends Goddard<ReplicatedPersonCases, EsCaseRelatedPerson> {

  CaseRocket target;
  ReplicatedPersonCasesDao dao;
  ReplicatedClientDao clientDao;
  StaffPersonDao staffPersonDao;

  @Override
  public void setup() throws Exception {
    super.setup();
    dao = new ReplicatedPersonCasesDao(sessionFactory);
    clientDao = new ReplicatedClientDao(sessionFactory);
    staffPersonDao = new StaffPersonDao(sessionFactory);
    target = new CaseRocket(dao, esDao, clientDao, staffPersonDao, lastRunFile, mapper, flightPlan);
  }

  @Test
  public void type() throws Exception {
    assertThat(CaseRocket.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void useTransformThread_Args__() throws Exception {
    boolean actual = target.useTransformThread();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPrepLastChangeSQL_Args__() throws Exception {
    final String actual = target.getPrepLastChangeSQL();
    final String expected =
        "INSERT INTO GT_ID (IDENTIFIER)WITH step1 AS ( \n SELECT CAS1.FKCHLD_CLT \n FROM  CASE_T CAS1  \n WHERE CAS1.IBMSNAP_LOGMARKER > ? \nUNION   \n SELECT CAS2.FKCHLD_CLT  \n FROM CASE_T CAS2 \n JOIN CHLD_CLT CCL1 ON CCL1.FKCLIENT_T = CAS2.FKCHLD_CLT   \n JOIN CLIENT_T CLC1 ON CLC1.IDENTIFIER = CCL1.FKCLIENT_T  \n WHERE CCL1.IBMSNAP_LOGMARKER > ?  \nUNION     \n SELECT CAS3.FKCHLD_CLT  \n FROM CASE_T CAS3  \n JOIN CLIENT_T CLC2 ON CLC2.IDENTIFIER = CAS3.FKCHLD_CLT \n WHERE CLC2.IBMSNAP_LOGMARKER > ? \nUNION  \n SELECT CAS4.FKCHLD_CLT  \n FROM CASE_T CAS4  \n JOIN CLN_RELT CLR  ON CLR.FKCLIENT_T = CAS4.FKCHLD_CLT \n WHERE CLR.IBMSNAP_LOGMARKER > ? \nUNION  \n SELECT CAS5.FKCHLD_CLT  \n FROM CASE_T CAS5 \n JOIN CLN_RELT CLR ON CLR.FKCLIENT_T = CAS5.FKCHLD_CLT \n JOIN CLIENT_T CLP ON CLP.IDENTIFIER = CLR.FKCLIENT_0  \n WHERE CLP.IBMSNAP_LOGMARKER > ? \n), step2 AS ( \n SELECT DISTINCT s1.FKCHLD_CLT FROM step1 s1 \n) \nSELECT c.FKCHLD_CLT  \nFROM step2 c \nWHERE   \n   EXISTS (  \n    SELECT CAS1.FKCHLD_CLT  \n    FROM CASE_T CAS1   \n    WHERE CAS1.FKCHLD_CLT = c.FKCHLD_CLT \n) \nOR EXISTS ( \n     SELECT REL2.FKCLIENT_T  \n     FROM CLN_RELT REL2  \n     JOIN CASE_T   CAS2 ON CAS2.FKCHLD_CLT = REL2.FKCLIENT_0  \n     WHERE REL2.FKCLIENT_T = c.FKCHLD_CLT  \n)  \nOR EXISTS (  \n     SELECT REL3.FKCLIENT_0  \n     FROM CLN_RELT REL3  \n     JOIN CASE_T   CAS3 ON CAS3.FKCHLD_CLT = REL3.FKCLIENT_T  \n     WHERE REL3.FKCLIENT_0 = c.FKCHLD_CLT  \n) \n";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getInitialLoadViewName_Args__() throws Exception {
    String actual = target.getInitialLoadViewName();
    String expected = "VW_MQT_REFRL_ONLY";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isInitialLoadJdbc_Args__() throws Exception {
    boolean actual = target.isInitialLoadJdbc();
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPartitionRanges_Args__() throws Exception {
    final List<Pair<String, String>> actual = target.getPartitionRanges();
    final List<Pair<String, String>> expected = new ArrayList<>();
    expected.add(pair);
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getOptionalElementName_Args__() throws Exception {
    String actual = target.getOptionalElementName();
    String expected = "cases";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void mustDeleteLimitedAccessRecords_Args__() throws Exception {
    boolean actual = target.mustDeleteLimitedAccessRecords();
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getJdbcOrderBy_Args__() throws Exception {
    String actual = target.getJdbcOrderBy();
    String expected = "";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getInitialLoadQuery_Args__String() throws Exception {
    String dbSchemaName = "CWSRS1";
    String actual = target.getInitialLoadQuery(dbSchemaName);
    String expected =
        "WITH DRIVER AS (\n SELECT     \n       c.IDENTIFIER        AS THIS_CLIENT_ID \n     , trim(c.COM_FST_NM)  AS THIS_CLIENT_FIRST_NM \n     , trim(c.COM_LST_NM)  AS THIS_CLIENT_LAST_NM \n     , c.SENSTV_IND        AS THIS_CLIENT_SENSITIVITY_IND \n     , c.LST_UPD_TS        AS THIS_CLIENT_LAST_UPDATED \n     , c.IBMSNAP_LOGMARKER AS THIS_CLIENT_LOGMARKER \n     , c.IBMSNAP_OPERATION AS THIS_CLIENT_OPERATION \n FROM GT_ID GT \n JOIN CLIENT_T C ON C.IDENTIFIER = GT.IDENTIFIER \n) \n SELECT   \n CAS1.IDENTIFIER      AS CASE_ID, \n CAS1.FKCHLD_CLT      AS FOCUS_CHILD_ID, \n DRV1.THIS_CLIENT_ID  AS THIS_CLIENT_ID, \n 1                    AS STANZA, \n 0                    AS REL_FOCUS_TO_OTHER, \n 0                    AS REL_OTHER_TO_FOCUS, \n CAS1.CASE_NM         AS CASE_NAME, \n CAS1.START_DT        AS START_DATE, \n CAS1.END_DT          AS END_DATE, \n CAS1.SRV_CMPC        AS SERVICE_COMP, \n CAS1.CLS_RSNC        AS CLOSE_REASON_CODE, \n CAS1.FKSTFPERST      AS WORKER_ID, \n CAS1.LMT_ACSSCD      AS LIMITED_ACCESS_CODE, \n CAS1.LMT_ACS_DT      AS LIMITED_ACCESS_DATE, \n CAS1.LMT_ACSDSC      AS LIMITED_ACCESS_DESCRIPTION, \n CAS1.L_GVR_ENTC      AS LIMITED_ACCESS_GOVERNMENT_ENT, \n CAS1.LST_UPD_TS      AS CASE_LAST_UPDATED, \n CAS1.GVR_ENTC        AS COUNTY, \n CAS1.APV_STC \nFROM DRIVER DRV1 \nJOIN CASE_T CAS1 ON CAS1.FKCHLD_CLT = DRV1.THIS_CLIENT_ID \nWHERE CAS1.IBMSNAP_OPERATION IN ('I','U') \nUNION ALL \nSELECT     \n CAS2.IDENTIFIER      AS CASE_ID, \n CAS2.FKCHLD_CLT      AS FOCUS_CHILD_ID, \n DRV2.THIS_CLIENT_ID  AS THIS_CLIENT_ID, \n 2                    AS STANZA, \n REL2.CLNTRELC        AS REL_FOCUS_TO_OTHER, \n 0                    AS REL_OTHER_TO_FOCUS, \n CAS2.CASE_NM         AS CASE_NAME, \n CAS2.START_DT        AS START_DATE, \n CAS2.END_DT          AS END_DATE, \n CAS2.SRV_CMPC        AS SERVICE_COMP, \n CAS2.CLS_RSNC        AS CLOSE_REASON_CODE, \n CAS2.FKSTFPERST      AS WORKER_ID, \n CAS2.LMT_ACSSCD      AS LIMITED_ACCESS_CODE, \n CAS2.LMT_ACS_DT      AS LIMITED_ACCESS_DATE, \n CAS2.LMT_ACSDSC      AS LIMITED_ACCESS_DESCRIPTION, \n CAS2.L_GVR_ENTC      AS LIMITED_ACCESS_GOVERNMENT_ENT, \n CAS2.LST_UPD_TS      AS CASE_LAST_UPDATED, \n CAS2.GVR_ENTC        AS COUNTY, \n CAS2.APV_STC \nFROM DRIVER DRV2 \nJOIN CLN_RELT REL2 ON REL2.FKCLIENT_T = DRV2.THIS_CLIENT_ID \nJOIN CASE_T   CAS2 ON CAS2.FKCHLD_CLT = REL2.FKCLIENT_0 \nWHERE CAS2.IBMSNAP_OPERATION IN ('I','U') \n  AND REL2.IBMSNAP_OPERATION IN ('I','U') \nUNION ALL \nSELECT  \n CAS3.IDENTIFIER      AS CASE_ID, \n CAS3.FKCHLD_CLT      AS FOCUS_CHILD_ID, \n DRV3.THIS_CLIENT_ID  AS THIS_CLIENT_ID, \n 3                    AS STANZA, \n 0                    AS REL_FOCUS_TO_OTHER, \n REL3.CLNTRELC        AS REL_OTHER_TO_FOCUS, \n CAS3.CASE_NM         AS CASE_NAME, \n CAS3.START_DT        AS START_DATE, \n CAS3.END_DT          AS END_DATE, \n CAS3.SRV_CMPC        AS SERVICE_COMP, \n CAS3.CLS_RSNC        AS CLOSE_REASON_CODE, \n CAS3.FKSTFPERST      AS WORKER_ID, \n CAS3.LMT_ACSSCD      AS LIMITED_ACCESS_CODE, \n CAS3.LMT_ACS_DT      AS LIMITED_ACCESS_DATE, \n CAS3.LMT_ACSDSC      AS LIMITED_ACCESS_DESCRIPTION, \n CAS3.L_GVR_ENTC      AS LIMITED_ACCESS_GOVERNMENT_ENT, \n CAS3.LST_UPD_TS      AS CASE_LAST_UPDATED, \n CAS3.GVR_ENTC        AS COUNTY, \n CAS3.APV_STC \nFROM DRIVER DRV3, CLN_RELT REL3, CASE_T CAS3 \nWHERE CAS3.FKCHLD_CLT = REL3.FKCLIENT_T AND REL3.FKCLIENT_0 = DRV3.THIS_CLIENT_ID \n  AND CAS3.IBMSNAP_OPERATION IN ('I','U') \n  AND REL3.IBMSNAP_OPERATION IN ('I','U') \n FOR READ ONLY WITH UR  WHERE CAS.LMT_ACSSCD = 'N'";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getDenormalizedClass_Args__() throws Exception {
    Object actual = target.getDenormalizedClass();
    Object expected = EsPersonCase.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__List() throws Exception {
    List<EsCaseRelatedPerson> recs = new ArrayList<EsCaseRelatedPerson>();
    List<ReplicatedPersonCases> actual = target.normalize(recs);
    List<ReplicatedPersonCases> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  // public void readStaffWorkers_Args__() throws Exception {
  // Map<String, StaffPerson> actual = target.readStaffWorkers();
  // Map<String, StaffPerson> expected = null;
  // assertThat(actual, is(equalTo(expected)));
  // }

  @Test(expected = NeutronException.class)
  public void readStaffWorkers_Args___T__NeutronException() throws Exception {
    target.readStaffWorkers();
  }

  @Test
  public void extract_Args__ResultSet() throws Exception {
    EsCaseRelatedPerson actual = target.extract(rs);
    EsCaseRelatedPerson expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  // public void pullNextRange_Args__Pair() throws Exception {
  // final PreparedStatement stmtInsClient = mock(PreparedStatement.class);
  // final PreparedStatement stmtSelClient = mock(PreparedStatement.class);
  // final PreparedStatement stmtSelReferral = mock(PreparedStatement.class);
  // final PreparedStatement stmtSelAllegation = mock(PreparedStatement.class);
  // final ResultSet rsInsClient = mock(ResultSet.class);
  // final ResultSet rsSelClient = mock(ResultSet.class);
  // final ResultSet rsSelReferral = mock(ResultSet.class);
  // final ResultSet rsSelAllegation = mock(ResultSet.class);
  //
  // final String sqlAffectedClients = CaseSQLResource.PREP_AFFECTED_CLIENTS_FULL;
  // final String sqlClient = CaseSQLResource.SELECT_CLIENT_FULL;
  // final String sqlCase = CaseSQLResource.SELECT_CASE_DETAIL;
  // final String sqlRelation = CaseSQLResource.SELECT_CLIENT_CASE_RELATIONSHIP;
  //
  // when(con.prepareStatement(sqlAffectedClients)).thenReturn(stmtInsClient);
  // when(con.prepareStatement(sqlClient)).thenReturn(stmtSelClient);
  // when(con.prepareStatement(sqlCase)).thenReturn(stmtSelReferral);
  // when(con.prepareStatement(sqlRelation)).thenReturn(stmtSelAllegation);
  //
  // when(stmtInsClient.executeQuery()).thenReturn(rsInsClient);
  // when(stmtSelClient.executeQuery()).thenReturn(rsSelClient);
  // when(stmtSelReferral.executeQuery()).thenReturn(rsSelReferral);
  // when(stmtSelAllegation.executeQuery()).thenReturn(rsSelAllegation);
  // when(rsInsClient.next()).thenReturn(true).thenReturn(false);
  // when(rsSelClient.next()).thenReturn(true).thenReturn(false);
  // when(rsSelReferral.next()).thenReturn(false);
  // when(rsSelAllegation.next()).thenReturn(false);
  //
  // when(rsSelClient.getString("FKCLIENT_T")).thenReturn(DEFAULT_CLIENT_ID);
  // when(rsSelClient.getString("SENSTV_IND")).thenReturn("N");
  //
  // int actual = target.pullNextRange(pair);
  // int expected = 0;
  // assertThat(actual, is(equalTo(expected)));
  // }

  @Test(expected = NeutronException.class)
  public void pullNextRange_Args__Pair_T__NeutronException() throws Exception {
    final Pair<String, String> p = pair;
    target.pullNextRange(p);
  }

  // @Test
  // public void threadRetrieveByJdbc_Args__() throws Exception {
  // target.threadRetrieveByJdbc();
  // }

  @Test
  public void allocateThreadMemory_Args__() throws Exception {
    target.allocateThreadMemory();
  }

  @Test
  public void getClientDao_Args__() throws Exception {
    ReplicatedClientDao actual = target.getClientDao();
    ReplicatedClientDao expected = this.clientDao;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  // public void fetchLastRunResults_Args__Date__Set() throws Exception {
  // Date lastRunDt = mock(Date.class);
  // Set<String> deletionResults = mock(Set.class);
  // List<ReplicatedPersonCases> actual = target.fetchLastRunResults(lastRunDt, deletionResults);
  // List<ReplicatedPersonCases> expected = new ArrayList<>();
  // assertThat(actual, is(equalTo(expected)));
  // }

  // @Test
  // public void main_Args__StringArray() throws Exception {
  // String[] args = new String[] {};
  // CaseRocket.main(args);
  // }

}
