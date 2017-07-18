package gov.ca.cwds.data.persistence.cms;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import gov.ca.cwds.data.es.ElasticSearchPersonRelationship;

public class ReplicatedRelationshipsTest {

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedRelationships.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
    assertThat(target, notNullValue());
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
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
  public void addRelation_Args__ElasticSearchPersonRelationship() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
    // given
    ElasticSearchPersonRelationship relation = mock(ElasticSearchPersonRelationship.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.addRelation(relation);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getBirthDate_Args__() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    Date actual = target.getBirthDate();
    // then
    // e.g. : verify(mocked).called();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getFirstName_Args__() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getFirstName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getGender_Args__() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getGender();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLastName_Args__() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getLastName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getMiddleName_Args__() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getMiddleName();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getNameSuffix_Args__() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getNameSuffix();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getSsn_Args__() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getSsn();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getId_Args__() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
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
  public void setId_Args__String() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
    // given
    String id = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setId(id);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getRelations_Args__() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    List<ElasticSearchPersonRelationship> actual = target.getRelations();
    // then
    // e.g. : verify(mocked).called();
    List<ElasticSearchPersonRelationship> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setRelations_Args__List() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
    // given
    List<ElasticSearchPersonRelationship> relations =
        new ArrayList<ElasticSearchPersonRelationship>();
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setRelations(relations);
    // then
    // e.g. : verify(mocked).called();
  }

}
