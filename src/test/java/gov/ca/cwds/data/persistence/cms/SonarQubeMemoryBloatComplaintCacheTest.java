package gov.ca.cwds.data.persistence.cms;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.ca.cwds.data.es.ElasticSearchPersonRelationship;
import gov.ca.cwds.data.persistence.cms.rep.CmsReplicationOperation;
import gov.ca.cwds.jobs.Goddard;
import gov.ca.cwds.jobs.test.SimpleTestSystemCodeCache;

public class SonarQubeMemoryBloatComplaintCacheTest extends Goddard<ReplicatedRelationships, EsRelationship> {

  EsRelationship target = new EsRelationship();

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    target = new EsRelationship();
    target.setThisLegacyId(DEFAULT_CLIENT_ID);
  }

  @BeforeClass
  public static void setupTests() {
    SimpleTestSystemCodeCache.init();
  }

  @Test
  public void type() throws Exception {
    assertThat(EsRelationship.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void mapRow_Args__ResultSet() throws Exception {
    when(rs.getString(any(String.class))).thenReturn("Y");

    EsRelationship actual = EsRelationship.mapRow(rs);
    EsRelationship expected = new EsRelationship();
    expected.setRelCode(Short.valueOf((short) 0));
    expected.setReverseRelationship(false);
    assertThat(actual, is(notNullValue()));
  }

  @Test(expected = SQLException.class)
  public void mapRow_Args__ResultSet_T__SQLException() throws Exception {
    doThrow(new SQLException()).when(rs).getString(any());
    EsRelationship.mapRow(rs);
    fail("Expected exception was not thrown!");
  }

  @Test
  public void getNormalizationClass_Args__() throws Exception {
    Class<ReplicatedRelationships> actual = target.getNormalizationClass();
    Class<ReplicatedRelationships> expected = ReplicatedRelationships.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void parseBiDirectionalRelationship_Args__ElasticSearchPersonRelationship()
      throws Exception {
    target.setRelCode((short) 196);
    ElasticSearchPersonRelationship rel = new ElasticSearchPersonRelationship();
    rel.setIndexedPersonRelationship("daughter");
    rel.setRelatedPersonFirstName("Britney");
    rel.setRelatedPersonLastName("Spears");
    rel.setRelatedPersonRelationship("mother");
    rel.setRelationshipContext("birth");
    rel.setRelatedPersonId("abc12347x6");
    target.parseBiDirectionalRelationship(rel);
  }

  @Test
  public void normalize_Args__Map() throws Exception {
    target.setRelatedLegacyId("abc12340x7");
    target.setRelatedFirstName("Idina");
    target.setRelatedLastName("Menzel");
    Map<Object, ReplicatedRelationships> map = new HashMap<Object, ReplicatedRelationships>();
    ReplicatedRelationships actual = target.normalize(map);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getNormalizationGroupKey_Args__() throws Exception {
    Object actual = target.getNormalizationGroupKey();
    Object expected = DEFAULT_CLIENT_ID;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    Serializable actual = target.getPrimaryKey();
    Serializable expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void hashCode_Args__() throws Exception {
    int actual = target.hashCode();
    int expected = -2120005431;
    assertThat(actual, not(equalTo(expected)));
  }

  @Test
  public void equals_Args__Object() throws Exception {
    Object obj = null;
    boolean actual = target.equals(obj);
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void toString_Args__() throws Exception {
    String actual = target.toString();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getThisLegacyId_Args__() throws Exception {
    String actual = target.getThisLegacyId();
    String expected = DEFAULT_CLIENT_ID;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setThisLegacyId_Args__String() throws Exception {
    String thisLegacyId = DEFAULT_CLIENT_ID;
    target.setThisLegacyId(thisLegacyId);
  }

  @Test
  public void getThisFirstName_Args__() throws Exception {
    String actual = target.getThisFirstName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setThisFirstName_Args__String() throws Exception {
    String thisFirstName = null;
    target.setThisFirstName(thisFirstName);
  }

  @Test
  public void getThisLastName_Args__() throws Exception {
    String actual = target.getThisLastName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setThisLastName_Args__String() throws Exception {
    String thisLastName = null;
    target.setThisLastName(thisLastName);
  }

  @Test
  public void getRelCode_Args__() throws Exception {
    Short actual = target.getRelCode();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setRelCode_Args__Short() throws Exception {
    Short relCode = null;
    target.setRelCode(relCode);
  }

  @Test
  public void getRelatedLegacyId_Args__() throws Exception {
    String actual = target.getRelatedLegacyId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setRelatedLegacyId_Args__String() throws Exception {
    String relatedLegacyId = null;
    target.setRelatedLegacyId(relatedLegacyId);
  }

  @Test
  public void getRelatedFirstName_Args__() throws Exception {
    String actual = target.getRelatedFirstName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setRelatedFirstName_Args__String() throws Exception {
    String relatedFirstName = null;
    target.setRelatedFirstName(relatedFirstName);
  }

  @Test
  public void getRelatedLastName_Args__() throws Exception {
    String actual = target.getRelatedLastName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setRelatedLastName_Args__String() throws Exception {
    String relatedLastName = null;
    target.setRelatedLastName(relatedLastName);
  }

  @Test
  public void getReverseRelationship_Args__() throws Exception {
    Boolean actual = target.getReverseRelationship();
    Boolean expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReverseRelationship_Args__Boolean() throws Exception {
    Boolean reverseRelationship = null;
    target.setReverseRelationship(reverseRelationship);
  }

  @Test
  public void getThisLegacyLastUpdated_Args__() throws Exception {
    Date actual = target.getThisLegacyLastUpdated();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setThisLegacyLastUpdated_Args__Date() throws Exception {
    Date thisLegacyLastUpdated = new Date();
    target.setThisLegacyLastUpdated(thisLegacyLastUpdated);
  }

  @Test
  public void getRelatedLegacyLastUpdated_Args__() throws Exception {
    Date actual = target.getRelatedLegacyLastUpdated();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setRelatedLegacyLastUpdated_Args__Date() throws Exception {
    Date relatedLegacyLastUpdated = new Date();
    target.setRelatedLegacyLastUpdated(relatedLegacyLastUpdated);
  }

  @Test
  public void getThisReplicationOperation_Args__() throws Exception {
    CmsReplicationOperation actual = target.getThisReplicationOperation();
    CmsReplicationOperation expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setThisReplicationOperation_Args__CmsReplicationOperation() throws Exception {
    CmsReplicationOperation thisReplicationOperation = CmsReplicationOperation.I;
    target.setThisReplicationOperation(thisReplicationOperation);
  }

  @Test
  public void getThisReplicationDate_Args__() throws Exception {
    Date actual = target.getThisReplicationDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setThisReplicationDate_Args__Date() throws Exception {
    Date thisReplicationDate = new Date();
    target.setThisReplicationDate(thisReplicationDate);
  }

  @Test
  public void getRelatedReplicationOperation_Args__() throws Exception {
    CmsReplicationOperation actual = target.getRelatedReplicationOperation();
    CmsReplicationOperation expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setRelatedReplicationOperation_Args__CmsReplicationOperation() throws Exception {
    CmsReplicationOperation relatedReplicationOperation = CmsReplicationOperation.U;
    target.setRelatedReplicationOperation(relatedReplicationOperation);
  }

  @Test
  public void getRelatedReplicationDate_Args__() throws Exception {
    Date actual = target.getRelatedReplicationDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setRelatedReplicationDate_Args__Date() throws Exception {
    Date relatedReplicationDate = new Date();
    target.setRelatedReplicationDate(relatedReplicationDate);
  }

  @Test
  public void compare_Args__EsRelationship__EsRelationship() throws Exception {
    EsRelationship o1 = new EsRelationship();
    o1.setThisLegacyId(DEFAULT_CLIENT_ID);

    EsRelationship o2 = new EsRelationship();
    o2.setThisLegacyId(DEFAULT_CLIENT_ID);

    int actual = target.compare(o1, o2);
    int expected = 0;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void compareTo_Args__EsRelationship() throws Exception {
    EsRelationship o = new EsRelationship();
    o.setThisLegacyId(DEFAULT_CLIENT_ID);

    int actual = target.compareTo(o);
    int expected = 0;
    assertThat(actual, is(equalTo(expected)));
  }

}
