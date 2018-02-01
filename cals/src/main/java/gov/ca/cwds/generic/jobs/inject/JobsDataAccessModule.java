package gov.ca.cwds.generic.jobs.inject;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import io.dropwizard.db.DataSourceFactory;
import org.apache.commons.lang3.Validate;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Map;

/**
 * @author CWDS TPT-2
 */
public abstract class JobsDataAccessModule extends AbstractModule {

  private DataSourceFactory dataSourceFactory;
  private String dataSourceName;
  private SessionFactory sessionFactory;

  public JobsDataAccessModule(DataSourceFactory dataSourceFactory, String dataSourceName) {
    this.dataSourceFactory = dataSourceFactory;
    this.dataSourceName = dataSourceName;
  }

  protected abstract ImmutableList<Class<?>> getEntityClasses();

  protected void configure(Configuration configuration) {
    //empty by default
  }

  @Override
  protected void configure() {
    Validate.notNull(dataSourceFactory, String.format("%s data source configuration is empty", dataSourceName));
    final Configuration configuration = new Configuration();
    for (Map.Entry<String, String> property : dataSourceFactory.getProperties().entrySet()) {
      configuration.setProperty(property.getKey(), property.getValue());
    }
    getEntityClasses().forEach(configuration::addAnnotatedClass);
    configure(configuration);
    sessionFactory = configuration.buildSessionFactory();
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }
}
