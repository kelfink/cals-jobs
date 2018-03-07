package gov.ca.cwds.jobs.cals;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import gov.ca.cwds.cals.inject.CalsnsSessionFactory;
import gov.ca.cwds.cals.inject.FasSessionFactory;
import gov.ca.cwds.cals.inject.LisSessionFactory;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.cals.facility.inject.CwsCmsRsDataAccessModule;
import gov.ca.cwds.jobs.cals.facility.inject.FasDataAccessModule;
import gov.ca.cwds.jobs.cals.facility.inject.LisDataAccessModule;
import gov.ca.cwds.jobs.cals.facility.inject.NsDataAccessModule;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.SessionFactoryFactory;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.setup.Bootstrap;
import org.hibernate.SessionFactory;

import static gov.ca.cwds.cals.Constants.UnitOfWork.CALSNS;
import static gov.ca.cwds.cals.Constants.UnitOfWork.CMS;
import static gov.ca.cwds.cals.Constants.UnitOfWork.FAS;
import static gov.ca.cwds.cals.Constants.UnitOfWork.LIS;


/**
 * Created by Alexander Serbin on 1/30/2018.
 */
public class TestDataAccessModule extends AbstractModule {

    private final HibernateBundle<TestCalsJobsConfiguration> fasHibernateBundle =
            new HibernateBundle<TestCalsJobsConfiguration>(FasDataAccessModule.fasEntityClasses, new SessionFactoryFactory()) {
                @Override
                public PooledDataSourceFactory getDataSourceFactory(TestCalsJobsConfiguration configuration) {
                    return configuration.getFasDataSourceFactory();
                }

                @Override
                public String name() {
                    return FAS;
                }
            };

    private final HibernateBundle<TestCalsJobsConfiguration> cwsCmsRsHibernateBundle =
            new HibernateBundle<TestCalsJobsConfiguration>(CwsCmsRsDataAccessModule.cwsrsEntityClasses, new SessionFactoryFactory()) {
                @Override
                public PooledDataSourceFactory getDataSourceFactory(TestCalsJobsConfiguration configuration) {
                    return configuration.getCmsrsDataSourceFactory();
                }

                @Override
                public String name() {
                    return CMS;
                }
            };

    private final HibernateBundle<TestCalsJobsConfiguration> lisHibernateBundle =
            new HibernateBundle<TestCalsJobsConfiguration>(LisDataAccessModule.lisEntityClasses, new SessionFactoryFactory()) {
                @Override
                public PooledDataSourceFactory getDataSourceFactory(TestCalsJobsConfiguration configuration) {
                    return configuration.getLisDataSourceFactory();
                }

                @Override
                public String name() {
                    return LIS;
                }
            };

    private final HibernateBundle<TestCalsJobsConfiguration> nsHibernateBundle =
            new HibernateBundle<TestCalsJobsConfiguration>(NsDataAccessModule.nsEntityClasses, new SessionFactoryFactory()) {
                @Override
                public PooledDataSourceFactory getDataSourceFactory(TestCalsJobsConfiguration configuration) {
                    return configuration.getNsDataSourceFactory();
                }

                @Override
                public String name() {
                    return CALSNS;
                }

                @Override
                public void configure(org.hibernate.cfg.Configuration configuration) {
                    configuration.addPackage("gov.ca.cwds.cals.persistence.model.calsns.rfa");
                }
            };


    public TestDataAccessModule(Bootstrap<? extends TestCalsJobsConfiguration> bootstrap) {
        bootstrap.addBundle(fasHibernateBundle);
        bootstrap.addBundle(cwsCmsRsHibernateBundle);
        bootstrap.addBundle(lisHibernateBundle);
        bootstrap.addBundle(nsHibernateBundle);
    }

    @Override
    protected void configure() {
        //do nothing
    }

    @Provides
    @LisSessionFactory
    SessionFactory lisSessionFactory() {
        return lisHibernateBundle.getSessionFactory();
    }

    @Provides
    @FasSessionFactory
    SessionFactory fasSessionFactory() {
        return fasHibernateBundle.getSessionFactory();
    }

    @Provides
    @CalsnsSessionFactory
    SessionFactory calsnsSessionFactory() {
        return nsHibernateBundle.getSessionFactory();
    }

    @Provides
    @CmsSessionFactory
    SessionFactory cwsSessionFactory() {
        return cwsCmsRsHibernateBundle.getSessionFactory();
    }

    @Provides
    UnitOfWorkAwareProxyFactory provideUnitOfWorkAwareProxyFactory() {
        return new UnitOfWorkAwareProxyFactory(
                lisHibernateBundle,
                fasHibernateBundle,
                cwsCmsRsHibernateBundle,
                nsHibernateBundle
        );
    }

}
