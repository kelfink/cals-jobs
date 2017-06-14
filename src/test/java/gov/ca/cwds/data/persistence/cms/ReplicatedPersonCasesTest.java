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

import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonCase;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonParent;

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
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    List<ElasticSearchPersonCase> actual = target.getCases();
    // then
    // e.g. : verify(mocked).called();
    List<ElasticSearchPersonCase> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void addCase_Args__ElasticSearchPersonCase__ElasticSearchPersonParent() throws Exception {
    String groupId = null;
    ReplicatedPersonCases target = new ReplicatedPersonCases(groupId);
    // given
    ElasticSearchPersonCase personCase = mock(ElasticSearchPersonCase.class);
    ElasticSearchPersonParent caseParent = mock(ElasticSearchPersonParent.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.addCase(personCase, caseParent);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    String groupId = null;
    ReplicatedPersonCases target = new ReplicatedPersonCases(groupId);
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
  public void getBirthDate_Args__() throws Exception {
    String groupId = null;
    ReplicatedPersonCases target = new ReplicatedPersonCases(groupId);
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
    String groupId = null;
    ReplicatedPersonCases target = new ReplicatedPersonCases(groupId);
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
    String groupId = null;
    ReplicatedPersonCases target = new ReplicatedPersonCases(groupId);
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
    String groupId = null;
    ReplicatedPersonCases target = new ReplicatedPersonCases(groupId);
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
    String groupId = null;
    ReplicatedPersonCases target = new ReplicatedPersonCases(groupId);
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
    String groupId = null;
    ReplicatedPersonCases target = new ReplicatedPersonCases(groupId);
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
    String groupId = null;
    ReplicatedPersonCases target = new ReplicatedPersonCases(groupId);
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getSsn();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

}
