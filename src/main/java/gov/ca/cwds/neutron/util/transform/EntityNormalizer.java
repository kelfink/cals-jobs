package gov.ca.cwds.neutron.util.transform;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;

/**
 * Transform a List of denormalized objects from a view and transform them into a normalized object.
 * Normalize ("reduce") denormalized type D to normalized type N.
 * 
 * @author CWDS API Team
 */
public final class EntityNormalizer {

  /**
   * Utility class, static methods only.
   */
  private EntityNormalizer() {
    // Utility class.
  }

  /**
   * Transform a List of denormalized objects from a view and transform them into a normalized
   * object. Normalize a denormalized type D to normalized type N.
   * 
   * <p>
   * The "transform" step usually runs in a single thread. Therefore, most of the time, one can
   * safely reuse the same map object. Alternatively, a concurrent map implementation would likewise
   * alleviate concerns of thread safety.
   * </p>
   * 
   * @param denormalized denormalized records
   * @return List of normalized objects
   * @param <N> normalized, storable in Elasticsearch, Person persistence class
   * @param <D> MQT entity class, if any, or N
   */
  public static <N extends PersistentObject, D extends ApiGroupNormalizer<N>> List<N> normalizeList(
      List<D> denormalized) {
    final Map<Object, N> m = new LinkedHashMap<>();
    return denormalized.stream().map(d -> d.normalize(m)).collect(Collectors.toList());
  }

}
