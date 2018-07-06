package gov.ca.cwds.jobs.cals.facility.lisfas.inject;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import gov.ca.cwds.DataSourceName;
import gov.ca.cwds.cals.inject.LisSessionFactory;
import gov.ca.cwds.cals.persistence.dao.lis.LisFacFileLisDao;
import gov.ca.cwds.cals.persistence.dao.lis.LisTableFileDao;
import gov.ca.cwds.cals.persistence.model.lisfas.LisDoFile;
import gov.ca.cwds.cals.persistence.model.lisfas.LisFacFile;
import gov.ca.cwds.cals.persistence.model.lisfas.LisTableFile;
import gov.ca.cwds.jobs.cals.facility.lisfas.LisFacilityJobConfiguration;
import gov.ca.cwds.jobs.cals.facility.lisfas.dao.LicenseNumberIdentifierDao;
import gov.ca.cwds.jobs.cals.facility.lisfas.identifier.LicenseNumberIdentifier;
import gov.ca.cwds.jobs.cals.facility.lisfas.identifier.LisTimestampIdentifier;
import gov.ca.cwds.jobs.common.util.SessionFactoryUtil;
import org.hibernate.SessionFactory;

/**
 * @author CWDS TPT-2
 */
public class LisDataAccessModule extends AbstractModule {

  public static final ImmutableList<Class<?>> lisEntityClasses =
      ImmutableList.<Class<?>>builder().add(
          LicenseNumberIdentifier.class,
          LisTimestampIdentifier.class,
          LisFacFile.class,
          LisTableFile.class,
          LisDoFile.class
      ).build();

  @Override
  protected void configure() {
    bind(SessionFactory.class).annotatedWith(LisSessionFactory.class)
        .toProvider(LisSessionFactoryProvider.class).in(Singleton.class);

    bind(LicenseNumberIdentifierDao.class);
    bind(LisFacFileLisDao.class);
    bind(LisTableFileDao.class);

  }

  private static class LisSessionFactoryProvider implements Provider<SessionFactory> {

    @Inject
    private LisFacilityJobConfiguration facilityJobConfiguration;

    @Override
    public SessionFactory get() {
      return SessionFactoryUtil
          .buildSessionFactory(facilityJobConfiguration.getLisDataSourceFactory(),
              DataSourceName.LIS.name(), lisEntityClasses);
    }

  }

}
