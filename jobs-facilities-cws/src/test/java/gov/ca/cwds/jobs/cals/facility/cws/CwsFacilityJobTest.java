package gov.ca.cwds.jobs.cals.facility.cws;

import static gov.ca.cwds.test.support.DatabaseHelper.setUpDatabase;

import com.google.inject.AbstractModule;
import gov.ca.cwds.DataSourceName;
import gov.ca.cwds.jobs.cals.facility.AssertFacilityHelper;
import gov.ca.cwds.jobs.cals.facility.TestWriter;
import gov.ca.cwds.jobs.common.BaseJobConfiguration;
import gov.ca.cwds.jobs.common.inject.JobRunner;
import gov.ca.cwds.jobs.common.job.JobPreparator;
import gov.ca.cwds.jobs.common.job.timestamp.LastRunDirHelper;
import gov.ca.cwds.jobs.utils.DataSourceFactoryUtils;
import gov.ca.cwds.test.support.DatabaseHelper;
import io.dropwizard.db.DataSourceFactory;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import liquibase.exception.LiquibaseException;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 3/18/2018.
 */
public class CwsFacilityJobTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(CwsFacilityJobTest.class);

  private static LastRunDirHelper lastRunDirHelper = new LastRunDirHelper("cws_job_temp");

  static final String CWSCMS_INITIAL_LOAD_FACILITY_ID = "3w6sOO50Ki";

  static final String CWSCMS_INCREMENTAL_LOAD_NEW_FACILITY_ID = "AAAAAAAAAA";
  static final String CWSCMS_INCREMENTAL_LOAD_UPDATED_FACILITY_ID = "AP9Ewb409u";
  static final String CWSCMS_INCREMENTAL_LOAD_DELETED_FACILITY_ID = "AyT7r860AB";

  @Ignore
  @Test
  public void cwsFacilityJobTest()
      throws IOException, JSONException, InterruptedException, LiquibaseException {
    try {
      Assert.assertEquals(0, TestWriter.getItems().size());
      lastRunDirHelper.deleteTimestampDirectory();
      runInitialLoad();
      Assert.assertEquals(167, TestWriter.getItems().size());
      AssertFacilityHelper.assertFacility("fixtures/facilities-initial-load-cwscms.json",
          CWSCMS_INITIAL_LOAD_FACILITY_ID);
      runIncrementalLoad();
      Assert.assertEquals(0, TestWriter.getItems().size());
      Thread.sleep(100);
      addCwsDataForIncrementalLoad();
      runIncrementalLoad();
      Assert.assertEquals(3, TestWriter.getItems().size());
      AssertFacilityHelper.assertFacility("fixtures/cwsrs_new_facility.json",
          CWSCMS_INCREMENTAL_LOAD_NEW_FACILITY_ID);
      AssertFacilityHelper.assertFacility("fixtures/cwsrs_updated_facility.json",
          CWSCMS_INCREMENTAL_LOAD_UPDATED_FACILITY_ID);
      AssertFacilityHelper.assertFacility("fixtures/cwsrs_deleted_facility.json",
          CWSCMS_INCREMENTAL_LOAD_DELETED_FACILITY_ID);
    } finally {
      lastRunDirHelper.deleteTimestampDirectory();
      TestWriter.reset();
    }
  }

  private static CwsFacilityJobConfiguration getFacilityJobConfiguration() {
    CwsFacilityJobConfiguration facilityJobConfiguration =
        BaseJobConfiguration
            .getJobsConfiguration(CwsFacilityJobConfiguration.class, getConfigFilePath());
    DataSourceFactoryUtils.fixDatasourceFactory(facilityJobConfiguration.getCmsDataSourceFactory());
    return facilityJobConfiguration;
  }

  private void addCwsDataForIncrementalLoad() throws LiquibaseException {
    DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    DataSourceFactory cwsDataSourceFactory = getFacilityJobConfiguration()
        .getCmsDataSourceFactory();
    DatabaseHelper cwsDatabaseHelper = new DatabaseHelper(cwsDataSourceFactory.getUrl(),
        cwsDataSourceFactory.getUser(), cwsDataSourceFactory.getPassword());
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("now", datetimeFormatter.format(LocalDateTime.now()));
    cwsDatabaseHelper
        .runScript("liquibase/cwsrs_facility_incremental_load.xml", parameters, "CWSCMSRS");
  }

  private void runInitialLoad() {
    TestWriter.reset();
    JobRunner.run(createCwsFacilityJobModule());
  }

  private CwsFacilityJobModule createCwsFacilityJobModule() {
    CwsFacilityJobModule cwsFacilityJobModule = new CwsFacilityJobModule(getModuleArgs());
    cwsFacilityJobModule.setElasticSearchModule(new AbstractModule() {
      @Override
      protected void configure() {
        // Do nothing here
      }
    });
    cwsFacilityJobModule.setFacilityElasticWriterClass(TestWriter.class);
    cwsFacilityJobModule.setJobPreparatorClass(CwsJobPreparator.class);
    return cwsFacilityJobModule;
  }

  private void runIncrementalLoad() {
    runInitialLoad();
  }

  private String[] getModuleArgs() {
    return new String[]{"-c", getConfigFilePath(), "-l",
        lastRunDirHelper.getLastRunDir().toString()};
  }

  private static String getConfigFilePath() {
    return Paths.get("src", "test", "resources", "cws-test-facility-job.yaml")
        .normalize().toAbsolutePath().toString();
  }

  static class CwsJobPreparator implements JobPreparator {

    @Override
    public void run() {
      LOGGER.info("Setup database has been started!!!");
      CwsFacilityJobConfiguration configuration = getFacilityJobConfiguration();
      try {
        setUpDatabase(configuration.getCmsDataSourceFactory(), DataSourceName.CWSRS);
      } catch (LiquibaseException e) {
        LOGGER.error(e.getMessage(),e);
      }

      LOGGER.info("Setup database has been finished!!!");
    }
  }

}