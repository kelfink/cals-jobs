package gov.ca.cwds.jobs.cals;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import gov.ca.cwds.cals.inject.CwsFacilityServiceProvider;
import gov.ca.cwds.cals.inject.DataAccessServicesModule;
import gov.ca.cwds.cals.inject.FasFacilityServiceProvider;
import gov.ca.cwds.cals.inject.LisFacilityServiceProvider;
import gov.ca.cwds.cals.inject.MappingModule;
import gov.ca.cwds.cals.service.CwsFacilityService;
import gov.ca.cwds.cals.service.FasFacilityService;
import gov.ca.cwds.cals.service.LisFacilityService;
import gov.ca.cwds.cals.service.builder.FacilityParameterObjectBuilder;
import gov.ca.cwds.inject.AuditingModule;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.common.inject.LastRunDir;
import gov.ca.cwds.rest.BaseApiApplication;
import io.dropwizard.setup.Bootstrap;
import org.hibernate.SessionFactory;

/**
 * Created by Alexander Serbin on 1/29/2018.
 */

//TODO remove after restoring RFA ES job
public class TestCalsJobsApplication extends BaseApiApplication<TestCalsJobsConfiguration> {

  @Override
  public Module applicationModule(Bootstrap<TestCalsJobsConfiguration> bootstrap) {

    return new AbstractModule() {

      @Override
      protected void configure() {
        install(new AuditingModule());
        install(new TestDataAccessModule(bootstrap));

        install(new MappingModule());

        install(new DataAccessServicesModule() {
          private SessionFactory getXaCmsSessionFactory(Injector injector) {
            return injector.getInstance(Key.get(SessionFactory.class, CmsSessionFactory.class));
          }

          @Override
          protected SessionFactory getDataAccessSercvicesSessionFactory(Injector injector) {
            return getXaCmsSessionFactory(injector);
          }

        });
        //bind(ChangedIdentifiersService.class).toProvider(ChangedFacilityIdentifiersProvider.class);
        bind(LisFacilityService.class).toProvider(LisFacilityServiceProvider.class);
        bind(FasFacilityService.class).toProvider(FasFacilityServiceProvider.class);
        bind(CwsFacilityService.class).toProvider(CwsFacilityServiceProvider.class);
        //bind(ChangedFacilityService.class).toProvider(ChangedFacilityServiceProvider.class);
        bind(FacilityParameterObjectBuilder.class);
        bindConstant().annotatedWith(LastRunDir.class).to("out");
      }
    };
  }

}
