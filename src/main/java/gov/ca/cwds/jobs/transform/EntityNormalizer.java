package gov.ca.cwds.jobs.transform;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;

/**
 * Transform a List of denormalized objects from a view and transform them into a normalized object.
 * Normalize ("reduce") denormalized type M to normalized type T.
 * 
 * @author CWDS API Team
 */
public class EntityNormalizer {

  private EntityNormalizer() {
    // Util class. Class static methods.
  }

  /**
   * Transform a List of denormalized objects from a view and transform them into a normalized
   * object. Normalize ("reduce") denormalized type M to normalized type T.
   * 
   * <p>
   * The "transform" step usually runs in a single thread. Therefore, most of the time, one can
   * safely reuse the same map object. Alternatively, a concurrent map implementation would likewise
   * alleviate concerns of thread safety.
   * </p>
   * 
   * @param denormalized denormalized records
   * @return List of normalized objects
   * @param <T> ES storable, replicated Person persistence class
   * @param <M> MQT entity class, if any, or T
   */
  public static <T extends PersistentObject, M extends ApiGroupNormalizer<T>> List<T> reduceList(
      List<M> denormalized) {
    final int len = (int) (denormalized.size() * 1.25);
    final Map<Object, T> map = new LinkedHashMap<>(len);

    // In order to stream to map(), method reduce() must return the normalized object.
    // denormalized.stream().map(r -> r.reduce(map));

    for (ApiGroupNormalizer<T> reducer : denormalized) {
      reducer.reduce(map);
    }

    return map.values().stream().collect(Collectors.toList());
  }

}
