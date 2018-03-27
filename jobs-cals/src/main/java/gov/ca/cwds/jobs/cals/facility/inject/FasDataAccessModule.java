package gov.ca.cwds.jobs.cals.facility.inject;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import gov.ca.cwds.DataSourceName;
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
import gov.ca.cwds.jobs.cals.facility.FacilityJobConfiguration;
import gov.ca.cwds.jobs.cals.facility.recordchange.RecordChange;
import gov.ca.cwds.jobs.common.util.SessionFactoryUtil;
import org.hibernate.SessionFactory;

/**
 * @author CWDS TPT-2
 */
public class FasDataAccessModule extends AbstractModule {

  public static final ImmutableList<Class<?>> fasEntityClasses = ImmutableList.<Class<?>>builder()
      .add(
          RecordChange.class,
          FacilityInformation.class,
          ComplaintReportLic802.class,
          LpaInformation.class,
          Rrcpoc.class,
          Rr809Dn.class
      ).build();

  @Override
  protected void configure() {
    bind(SessionFactory.class).annotatedWith(FasSessionFactory.class)
        .toProvider(FasSessionFactoryProvider.class).in(Singleton.class);
    bind(FacilityInformationDao.class);
    bind(ComplaintReportLic802Dao.class);
    bind(LpaInformationDao.class);
    bind(InspectionDao.class);
  }

  private static class FasSessionFactoryProvider implements Provider<SessionFactory> {

    @Inject
    private FacilityJobConfiguration facilityJobConfiguration;

    @Override
    public SessionFactory get() {
      return SessionFactoryUtil
          .buildSessionFactory(facilityJobConfiguration.getFasDataSourceFactory(),
              DataSourceName.FAS.name(), fasEntityClasses);
    }

  }

}
