package gov.ca.cwds.jobs.component;

import org.slf4j.Logger;

import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.jobs.config.JobOptions;

/**
 * Common features of all Elasticsearch indexing jobs.
 * 
 * @author CWDS API Team
 */
public interface AtomShared extends ApiMarker {

  /**
   * @return job's progress tracker
   */
  JobProgressTrack getTrack();

  /**
   * @return Elasticsearch DAO
   */
  ElasticsearchDao getEsDao();

  Logger getLogger();

  /**
   * Getter for the job's command line options.
   * 
   * @return this job's options
   */
  JobOptions getOpts();

  default void nameThread(final String title) {
    Thread.currentThread().setName(title);
  }

}
