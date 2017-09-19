package gov.ca.cwds.jobs.test;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import gov.ca.cwds.dao.ApiMultiplePersonAware;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.data.std.ApiPersonAware;

/**
 * Denormalized
 */
@JsonPropertyOrder(alphabetic = true)
@Entity
@Table(name = "VW_WHATEVER")
@NamedNativeQueries({
    @NamedNativeQuery(name = "gov.ca.cwds.jobs.test.TestDenormalizedEntity.findAllUpdatedAfter",
        query = "WITH driver as ( "
            + " SELECT v1.THIS_LEGACY_ID, v1.RELATED_LEGACY_ID FROM {h-schema}VW_LST_BI_DIR_RELATION v1 "
            + "where v1.LAST_CHG > CAST(:after AS TIMESTAMP) "
            + ") SELECT v.REVERSE_RELATIONSHIP, v.THIS_LEGACY_ID, v.THIS_SENSITIVITY_IND, "
            + "v.THIS_LEGACY_LAST_UPDATED, v.THIS_LEGACY_LAST_UPDATED_ID, v.THIS_FIRST_NAME, v.THIS_LAST_NAME, "
            + "v.REL_CODE, v.RELATED_LEGACY_ID, v.RELATED_SENSITIVITY_IND, "
            + "v.RELATED_LEGACY_LAST_UPDATED, v.RELATED_LEGACY_LAST_UPDATED_ID, "
            + "v.RELATED_FIRST_NAME, v.RELATED_LAST_NAME, "
            + "v.THIS_IBMSNAP_LOGMARKER, v.THIS_IBMSNAP_OPERATION, "
            + "v.RELATED_IBMSNAP_LOGMARKER, v.RELATED_IBMSNAP_OPERATION, v.LAST_CHG "
            + "FROM {h-schema}VW_LST_BI_DIR_RELATION v "
            + "WHERE v.THIS_LEGACY_ID IN (select d1.THIS_LEGACY_ID from driver d1) "
            + "OR v.RELATED_LEGACY_ID IN (select d2.RELATED_LEGACY_ID from driver d2) "
            + "ORDER BY THIS_LEGACY_ID, RELATED_LEGACY_ID FOR READ ONLY WITH UR ",
        resultClass = TestDenormalizedEntity.class, readOnly = true),
    @NamedNativeQuery(
        name = "gov.ca.cwds.jobs.test.TestDenormalizedEntity.findAllUpdatedAfterWithUnlimitedAccess",
        query = "WITH driver as ( "
            + " SELECT v1.THIS_LEGACY_ID, v1.RELATED_LEGACY_ID FROM {h-schema}VW_LST_BI_DIR_RELATION v1 "
            + "where v1.LAST_CHG > CAST(:after AS TIMESTAMP) "
            + ") SELECT v.REVERSE_RELATIONSHIP, v.THIS_LEGACY_ID, v.THIS_SENSITIVITY_IND, "
            + "v.THIS_LEGACY_LAST_UPDATED, v.THIS_LEGACY_LAST_UPDATED_ID, v.THIS_FIRST_NAME, v.THIS_LAST_NAME, "
            + "v.REL_CODE, v.RELATED_LEGACY_ID, v.RELATED_SENSITIVITY_IND, "
            + "v.RELATED_LEGACY_LAST_UPDATED, v.RELATED_LEGACY_LAST_UPDATED_ID, "
            + "v.RELATED_FIRST_NAME, v.RELATED_LAST_NAME, "
            + "v.THIS_IBMSNAP_LOGMARKER, v.THIS_IBMSNAP_OPERATION, "
            + "v.RELATED_IBMSNAP_LOGMARKER, v.RELATED_IBMSNAP_OPERATION, v.LAST_CHG "
            + "FROM {h-schema}VW_LST_BI_DIR_RELATION v "
            + "WHERE (v.THIS_LEGACY_ID IN (select d1.THIS_LEGACY_ID from driver d1) "
            + "OR v.RELATED_LEGACY_ID IN (select d2.RELATED_LEGACY_ID from driver d2)) "
            + "AND (v.THIS_SENSITIVITY_IND = 'N' AND v.RELATED_SENSITIVITY_IND = 'N') "
            + "ORDER BY THIS_LEGACY_ID, RELATED_LEGACY_ID FOR READ ONLY WITH UR ",
        resultClass = TestDenormalizedEntity.class, readOnly = true),
    @NamedNativeQuery(name = "gov.ca.cwds.jobs.test.TestDenormalizedEntity.findBucketRange",
        query = "WITH driver as ( "
            + " SELECT v1.THIS_LEGACY_ID, v1.RELATED_LEGACY_ID FROM {h-schema}VW_LST_BI_DIR_RELATION v1 "
            + "where v1.LAST_CHG > CAST(:after AS TIMESTAMP) "
            + ") SELECT v.REVERSE_RELATIONSHIP, v.THIS_LEGACY_ID, v.THIS_SENSITIVITY_IND, "
            + "v.THIS_LEGACY_LAST_UPDATED, v.THIS_LEGACY_LAST_UPDATED_ID, v.THIS_FIRST_NAME, v.THIS_LAST_NAME, "
            + "v.REL_CODE, v.RELATED_LEGACY_ID, v.RELATED_SENSITIVITY_IND, "
            + "v.RELATED_LEGACY_LAST_UPDATED, v.RELATED_LEGACY_LAST_UPDATED_ID, "
            + "v.RELATED_FIRST_NAME, v.RELATED_LAST_NAME, "
            + "v.THIS_IBMSNAP_LOGMARKER, v.THIS_IBMSNAP_OPERATION, "
            + "v.RELATED_IBMSNAP_LOGMARKER, v.RELATED_IBMSNAP_OPERATION, v.LAST_CHG "
            + "FROM {h-schema}VW_LST_BI_DIR_RELATION v "
            + "WHERE (v.THIS_LEGACY_ID IN (select d1.THIS_LEGACY_ID from driver d1) "
            + "OR v.RELATED_LEGACY_ID IN (select d2.RELATED_LEGACY_ID from driver d2)) "
            + "AND (v.THIS_SENSITIVITY_IND = 'N' AND v.RELATED_SENSITIVITY_IND = 'N') "
            + "ORDER BY THIS_LEGACY_ID, RELATED_LEGACY_ID FOR READ ONLY WITH UR ",
        resultClass = TestDenormalizedEntity.class, readOnly = true)})
public class TestDenormalizedEntity
    implements PersistentObject, ApiGroupNormalizer<TestNormalizedEntity>, ApiMultiplePersonAware {

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

  @Override
  public Serializable getPrimaryKey() {
    return "abc1234567";
  }

  @Override
  public ApiPersonAware[] getPersons() {
    final ApiPersonAware[] ret = {new TestOnlyApiPersonAware()};
    return ret;
  }

}
