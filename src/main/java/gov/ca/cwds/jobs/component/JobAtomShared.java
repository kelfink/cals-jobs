package gov.ca.cwds.jobs.component;

import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.jobs.config.JobOptions;

public interface JobAtomShared extends ApiMarker {

  JobProgressTrack getTrack();

  ElasticsearchDao getEsDao();

  /**
   * Getter for the job's command line options.
   * 
   * @return this job's options
   */
  JobOptions getOpts();

  default void nameThread(String title) {
    Thread.currentThread().setName(title);
  }

}
