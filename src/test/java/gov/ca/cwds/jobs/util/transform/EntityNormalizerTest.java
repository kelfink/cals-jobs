package gov.ca.cwds.jobs.util.transform;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import gov.ca.cwds.data.persistence.cms.EsRelationship;
import gov.ca.cwds.data.persistence.cms.ReplicatedRelationships;
import gov.ca.cwds.jobs.util.transform.EntityNormalizer;

public class EntityNormalizerTest {

  @Test
  public void type() throws Exception {
    assertThat(EntityNormalizer.class, notNullValue());
  }

  @Test
  public void normalizeList_Args__List() throws Exception {
    // given
    final List<EsRelationship> denormalized = new ArrayList<EsRelationship>();
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    final List<ReplicatedRelationships> actual =
        EntityNormalizer.<ReplicatedRelationships, EsRelationship>normalizeList(denormalized);
    // then
    // e.g. : verify(mocked).called();
    final List<ReplicatedRelationships> expected = new ArrayList<>();
    // expected.add(new ReplicatedRelationships());
    assertThat(actual, is(equalTo(expected)));
  }

}
