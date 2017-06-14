package gov.ca.cwds.jobs;

import java.util.Map;

import gov.ca.cwds.data.std.ApiGroupNormalizer;

/**
 * Denormalized
 */
public class TestDenormalizedEntity implements ApiGroupNormalizer<TestNormalizedEntity> {

  private String id;
  private String[] names;

  public TestDenormalizedEntity() {

  }

  public TestDenormalizedEntity(String id, String... names) {
    this.id = id;
    this.names = names;
  }

  @Override
  public Class<TestNormalizedEntity> getNormalizationClass() {
    return TestNormalizedEntity.class;
  }

  @Override
  public Object getNormalizationGroupKey() {
    return null;
  }

  @Override
  public TestNormalizedEntity normalize(Map<Object, TestNormalizedEntity> arg0) {
    return null;
  }

}
