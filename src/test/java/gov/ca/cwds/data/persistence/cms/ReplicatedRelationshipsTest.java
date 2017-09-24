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
    Serializable actual = target.getPrimaryKey();
    Serializable expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void addRelation_Args__ElasticSearchPersonRelationship() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
    ElasticSearchPersonRelationship relation = mock(ElasticSearchPersonRelationship.class);
    target.addRelation(relation);
  }

  @Test
  public void getBirthDate_Args__() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
    Date actual = target.getBirthDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getFirstName_Args__() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
    String actual = target.getFirstName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getGender_Args__() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
    String actual = target.getGender();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLastName_Args__() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
    String actual = target.getLastName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getMiddleName_Args__() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
    String actual = target.getMiddleName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getNameSuffix_Args__() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
    String actual = target.getNameSuffix();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getSsn_Args__() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
    String actual = target.getSsn();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getId_Args__() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
    String actual = target.getId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setId_Args__String() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
    String id = null;
    target.setId(id);
  }

  @Test
  public void getRelations_Args__() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
    List<ElasticSearchPersonRelationship> actual = target.getRelations();
    List<ElasticSearchPersonRelationship> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setRelations_Args__List() throws Exception {
    ReplicatedRelationships target = new ReplicatedRelationships();
    List<ElasticSearchPersonRelationship> relations =
        new ArrayList<ElasticSearchPersonRelationship>();
    target.setRelations(relations);
  }

}
