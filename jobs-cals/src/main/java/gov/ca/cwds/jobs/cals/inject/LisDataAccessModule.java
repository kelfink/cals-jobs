package gov.ca.cwds.jobs.cals.inject;

import com.google.common.collect.ImmutableList;
import gov.ca.cwds.cals.inject.LisSessionFactory;
import gov.ca.cwds.cals.persistence.dao.lis.LisFacFileLisDao;
import gov.ca.cwds.cals.persistence.dao.lis.LisTableFileDao;
import gov.ca.cwds.cals.persistence.model.lisfas.LisDoFile;
import gov.ca.cwds.cals.persistence.model.lisfas.LisFacFile;
import gov.ca.cwds.cals.persistence.model.lisfas.LisTableFile;
import gov.ca.cwds.jobs.common.inject.JobsDataAccessModule;
import gov.ca.cwds.jobs.cals.facility.RecordChange;
import gov.ca.cwds.jobs.cals.facility.RecordChangeLisDao;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.SessionFactory;

/**
 * @author CWDS TPT-2
 */
public class LisDataAccessModule extends JobsDataAccessModule {

  public static final ImmutableList<Class<?>> lisEntityClasses =
          ImmutableList.<Class<?>>builder().add(
                  RecordChange.class,
                  LisFacFile.class,
                  LisTableFile.class,
                  LisDoFile.class
          ).build();

  public LisDataAccessModule(DataSourceFactory dataSourceFactory, String dataSourceName) {
    super(dataSourceFactory, dataSourceName);
  }

  @Override
  protected ImmutableList<Class<?>> getEntityClasses() {
    return lisEntityClasses;
  }

  @Override
  protected void configure() {
    super.configure();
    bind(SessionFactory.class).annotatedWith(LisSessionFactory.class).toInstance(getSessionFactory());

    // schema: lis
    bind(RecordChangeLisDao.class);
    bind(LisFacFileLisDao.class);
    bind(LisTableFileDao.class);
  }
}
