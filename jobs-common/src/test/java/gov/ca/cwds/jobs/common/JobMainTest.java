package gov.ca.cwds.jobs.common;

import static gov.ca.cwds.jobs.common.mode.DefaultJobMode.INCREMENTAL_LOAD;
import static gov.ca.cwds.jobs.common.mode.DefaultJobMode.INITIAL_LOAD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import gov.ca.cwds.jobs.common.core.JobRunner;
import gov.ca.cwds.jobs.common.exception.JobsException;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePointContainer;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePointContainerService;
import gov.ca.cwds.jobs.common.util.LastRunDirHelper;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Alexander Serbin on 6/22/2018.
 */
public class JobMainTest {

  private LastRunDirHelper lastRunDirHelper = new LastRunDirHelper("temp");
  private TestWriter<TestEntity> testWriter = new TestWriter<>();
  private TimestampSavePointContainerService savePointContainerService =
      new TimestampSavePointContainerService(
          lastRunDirHelper.getSavepointContainerFolder().toString());

  private static LocalDateTime FIRST_TIMESTAMP = null;
  private static LocalDateTime SECOND_TIMESTAMP = LocalDateTime.of(2013, 1, 1, 1, 1, 1);
  private static LocalDateTime THIRD_TIMESTAMP = LocalDateTime.of(2014, 2, 2, 2, 2, 2);
  private static LocalDateTime FOURTH_TIMESTAMP = LocalDateTime.of(2015, 3, 3, 3, 3, 3);

  private static final String BROKEN_ENTITY_ID = "brokenEntityId";

  private ChangedEntityIdentifier<TimestampSavePoint> id1 = new ChangedEntityIdentifier<>("1",
      new TimestampSavePoint(FIRST_TIMESTAMP));
  private ChangedEntityIdentifier<TimestampSavePoint> id2 = new ChangedEntityIdentifier<>("2",
      new TimestampSavePoint(SECOND_TIMESTAMP));
  private ChangedEntityIdentifier<TimestampSavePoint> id3 = new ChangedEntityIdentifier<>("3",
      new TimestampSavePoint(THIRD_TIMESTAMP));
  private ChangedEntityIdentifier<TimestampSavePoint> id4 = new ChangedEntityIdentifier<>("4",
      new TimestampSavePoint(FOURTH_TIMESTAMP));
  private ChangedEntityIdentifier<TimestampSavePoint> brokenEntityId = new ChangedEntityIdentifier<>(
      BROKEN_ENTITY_ID,
      new TimestampSavePoint(LocalDateTime.of(2017, 1, 1, 1, 1)));

  private TestEntity entity1 = new TestEntity("1", FIRST_TIMESTAMP);
  private TestEntity entity2 = new TestEntity("2", SECOND_TIMESTAMP);
  private TestEntity entity3 = new TestEntity("3", THIRD_TIMESTAMP);
  private TestEntity entity4 = new TestEntity("4", FOURTH_TIMESTAMP);

  @Test
  public void happyPathTest() {
    List<ChangedEntityIdentifier<TimestampSavePoint>> identifiers = new ArrayList<>(
        Arrays.asList(id1, id2, id3, id4));
    List<TestEntity> entities = new ArrayList<>(Arrays.asList(entity1, entity2, entity3, entity4));
    runInitialJob(identifiers, entities);
    assertEquals(4, testWriter.getItems().size());
    assertEquals(entity1, testWriter.getItems().get(0));
    assertEquals(entity2, testWriter.getItems().get(1));
    assertEquals(entity3, testWriter.getItems().get(2));
    assertEquals(entity4, testWriter.getItems().get(3));
    TimestampSavePointContainer savePointContainer = savePointContainerService
        .readSavePointContainer();
    assertEquals(FOURTH_TIMESTAMP, savePointContainer.getSavePoint().getTimestamp());
    assertEquals(INCREMENTAL_LOAD, savePointContainer.getJobMode());
    System.out.println("-------------------------------------------------------------------------");
    runIncrementalJob(identifiers, entities);
    assertEquals(0, testWriter.getItems().size());
    savePointContainer = savePointContainerService.readSavePointContainer();
    assertEquals(FOURTH_TIMESTAMP, savePointContainer.getSavePoint().getTimestamp());
    assertEquals(INCREMENTAL_LOAD, savePointContainer.getJobMode());
    LocalDateTime now = LocalDateTime.now();
    identifiers.add(new ChangedEntityIdentifier<>("incrementalId", new TimestampSavePoint(now)));
    entities.add(new TestEntity("incrementalId", now));
    runIncrementalJob(identifiers, entities);
    assertEquals(1, testWriter.getItems().size());
    assertEquals(new TestEntity("incrementalId", now), testWriter.getItems().get(0));
    savePointContainer = savePointContainerService.readSavePointContainer();
    assertEquals(now, savePointContainer.getSavePoint().getTimestamp());
    assertEquals(INCREMENTAL_LOAD, savePointContainer.getJobMode());
  }

  @Test(expected = JobsException.class)
  public void initialModeFailsAtOnceTest() {
    try {
      List<ChangedEntityIdentifier<TimestampSavePoint>> identifiers = new ArrayList<>(
          Collections.singletonList(brokenEntityId));
      List<TestEntity> entities = Collections.emptyList();
      runInitialJob(identifiers, entities);
    } finally {
      assertFalse(savePointContainerService.savePointContainerExists());
    }
  }

