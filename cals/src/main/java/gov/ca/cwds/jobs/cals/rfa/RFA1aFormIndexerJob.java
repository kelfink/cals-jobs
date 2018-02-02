package gov.ca.cwds.jobs.cals.rfa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provides;
import gov.ca.cwds.cals.Constants;
import gov.ca.cwds.cals.service.rfa.RFA1aFormsCollectionService;
import gov.ca.cwds.generic.jobs.Job;
import gov.ca.cwds.generic.jobs.util.AsyncReadWriteJob;
import gov.ca.cwds.jobs.cals.BaseIndexerJob;
import gov.ca.cwds.jobs.cals.CalsElasticJobWriter;
import gov.ca.cwds.jobs.cals.ElasticsearchIndexerDao;
import gov.ca.cwds.jobs.cals.BaseJobConfiguration;
import gov.ca.cwds.jobs.cals.inject.NsDataAccessModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p> Command line arguments: </p>
 *
 * <pre>
 * {@code run script: $ java -DDB_CALSNS_JDBC_URL="jdbc:postgresql://192.168.99.100:5432/?currentSchema=calsns" \
-DDB_CALSNS_USER="postgres_data" -DDB_CALSNS_PASSWORD="CHANGEME" \
-cp build/libs/DocumentIndexerJob-0.24.jar gov.ca.cwds.jobs.cals.rfa.RFA1aFormIndexerJob \
-c config/cals/rfa/CALS_RFA1aForm.yaml -l ./ ^}
 * </pre>
 *
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
    bind(BaseJobConfiguration.class).toInstance(getJobsConfiguration());
    install(new NsDataAccessModule(getJobsConfiguration().getCalsnsDataSourceFactory(), Constants.UnitOfWork.CALSNS));
    bind(RFA1aFormReader.class);
    bind(RFA1aFormElasticJobWriter.class);
    bind(RFA1aFormsCollectionService.class);
  }

  @Override
  public BaseJobConfiguration getJobsConfiguration() {
    return BaseJobConfiguration.getCalsJobsConfiguration(BaseJobConfiguration.class, getJobOptions().getEsConfigLoc());
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
