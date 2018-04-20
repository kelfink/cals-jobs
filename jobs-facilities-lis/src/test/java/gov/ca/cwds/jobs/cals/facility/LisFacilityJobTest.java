package gov.ca.cwds.jobs.cals.facility;

import static gov.ca.cwds.jobs.cals.facility.AssertFacilityHelper.assertFacility;
import static gov.ca.cwds.jobs.cals.facility.lis.LisRecordChange.lisTimestampFormatter;
import static gov.ca.cwds.test.support.DatabaseHelper.setUpDatabase;
import static org.junit.Assert.assertEquals;

import com.google.inject.AbstractModule;
import gov.ca.cwds.DataSourceName;
import gov.ca.cwds.jobs.cals.facility.lis.LisFacilityJobConfiguration;
import gov.ca.cwds.jobs.cals.facility.lis.LisFacilityJobModule;
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
import java.util.HashMap;
import java.util.Map;
import liquibase.exception.LiquibaseException;
import org.json.JSONException;
import org.junit.Test;

/**
 * Created by Alexander Serbin on 3/18/2018.
 */
public class LisFacilityJobTest {

  private static LastRunDirHelper lastRunDirHelper = new LastRunDirHelper("lis_job_temp");
  private static final String LIS_INITIAL_LOAD_FACILITY_ID = "9069";

  @Test
  public void lisFacilityJobTest()
      throws IOException, JSONException, InterruptedException, LiquibaseException {
    try {
      assertEquals(0, TestWriter.getItems().size());
      lastRunDirHelper.deleteTimestampDirectory();
      runInitialLoad();
      assertEquals(330, TestWriter.getItems().size());
      assertFacility("fixtures/facilities-lis.json", LIS_INITIAL_LOAD_FACILITY_ID);
      runIncrementalLoad();
      assertEquals(0, TestWriter.getItems().size());
      Thread.sleep(1000);
      addLisDataForIncrementalLoad();
      runIncrementalLoad();
      assertEquals(1, TestWriter.getItems().size());
      assertFacility("fixtures/facilities-lis.json",
          LIS_INITIAL_LOAD_FACILITY_ID);
    } finally {
      lastRunDirHelper.deleteTimestampDirectory();
      TestWriter.reset();
    }
  }

  private void addLisDataForIncrementalLoad() throws LiquibaseException {
    DataSourceFactory lisDataSourceFactory = getFacilityJobConfiguration()
        .getLisDataSourceFactory();
    DatabaseHelper lisDatabaseHelper = new DatabaseHelper(lisDataSourceFactory.getUrl(),
        lisDataSourceFactory.getUser(), lisDataSourceFactory.getPassword());
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("now", lisTimestampFormatter.format(LocalDateTime.now()));
    lisDatabaseHelper.runScript("liquibase/lis_facility_incremental_load.xml", parameters, "lis");
  }

  private static LisFacilityJobConfiguration getFacilityJobConfiguration() {
    LisFacilityJobConfiguration facilityJobConfiguration =
        BaseJobConfiguration
            .getJobsConfiguration(LisFacilityJobConfiguration.class, getConfigFilePath());
    DataSourceFactoryUtils.fixDatasourceFactory(facilityJobConfiguration.getLisDataSourceFactory());
    DataSourceFactoryUtils.fixDatasourceFactory(facilityJobConfiguration.getFasDataSourceFactory());
    return facilityJobConfiguration;
  }

  private void runInitialLoad() {
    TestWriter.reset();
    JobRunner.run(createLisFacilityJobModule());
  }

  private void runIncrementalLoad() {
    runInitialLoad();
  }

  private LisFacilityJobModule createLisFacilityJobModule() {
    LisFacilityJobModule lisFacilityJobModule = new LisFacilityJobModule(getModuleArgs());
    lisFacilityJobModule.setElasticSearchModule(new AbstractModule() {
      @Override
      protected void configure() {

      }
    });
    lisFacilityJobModule.setFacilityElasticWriterClass(TestWriter.class);
    lisFacilityJobModule.setJobPreparatorClass(LisJobPreparator.class);
    return lisFacilityJobModule;
  }


  private String[] getModuleArgs() {
    return new String[]{"-c", getConfigFilePath(), "-l",
        lastRunDirHelper.getLastRunDir().toString()};
  }

  private static String getConfigFilePath() {
    return Paths.get("src", "test", "resources", "lis-test-facility-job.yaml")
        .normalize().toAbsolutePath().toString();
  }

  static class LisJobPreparator implements JobPreparator {

    @Override
    public void run() {
      System.out.println("Setup database has been started!!!");
      LisFacilityJobConfiguration configuration = getFacilityJobConfiguration();
      try {
        setUpDatabase(configuration.getLisDataSourceFactory(), DataSourceName.LIS);
        setUpDatabase(configuration.getFasDataSourceFactory(), DataSourceName.FAS);
      } catch (LiquibaseException e) {
        e.printStackTrace();
      }
      System.out.println("Setup database has been finished!!!");
    }
  }

}