package gov.ca.cwds.jobs.cap.users;

import com.google.inject.AbstractModule;
import gov.ca.cwds.jobs.common.core.JobRunner;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainer;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainerService;
import gov.ca.cwds.jobs.common.util.LastRunDirHelper;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static gov.ca.cwds.jobs.common.mode.DefaultJobMode.INCREMENTAL_LOAD;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class CapUsersJobTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(CapUsersJobTest.class);

  private static LastRunDirHelper lastRunDirHelper = new LastRunDirHelper("cap_job_temp");
  private LocalDateTimeSavePointContainerService savePointContainerService =
          new LocalDateTimeSavePointContainerService(
                  lastRunDirHelper.getSavepointContainerFolder().toString());

  @Test
  public void capUsersJobTest() throws IOException {
    try {
      lastRunDirHelper.deleteSavePointContainerFolder();
      testInitialLoad();

    }
    finally {
      lastRunDirHelper.deleteSavePointContainerFolder();
      TestCapUserWriter.reset();
    }



  }

  private void testInitialLoad() {
    LocalDateTime now = LocalDateTime.now();
    assertEquals(0, TestCapUserWriter.getItems().size());
    runInitialLoad();
    assertEquals(MockedIterator.NUMBER_OF_USERS, TestCapUserWriter.getItems().size());

    LocalDateTimeSavePointContainer savePointContainer = (LocalDateTimeSavePointContainer) savePointContainerService
            .readSavePointContainer(LocalDateTimeSavePointContainer.class);
        assertTrue(savePointContainer.getSavePoint().getTimestamp().isAfter(now));
        assertEquals(INCREMENTAL_LOAD, savePointContainer.getJobMode());
  }

  //private void testInitialLoad() throws IOException, JSONException {
  //    LocalDateTime now = LocalDateTime.now();
  //    assertEquals(0, TestWriter.getItems().size());
  //    runInitialLoad();
  //    assertEquals(167, TestWriter.getItems().size());
  //    assertFacility("fixtures/facilities-initial-load-cwscms.json",
  //        CWSCMS_INITIAL_LOAD_FACILITY_ID);
  //
  //    LocalDateTimeSavePointContainer savePointContainer = (LocalDateTimeSavePointContainer) savePointContainerService
  //        .readSavePointContainer(LocalDateTimeSavePointContainer.class);
  //    assertTrue(savePointContainer.getSavePoint().getTimestamp().isAfter(now));
  //    assertEquals(INCREMENTAL_LOAD, savePointContainer.getJobMode());
  //  }

  private void runInitialLoad() {
    JobRunner.run(createCapUsersJobModule());
  }

  private CapUsersJobModule createCapUsersJobModule() {
    CapUsersJobModule capUsersJobModule = new CapUsersJobModule(getModuleArgs());
    capUsersJobModule.setCapUsersJobBatchIterator(MockedIterator.class);
    capUsersJobModule.setElasticSearchModule(new AbstractModule() {
      @Override
      protected void configure() {
        // Do nothing here
      }
    });
    TestCapUserWriter.reset();
    capUsersJobModule.setCapElasticWriterClass(TestCapUserWriter.class);
    return capUsersJobModule;
  }

  private String[] getModuleArgs() {
    return new String[]{"-c", getConfigFilePath(), "-l",
            lastRunDirHelper.getSavepointContainerFolder().toString()};
  }

  private static String getConfigFilePath() {
    return Paths.get("src", "test", "resources", "cap-users-job-test.yaml")
            .normalize().toAbsolutePath().toString();
  }

}
