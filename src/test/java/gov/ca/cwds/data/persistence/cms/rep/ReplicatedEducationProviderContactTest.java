package gov.ca.cwds.data.persistence.cms.rep;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import gov.ca.cwds.data.es.ElasticSearchLegacyDescriptor;

public class ReplicatedEducationProviderContactTest {

  @Test
  public void testReplicationOperation() throws Exception {
    ReplicatedEducationProviderContact target = new ReplicatedEducationProviderContact();
    target.setReplicationOperation(CmsReplicationOperation.I);
    CmsReplicationOperation actual = target.getReplicationOperation();
    CmsReplicationOperation expected = CmsReplicationOperation.I;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void testReplicationDate() throws Exception {
    ReplicatedEducationProviderContact target = new ReplicatedEducationProviderContact();
    DateFormat fmt = new SimpleDateFormat("yyyy-mm-dd");
    Date date = fmt.parse("2012-10-31");
    target.setReplicationDate(date);
    Date actual = target.getReplicationDate();
    Date expected = fmt.parse("2012-10-31");
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedEducationProviderContact.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ReplicatedEducationProviderContact target = new ReplicatedEducationProviderContact();
    assertThat(target, notNullValue());
  }

  @Test
  public void getNormalizationClass_Args__() throws Exception {
    ReplicatedEducationProviderContact target = new ReplicatedEducationProviderContact();
    Class<ReplicatedEducationProviderContact> actual = target.getNormalizationClass();
    Class<ReplicatedEducationProviderContact> expected = ReplicatedEducationProviderContact.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__Map() throws Exception {
    ReplicatedEducationProviderContact target = new ReplicatedEducationProviderContact();
    Map<Object, ReplicatedEducationProviderContact> map =
        new HashMap<Object, ReplicatedEducationProviderContact>();
    ReplicatedEducationProviderContact actual = target.normalize(map);
    ReplicatedEducationProviderContact expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getNormalizationGroupKey_Args__() throws Exception {
    ReplicatedEducationProviderContact target = new ReplicatedEducationProviderContact();
    Object actual = target.getNormalizationGroupKey();
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacyId_Args__() throws Exception {
    ReplicatedEducationProviderContact target = new ReplicatedEducationProviderContact();
    String actual = target.getLegacyId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacyDescriptor_Args__() throws Exception {
    ReplicatedEducationProviderContact target = new ReplicatedEducationProviderContact();
    Date lastUpdatedTime = new Date();
    target.setReplicationOperation(CmsReplicationOperation.U);
    target.setLastUpdatedId("0x5");
    target.setLastUpdatedTime(lastUpdatedTime);
    target.setReplicationDate(lastUpdatedTime);
    ElasticSearchLegacyDescriptor actual = target.getLegacyDescriptor();
    assertThat(actual, is(notNullValue()));
  }

}
