package gov.ca.cwds.jobs.util.elastic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.dao.ApiMultiplePersonAware;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiPersonAware;
import gov.ca.cwds.jobs.util.transform.ElasticTransformer;

public class JobElasticUtils {

  private static ObjectMapper mapper;

  private JobElasticUtils() {
    // Default, no-op.
  }

  // ===================
  // BUILD ES PERSON:
  // ===================

  /**
   * Handle both {@link ApiMultiplePersonAware} and {@link ApiPersonAware} implementations of type
   * T.
   * 
   * @param p instance of type T
   * @return array of person documents
   * @throws JsonProcessingException on parse error
   */
  public static ElasticSearchPerson[] buildElasticSearchPersons(final PersistentObject p)
      throws JsonProcessingException {
    ElasticSearchPerson[] ret;
    if (p instanceof ApiMultiplePersonAware) {
      final ApiPersonAware[] persons = ((ApiMultiplePersonAware) p).getPersons();
      ret = new ElasticSearchPerson[persons.length];
      int i = 0;
      for (ApiPersonAware px : persons) {
        ret[i++] = buildElasticSearchPersonDoc(px);
      }
    } else {
      ret = new ElasticSearchPerson[] {buildElasticSearchPerson((ApiPersonAware) p)};
    }
    return ret;
  }

  /**
   * Produce an ElasticSearchPerson suitable as an Elasticsearch person document.
   * 
   * @param p ApiPersonAware persistence object
   * @return populated ElasticSearchPerson
   * @throws JsonProcessingException if unable to serialize JSON
   */
  public static ElasticSearchPerson buildElasticSearchPerson(ApiPersonAware p)
      throws JsonProcessingException {
    return buildElasticSearchPersonDoc(p);
  }

  /**
   * Produce an ElasticSearchPerson objects suitable for an Elasticsearch person document.
   * 
   * @param p ApiPersonAware persistence object
   * @return populated ElasticSearchPerson
   * @throws JsonProcessingException if unable to serialize JSON
   */
  public static ElasticSearchPerson buildElasticSearchPersonDoc(ApiPersonAware p)
      throws JsonProcessingException {
    return ElasticTransformer.buildElasticSearchPersonDoc(mapper, p);
  }

  public static ObjectMapper getMapper() { // NOSONAR
    return mapper;
  }

  public static synchronized void setMapper(final ObjectMapper mapper) {
    if (JobElasticUtils.mapper == null) {
      JobElasticUtils.mapper = mapper;
    }
  }

}
