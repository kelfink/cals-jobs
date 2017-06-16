package gov.ca.cwds.data.persistence.cms;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.Serializable;

import org.junit.Test;

public class ReplicatedRelationshipTest {

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedRelationship.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
    assertThat(target, notNullValue());
  }

  @Test
  public void buildUpdateJson_Args__() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.buildUpdateJson();
    // then
    // e.g. : verify(mocked).called();
    String expected = "";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPrimaryKey_Args__() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
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
  public void getThisLegacyTable_Args__() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getThisLegacyTable();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setThisLegacyTable_Args__String() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
    // given
    String thisLegacyTable = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setThisLegacyTable(thisLegacyTable);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getRelatedLegacyTable_Args__() throws Exception {

    ReplicatedRelationship target = new ReplicatedRelationship();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getRelatedLegacyTable();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setRelatedLegacyTable_Args__String() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
    // given
    String relatedLegacyTable = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setRelatedLegacyTable(relatedLegacyTable);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getThisFirstName_Args__() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
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
    ReplicatedRelationship target = new ReplicatedRelationship();
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
    ReplicatedRelationship target = new ReplicatedRelationship();
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
    ReplicatedRelationship target = new ReplicatedRelationship();
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

    ReplicatedRelationship target = new ReplicatedRelationship();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getRelCode();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setRelCode_Args__String() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
    // given
    String relCode = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setRelCode(relCode);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getRelatedLegacyId_Args__() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
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
    ReplicatedRelationship target = new ReplicatedRelationship();
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
    ReplicatedRelationship target = new ReplicatedRelationship();
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
    ReplicatedRelationship target = new ReplicatedRelationship();
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
    ReplicatedRelationship target = new ReplicatedRelationship();
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
    ReplicatedRelationship target = new ReplicatedRelationship();
    // given
    String relatedLastName = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setRelatedLastName(relatedLastName);
    // then
    // e.g. : verify(mocked).called();
  }

}
