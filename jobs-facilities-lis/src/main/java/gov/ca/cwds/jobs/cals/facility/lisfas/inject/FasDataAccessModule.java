package gov.ca.cwds.jobs.cals.facility.lisfas.inject;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import gov.ca.cwds.DataSourceName;
import gov.ca.cwds.cals.inject.FasFfaSessionFactory;
import gov.ca.cwds.cals.inject.FasSessionFactory;
import gov.ca.cwds.cals.persistence.model.fas.LpaInformation;
import gov.ca.cwds.jobs.cals.facility.lisfas.LisFacilityJobConfiguration;
import gov.ca.cwds.jobs.common.util.SessionFactoryUtil;
import org.hibernate.SessionFactory;

/**
 * @author CWDS TPT-2
 */
public class FasDataAccessModule extends AbstractModule {

  public static final ImmutableList<Class<?>> fasEntityClasses = ImmutableList.<Class<?>>builder()
      .add(
//          RecordChange.class,
          LpaInformation.class
      ).build();

  @Override
  protected void configure() {
    bind(SessionFactory.class).annotatedWith(FasSessionFactory.class)
        .toProvider(FasSessionFactoryProvider.class).in(Singleton.class);
    bind(SessionFactory.class).annotatedWith(FasFfaSessionFactory.class)
        .toProvider(FasSessionFactoryProvider.class).in(Singleton.class);
  }

  private static class FasSessionFactoryProvider implements Provider<SessionFactory> {

    @Inject
    private LisFacilityJobConfiguration facilityJobConfiguration;

    @Override
    public SessionFactory get() {
      return SessionFactoryUtil
          .buildSessionFactory(facilityJobConfiguration.getFasDataSourceFactory(),
              DataSourceName.FAS.name(), fasEntityClasses);
    }
  }

}
