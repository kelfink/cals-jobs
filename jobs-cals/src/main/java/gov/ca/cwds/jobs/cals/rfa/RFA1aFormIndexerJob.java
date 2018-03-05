package gov.ca.cwds.jobs.cals.rfa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provides;
import gov.ca.cwds.cals.Constants;
import gov.ca.cwds.cals.inject.MappingModule;
import gov.ca.cwds.cals.service.rfa.RFA1aFormsCollectionService;
import gov.ca.cwds.jobs.cals.CalsElasticJobWriter;
import gov.ca.cwds.jobs.cals.inject.NsDataAccessModule;
import gov.ca.cwds.jobs.common.BaseIndexerJob;
import gov.ca.cwds.jobs.common.BaseJobConfiguration;
import gov.ca.cwds.jobs.common.ElasticsearchIndexerDao;
import gov.ca.cwds.jobs.common.job.Job;
import gov.ca.cwds.jobs.common.job.impl.AsyncReadWriteJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CWDS TPT-2
 */
public final class RFA1aFormIndexerJob extends BaseIndexerJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(RFA1aFormIndexerJob.class);

  public static void main(String[] args) {
    RFA1aFormIndexerJob job = new RFA1aFormIndexerJob();
    job.run(args);
  }

  @Override
  protected void configure() {
    super.configure();
    install(new MappingModule());
    bind(BaseJobConfiguration.class).toInstance(getJobsConfiguration());
    install(new NsDataAccessModule());
    bind(RFA1aFormReader.class);
    bind(RFA1aFormElasticJobWriter.class);
    bind(RFA1aFormsCollectionService.class);
    bind(ChangedRFAFormsService.class);
  }

  @Override
  public RFA1aJobConfiguration getJobsConfiguration() {
    RFA1aJobConfiguration jobConfiguration = BaseJobConfiguration.getJobsConfiguration(RFA1aJobConfiguration.class,
            getJobOptions().getEsConfigLoc());
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
    public RFA1aFormElasticJobWriter(ElasticsearchIndexerDao elasticsearchDao,
        ObjectMapper objectMapper) {
      super(elasticsearchDao, objectMapper);
    }
  }
}
