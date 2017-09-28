package gov.ca.cwds.jobs.util.transform;

import java.util.List;

import gov.ca.cwds.data.ApiTypedIdentifier;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.data.std.ApiPersonAware;

public interface JobElasticPersonDocPrep<T extends PersistentObject> extends ApiMarker {

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

}
