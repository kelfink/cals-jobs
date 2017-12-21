package gov.ca.cwds.jobs.cals.facility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provides;
import gov.ca.cwds.cals.inject.CalsnsDataAccessModule;
import gov.ca.cwds.cals.inject.CwsCmsDataAccessModule;
import gov.ca.cwds.cals.inject.FasDataAccessModule;
import gov.ca.cwds.cals.inject.LisDataAccessModule;
import gov.ca.cwds.cals.service.ChangedFacilityService;
import gov.ca.cwds.cals.service.dto.changed.ChangedFacilityDTO;
import gov.ca.cwds.generic.jobs.Job;
import gov.ca.cwds.generic.jobs.util.AsyncReadWriteJob;
import gov.ca.cwds.jobs.cals.BaseCalsIndexerJob;
import gov.ca.cwds.jobs.cals.CalsElasticJobWriter;
import gov.ca.cwds.jobs.cals.CalsElasticsearchIndexerDao;

/**
 * <p> Command line arguments: </p>
 *
 * <pre>
 * {@code run script: $ java -DDB_FAS_JDBC_URL="jdbc:postgresql://192.168.99.100:5432/?currentSchema=fas" \
-DDB_FAS_USER="postgres_data" -DDB_FAS_PASSWORD="CHANGEME" \
-DDB_LIS_JDBC_URL="jdbc:postgresql://192.168.99.100:5432/?currentSchema=lis" \
-DDB_LIS_USER="postgres_data" -DDB_LIS_PASSWORD="CHANGEME" \
-DDB_CMS_JDBC_URL="jdbc:db2://192.168.99.100:50000/DB0TDEV" -DDB_CMS_SCHEMA="CWSCMSRS" \
-DDB_CMS_USER="db2inst1" -DDB_CMS_PASSWORD="CHANGEME" \
-DDB_CALSNS_JDBC_URL="jdbc:postgresql://192.168.99.100:5432/?currentSchema=calsns" \
-DDB_CALSNS_USER="postgres_data" -DDB_CALSNS_PASSWORD="CHANGEME" \
-cp cals/build/libs/cals-jobs-<version>.jar gov.ca.cwds.jobs.cals.facility.FacilityIndexerJob \
-c cals/config/cals/facility/facility.yaml -l ./}
 * </pre>
 *
 * @author CWDS TPT-2
 */
public final class FacilityIndexerJob extends BaseCalsIndexerJob {

  public static void main(String[] args) {
    runJob(FacilityIndexerJob.class, args);
  }

  @Override
  protected void configure() {
    super.configure();
    install(new CwsCmsDataAccessModule("cals-jobs-cms-hibernate.cfg.xml"));
    install(new LisDataAccessModule("cals-jobs-lis-hibernate.cfg.xml"));
    install(new FasDataAccessModule("cals-jobs-fas-hibernate.cfg.xml"));
    install(new CalsnsDataAccessModule("cals-jobs-calsns-hibernate.cfg.xml"));
    bind(FacilityReader.class);
    bind(FacilityElasticJobWriter.class);
    bind(ChangedFacilityService.class);
  }

  @Provides
  @Inject
  public Job provideJob(FacilityReader jobReader, FacilityElasticJobWriter jobWriter) {
    return new AsyncReadWriteJob(jobReader, jobWriter);
  }

  static class FacilityElasticJobWriter extends CalsElasticJobWriter<ChangedFacilityDTO> {

    /**
     * Constructor.
     *
     * @param elasticsearchDao ES DAO
     * @param objectMapper Jackson object mapper
     */
    @Inject
    FacilityElasticJobWriter(CalsElasticsearchIndexerDao elasticsearchDao, ObjectMapper objectMapper) {
      super(elasticsearchDao, objectMapper);
    }
  }
}
