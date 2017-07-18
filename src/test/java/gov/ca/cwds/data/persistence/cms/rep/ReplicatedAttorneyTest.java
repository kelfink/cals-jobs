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

public class ReplicatedAttorneyTest {

  @Test
  public void testReplicationOperation() throws Exception {
    ReplicatedAttorney target = new ReplicatedAttorney();
    target.setReplicationOperation(CmsReplicationOperation.I);
    CmsReplicationOperation actual = target.getReplicationOperation();
    CmsReplicationOperation expected = CmsReplicationOperation.I;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void testReplicationDate() throws Exception {
    ReplicatedAttorney target = new ReplicatedAttorney();
    DateFormat fmt = new SimpleDateFormat("yyyy-mm-dd");
    Date date = fmt.parse("2012-10-31");
    target.setReplicationDate(date);
    Date actual = target.getReplicationDate();
    Date expected = fmt.parse("2012-10-31");
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedAttorney.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ReplicatedAttorney target = new ReplicatedAttorney();
    assertThat(target, notNullValue());
  }

  @Test
  public void getNormalizationClass_Args__() throws Exception {
    ReplicatedAttorney target = new ReplicatedAttorney();
    Class<ReplicatedAttorney> actual = target.getNormalizationClass();
    Class<ReplicatedAttorney> expected = ReplicatedAttorney.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__Map() throws Exception {
    ReplicatedAttorney target = new ReplicatedAttorney();
    Map<Object, ReplicatedAttorney> map = new HashMap<Object, ReplicatedAttorney>();
    ReplicatedAttorney actual = target.normalize(map);
    ReplicatedAttorney expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getNormalizationGroupKey_Args__() throws Exception {
    ReplicatedAttorney target = new ReplicatedAttorney();
    Object actual = target.getNormalizationGroupKey();
    Object expected = "";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacyId_Args__() throws Exception {
    ReplicatedAttorney target = new ReplicatedAttorney();
    String actual = target.getLegacyId();
    String expected = "";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacyDescriptor_Args__() throws Exception {
    ReplicatedAttorney target = new ReplicatedAttorney();
    Date lastUpdatedTime = new Date();
    target.setReplicationOperation(CmsReplicationOperation.U);
    target.setLastUpdatedId("0x5");
    target.setLastUpdatedTime(lastUpdatedTime);
    target.setReplicationDate(lastUpdatedTime);
    ElasticSearchLegacyDescriptor actual = target.getLegacyDescriptor();
    assertThat(actual, is(notNullValue()));
  }

}
