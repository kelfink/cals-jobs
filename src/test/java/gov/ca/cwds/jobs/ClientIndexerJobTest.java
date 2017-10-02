package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.ObjectMapperUtils;
import gov.ca.cwds.dao.cms.ReplicatedClientDao;
import gov.ca.cwds.data.persistence.cms.EsClientAddress;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedClient;
import gov.ca.cwds.jobs.exception.JobsException;

/**
 * 
 * @author CWDS API Team
 */
@SuppressWarnings("javadoc")
public class ClientIndexerJobTest extends PersonJobTester<ReplicatedClient, EsClientAddress> {

  protected static final ObjectMapper mapper = ObjectMapperUtils.createObjectMapper();

  ReplicatedClientDao dao;
  ClientIndexerJob target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    dao = new ReplicatedClientDao(sessionFactory);
    target = new ClientIndexerJob(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
    target.setOpts(opts);
  }

  @Test
  public void type() throws Exception {
    assertThat(ClientIndexerJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    target = new ClientIndexerJob(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
    assertThat(target, notNullValue());
  }

  @Test(expected = JobsException.class)
  @Ignore
  public void main_Args$StringArray() throws Exception {
    final String[] args = new String[] {};
    ClientIndexerJob.main(args);
  }

  @Test(expected = JobsException.class)
  @Ignore
  public void main_Args__StringArray__t_je() throws Exception {
    String[] args = new String[] {};
    ClientIndexerJob.main(args);
  }

  @Test(expected = JobsException.class)
  @Ignore
  public void main_Args__bucket_range() throws Exception {
    final String[] args = new String[] {"-c", "config/local.yaml", "-r", "21-22", "-b", "500"};
    ClientIndexerJob.main(args);
  }

  @Test(expected = JobsException.class)
  @Ignore
  public void main_Args__bucket_range_not_digit() throws Exception {
    final String[] args = new String[] {"-c", "config/local.yaml", "-r", "abc-xyz", "-b", "500"};
    ClientIndexerJob.main(args);
  }

  @Test
  public void extract_Args__ResultSet() throws Exception {
    when(rs.getString("CLT_IBMSNAP_OPERATION")).thenReturn("I");
    final EsClientAddress actual = target.extract(rs);
    final EsClientAddress expected = new EsClientAddress();

    final short s = (short) 0;
    expected.setCltBirthCountryCodeType(s);
    expected.setCltBirthStateCodeType(s);
    expected.setCltDriverLicenseStateCodeType(s);
    expected.setCltImmigrationCountryCodeType(s);
    expected.setCltMaritalStatusType(s);
    expected.setCltNameType(s);
    expected.setCltPrimaryEthnicityType(s);
    expected.setCltPrimaryLanguageType(s);
    expected.setCltSecondaryLanguageType(s);
    expected.setCltReligionType(s);
    expected.setClaAddressType(s);
    expected.setClaAddressType(s);
    expected.setClaAddressType(s);
    expected.setAdrEmergencyExtension(0);
    expected.setAdrPrimaryExtension(0);
    expected.setAdrState(s);
    expected.setAdrZip4(s);
    expected.setAdrUnitDesignationCd(s);
    // expected.setAdrPostDirCd(0);

    // assertThat(actual, is(equalTo(expected)));
    assertThat(actual, notNullValue());
  }

  @Test
  @Ignore
  public void extract_Args__ResultSet_T__SQLException() throws Exception {
    try {
      target.extract(rs);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
    }
  }

  @Test
  public void getDenormalizedClass_Args__() throws Exception {
    Object actual = target.getDenormalizedClass();
    Object expected = EsClientAddress.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getViewName_Args__() throws Exception {
    String actual = target.getInitialLoadViewName();
    String expected = "MQT_CLIENT_ADDRESS";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__List() throws Exception {
    List<EsClientAddress> recs = new ArrayList<EsClientAddress>();
    List<ReplicatedClient> actual = target.normalize(recs);
    List<ReplicatedClient> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getInitialLoadViewName_Args__() throws Exception {
    String actual = target.getInitialLoadViewName();
    String expected = "MQT_CLIENT_ADDRESS";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getJdbcOrderBy_Args__() throws Exception {
    final String actual = target.getJdbcOrderBy().trim().toUpperCase();
    final String expected = "ORDER BY X.CLT_IDENTIFIER";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getInitialLoadQuery_Args__String() throws Exception {
    final String actual =
        target.getInitialLoadQuery("CWSINT").trim().replace("\\s{2,}", " ").replaceAll("  ", " ");
    final String expected =
        "SELECT x.* FROM CWSINT.MQT_CLIENT_ADDRESS x WHERE x.clt_identifier > ':fromId' AND x.clt_identifier <= ':toId' AND x.CLT_SENSTV_IND = 'N' ORDER BY x.clt_identifier FOR READ ONLY WITH UR";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void handOff_Args__List() throws Exception {
    List<EsClientAddress> grpRecs = new ArrayList<EsClientAddress>();
    target.normalizeAndQueueIndex(grpRecs);
  }

  @Test
  public void threadExtractJdbc_Args__() throws Exception {
    target.threadRetrieveByJdbc();
  }

  @Test
  public void pullRange_Args__Pair() throws Exception {
    final Pair<String, String> p = Pair.of("aaaaaaaaaa", "9999999999");
    target.pullRange(p);
  }

  @Test
  public void getPartitionRanges_Args() throws Exception {
    final List actual = target.getPartitionRanges();
    final List expected = new ArrayList<>();
    expected.add(Pair.of("aaaaaaaaaa", "9999999999"));
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPartitionRanges_RSQ() throws Exception {
    System.setProperty("DB_CMS_SCHEMA", "CWSRSQ");
    final List actual = target.getPartitionRanges();
    assertThat(actual.size(), is(equalTo(16)));
  }

  @Test
  public void mustDeleteLimitedAccessRecords_Args__() throws Exception {
    boolean actual = target.mustDeleteLimitedAccessRecords();
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

}
