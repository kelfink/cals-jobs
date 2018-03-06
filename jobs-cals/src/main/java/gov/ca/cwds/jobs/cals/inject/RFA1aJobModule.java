package gov.ca.cwds.jobs.cals.inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import gov.ca.cwds.cals.inject.MappingModule;
import gov.ca.cwds.cals.service.rfa.RFA1aFormsCollectionService;
import gov.ca.cwds.jobs.cals.CalsElasticJobWriter;
import gov.ca.cwds.jobs.cals.rfa.ChangedRFA1aFormDTO;
import gov.ca.cwds.jobs.cals.rfa.ChangedRFAFormsService;
import gov.ca.cwds.jobs.cals.rfa.RFA1aFormReader;
import gov.ca.cwds.jobs.cals.rfa.RFA1aJob;
import gov.ca.cwds.jobs.cals.rfa.RFA1aJobConfiguration;
import gov.ca.cwds.jobs.common.BaseJobConfiguration;
import gov.ca.cwds.jobs.common.ElasticSearchIndexerDao;
import gov.ca.cwds.jobs.common.config.JobOptions;
import gov.ca.cwds.jobs.common.inject.AbstractBaseJobModule;
import gov.ca.cwds.jobs.common.job.Job;
import gov.ca.cwds.jobs.common.job.JobReader;
import gov.ca.cwds.jobs.common.job.JobWriter;

/**
 * Created by Alexander Serbin on 3/4/2018.
 */
public class RFA1aJobModule extends AbstractBaseJobModule {

    public RFA1aJobModule(String[] args) {
        super(args);
    }

    @Override
    protected void configure() {
        super.configure();
        install(new MappingModule());
        install(new NsDataAccessModule());
        bind(JobReader.class).to(RFA1aFormReader.class);
        bind(JobWriter.class).to(RFA1aFormElasticJobWriter.class);
        bind(RFA1aFormsCollectionService.class);
        bind(ChangedRFAFormsService.class);
        bind(Job.class).to(RFA1aJob.class).in(Singleton.class);
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
