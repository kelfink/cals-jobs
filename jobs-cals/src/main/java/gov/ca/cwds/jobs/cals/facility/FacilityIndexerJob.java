package gov.ca.cwds.jobs.cals.facility;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import gov.ca.cwds.cals.inject.FasSessionFactory;
import gov.ca.cwds.cals.inject.LisSessionFactory;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.cals.inject.FacilityBaseJobModule;
import gov.ca.cwds.jobs.common.BaseIndexerJob;
import gov.ca.cwds.jobs.common.config.JobOptions;
import gov.ca.cwds.jobs.common.job.utils.ConsumerCounter;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;

/**
 * @author CWDS TPT-2
 */
public final class FacilityIndexerJob extends BaseIndexerJob {

  private static final Logger LOG = LoggerFactory.getLogger(FacilityIndexerJob.class);

  public static void main(String[] args) {
    FacilityIndexerJob facilityIndexerJob = new FacilityIndexerJob();
    facilityIndexerJob.run(args);
    LOG.info(String.format("Added %s facilities to ES bulk uploader", ConsumerCounter.getCounter()));
  }

  @Override
  protected AbstractModule getJobModule(JobOptions jobOptions) {
    return new FacilityBaseJobModule(jobOptions);
  }

  @Override
  protected void close(Injector injector) {
    closeSessionFactory(injector, FasSessionFactory.class);
    closeSessionFactory(injector, CmsSessionFactory.class);
    closeSessionFactory(injector, LisSessionFactory.class);
  }

  private void closeSessionFactory(Injector injector, Class<? extends Annotation> clazz) {
    SessionFactory sessionFactory = injector.getInstance(Key.get(SessionFactory.class, clazz));
    sessionFactory.close();
  }

}
