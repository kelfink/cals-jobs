package gov.ca.cwds.jobs.common.mode;

import static gov.ca.cwds.jobs.common.mode.DefaultJobMode.INCREMENTAL_LOAD;
import static gov.ca.cwds.jobs.common.mode.DefaultJobMode.INITIAL_LOAD;
import static gov.ca.cwds.jobs.common.mode.DefaultJobMode.INITIAL_LOAD_RESUME;
import static org.junit.Assert.assertEquals;

import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePointContainer;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePointContainerService;
import gov.ca.cwds.jobs.common.util.LastRunDirHelper;
import java.io.IOException;
import java.time.LocalDateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Alexander Serbin on 6/20/2018.
 */
public class DefaultJobModeServiceTest {

  private LastRunDirHelper lastRunDirHelper = new LastRunDirHelper("temp");

  private TimestampSavePointContainerService savePointContainerService = new TimestampSavePointContainerService(
      lastRunDirHelper.getSavepointContainerFolder().toString());

  private TimestampSavePointContainer savePointContainer = new TimestampSavePointContainer();

  private TimestampDefaultJobModeService defaultJobModeService = new TimestampDefaultJobModeService();

  {
    defaultJobModeService.setSavePointContainerService(savePointContainerService);
  }

  @Test
  public void getInitialJobModeTest() throws Exception {
    assertEquals(INITIAL_LOAD, defaultJobModeService.getCurrentJobMode());
  }

  @Test
  public void getInitialResumeJobModeTest() throws Exception {
    savePointContainer.setJobMode(INITIAL_LOAD);
    savePointContainer.setSavePoint(new TimestampSavePoint(LocalDateTime.of(2017, 1, 1, 5, 4)));
    savePointContainerService.writeSavePointContainer(savePointContainer);
    assertEquals(INITIAL_LOAD_RESUME, defaultJobModeService.getCurrentJobMode());
  }

  @Test
  public void getIncrementalJobModeTest() throws Exception {
    savePointContainer.setJobMode(INCREMENTAL_LOAD);
    savePointContainer.setSavePoint(new TimestampSavePoint(LocalDateTime.now()));
    savePointContainerService.writeSavePointContainer(savePointContainer);
    assertEquals(INCREMENTAL_LOAD, defaultJobModeService.getCurrentJobMode());
  }

  @Before
  public void beforeMethod() throws IOException {
    lastRunDirHelper.createSavePointContainerFolder();
  }

  @After
  public void afterMethod() throws IOException {
    lastRunDirHelper.deleteSavePointContainerFolder();
  }

}