package gov.ca.cwds.jobs.cals.inject;

import gov.ca.cwds.cals.inject.FasSessionFactory;
import gov.ca.cwds.cals.persistence.dao.fas.*;
import gov.ca.cwds.cals.persistence.model.RecordChange;
import gov.ca.cwds.cals.persistence.model.fas.*;
import gov.ca.cwds.generic.jobs.inject.JobsDataAccessModule;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * @author CWDS TPT-2
 */
public class FasDataAccessModule extends JobsDataAccessModule {

  public FasDataAccessModule(DataSourceFactory dataSourceFactory, String dataSourceName) {
    super(dataSourceFactory, dataSourceName);
  }

  @Override
  protected void addEntityClasses(Configuration configuration) {
    configuration
            .addAnnotatedClass(RecordChange.class)
            .addAnnotatedClass(FacilityInformation.class)
            .addAnnotatedClass(ComplaintReportLic802.class)
            .addAnnotatedClass(LpaInformation.class)
            .addAnnotatedClass(Rrcpoc.class)
            .addAnnotatedClass(Rr809Dn.class);
  }

  @Override
  protected void configure() {
    super.configure();
    bind(SessionFactory.class).annotatedWith(FasSessionFactory.class).toInstance(getSessionFactory());

    // schema: fas
    bind(RecordChangeFasDao.class);
    bind(FacilityInformationDao.class);
    bind(ComplaintReportLic802Dao.class);
    bind(LpaInformationDao.class);
    bind(InspectionDao.class);
  }
}