  @Test
  public void initialModeCrushTest() {
    List<ChangedEntityIdentifier<TimestampSavePoint>> identifiers = new ArrayList<>(
        Arrays.asList(id1, id2, brokenEntityId, id3));
    List<TestEntity> entities = new ArrayList<>(Arrays.asList(entity1, entity2, entity3));
    try {
      runInitialJob(identifiers, entities);
    } catch (JobsException e) {
      assertEquals("java.lang.RuntimeException: Broken entity!!!", e.getCause().getMessage());
      assertEquals(2, testWriter.getItems().size());
      TimestampSavePointContainer savePointContainer = savePointContainerService
          .readSavePointContainer();
      assertEquals(SECOND_TIMESTAMP, savePointContainer.getSavePoint().getTimestamp());
      assertEquals(INITIAL_LOAD, savePointContainer.getJobMode());
      assertEquals(entity1, testWriter.getItems().get(0));
      assertEquals(entity2, testWriter.getItems().get(1));
    }
    identifiers.remove(brokenEntityId);
    runResumeInitialJob(identifiers, entities);
    TimestampSavePointContainer savePointContainer = savePointContainerService
        .readSavePointContainer();
    assertEquals(THIRD_TIMESTAMP, savePointContainer.getSavePoint().getTimestamp());
    assertEquals(INCREMENTAL_LOAD, savePointContainer.getJobMode());
    assertEquals(1, testWriter.getItems().size());
    assertEquals(entity3, testWriter.getItems().get(0));
  }

  @Test
  public void incrementalModeCrushTest() {
    List<ChangedEntityIdentifier<TimestampSavePoint>> identifiers = new ArrayList<>(
        Collections.singletonList(id2));
    List<TestEntity> entities = new ArrayList<>(Arrays.asList(entity1, entity2, entity3, entity4));
    runInitialJob(identifiers, entities);
    TimestampSavePointContainer savePointContainer = savePointContainerService
        .readSavePointContainer();
    assertEquals(SECOND_TIMESTAMP, savePointContainer.getSavePoint().getTimestamp());
    assertEquals(INCREMENTAL_LOAD, savePointContainer.getJobMode());
    assertEquals(1, testWriter.getItems().size());
    assertEquals(entity2, testWriter.getItems().get(0));
    identifiers.add(id3);
    runIncrementalJob(identifiers, entities);
    savePointContainer = savePointContainerService.readSavePointContainer();
    assertEquals(THIRD_TIMESTAMP, savePointContainer.getSavePoint().getTimestamp());
    assertEquals(INCREMENTAL_LOAD, savePointContainer.getJobMode());
    assertEquals(1, testWriter.getItems().size());
    assertEquals(entity3, testWriter.getItems().get(0));
    identifiers.add(brokenEntityId);
    try {
      runIncrementalJob(identifiers, entities);
    } catch (JobsException e) {
      assertEquals("java.lang.RuntimeException: Broken entity!!!", e.getCause().getMessage());
      assertEquals(0, testWriter.getItems().size());
      savePointContainer = savePointContainerService
          .readSavePointContainer();
      assertEquals(THIRD_TIMESTAMP, savePointContainer.getSavePoint().getTimestamp());
      assertEquals(INCREMENTAL_LOAD, savePointContainer.getJobMode());
      assertEquals(0, testWriter.getItems().size());
    }
  }

  @Test
  public void emptyInitialJobTest() {
    List<ChangedEntityIdentifier<TimestampSavePoint>> identifiers = new ArrayList<>(
        Collections.emptyList());
    List<TestEntity> entities = new ArrayList<>(Arrays.asList(entity1, entity2, entity3, entity4));
    runInitialJob(identifiers, entities);
    assertFalse(savePointContainerService.savePointContainerExists());
  }

  @Before
  public void beforeMethod() throws IOException {
    lastRunDirHelper.createSavePointContainerFolder();
  }

  @After
  public void afterMethod() throws IOException {
    lastRunDirHelper.deleteSavePointContainerFolder();
  }

  private void runInitialJob(
      List<ChangedEntityIdentifier<TimestampSavePoint>> identifiers,
      List<TestEntity> entities) {
    runTestJob(identifiers, entities);
  }

  private void runResumeInitialJob(List<ChangedEntityIdentifier<TimestampSavePoint>> identifiers,
      List<TestEntity> entities) {
    runTestJob(identifiers, entities);
  }

  private void runIncrementalJob(
      List<ChangedEntityIdentifier<TimestampSavePoint>> identifiers,
      List<TestEntity> entities) {
    runTestJob(identifiers, entities);
  }

  private void runTestJob(
      List<ChangedEntityIdentifier<TimestampSavePoint>> identifiers,
      List<TestEntity> entities) {
    JobRunner.run(createTestJobModule(identifiers, entities));
  }

  private TestModule createTestJobModule(
      List<ChangedEntityIdentifier<TimestampSavePoint>> identifiers,
      List<TestEntity> entities
  ) {
    TestModule testModule = new TestModule(getModuleArgs());
    testModule.setChangedEntityService(
        identifier -> {
          if (identifier.getId().equals(BROKEN_ENTITY_ID)) {
            throw new RuntimeException("Broken entity!!!");
          }
          return entities.stream().filter(entity -> entity.getId().equals(identifier.getId()))
              .findAny().get();
        });
    testModule.setChangedEntitiesIdentifiers(new TestChangedIdentifiersService(identifiers));
    testWriter.reset();
    testModule.setBulkWriter(testWriter);
    return testModule;
  }

  private String[] getModuleArgs() {
    String configFilePath = Paths.get("src", "test", "resources", "config.yaml").normalize()
        .toAbsolutePath().toString();
    return new String[]{"-c", configFilePath, "-l",
        lastRunDirHelper.getSavepointContainerFolder().toString()};
  }

}
