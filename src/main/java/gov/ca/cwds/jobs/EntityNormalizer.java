package gov.ca.cwds.jobs;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;

/**
 * Normalize ("reduce") denormalized type M to normalized type T.
 * 
 * @author CWDS API Team
 */
public class EntityNormalizer {

  private EntityNormalizer() {
    // Util class. Class static methods.
  }

  /**
   * Take a List of denormalized objects from a view and transform them into a normalized object.
   * 
   * @param denormalized denormalized records
   * @return List of normalized objects
   * @param <T> ES storable, replicated Person persistence class
   * @param <M> MQT entity class, if any, or T
   */
  public static <T extends PersistentObject, M extends ApiGroupNormalizer<T>> List<T> reduceList(
      List<M> denormalized) {
    // The "transform" step usually runs in the same thread.
    // Therefore, you *could* safely reuse the same map object.
    final int len = (int) (denormalized.size() * 1.25);
    Map<Object, T> map = new LinkedHashMap<>(len);
    for (ApiGroupNormalizer<T> reducer : denormalized) {
      reducer.reduce(map);
    }

    return map.values().stream().collect(Collectors.toList());
  }

}
