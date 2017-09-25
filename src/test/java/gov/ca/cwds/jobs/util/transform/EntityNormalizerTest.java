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

public class EntityNormalizerTest {

  @Test
  public void type() throws Exception {
    assertThat(EntityNormalizer.class, notNullValue());
  }

  @Test
  public void normalizeList_Args__List() throws Exception {
    final List<EsRelationship> denormalized = new ArrayList<EsRelationship>();
    final List<ReplicatedRelationships> actual =
        EntityNormalizer.<ReplicatedRelationships, EsRelationship>normalizeList(denormalized);
    final List<ReplicatedRelationships> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

}
