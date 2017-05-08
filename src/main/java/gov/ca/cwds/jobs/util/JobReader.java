package gov.ca.cwds.jobs.util;

/**
 * Created by dmitry.rudenko on 4/28/2017.
 * 
 * @param <T> type to read into
 */
public interface JobReader<T> extends JobComponent {

  /**
   *
   * @return extracted object, MUST return null when done
   * @throws Exception
   */
  T read() throws Exception;

}
