package gov.ca.cwds.data.persistence.cms.rep;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.data.es.ElasticSearchLegacyDescriptor;
import gov.ca.cwds.jobs.Goddard;

public class ReplicatedClientRelationshipTest
    extends Goddard<ReplicatedClientRelationship, ReplicatedClientRelationship> {

  ReplicatedClientRelationship target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    target = new ReplicatedClientRelationship();
  }

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedClientRelationship.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void instantiation2() throws Exception {
    target = new ReplicatedClientRelationship("A", (short) 0, new Date(), "xyz1234567",
        DEFAULT_CLIENT_ID, DEFAULT_CLIENT_ID, "XYZ", new Date());
    assertThat(target, notNullValue());
  }

  @Test
  public void getReplicatedEntity_Args__() throws Exception {
    EmbeddableCmsReplicatedEntity actual = target.getReplicatedEntity();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getNormalizationClass_Args__() throws Exception {
    Class<ReplicatedClientRelationship> actual = target.getNormalizationClass();
    Class<ReplicatedClientRelationship> expected = ReplicatedClientRelationship.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__Map() throws Exception {
    Map<Object, ReplicatedClientRelationship> map =
        new HashMap<Object, ReplicatedClientRelationship>();
    ReplicatedClientRelationship actual = target.normalize(map);
    ReplicatedClientRelationship expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getNormalizationGroupKey_Args__() throws Exception {
    String actual = target.getNormalizationGroupKey();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacyId_Args__() throws Exception {
    String actual = target.getLegacyId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getLegacyDescriptor_Args__() throws Exception {
    ElasticSearchLegacyDescriptor actual = target.getLegacyDescriptor();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void hashCode_Args__() throws Exception {
    int actual = target.hashCode();
    assertThat(actual, is(not(0)));
  }

  @Test
  public void equals_Args__Object() throws Exception {
    Object obj = null;
    boolean actual = target.equals(obj);
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

}
