package gov.ca.cwds.jobs.cals.inject;

import com.google.common.collect.ImmutableList;
import gov.ca.cwds.cals.inject.FasSessionFactory;
import gov.ca.cwds.cals.persistence.dao.fas.ComplaintReportLic802Dao;
import gov.ca.cwds.cals.persistence.dao.fas.FacilityInformationDao;
import gov.ca.cwds.cals.persistence.dao.fas.InspectionDao;
import gov.ca.cwds.cals.persistence.dao.fas.LpaInformationDao;
import gov.ca.cwds.cals.persistence.model.fas.ComplaintReportLic802;
import gov.ca.cwds.cals.persistence.model.fas.FacilityInformation;
import gov.ca.cwds.cals.persistence.model.fas.LpaInformation;
import gov.ca.cwds.cals.persistence.model.fas.Rr809Dn;
import gov.ca.cwds.cals.persistence.model.fas.Rrcpoc;
import gov.ca.cwds.jobs.cals.facility.RecordChange;
import gov.ca.cwds.jobs.common.inject.JobsDataAccessModule;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.SessionFactory;

/**
 * @author CWDS TPT-2
 */
public class FasDataAccessModule extends JobsDataAccessModule {

  public static final ImmutableList<Class<?>> fasEntityClasses = ImmutableList.<Class<?>>builder().add(
          RecordChange.class,
          FacilityInformation.class,
          ComplaintReportLic802.class,
          LpaInformation.class,
          Rrcpoc.class,
          Rr809Dn.class
  ).build();

  public FasDataAccessModule(DataSourceFactory dataSourceFactory, String dataSourceName) {
    super(dataSourceFactory, dataSourceName);
  }

  @Override
  protected ImmutableList<Class<?>> getEntityClasses() {
    return fasEntityClasses;
  }

  @Override
  protected void configure() {
    super.configure();
    bind(SessionFactory.class).annotatedWith(FasSessionFactory.class).toInstance(getSessionFactory());

    // schema: fas
    bind(FacilityInformationDao.class);
    bind(ComplaintReportLic802Dao.class);
    bind(LpaInformationDao.class);
    bind(InspectionDao.class);
  }
}
