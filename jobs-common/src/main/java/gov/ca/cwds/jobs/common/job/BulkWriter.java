package gov.ca.cwds.jobs.common.job;

import java.util.List;

/**
 * @param <T> type to write
 * @author CWDS TPT-2
 */
@FunctionalInterface
public interface BulkWriter<T> {

  void write(List<T> items);

  default void destroy() {
  }

  ;
}
