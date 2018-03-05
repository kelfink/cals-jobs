package gov.ca.cwds.jobs.common;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import gov.ca.cwds.jobs.common.config.JobOptions;
import gov.ca.cwds.jobs.common.exception.JobExceptionHandler;
import gov.ca.cwds.jobs.common.job.Job;
import gov.ca.cwds.jobs.common.job.timestamp.TimestampOperator;
import gov.ca.cwds.jobs.common.job.utils.ConsumerCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * @author CWDS TPT-2
 */
public abstract class BaseIndexerJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseIndexerJob.class);

  protected abstract AbstractModule getJobModule(JobOptions jobOptions);

  public void run(String[] args) {
    Injector injector = null;
    try {
      final JobOptions jobOptions = JobOptions.parseCommandLine(args);
      injector = Guice.createInjector(getJobModule(jobOptions));
      injector.getInstance(Job.class).run();
      if (!JobExceptionHandler.isExceptionHappened()) {
        injector.getInstance(TimestampOperator.class).writeTimestamp(LocalDateTime.now());
      }
      LOGGER.info(String.format("Added %s entities to ES bulk uploader", ConsumerCounter.getCounter()));
    } catch (RuntimeException e) {
      LOGGER.error("ERROR: ", e.getMessage(), e);
      System.exit(1);
    } finally {
      JobExceptionHandler.reset();
      close(injector);
    }
  }

  protected void close(Injector inject) {
    //empty by default
  }

}
