package gov.ca.cwds.data.persistence.cms;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.Serializable;

import org.junit.Test;

import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonRelationship;

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

  // @Test
  public void getEsRelationship_Args__() throws Exception {
    ReplicatedRelationship target = new ReplicatedRelationship();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    ElasticSearchPersonRelationship actual = target.getEsRelationship();
    // then
    // e.g. : verify(mocked).called();
    ElasticSearchPersonRelationship expected = new ElasticSearchPersonRelationship();
    assertThat(actual, is(equalTo(expected)));
  }

}
