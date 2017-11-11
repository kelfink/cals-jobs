package gov.ca.cwds.neutron.rocket;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import gov.ca.cwds.dao.cms.ReplicatedClientDao;
import gov.ca.cwds.data.persistence.cms.EsClientAddress;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedClient;
import gov.ca.cwds.jobs.Goddard;
import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.jobs.schedule.LaunchCommandSettings;

public class ClientCountyRocketTest extends Goddard<ReplicatedClient, EsClientAddress> {

  ReplicatedClientDao dao;
  ClientCountyRocket target;
  Pair<String, String> pair;

  @Override
  public void setup() throws Exception {
    super.setup();
    dao = new ReplicatedClientDao(sessionFactory);
    target = new ClientCountyRocket(dao, esDao, lastJobRunTimeFilename, MAPPER, sessionFactory,
        flightPlan);
    pair = Pair.of("aaaaaaaaaa", "9999999999");
  }

  @Test
  public void type() throws Exception {
    assertThat(ClientCountyRocket.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void extract_Args__ResultSet() throws Exception {
    ResultSet rs = mock(ResultSet.class);
    EsClientAddress actual = target.extract(rs);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getDenormalizedClass_Args__() throws Exception {
    final Class<?> actual = target.getDenormalizedClass();
    final Class<?> expected = EsClientAddress.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getInitialLoadViewName_Args__() throws Exception {
    String actual = target.getInitialLoadViewName();
    String expected = "CLIENT_T";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getMQTName_Args__() throws Exception {
    String actual = target.getMQTName();
    String expected = "CLIENT_T";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getInitialLoadQuery_Args__String() throws Exception {
    String dbSchemaName = null;
    String actual = target.getInitialLoadQuery(dbSchemaName);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getJdbcOrderBy_Args__() throws Exception {
    String actual = target.getJdbcOrderBy();
    String expected = " ORDER BY x.IDENTIFIER ";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void useTransformThread_Args__() throws Exception {
    boolean actual = target.useTransformThread();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void isInitialLoadJdbc_Args__() throws Exception {
    boolean actual = target.isInitialLoadJdbc();
    boolean expected = true;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void pullRange_Args__Pair() throws Exception {
    target.pullRange(pair);
  }

  @Test
  public void callProc_Args__() throws Exception {
    final LaunchCommandSettings settings = new LaunchCommandSettings();
    settings.setInitialMode(true);
    LaunchCommand.setSettings(settings);

    when(procedureCall.getOutputParameterValue(any(String.class))).thenReturn(new Integer(0));
    target.callProc();
  }

  @Test
  public void threadRetrieveByJdbc_Args__() throws Exception {
    target.threadRetrieveByJdbc();
  }

  @Test
  public void getPartitionRanges_Args__() throws Exception {
    List<Pair<String, String>> actual = target.getPartitionRanges();
    List<Pair<String, String>> expected = new ArrayList<>();
    expected.add(Pair.of("aaaaaaaaaa", "9999999999"));
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void nextThreadNumber_Args__() throws Exception {
    int actual = target.nextThreadNumber();
    int expected = 1;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void main_Args__StringArray() throws Exception {
    final String[] args = new String[] {"-c", "config/local.yaml", "-l",
        "/Users/CWS-NS3/client_indexer_time.txt", "-S"};
    ClientCountyRocket.main(args);
  }

}
