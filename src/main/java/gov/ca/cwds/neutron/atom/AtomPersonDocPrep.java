package gov.ca.cwds.neutron.atom;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;

import gov.ca.cwds.data.ApiTypedIdentifier;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPerson.ESOptionalCollection;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.data.std.ApiPersonAware;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.neutron.jetpack.JobLogs;
import gov.ca.cwds.neutron.util.transform.ElasticTransformer;
import gov.ca.cwds.rest.ElasticsearchConfiguration;

public interface AtomPersonDocPrep<T extends PersistentObject> extends ApiMarker, AtomShared {

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
    return new ESOptionalCollection[] {ESOptionalCollection.NONE};
  }

  /**
   * Return the optional collection used to build the update JSON, if any. Child classes that
   * populate optional collections should override this method.
   * 
   * @param esp ES person document object
   * @param t normalized type
   * @return List of ES person elements
   */
  default List<ApiTypedIdentifier<String>> getOptionalCollection(ElasticSearchPerson esp, T t) {
    return new ArrayList<>();
  }

  /**
   * Prepare an upsert request.
   * 
   * @param esp ES person
   * @param p normalized person doc
   * @param elements elements to send
   * @param updateOnly update only, no upsert (i.e., don't create a new document)
   * @return upsert request
   * @throws NeutronException general error
   * @param <E> Element type
   */
  default <E> UpdateRequest prepareUpdateRequest(ElasticSearchPerson esp, T p, List<E> elements,
      boolean updateOnly) throws NeutronException {
    final StringBuilder buf = new StringBuilder();
    String insertJson;
    try {
      buf.append("{\"").append(getOptionalElementName()).append("\":[");

      if (!elements.isEmpty()) {
        buf.append(elements.stream().map(ElasticTransformer::jsonify).sorted(String::compareTo)
            .collect(Collectors.joining(",")));
      }

      buf.append("]}");
      insertJson = getMapper().writeValueAsString(esp);
    } catch (Exception e) {
      throw JobLogs.checked(getLogger(), e, "NESTED ELEMENT ERROR! {}", e.getMessage());
    }

    final String updateJson = buf.toString();
    final String id = esp.getId();
    final ElasticsearchConfiguration config = getEsDao().getConfig();
    final String alias = config.getElasticsearchAlias();
    final String docType = config.getElasticsearchDocType();
    final UpdateRequest ur =
        new UpdateRequest(alias, docType, id).doc(updateJson, XContentType.JSON);

    return updateOnly ? ur
        : ur.upsert(new IndexRequest(alias, docType, id).source(insertJson, XContentType.JSON));
  }

  /**
   * Prepare an upsert request.
   * 
   * @param esp ES person
   * @param p normalized person doc
   * @param elements elements to send
   * @return upsert request
   * @throws NeutronException general error
   * @param <E> Element type
   */
  default <E> UpdateRequest prepareUpsertRequest(ElasticSearchPerson esp, T p, List<E> elements)
      throws NeutronException {
    return prepareUpdateRequest(esp, p, elements, false);
  }

}
