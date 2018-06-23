package gov.ca.cwds.jobs.common.batch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.TestChangedIdentifiersService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.mode.TimestampInitialModeImplementor;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePointService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

/**
 * Created by Alexander Serbin on 3/30/2018.
 */
public class TimestampIteratorIteratorTest {

  private TimestampSavePointService savePointService = new TimestampSavePointService();

  @Test
  public void noRecordsTest() {
    JobBatchIterator jobIterator = prepareBatchIterator(1,
        new TestChangedIdentifiersService(Collections.emptyList()));
    assertTrue(jobIterator.getNextPortion().isEmpty());
  }

  @Test
  public void savePointInSecondPageTest() {
    LocalDateTime timestamp = LocalDateTime.of(2013, 5, 8, 1, 10, 25);
    LocalDateTime differentTimestamp = LocalDateTime.of(2017, 6, 4, 1, 10, 22);
    ChangedEntityIdentifier<TimestampSavePoint> sameTimestampIdentifier = new ChangedEntityIdentifier<>(
        "testId",
        RecordChangeOperation.I,
        new TimestampSavePoint(timestamp));
    ChangedEntityIdentifier<TimestampSavePoint> differentTimestampIdentifier = new ChangedEntityIdentifier<>(
        "testId",
        RecordChangeOperation.I,
        new TimestampSavePoint(differentTimestamp));
    List<ChangedEntityIdentifier<TimestampSavePoint>> identifiers = new ArrayList<>(Arrays.asList(
        sameTimestampIdentifier,
        sameTimestampIdentifier,
        sameTimestampIdentifier,
        sameTimestampIdentifier,
        sameTimestampIdentifier,
        differentTimestampIdentifier
    ));
    JobBatchIterator<TimestampSavePoint> jobIterator = prepareBatchIterator(3,
        new TestChangedIdentifiersService(identifiers));
    List<JobBatch<TimestampSavePoint>> firstPortion = jobIterator.getNextPortion();
    assertEquals(1, firstPortion.size());
    assertEquals(5, firstPortion.get(0).getSize());
    assertEquals(timestamp, savePointService.defineSavepoint(firstPortion.get(0)).getTimestamp() /*firstPortion.get(0).getSavePoint()*/);
    List<JobBatch<TimestampSavePoint>> secondPortion = jobIterator.getNextPortion();
    assertEquals(1, secondPortion.size());
    assertEquals(1, secondPortion.get(0).getSize());
    assertEquals(differentTimestamp,
        savePointService.defineSavepoint(secondPortion.get(0)).getTimestamp());
  }

  @Test
  public void emptyTimestampsLessThenOnePage() {
    ChangedEntityIdentifier emptyTimestampIdentifier = createEmptyIdentifier();
    JobBatchIterator<TimestampSavePoint> jobIterator = prepareBatchIterator(3,
        new TestChangedIdentifiersService(Arrays.asList(emptyTimestampIdentifier,
            emptyTimestampIdentifier)));
    List<JobBatch<TimestampSavePoint>> portion = jobIterator.getNextPortion();
    assertEquals(1, portion.size());
    assertEquals(2, portion.get(0).getSize());
    assertEquals(null, savePointService.defineSavepoint(portion.get(0)).getTimestamp());
    assertEquals(new TimestampSavePoint(null),
        portion.get(0).getChangedEntityIdentifiers().get(0).getSavePoint());
    assertEquals(new TimestampSavePoint(null),
        portion.get(0).getChangedEntityIdentifiers().get(1).getSavePoint());
  }

  @Test
  public void emptyTimestampsExactOnePageSize() {
    ChangedEntityIdentifier<TimestampSavePoint> emptyTimestampIdentifier = createEmptyIdentifier();
    JobBatchIterator<TimestampSavePoint> jobIterator = prepareBatchIterator(3,
        new TestChangedIdentifiersService(Arrays.asList(emptyTimestampIdentifier,
            emptyTimestampIdentifier, emptyTimestampIdentifier)));
    List<JobBatch<TimestampSavePoint>> portion = jobIterator.getNextPortion();
    assertEquals(1, portion.size());
    assertEquals(3, portion.get(0).getSize());
    assertEquals(new TimestampSavePoint(null), savePointService.defineSavepoint(portion.get(0)));
    assertEquals(new TimestampSavePoint(null),
        portion.get(0).getChangedEntityIdentifiers().get(0).getSavePoint());
    assertEquals(new TimestampSavePoint(null),
        portion.get(0).getChangedEntityIdentifiers().get(1).getSavePoint());
    assertEquals(new TimestampSavePoint(null),
        portion.get(0).getChangedEntityIdentifiers().get(2).getSavePoint());
  }

