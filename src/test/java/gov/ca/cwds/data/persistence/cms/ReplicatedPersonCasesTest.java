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

import gov.ca.cwds.data.es.ElasticSearchPersonCase;
import gov.ca.cwds.data.es.ElasticSearchPersonParent;

public class ReplicatedPersonCasesTest {

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedPersonCases.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    String groupId = null;
    ReplicatedPersonCases target = new ReplicatedPersonCases(groupId);
    assertThat(target, notNullValue());
  }

  @Test
  public void getCases_Args__() throws Exception {
    String groupId = null;
    ReplicatedPersonCases target = new ReplicatedPersonCases(groupId);
    List<ElasticSearchPersonCase> actual = target.getCases();
    List<ElasticSearchPersonCase> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void addCase_Args__ElasticSearchPersonCase__ElasticSearchPersonParent() throws Exception {
    String groupId = null;
    ReplicatedPersonCases target = new ReplicatedPersonCases(groupId);
    ElasticSearchPersonCase personCase = mock(ElasticSearchPersonCase.class);
    ElasticSearchPersonParent caseParent = mock(ElasticSearchPersonParent.class);
    target.addCase(personCase, caseParent);
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    String groupId = null;
    ReplicatedPersonCases target = new ReplicatedPersonCases(groupId);
    Serializable actual = target.getPrimaryKey();
    Serializable expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getBirthDate_Args__() throws Exception {
    String groupId = null;
    ReplicatedPersonCases target = new ReplicatedPersonCases(groupId);
    Date actual = target.getBirthDate();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getFirstName_Args__() throws Exception {
    String groupId = null;
    ReplicatedPersonCases target = new ReplicatedPersonCases(groupId);
    String actual = target.getFirstName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getGender_Args__() throws Exception {
    String groupId = null;
    ReplicatedPersonCases target = new ReplicatedPersonCases(groupId);
    String actual = target.getGender();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLastName_Args__() throws Exception {
    String groupId = null;
    ReplicatedPersonCases target = new ReplicatedPersonCases(groupId);
    String actual = target.getLastName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getMiddleName_Args__() throws Exception {
    String groupId = null;
    ReplicatedPersonCases target = new ReplicatedPersonCases(groupId);
    String actual = target.getMiddleName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getNameSuffix_Args__() throws Exception {
    String groupId = null;
    ReplicatedPersonCases target = new ReplicatedPersonCases(groupId);
    String actual = target.getNameSuffix();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getSsn_Args__() throws Exception {
    String groupId = null;
    ReplicatedPersonCases target = new ReplicatedPersonCases(groupId);
    String actual = target.getSsn();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

}
