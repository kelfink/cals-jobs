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

public class ReplicatedOtherChildInPlacemtHomeTest {

  @Test
  public void testReplicationOperation() throws Exception {
    ReplicatedOtherChildInPlacemtHome target = new ReplicatedOtherChildInPlacemtHome();
    target.setReplicationOperation(CmsReplicationOperation.I);
    CmsReplicationOperation actual = target.getReplicationOperation();
    CmsReplicationOperation expected = CmsReplicationOperation.I;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void testReplicationDate() throws Exception {
    ReplicatedOtherChildInPlacemtHome target = new ReplicatedOtherChildInPlacemtHome();
    DateFormat fmt = new SimpleDateFormat("yyyy-mm-dd");
    Date date = fmt.parse("2012-10-31");
    target.setReplicationDate(date);
    Date actual = target.getReplicationDate();
    Date expected = fmt.parse("2012-10-31");
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedOtherChildInPlacemtHome.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ReplicatedOtherChildInPlacemtHome target = new ReplicatedOtherChildInPlacemtHome();
    assertThat(target, notNullValue());
  }

  @Test
  public void getNormalizationClass_Args__() throws Exception {
    ReplicatedOtherChildInPlacemtHome target = new ReplicatedOtherChildInPlacemtHome();
    Class<ReplicatedOtherChildInPlacemtHome> actual = target.getNormalizationClass();
    Class<ReplicatedOtherChildInPlacemtHome> expected = ReplicatedOtherChildInPlacemtHome.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__Map() throws Exception {
    ReplicatedOtherChildInPlacemtHome target = new ReplicatedOtherChildInPlacemtHome();
    Map<Object, ReplicatedOtherChildInPlacemtHome> map =
        new HashMap<Object, ReplicatedOtherChildInPlacemtHome>();
    ReplicatedOtherChildInPlacemtHome actual = target.normalize(map);
    ReplicatedOtherChildInPlacemtHome expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getNormalizationGroupKey_Args__() throws Exception {
    ReplicatedOtherChildInPlacemtHome target = new ReplicatedOtherChildInPlacemtHome();
    Object actual = target.getNormalizationGroupKey();
    Object expected = "";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacyId_Args__() throws Exception {
    ReplicatedOtherChildInPlacemtHome target = new ReplicatedOtherChildInPlacemtHome();
    String actual = target.getLegacyId();
    String expected = "";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacyDescriptor_Args__() throws Exception {
    Date lastUpdatedTime = new Date();
    ReplicatedOtherChildInPlacemtHome target = new ReplicatedOtherChildInPlacemtHome();
    target.setReplicationOperation(CmsReplicationOperation.U);
    target.setLastUpdatedId("0x5");
    target.setLastUpdatedTime(lastUpdatedTime);
    target.setReplicationDate(lastUpdatedTime);
    ElasticSearchLegacyDescriptor actual = target.getLegacyDescriptor();
    assertThat(actual, is(notNullValue()));
  }

}
