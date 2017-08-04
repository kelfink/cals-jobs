package gov.ca.cwds.jobs.test;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import gov.ca.cwds.data.std.ApiGroupNormalizer;

/**
 * Denormalized
 */
@JsonPropertyOrder(alphabetic = true)
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
  public String getNormalizationGroupKey() {
    return id;
  }

  @Override
  public TestNormalizedEntity normalize(Map<Object, TestNormalizedEntity> map) {
    final String thisId = getNormalizationGroupKey();

    TestNormalizedEntity ret;

    if (map.containsKey(thisId)) {
      ret = map.get(thisId);
    } else {
      ret = new TestNormalizedEntity(thisId);
      map.put(thisId, ret);
    }

    if (names != null && names.length > 0) {
      for (String x : names) {
        ret.addEntry(new TestNormalizedEntry(thisId, x));
      }
    }

    return ret;
  }

}