  @Test
  public void emptyTimestampsLessThenOnePageSizeAndSomeNotEmpty() {
    ChangedEntityIdentifier<TimestampSavePoint> emptyTimestampIdentifier = createEmptyIdentifier();
    LocalDateTime timestamp1 = LocalDateTime.of(2013, 5, 8, 1, 10, 25);
    LocalDateTime timestamp2 = LocalDateTime.of(2014, 6, 1, 2, 10, 13);
    JobBatchIterator<TimestampSavePoint> jobIterator = prepareBatchIterator(3,
        new TestChangedIdentifiersService(Arrays.asList(
            emptyTimestampIdentifier,
            emptyTimestampIdentifier,
            new ChangedEntityIdentifier<>("testId2",
                RecordChangeOperation.I,
                new TimestampSavePoint(timestamp1)),
            new ChangedEntityIdentifier<>("testId3",
                RecordChangeOperation.I,
                new TimestampSavePoint(timestamp2))
        )));
    List<JobBatch<TimestampSavePoint>> firstPortion = jobIterator.getNextPortion();
    assertEquals(1, firstPortion.size());
    assertEquals(3, firstPortion.get(0).getSize());
    assertEquals(timestamp1, savePointService.defineSavepoint(firstPortion.get(0)).getTimestamp());
    List<JobBatch<TimestampSavePoint>> secondPortion = jobIterator.getNextPortion();
    assertEquals(1, secondPortion.size());
    assertEquals(1, secondPortion.get(0).getSize());
    assertEquals(timestamp2, savePointService.defineSavepoint(secondPortion.get(0)).getTimestamp());
    assertEquals("testId3", secondPortion.get(0).getChangedEntityIdentifiers().get(0).getId());
  }

  @Test
  public void manyPagesOfEqualTimestamps() {
    LocalDateTime timestamp = LocalDateTime.of(2013, 5, 8, 1, 10, 25);
    ChangedEntityIdentifier<TimestampSavePoint> sameTimestampIdentifier = new ChangedEntityIdentifier<>(
        "testId",
        RecordChangeOperation.I,
        new TimestampSavePoint(timestamp));
    JobBatchIterator<TimestampSavePoint> jobIterator = prepareBatchIterator(2,
        new TestChangedIdentifiersService(Arrays.asList(
            sameTimestampIdentifier,
            sameTimestampIdentifier,
            sameTimestampIdentifier,
            sameTimestampIdentifier,
            sameTimestampIdentifier
        )));
    List<JobBatch<TimestampSavePoint>> portion = jobIterator.getNextPortion();
    assertEquals(3, portion.size());
    assertEquals(2, portion.get(0).getSize());
    assertEquals(2, portion.get(1).getSize());
    assertEquals(1, portion.get(2).getSize());
    assertEquals(timestamp, savePointService.defineSavepoint(portion.get(2)).getTimestamp());
  }

  private ChangedEntityIdentifier<TimestampSavePoint> createEmptyIdentifier() {
    return new ChangedEntityIdentifier<>("testId",
        RecordChangeOperation.I,
        new TimestampSavePoint(null));
  }

  private JobBatchIterator<TimestampSavePoint> prepareBatchIterator(int batchSize,
      ChangedEntitiesIdentifiersService<TimestampSavePoint, TimestampSavePoint> identifiersProvider) {
    TimestampInitialModeImplementor jobBatchIterator = new TimestampInitialModeImplementor();
    jobBatchIterator.setBatchSize(batchSize);
    jobBatchIterator.setChangedEntitiesIdentifiersService(identifiersProvider);
    return jobBatchIterator;
  }

}