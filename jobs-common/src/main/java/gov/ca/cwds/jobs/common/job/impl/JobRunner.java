package gov.ca.cwds.jobs.common.job.impl;

import com.google.inject.Guice;
import gov.ca.cwds.jobs.common.inject.AbstractBaseJobModule;
import gov.ca.cwds.jobs.common.job.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CWDS TPT-2
 */
public abstract class JobRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobRunner.class);

  public static void run(AbstractBaseJobModule mainModule) {
    Job job = null;
    try {
      LOGGER.info("Job has been started");
      job = Guice.createInjector(mainModule).getInstance(Job.class);
      job.run();
      LOGGER.info("Job has been finished");
    } finally {
      if (job != null) {
        job.close();
      }
    }
  }

}
