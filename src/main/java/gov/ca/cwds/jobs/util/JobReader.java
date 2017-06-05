package gov.ca.cwds.jobs.util;

/**
 * @author CWDS Elasticsearch Team
 * 
 * @param <T> type to read into
 */
public interface JobReader<T> extends JobComponent {

  /**
   * Read source into specified type. MUST return null, if underlying source/stream is finished.
   * 
   * @return extracted object, MUST return null when done
   * @throws Exception if unable to read from source
   */
  T read() throws Exception;

}
