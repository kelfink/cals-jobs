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

import org.junit.BeforeClass;
import org.junit.Test;

import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchLegacyDescriptor;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonAka;
import gov.ca.cwds.data.persistence.cms.ReplicatedAkas;
import gov.ca.cwds.jobs.test.TestSystemCodeCache;
import gov.ca.cwds.rest.api.domain.cms.LegacyTable;

public class ReplicatedOtherClientNameTest {

  @BeforeClass
  public static void setupTests() {
    TestSystemCodeCache.init();
  }

  @Test
  public void testReplicationOperation() throws Exception {
    ReplicatedOtherClientName target = new ReplicatedOtherClientName();
    target.setReplicationOperation(CmsReplicationOperation.I);
    CmsReplicationOperation actual = target.getReplicationOperation();
    CmsReplicationOperation expected = CmsReplicationOperation.I;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void testReplicationDate() throws Exception {
    ReplicatedOtherClientName target = new ReplicatedOtherClientName();
    DateFormat fmt = new SimpleDateFormat("yyyy-mm-dd");
    Date date = fmt.parse("2012-10-31");
    target.setReplicationDate(date);
    Date actual = target.getReplicationDate();
    Date expected = fmt.parse("2012-10-31");
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedOtherClientName.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ReplicatedOtherClientName target = new ReplicatedOtherClientName();
    assertThat(target, notNullValue());
  }

  @Test
  public void getNormalizationClass_Args__() throws Exception {
    ReplicatedOtherClientName target = new ReplicatedOtherClientName();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Class<ReplicatedAkas> actual = target.getNormalizationClass();
    // then
    // e.g. : verify(mocked).called();
    Class<ReplicatedAkas> expected = ReplicatedAkas.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__Map() throws Exception {
    String key = "asd12340x5";
    String thirdId = "ABC12340x5";
    ReplicatedOtherClientName target = new ReplicatedOtherClientName();
    target.setClientId(key);
    target.setThirdId("thirdId");
    target.setFirstName("fred");
    target.setMiddleName("jason");
    target.setLastName("meyer");
    target.setNameType(new Short((short) 1311));
    target.setSuffixTitleDescription("junior");

    Map<Object, ReplicatedAkas> map = new HashMap<Object, ReplicatedAkas>();
    ReplicatedAkas akas = new ReplicatedAkas();
    akas.setId(key);
    ElasticSearchPersonAka aka = new ElasticSearchPersonAka();
    aka.setFirstName("fred");
    aka.setMiddleName("jason");
    aka.setLastName("meyer");
    aka.setLegacyDescriptor(new ElasticSearchLegacyDescriptor(key, thirdId, "2017-12-31",
        LegacyTable.ADDRESS.getName(), "whatever"));
    akas.addAka(aka);
    map.put(key, akas);

    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ReplicatedAkas actual = target.normalize(map);
    // then
    // e.g. : verify(mocked).called();
    // ReplicatedAkas expected = new ReplicatedAkas();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getNormalizationGroupKey_Args__() throws Exception {
    ReplicatedOtherClientName target = new ReplicatedOtherClientName();
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
    ReplicatedOtherClientName target = new ReplicatedOtherClientName();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getLegacyId();
    // then
    // e.g. : verify(mocked).called();
    String expected = "";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getId_Args__() throws Exception {
    ReplicatedOtherClientName target = new ReplicatedOtherClientName();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getId();
    // then
    // e.g. : verify(mocked).called();
    String expected = "";
    assertThat(actual, is(equalTo(expected)));
  }

}
