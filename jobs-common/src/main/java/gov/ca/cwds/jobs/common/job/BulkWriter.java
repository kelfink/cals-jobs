package gov.ca.cwds.jobs.common.job;

import java.util.List;

/**
 * @author CWDS TPT-2
 * @param <T> type to write
 */
@FunctionalInterface
public interface BulkWriter<T> {

  void write(List<T> items);

  default void destroy() {};
}
