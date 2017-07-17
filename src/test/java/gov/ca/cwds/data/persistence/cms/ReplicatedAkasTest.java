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

import gov.ca.cwds.data.es.ElasticSearchPersonAka;

public class ReplicatedAkasTest {

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedAkas.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ReplicatedAkas target = new ReplicatedAkas();
    assertThat(target, notNullValue());
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    ReplicatedAkas target = new ReplicatedAkas();
    Serializable actual = target.getPrimaryKey();
    Serializable expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getId_Args__() throws Exception {
    ReplicatedAkas target = new ReplicatedAkas();
    String actual = target.getId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setId_Args__String() throws Exception {
    ReplicatedAkas target = new ReplicatedAkas();
    String id = null;
    target.setId(id);
  }

  @Test
  public void getAkas_Args__() throws Exception {
    ReplicatedAkas target = new ReplicatedAkas();
    List<ElasticSearchPersonAka> actual = target.getAkas();
    List<ElasticSearchPersonAka> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setAkas_Args__List() throws Exception {
    ReplicatedAkas target = new ReplicatedAkas();
    List<ElasticSearchPersonAka> akas = new ArrayList<ElasticSearchPersonAka>();
    target.setAkas(akas);
  }

  @Test
  public void addAka_Args__ElasticSearchPersonAka() throws Exception {
    ReplicatedAkas target = new ReplicatedAkas();
    ElasticSearchPersonAka aka = mock(ElasticSearchPersonAka.class);
    target.addAka(aka);
  }

  @Test
  public void getBirthDate_Args__() throws Exception {
    ReplicatedAkas target = new ReplicatedAkas();
    Date actual = target.getBirthDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getFirstName_Args__() throws Exception {
    ReplicatedAkas target = new ReplicatedAkas();
    String actual = target.getFirstName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getGender_Args__() throws Exception {
    ReplicatedAkas target = new ReplicatedAkas();
    String actual = target.getGender();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLastName_Args__() throws Exception {
    ReplicatedAkas target = new ReplicatedAkas();
    String actual = target.getLastName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getMiddleName_Args__() throws Exception {
    ReplicatedAkas target = new ReplicatedAkas();
    String actual = target.getMiddleName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getNameSuffix_Args__() throws Exception {
    ReplicatedAkas target = new ReplicatedAkas();
    String actual = target.getNameSuffix();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getSsn_Args__() throws Exception {
    ReplicatedAkas target = new ReplicatedAkas("abc1234x7s");
    String actual = target.getSsn();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

}
