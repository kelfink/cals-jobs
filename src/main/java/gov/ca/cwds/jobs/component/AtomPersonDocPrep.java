package gov.ca.cwds.jobs.component;

import java.util.ArrayList;
import java.util.List;

import gov.ca.cwds.data.ApiTypedIdentifier;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPerson.ESOptionalCollection;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.data.std.ApiPersonAware;
import gov.ca.cwds.jobs.util.transform.ElasticTransformer;

public interface AtomPersonDocPrep<T extends PersistentObject> extends ApiMarker {

  static final String ES_PEOPLE_INDEX_SETTINGS =
      "/elasticsearch/setting/people-index-settings.json";
  static final String ES_PERSON_MAPPING = "/elasticsearch/mapping/map_person_5x_snake.json";

  static final ESOptionalCollection[] KEEP_COLLECTIONS =
      new ESOptionalCollection[] {ESOptionalCollection.NONE};

  static final List<? extends ApiTypedIdentifier<String>> EMPTY_OPTIONAL_LIST = new ArrayList<>();

  /**
   * Set optional ES person collections before serializing JSON for insert. Child classes which
   * handle optional collections should override this method.
   *
   * <p>
   * <strong>Example:</strong>
   * </p>
   * 
   * <pre>
   * {@code esp.setScreenings((List<ElasticSearchPerson.ElasticSearchPersonScreening>) col);}
   * </pre>
   * 
   * @param esp ES document, already prepared by
   *        {@link ElasticTransformer#buildElasticSearchPersonDoc(ApiPersonAware)}
   * @param t target ApiPersonAware instance
   * @param list list of ES child objects
   */
  default void setInsertCollections(ElasticSearchPerson esp, T t,
      List<? extends ApiTypedIdentifier<String>> list) {
    // Default, no-op.
  }

  /**
   * Get the optional element name populated by this job or null if none.
   * 
   * @return optional element name
   */
  default String getOptionalElementName() {
    return null;
  }

  /**
   * Which optional ES collections to retain for insert JSON. Child classes that populate optional
   * collections should override this method.
   * 
   * @return array of optional collections to keep in insert JSON
   */
  default ESOptionalCollection[] keepCollections() {
    return KEEP_COLLECTIONS;
  }

  /**
   * Return the optional collection used to build the update JSON, if any. Child classes that
   * populate optional collections should override this method.
   * 
   * @param esp ES person document object
   * @param t normalized type
   * @return List of ES person elements
   */
  default List<? extends ApiTypedIdentifier<String>> getOptionalCollection(ElasticSearchPerson esp,
      T t) {
    return EMPTY_OPTIONAL_LIST;
  }

}
