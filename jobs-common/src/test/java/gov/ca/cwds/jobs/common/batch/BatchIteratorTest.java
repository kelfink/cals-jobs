package gov.ca.cwds.jobs.common.batch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import gov.ca.cwds.jobs.common.JobMode;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.api.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.job.TestChangedIdentifiersService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

/**
 * Created by Alexander Serbin on 3/30/2018.
 */
public class BatchIteratorTest {

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
    ChangedEntityIdentifier sameTimestampIdentifier = new ChangedEntityIdentifier("testId",
        RecordChangeOperation.I,
        timestamp);
    ChangedEntityIdentifier differentTimestampIdentifier = new ChangedEntityIdentifier("testId",
        RecordChangeOperation.I,
        differentTimestamp);
    List<ChangedEntityIdentifier> identifiers = new ArrayList<>(Arrays.asList(
        sameTimestampIdentifier,
        sameTimestampIdentifier,
        sameTimestampIdentifier,
        sameTimestampIdentifier,
        sameTimestampIdentifier,
        differentTimestampIdentifier
    ));
    JobBatchIterator jobIterator = prepareBatchIterator(3,
        new TestChangedIdentifiersService(identifiers));
    List<JobBatch> firstPortion = jobIterator.getNextPortion();
    assertEquals(1, firstPortion.size());
    assertEquals(5, firstPortion.get(0).getSize());
    assertEquals(timestamp, firstPortion.get(0).getTimestamp());
    List<JobBatch> secondPortion = jobIterator.getNextPortion();
    assertEquals(1, secondPortion.size());
    assertEquals(1, secondPortion.get(0).getSize());
    assertEquals(differentTimestamp, secondPortion.get(0).getTimestamp());
  }

  @Test
  public void emptyTimestampsLessThenOnePage() {
    ChangedEntityIdentifier emptyTimestampIdentifier = createEmptyIdentifier();
    JobBatchIterator jobIterator = prepareBatchIterator(3,
        new TestChangedIdentifiersService(Arrays.asList(emptyTimestampIdentifier,
            emptyTimestampIdentifier)));
    List<JobBatch> portion = jobIterator.getNextPortion();
    assertEquals(1, portion.size());
    assertEquals(2, portion.get(0).getSize());
    assertEquals(null, portion.get(0).getTimestamp());
    assertEquals(null, portion.get(0).getChangedEntityIdentifiers().get(0).getTimestamp());
    assertEquals(null, portion.get(0).getChangedEntityIdentifiers().get(1).getTimestamp());
  }

  @Test
  public void emptyTimestampsExactOnePageSize() {
    ChangedEntityIdentifier emptyTimestampIdentifier = createEmptyIdentifier();
    JobBatchIterator jobIterator = prepareBatchIterator(3,
        new TestChangedIdentifiersService(Arrays.asList(emptyTimestampIdentifier,
            emptyTimestampIdentifier, emptyTimestampIdentifier)));
    List<JobBatch> portion = jobIterator.getNextPortion();
    assertEquals(1, portion.size());
    assertEquals(3, portion.get(0).getSize());
    assertEquals(null, portion.get(0).getTimestamp());
    assertEquals(null, portion.get(0).getChangedEntityIdentifiers().get(0).getTimestamp());
    assertEquals(null, portion.get(0).getChangedEntityIdentifiers().get(1).getTimestamp());
    assertEquals(null, portion.get(0).getChangedEntityIdentifiers().get(2).getTimestamp());
  }

  @Test
  public void emptyTimestampsLessThenOnePageSizeAndSomeNotEmpty() {
    ChangedEntityIdentifier emptyTimestampIdentifier = createEmptyIdentifier();
    LocalDateTime timestamp1 = LocalDateTime.of(2013, 5, 8, 1, 10, 25);
    LocalDateTime timestamp2 = LocalDateTime.of(2014, 6, 1, 2, 10, 13);
    JobBatchIterator jobIterator = prepareBatchIterator(3,
        new TestChangedIdentifiersService(Arrays.asList(
            emptyTimestampIdentifier,
            emptyTimestampIdentifier,
            new ChangedEntityIdentifier("testId2",
                RecordChangeOperation.I,
                timestamp1),
            new ChangedEntityIdentifier("testId3",
                RecordChangeOperation.I,
                timestamp2)
        )));
    List<JobBatch> firstPortion = jobIterator.getNextPortion();
    assertEquals(1, firstPortion.size());
    assertEquals(3, firstPortion.get(0).getSize());
    assertEquals(timestamp1, firstPortion.get(0).getTimestamp());
    List<JobBatch> secondPortion = jobIterator.getNextPortion();
    assertEquals(1, secondPortion.size());
    assertEquals(1, secondPortion.get(0).getSize());
    assertEquals(timestamp2, secondPortion.get(0).getTimestamp());
    assertEquals("testId3", secondPortion.get(0).getChangedEntityIdentifiers().get(0).getId());
  }

  @Test
  public void manyPagesOfEqualTimestamps() {
    ChangedEntityIdentifier emptyTimestampIdentifier = createEmptyIdentifier();
    LocalDateTime timestamp = LocalDateTime.of(2013, 5, 8, 1, 10, 25);
    ChangedEntityIdentifier sameTimestampIdentifier = new ChangedEntityIdentifier("testId",
        RecordChangeOperation.I,
        timestamp);
    JobBatchIterator jobIterator = prepareBatchIterator(2,
        new TestChangedIdentifiersService(Arrays.asList(
            sameTimestampIdentifier,
            sameTimestampIdentifier,
            sameTimestampIdentifier,
            sameTimestampIdentifier,
            sameTimestampIdentifier
        )));
    List<JobBatch> portion = jobIterator.getNextPortion();
    assertEquals(3, portion.size());
    assertEquals(2, portion.get(0).getSize());
    assertEquals(null, portion.get(0).getTimestamp());
    assertEquals(2, portion.get(1).getSize());
    assertEquals(null, portion.get(1).getTimestamp());
    assertEquals(1, portion.get(2).getSize());
    assertEquals(timestamp, portion.get(2).getTimestamp());
  }

  private ChangedEntityIdentifier createEmptyIdentifier() {
    return new ChangedEntityIdentifier("testId",
        RecordChangeOperation.I,
        null);
  }

  private JobBatchIterator prepareBatchIterator(int batchSize,
      ChangedEntitiesIdentifiersService identifiersProvider) {
    JobBatchIteratorImpl jobIterator = new JobBatchIteratorImpl();
    jobIterator.setBatchSize(batchSize);
    jobIterator.setJobMode(JobMode.INITIAL_LOAD);
    jobIterator.setChangedEntitiesIdentifiersService(identifiersProvider);
    return jobIterator;
  }

}