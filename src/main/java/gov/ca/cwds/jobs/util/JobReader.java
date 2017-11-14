package gov.ca.cwds.jobs.util;

import gov.ca.cwds.jobs.exception.NeutronException;

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
   * @throws NeutronException on read error
   */
  T read() throws NeutronException;

}
