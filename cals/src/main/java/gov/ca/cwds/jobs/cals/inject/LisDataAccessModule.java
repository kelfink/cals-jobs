package gov.ca.cwds.jobs.cals.inject;

import gov.ca.cwds.cals.inject.LisSessionFactory;
import gov.ca.cwds.cals.persistence.dao.lis.LisFacFileLisDao;
import gov.ca.cwds.cals.persistence.dao.lis.LisTableFileDao;
import gov.ca.cwds.cals.persistence.dao.lis.RecordChangeLisDao;
import gov.ca.cwds.cals.persistence.model.RecordChange;
import gov.ca.cwds.cals.persistence.model.lisfas.LisDoFile;
import gov.ca.cwds.cals.persistence.model.lisfas.LisFacFile;
import gov.ca.cwds.cals.persistence.model.lisfas.LisTableFile;
import gov.ca.cwds.generic.jobs.inject.JobsDataAccessModule;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * @author CWDS TPT-2
 */
public class LisDataAccessModule extends JobsDataAccessModule {

  public LisDataAccessModule(DataSourceFactory dataSourceFactory, String dataSourceName) {
    super(dataSourceFactory, dataSourceName);
  }

  @Override
  protected void addEntityClasses(Configuration configuration) {
    configuration
            .addAnnotatedClass(RecordChange.class)
            .addAnnotatedClass(LisFacFile.class)
            .addAnnotatedClass(LisTableFile.class)
            .addAnnotatedClass(LisDoFile.class);
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
