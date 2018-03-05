package gov.ca.cwds.jobs.cals.inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provides;
import gov.ca.cwds.cals.inject.MappingModule;
import gov.ca.cwds.cals.service.rfa.RFA1aFormsCollectionService;
import gov.ca.cwds.jobs.cals.CalsElasticJobWriter;
import gov.ca.cwds.jobs.cals.rfa.ChangedRFA1aFormDTO;
import gov.ca.cwds.jobs.cals.rfa.ChangedRFAFormsService;
import gov.ca.cwds.jobs.cals.rfa.RFA1aFormReader;
import gov.ca.cwds.jobs.cals.rfa.RFA1aJobConfiguration;
import gov.ca.cwds.jobs.common.BaseJobConfiguration;
import gov.ca.cwds.jobs.common.ElasticSearchIndexerDao;
import gov.ca.cwds.jobs.common.config.JobOptions;
import gov.ca.cwds.jobs.common.inject.AbstractBaseJobModule;
import gov.ca.cwds.jobs.common.job.Job;
import gov.ca.cwds.jobs.common.job.impl.AsyncReadWriteJob;

/**
 * Created by Alexander Serbin on 3/4/2018.
 */
public class RFA1aJobModule extends AbstractBaseJobModule {

    public RFA1aJobModule(JobOptions jobOptions) {
        super(jobOptions);
    }

    @Override
    protected void configure() {
        super.configure();
        install(new MappingModule());
        install(new NsDataAccessModule());
        bind(RFA1aFormReader.class);
        bind(RFA1aFormElasticJobWriter.class);
        bind(RFA1aFormsCollectionService.class);
        bind(ChangedRFAFormsService.class);
    }

    @Provides
    @Override
    @Inject
    public RFA1aJobConfiguration getJobsConfiguration(JobOptions jobOptions) {
        RFA1aJobConfiguration jobConfiguration = BaseJobConfiguration.getJobsConfiguration(RFA1aJobConfiguration.class,
                jobOptions.getEsConfigLoc());
        jobConfiguration.setDocumentMapping("rfa.mapping.json");
        jobConfiguration.setIndexSettings("rfa.settings.json");
        return jobConfiguration;
    }

    @Provides
    @Inject
    public Job provideJob(RFA1aFormReader jobReader, RFA1aFormElasticJobWriter jobWriter) {
        return new AsyncReadWriteJob(jobReader, jobWriter);
    }

    static class RFA1aFormElasticJobWriter extends CalsElasticJobWriter<ChangedRFA1aFormDTO> {

        /**
         * Constructor.
         *
         * @param elasticsearchDao ES DAO
         * @param objectMapper Jackson object mapper
         */
        @Inject
        public RFA1aFormElasticJobWriter(ElasticSearchIndexerDao elasticsearchDao,
                                         ObjectMapper objectMapper) {
            super(elasticsearchDao, objectMapper);
        }
    }

}
