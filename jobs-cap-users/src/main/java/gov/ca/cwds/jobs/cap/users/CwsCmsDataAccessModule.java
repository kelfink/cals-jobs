package gov.ca.cwds.jobs.cap.users;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import gov.ca.cwds.DataSourceName;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.cap.users.dao.CwsUsersDao;
import gov.ca.cwds.jobs.cap.users.entity.CwsOffice;
import gov.ca.cwds.jobs.cap.users.entity.StaffPerson;
import gov.ca.cwds.jobs.cap.users.entity.UserId;
import gov.ca.cwds.jobs.common.util.SessionFactoryUtil;
import org.hibernate.SessionFactory;

import java.util.Optional;

public class CwsCmsDataAccessModule extends AbstractModule {

  private SessionFactory sessionFactory;

  public static final ImmutableList<Class<?>> cwsEntityClasses = ImmutableList.<Class<?>>builder()
          .add(
                  CwsOffice.class,
                  StaffPerson.class,
                  UserId.class
          ).build();


  @Override
  protected void configure() {
    bind(CwsUsersDao.class);
  }

  @Inject
  @Provides
  @CmsSessionFactory
  public SessionFactory cmsSessionFactory(CapUsersJobConfiguration capUsersJobConfiguration) {
    return getCurrentSessionFactory(capUsersJobConfiguration);
  }

  private SessionFactory getCurrentSessionFactory(CapUsersJobConfiguration capUsersJobConfiguration) {
    return Optional.ofNullable(sessionFactory).orElseGet(() -> sessionFactory = SessionFactoryUtil
            .buildSessionFactory(capUsersJobConfiguration.getCmsDataSourceFactory(),
                    DataSourceName.CWS.name(), cwsEntityClasses));
  }
}
