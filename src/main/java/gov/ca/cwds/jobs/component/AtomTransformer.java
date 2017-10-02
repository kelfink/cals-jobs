package gov.ca.cwds.jobs.component;

import java.util.List;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.jobs.util.JobLogs;
import gov.ca.cwds.jobs.util.transform.EntityNormalizer;

/**
 * Normalization features for Elasticsearch indexing jobs.
 * 
 * @author CWDS API Team
 * @param <T> normalized type
 * @param <M> de-normalized type
 */
public interface AtomTransformer<T extends PersistentObject, M extends ApiGroupNormalizer<?>>
    extends AtomShared {

  /**
   * Transform (normalize) in the Job instead of relying on the transformation thread.
   * 
   * @return true if the transformer thread should run
   */
  default boolean useTransformThread() {
    return true;
  }

  /**
   * Return the job's entity class for its de-normalized source view or materialized query table, if
   * any, or null if not using a de-normalized source.
   * 
   * @return entity class of view or materialized query table
   */
  default Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    return null;
  }

  /**
   * True if the Job class reduces de-normalized results to normalized ones.
   * 
   * @return true if class overrides {@link #normalize(List)}
   */
  default boolean isViewNormalizer() {
    return getDenormalizedClass() != null;
  }

  /**
   * Default normalize method just returns the input. Child classes may customize this method to
   * normalize de-normalized result sets (view records) to normalized entities (parent/child)
   * records.
   * 
   * @param recs entity records
   * @return unmodified entity records
   * @see EntityNormalizer
   */
  @SuppressWarnings("unchecked")
  default List<T> normalize(List<M> recs) {
    return (List<T>) recs;
  }

  default void incrementNormalizeCount() {
    JobLogs.logEvery(getTrack().trackNormalized(), "Normalize", "single");
  }

  /**
   * Normalize view records for a single grouping (such as all the same client) into a normalized
   * entity bean, consisting of a parent object and its child objects.
   * 
   * @param recs de-normalized view beans
   * @return normalized entity bean instance
   */
  default T normalizeSingle(List<M> recs) {
    incrementNormalizeCount();
    final List<T> list = normalize(recs);
    return list != null && !list.isEmpty() ? list.get(0) : null;
  }

}
