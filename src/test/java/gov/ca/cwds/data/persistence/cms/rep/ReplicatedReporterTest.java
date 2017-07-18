package gov.ca.cwds.data.persistence.cms.rep;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import gov.ca.cwds.data.es.ElasticSearchLegacyDescriptor;

public class ReplicatedReporterTest {
  @Test
  public void testReplicationOperation() throws Exception {
    ReplicatedReporter target = new ReplicatedReporter();
    target.setReplicationOperation(CmsReplicationOperation.I);
    CmsReplicationOperation actual = target.getReplicationOperation();
    CmsReplicationOperation expected = CmsReplicationOperation.I;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void testReplicationDate() throws Exception {
    ReplicatedReporter target = new ReplicatedReporter();
    DateFormat fmt = new SimpleDateFormat("yyyy-mm-dd");
    Date date = fmt.parse("2012-10-31");
    target.setReplicationDate(date);
    Date actual = target.getReplicationDate();
    Date expected = fmt.parse("2012-10-31");
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedReporter.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ReplicatedReporter target = new ReplicatedReporter();
    assertThat(target, notNullValue());
  }

  @Test
  public void getNormalizationClass_Args__() throws Exception {
    ReplicatedReporter target = new ReplicatedReporter();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Class<ReplicatedReporter> actual = target.getNormalizationClass();
    // then
    // e.g. : verify(mocked).called();
    Class<ReplicatedReporter> expected = ReplicatedReporter.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__Map() throws Exception {
    ReplicatedReporter target = new ReplicatedReporter();
    // given
    Map<Object, ReplicatedReporter> map = new HashMap<Object, ReplicatedReporter>();
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ReplicatedReporter actual = target.normalize(map);
    // then
    // e.g. : verify(mocked).called();
    ReplicatedReporter expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getNormalizationGroupKey_Args__() throws Exception {
    ReplicatedReporter target = new ReplicatedReporter();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Object actual = target.getNormalizationGroupKey();
    // then
    // e.g. : verify(mocked).called();
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacyId_Args__() throws Exception {
    ReplicatedReporter target = new ReplicatedReporter();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getLegacyId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getId_Args__() throws Exception {
    ReplicatedReporter target = new ReplicatedReporter();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getReplicationOperation_Args__() throws Exception {
    ReplicatedReporter target = new ReplicatedReporter();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    CmsReplicationOperation actual = target.getReplicationOperation();
    // then
    // e.g. : verify(mocked).called();
    CmsReplicationOperation expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReplicationOperation_Args__CmsReplicationOperation() throws Exception {
    ReplicatedReporter target = new ReplicatedReporter();
    // given
    CmsReplicationOperation replicationOperation = CmsReplicationOperation.U;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setReplicationOperation(replicationOperation);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getReplicationDate_Args__() throws Exception {
    ReplicatedReporter target = new ReplicatedReporter();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Date actual = target.getReplicationDate();
    // then
    // e.g. : verify(mocked).called();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReplicationDate_Args__Date() throws Exception {
    ReplicatedReporter target = new ReplicatedReporter();
    // given
    Date replicationDate = mock(Date.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setReplicationDate(replicationDate);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getLegacyDescriptor_Args__() throws Exception {
    Date lastUpdatedTime = new Date();
    ReplicatedReporter target = new ReplicatedReporter();
    target.setReplicationOperation(CmsReplicationOperation.U);
    target.setLastUpdatedId("0x5");
    target.setLastUpdatedTime(lastUpdatedTime);
    target.setReplicationDate(lastUpdatedTime);
    ElasticSearchLegacyDescriptor actual = target.getLegacyDescriptor();
    assertThat(actual, is(notNullValue()));
  }

}
