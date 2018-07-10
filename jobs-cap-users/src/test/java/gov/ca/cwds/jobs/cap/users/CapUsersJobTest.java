package gov.ca.cwds.jobs.cap.users;

import com.google.inject.AbstractModule;
import gov.ca.cwds.jobs.common.core.JobRunner;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class CapUsersJobTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(CapUsersJobTest.class);

  @Test
  public void capUsersJobTest() {

    Assert.assertEquals(0, TestCapUserWriter.getItems().size());
    runInitialLoad();
    Assert.assertEquals(MockedIterator.LIMIT, TestCapUserWriter.getItems().size());

  }

  private void runInitialLoad() {
    JobRunner.run(createCapUsersJobModule());
  }

  private CapUsersJobModule createCapUsersJobModule() {
    CapUsersJobModule capUsersJobModule = new CapUsersJobModule(getModuleArgs());
    capUsersJobModule.setCapElasticWriterClass(TestCapUserWriter.class);
    capUsersJobModule.setCapUsersJobBatchIterator(MockedIterator.class);
    capUsersJobModule.setElasticSearchModule(new AbstractModule() {
      @Override
      protected void configure() {
        // Do nothing here
      }
    });
    return capUsersJobModule;
  }

  private String[] getModuleArgs() {
    return new String[]{"-c", getConfigFilePath(), "-l", "godsfd"
    };
  }

  private static String getConfigFilePath() {
    return Paths.get("src", "test", "resources", "cap-users-job-test.yaml")
            .normalize().toAbsolutePath().toString();
  }

}
