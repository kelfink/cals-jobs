package gov.ca.cwds.data.persistence.cms;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonRelationship;

public class EsRelationshipTest {

  @Test
  public void type() throws Exception {
    assertThat(EsRelationship.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    EsRelationship target = new EsRelationship();
    assertThat(target, notNullValue());
  }

  @Test
  public void mapRow_Args__ResultSet() throws Exception {
    // given
    ResultSet rs = mock(ResultSet.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    EsRelationship actual = EsRelationship.mapRow(rs);
    // then
    // e.g. : verify(mocked).called();
    EsRelationship expected = new EsRelationship();
    expected.setRelCode(Short.valueOf((short) 0));
    expected.setReverseRelationship(false);
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void mapRow_Args__ResultSet_T__SQLException() throws Exception {
    // given
    ResultSet rs = mock(ResultSet.class);
    // e.g. : given(mocked.called()).willReturn(1);
    doThrow(new SQLException()).when(rs).getString(any());
    try {
      // when
      EsRelationship.mapRow(rs);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
      // then
    }
  }

  @Test
  public void getNormalizationClass_Args__() throws Exception {
    EsRelationship target = new EsRelationship();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Class<ReplicatedRelationships> actual = target.getNormalizationClass();
    // then
    // e.g. : verify(mocked).called();
    Class<ReplicatedRelationships> expected = ReplicatedRelationships.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void parseBiDirectionalRelationship_Args__ElasticSearchPersonRelationship()
      throws Exception {
    EsRelationship target = new EsRelationship();
    // given
    ElasticSearchPersonRelationship rel = mock(ElasticSearchPersonRelationship.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.parseBiDirectionalRelationship(rel);
    // then
    // e.g. : verify(mocked).called();
  }

  // @Test
  public void normalize_Args__Map() throws Exception {
    EsRelationship target = new EsRelationship();
    // given
    Map<Object, ReplicatedRelationships> map = new HashMap<Object, ReplicatedRelationships>();
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ReplicatedRelationships actual = target.normalize(map);
    // then
    // e.g. : verify(mocked).called();
    ReplicatedRelationships expected = new ReplicatedRelationships();
    expected.addRelation(new ElasticSearchPersonRelationship());
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getNormalizationGroupKey_Args__() throws Exception {
    EsRelationship target = new EsRelationship();
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
  public void getPrimaryKey_Args__() throws Exception {
    EsRelationship target = new EsRelationship();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Serializable actual = target.getPrimaryKey();
    // then
    // e.g. : verify(mocked).called();
    Serializable expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void hashCode_Args__() throws Exception {
    EsRelationship target = new EsRelationship();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    int actual = target.hashCode();
    // then
    // e.g. : verify(mocked).called();
    int expected = -779599631;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void equals_Args__Object() throws Exception {
    EsRelationship target = new EsRelationship();
    // given
    Object obj = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    boolean actual = target.equals(obj);
    // then
    // e.g. : verify(mocked).called();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void toString_Args__() throws Exception {
    EsRelationship target = new EsRelationship();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.toString();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getThisLegacyId_Args__() throws Exception {

    EsRelationship target = new EsRelationship();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getThisLegacyId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setThisLegacyId_Args__String() throws Exception {

    EsRelationship target = new EsRelationship();
    // given
    String thisLegacyId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setThisLegacyId(thisLegacyId);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getThisFirstName_Args__() throws Exception {
    EsRelationship target = new EsRelationship();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getThisFirstName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setThisFirstName_Args__String() throws Exception {
    EsRelationship target = new EsRelationship();
    // given
    String thisFirstName = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setThisFirstName(thisFirstName);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getThisLastName_Args__() throws Exception {
    EsRelationship target = new EsRelationship();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getThisLastName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setThisLastName_Args__String() throws Exception {

    EsRelationship target = new EsRelationship();
    // given
    String thisLastName = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setThisLastName(thisLastName);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getRelCode_Args__() throws Exception {

    EsRelationship target = new EsRelationship();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Short actual = target.getRelCode();
    // then
    // e.g. : verify(mocked).called();
    Short expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setRelCode_Args__Short() throws Exception {
    EsRelationship target = new EsRelationship();
    // given
    Short relCode = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setRelCode(relCode);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getRelatedLegacyId_Args__() throws Exception {
    EsRelationship target = new EsRelationship();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getRelatedLegacyId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setRelatedLegacyId_Args__String() throws Exception {

    EsRelationship target = new EsRelationship();
    // given
    String relatedLegacyId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setRelatedLegacyId(relatedLegacyId);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getRelatedFirstName_Args__() throws Exception {

    EsRelationship target = new EsRelationship();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getRelatedFirstName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setRelatedFirstName_Args__String() throws Exception {
    EsRelationship target = new EsRelationship();
    // given
    String relatedFirstName = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setRelatedFirstName(relatedFirstName);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getRelatedLastName_Args__() throws Exception {
    EsRelationship target = new EsRelationship();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getRelatedLastName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setRelatedLastName_Args__String() throws Exception {
    EsRelationship target = new EsRelationship();
    // given
    String relatedLastName = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setRelatedLastName(relatedLastName);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getReverseRelationship_Args__() throws Exception {
    EsRelationship target = new EsRelationship();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Boolean actual = target.getReverseRelationship();
    // then
    // e.g. : verify(mocked).called();
    Boolean expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setReverseRelationship_Args__Boolean() throws Exception {
    EsRelationship target = new EsRelationship();
    // given
    Boolean reverseRelationship = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setReverseRelationship(reverseRelationship);
    // then
    // e.g. : verify(mocked).called();
  }

}
