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

}
