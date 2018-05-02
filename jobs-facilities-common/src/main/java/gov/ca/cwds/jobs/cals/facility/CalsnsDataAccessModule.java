package gov.ca.cwds.jobs.cals.facility;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import gov.ca.cwds.DataSourceName;
import gov.ca.cwds.cals.inject.CalsnsSessionFactory;
import gov.ca.cwds.jobs.common.util.SessionFactoryUtil;
import org.hibernate.SessionFactory;

/**
 * Created by Ievgenii Drozd on 4/30/2018.
 */
public class CalsnsDataAccessModule extends AbstractModule {

  public static final ImmutableList<Class<?>> nsEntityClasses = ImmutableList.<Class<?>>builder()
      .add(
          gov.ca.cwds.cals.persistence.model.calsns.dictionaries.FacilityType.class
      ).build();

  @Override
  protected void configure() {
    bind(SessionFactory.class).annotatedWith(CalsnsSessionFactory.class)
        .toProvider(CalsnsSessionFactoryProvider.class).in(Singleton.class);
  }

  private static class CalsnsSessionFactoryProvider implements Provider<SessionFactory> {

    @Inject
    private BaseFacilityJobConfiguration facilityJobConfiguration;

    @Override
    public SessionFactory get() {
      return SessionFactoryUtil
          .buildSessionFactory(facilityJobConfiguration.getCalsnsDataSourceFactory(),
              DataSourceName.NS.name(), nsEntityClasses);
    }
  }
}
