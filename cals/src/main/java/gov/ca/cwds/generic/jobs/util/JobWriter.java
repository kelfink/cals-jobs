package gov.ca.cwds.generic.jobs.util;

import java.util.List;

/**
 * @author CWDS TPT-2
 * @param <T> type to write
 */
@FunctionalInterface
public interface JobWriter<T> extends JobComponent {

  void write(List<T> items);
}
