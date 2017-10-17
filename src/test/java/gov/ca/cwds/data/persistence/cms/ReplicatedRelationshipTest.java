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
  public void getPrimaryKey_Args__() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
    Serializable actual = target.getPrimaryKey();
    Serializable expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getThisLegacyTable_Args__() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
    String actual = target.getThisLegacyTable();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setThisLegacyTable_Args__String() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
    String thisLegacyTable = null;
    target.setThisLegacyTable(thisLegacyTable);
  }

  @Test
  public void getRelatedLegacyTable_Args__() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
    String actual = target.getRelatedLegacyTable();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setRelatedLegacyTable_Args__String() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
    String relatedLegacyTable = null;
    target.setRelatedLegacyTable(relatedLegacyTable);
  }

  @Test
  public void getThisFirstName_Args__() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
    String actual = target.getThisFirstName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setThisFirstName_Args__String() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
    String thisFirstName = null;
    target.setThisFirstName(thisFirstName);
  }

  @Test
  public void getThisLastName_Args__() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
    String actual = target.getThisLastName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setThisLastName_Args__String() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
    String thisLastName = null;
    target.setThisLastName(thisLastName);
  }

  @Test
  public void getRelCode_Args__() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
    String actual = target.getRelCode();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setRelCode_Args__String() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
    String relCode = null;
    target.setRelCode(relCode);
  }

  @Test
  public void getRelatedLegacyId_Args__() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
    String actual = target.getRelatedLegacyId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setRelatedLegacyId_Args__String() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
    String relatedLegacyId = null;
    target.setRelatedLegacyId(relatedLegacyId);
  }

  @Test
  public void getRelatedFirstName_Args__() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
    String actual = target.getRelatedFirstName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setRelatedFirstName_Args__String() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
    String relatedFirstName = null;
    target.setRelatedFirstName(relatedFirstName);
  }

  @Test
  public void getRelatedLastName_Args__() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
    String actual = target.getRelatedLastName();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setRelatedLastName_Args__String() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
    String relatedLastName = null;
    target.setRelatedLastName(relatedLastName);
  }

}
