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

public class ReplicatedOtherAdultInPlacemtHomeTest {

  @Test
  public void testReplicationOperation() throws Exception {
    ReplicatedOtherAdultInPlacemtHome target = new ReplicatedOtherAdultInPlacemtHome();
    target.setReplicationOperation(CmsReplicationOperation.I);
    CmsReplicationOperation actual = target.getReplicationOperation();
    CmsReplicationOperation expected = CmsReplicationOperation.I;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void testReplicationDate() throws Exception {
    ReplicatedOtherAdultInPlacemtHome target = new ReplicatedOtherAdultInPlacemtHome();
    DateFormat fmt = new SimpleDateFormat("yyyy-mm-dd");
    Date date = fmt.parse("2012-10-31");
    target.setReplicationDate(date);
    Date actual = target.getReplicationDate();
    Date expected = fmt.parse("2012-10-31");
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedOtherAdultInPlacemtHome.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ReplicatedOtherAdultInPlacemtHome target = new ReplicatedOtherAdultInPlacemtHome();
    assertThat(target, notNullValue());
  }

  @Test
  public void getNormalizationClass_Args__() throws Exception {
    ReplicatedOtherAdultInPlacemtHome target = new ReplicatedOtherAdultInPlacemtHome();
    Class<ReplicatedOtherAdultInPlacemtHome> actual = target.getNormalizationClass();
    Class<ReplicatedOtherAdultInPlacemtHome> expected = ReplicatedOtherAdultInPlacemtHome.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__Map() throws Exception {
    ReplicatedOtherAdultInPlacemtHome target = new ReplicatedOtherAdultInPlacemtHome();
    Map<Object, ReplicatedOtherAdultInPlacemtHome> map =
        new HashMap<Object, ReplicatedOtherAdultInPlacemtHome>();
    ReplicatedOtherAdultInPlacemtHome actual = target.normalize(map);
    ReplicatedOtherAdultInPlacemtHome expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getNormalizationGroupKey_Args__() throws Exception {
    ReplicatedOtherAdultInPlacemtHome target = new ReplicatedOtherAdultInPlacemtHome();
    Object actual = target.getNormalizationGroupKey();
    Object expected = "";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacyId_Args__() throws Exception {
    ReplicatedOtherAdultInPlacemtHome target = new ReplicatedOtherAdultInPlacemtHome();
    String actual = target.getLegacyId();
    String expected = "";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacyDescriptor_Args__() throws Exception {
    Date lastUpdatedTime = new Date();
    ReplicatedOtherAdultInPlacemtHome target = new ReplicatedOtherAdultInPlacemtHome();
    target.setReplicationOperation(CmsReplicationOperation.U);
    target.setLastUpdatedId("0x5");
    target.setLastUpdatedTime(lastUpdatedTime);
    target.setReplicationDate(lastUpdatedTime);
    ElasticSearchLegacyDescriptor actual = target.getLegacyDescriptor();
    assertThat(actual, is(notNullValue()));
  }

}
