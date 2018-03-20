package gov.ca.cwds.jobs.cals.facility.inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import gov.ca.cwds.cals.Constants;
import gov.ca.cwds.cals.inject.DataAccessServicesModule;
import gov.ca.cwds.cals.inject.FasSessionFactory;
import gov.ca.cwds.cals.inject.LisSessionFactory;
import gov.ca.cwds.cals.inject.MappingModule;
import gov.ca.cwds.cals.service.builder.FacilityParameterObjectBuilder;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.cals.facility.ChangedFacilityDTO;
import gov.ca.cwds.jobs.cals.facility.FacilityBatchProcessor;
import gov.ca.cwds.jobs.cals.facility.FacilityJob;
import gov.ca.cwds.jobs.cals.facility.FacilityJobConfiguration;
import gov.ca.cwds.jobs.common.BaseJobConfiguration;
import gov.ca.cwds.jobs.common.ElasticSearchIndexerDao;
import gov.ca.cwds.jobs.common.config.JobOptions;
import gov.ca.cwds.jobs.common.elastic.ElasticWriter;
import gov.ca.cwds.jobs.common.exception.JobsException;
import gov.ca.cwds.jobs.common.identifier.ChangedIdentifiersService;
import gov.ca.cwds.jobs.common.inject.AbstractBaseJobModule;
import gov.ca.cwds.jobs.common.job.ChangedEntityService;
import gov.ca.cwds.jobs.common.job.Job;
import gov.ca.cwds.jobs.common.job.BulkWriter;
import gov.ca.cwds.jobs.common.job.impl.BatchProcessor;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 3/4/2018.
 */
public class FacilityJobModule extends AbstractBaseJobModule {

    private static final Logger LOG = LoggerFactory.getLogger(FacilityJobModule.class);
    private Class<? extends BulkWriter<ChangedFacilityDTO>> facilityElasticWriterClass;

    public FacilityJobModule(String[] args) {
        super(args);
        this.facilityElasticWriterClass = FacilityElasticWriter.class;
    }

    public void setFacilityElasticWriterClass(Class<? extends BulkWriter<ChangedFacilityDTO>> facilityElasticWriterClass) {
        this.facilityElasticWriterClass = facilityElasticWriterClass;
    }

    @Override
    protected void configure() {
        super.configure();
        bind(new TypeLiteral<BulkWriter<ChangedFacilityDTO>>() {}).to(facilityElasticWriterClass);
        bind(new TypeLiteral<BatchProcessor<ChangedFacilityDTO>>() {}).to(FacilityBatchProcessor.class);
        bind(ChangedIdentifiersService.class).toProvider(ChangedFacilityIdentifiersProvider.class);
        bind(new TypeLiteral<ChangedEntityService<ChangedFacilityDTO>>() {}).toProvider(ChangedFacilityServiceProvider.class);
        bind(FacilityParameterObjectBuilder.class);
        bind(Job.class).to(FacilityJob.class);
        install(new MappingModule());
        install(new CwsCmsRsDataAccessModule());
        install(new LisDataAccessModule());
        install(new FasDataAccessModule());
        install(new NsDataAccessModule());
        install(new DataAccessServicesModule() {
            private SessionFactory getXaCmsSessionFactory(Injector injector) {
                return injector.getInstance(Key.get(SessionFactory.class, CmsSessionFactory.class));
            }

            @Override
            protected SessionFactory getDataAccessSercvicesSessionFactory(Injector injector) {
                return getXaCmsSessionFactory(injector);
            }
        });
    }

    @Provides
    @Override
    @Inject
    public FacilityJobConfiguration getJobsConfiguration(JobOptions jobOptions) {
        FacilityJobConfiguration facilityJobConfiguration =
                BaseJobConfiguration.getJobsConfiguration(FacilityJobConfiguration.class,
                jobOptions.getEsConfigLoc());
        facilityJobConfiguration.setDocumentMapping("facility.mapping.json");
        facilityJobConfiguration.setIndexSettings("facility.settings.json");
        return facilityJobConfiguration;
    }

    static class FacilityElasticWriter extends ElasticWriter<ChangedFacilityDTO> {

        @Inject
        FacilityElasticWriter(ElasticSearchIndexerDao elasticsearchDao, ObjectMapper objectMapper) {
            super(elasticsearchDao, objectMapper);
        }
    }

    @Provides
    @Inject
    UnitOfWorkAwareProxyFactory provideUnitOfWorkAwareProxyFactory(@FasSessionFactory SessionFactory fasSessionFactory,
                                                                   @LisSessionFactory SessionFactory lisSessionFactory,
                                                                   @CmsSessionFactory SessionFactory cwsSessionFactory) {
        try {
            ImmutableMap<String, SessionFactory> sessionFactories = ImmutableMap.<String, SessionFactory>builder()
                    .put(Constants.UnitOfWork.CMS, cwsSessionFactory)
                    .put(Constants.UnitOfWork.FAS, fasSessionFactory)
                    .put(Constants.UnitOfWork.LIS, lisSessionFactory)
                    .build();
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory = new UnitOfWorkAwareProxyFactory();
            FieldUtils.writeField(unitOfWorkAwareProxyFactory, "sessionFactories", sessionFactories, true);
            return unitOfWorkAwareProxyFactory;
        } catch (IllegalAccessException e) {
            LOG.error("Can't build UnitOfWorkAwareProxyFactory", e);
            throw new JobsException(e);
        }
    }

}
