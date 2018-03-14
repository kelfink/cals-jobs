package gov.ca.cwds.jobs.common.job.impl;

import com.google.inject.Guice;
import com.google.inject.Injector;
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
    LOGGER.info("Job has been started");
    Injector injector = Guice.createInjector(mainModule);
    injector.getInstance(Job.class).run();
    LOGGER.info("Job has been finished");
  }

}
