package gov.ca.cwds.jobs.cap.users;

import static gov.ca.cwds.test.support.DatabaseHelper.setUpDatabase;

import com.google.inject.AbstractModule;
import gov.ca.cwds.DataSourceName;
import gov.ca.cwds.jobs.common.BaseJobConfiguration;
import gov.ca.cwds.jobs.common.inject.JobRunner;
import io.dropwizard.db.DataSourceFactory;
import liquibase.exception.LiquibaseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class CapUsersJobTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(CapUsersJobTest.class);


  @Before
  public void init() {

      LOGGER.info("Setup database has been started!!!");
      CapUsersJobConfiguration configuration = getFacilityJobConfiguration();
      try {
        setUpDatabase(configuration.getCmsDataSourceFactory(), DataSourceName.CWS);
      } catch (LiquibaseException e) {
        LOGGER.error(e.getMessage(), e);
      }

      LOGGER.info("Setup database has been finished!!!");

  }

  @Test
  public void capUsersJobTest() {

    Assert.assertEquals(0, TestCapUserWriter.getItems().size());
    //lastRunDirHelper.deleteTimestampDirectory();
    runInitialLoad();
    Assert.assertEquals(180, TestCapUserWriter.getItems().size());

  }

  private void runInitialLoad() {
    //TestCapUserWriter.reset();
    JobRunner.run(createCapUsersJobModule());
  }

  private CapUsersJobModule createCapUsersJobModule() {
    CapUsersJobModule capUsersJobModule = new CapUsersJobModule(getModuleArgs());
    capUsersJobModule.setElasticSearchModule(new AbstractModule() {
      @Override
      protected void configure() {
        // Do nothing here
      }
    });
    //capUsersJobModule.se(TestCapUserWriter.class);
    //cwsFacilityJobModule.setJobPreparatorClass(CwsJobPreparator.class);
    return capUsersJobModule;
  }

  private String[] getModuleArgs() {
    return new String[]{"-c", getConfigFilePath(), "-l", "godsfd"
            /*lastRunDirHelper.getLastRunDir().toString()*/};
  }

  private static String getConfigFilePath() {
    return Paths.get("src", "test", "resources", "cap-users-job-test.yaml")
            .normalize().toAbsolutePath().toString();
  }

  private static CapUsersJobConfiguration getFacilityJobConfiguration() {
    CapUsersJobConfiguration capUsersJobConfiguration =
            BaseJobConfiguration.getJobsConfiguration(CapUsersJobConfiguration.class, getConfigFilePath());
    DataSourceFactory dataSourceFactory = capUsersJobConfiguration.getCmsDataSourceFactory();


    dataSourceFactory.setUrl(dataSourceFactory.getProperties().get("hibernate.connection.url"));
    dataSourceFactory
            .setUser(dataSourceFactory.getProperties().get("hibernate.connection.username"));
    dataSourceFactory
            .setPassword(dataSourceFactory.getProperties().get("hibernate.connection.password"));

    return capUsersJobConfiguration;
  }

}
