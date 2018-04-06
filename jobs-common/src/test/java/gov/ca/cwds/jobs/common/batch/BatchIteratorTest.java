package gov.ca.cwds.jobs.common.batch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import gov.ca.cwds.jobs.common.JobMode;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.api.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.job.TestChangedIdentifiersService;
import java.time.LocalDateTime;
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
  public void emptyTimestampsLessThenOnePage() {
    ChangedEntityIdentifier emptyTimestampIdentifier = createEmptyIdentifier();
    JobBatchIterator jobIterator = prepareBatchIterator(3,
        new TestChangedIdentifiersService(Arrays.asList(emptyTimestampIdentifier,
            emptyTimestampIdentifier)));
    List<JobBatch> batch = jobIterator.getNextPortion();
    assertEquals(1, batch.size());
    assertEquals(2, batch.get(0).getSize());
    assertEquals(null, batch.get(0).getTimestamp());
    assertEquals(null, batch.get(0).getChangedEntityIdentifiers().get(0).getTimestamp());
    assertEquals(null, batch.get(0).getChangedEntityIdentifiers().get(1).getTimestamp());
  }

  @Test
  public void emptyTimestampsExactOnePageSize() {
    ChangedEntityIdentifier emptyTimestampIdentifier = createEmptyIdentifier();
    JobBatchIterator jobIterator = prepareBatchIterator(3,
        new TestChangedIdentifiersService(Arrays.asList(emptyTimestampIdentifier,
            emptyTimestampIdentifier, emptyTimestampIdentifier)));
    List<JobBatch> batch = jobIterator.getNextPortion();
    assertEquals(1, batch.size());
    assertEquals(3, batch.get(0).getSize());
    assertEquals(null, batch.get(0).getTimestamp());
    assertEquals(null, batch.get(0).getChangedEntityIdentifiers().get(0).getTimestamp());
    assertEquals(null, batch.get(0).getChangedEntityIdentifiers().get(1).getTimestamp());
    assertEquals(null, batch.get(0).getChangedEntityIdentifiers().get(2).getTimestamp());
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
    List<JobBatch> firstBatch = jobIterator.getNextPortion();
    assertEquals(1, firstBatch.size());
    assertEquals(3, firstBatch.get(0).getSize());
    assertEquals(timestamp1, firstBatch.get(0).getTimestamp());
    List<JobBatch> secondBatch = jobIterator.getNextPortion();
    assertEquals(1, secondBatch.size());
    assertEquals(1, secondBatch.get(0).getSize());
    assertEquals(timestamp2, secondBatch.get(0).getTimestamp());
    assertEquals("testId3", secondBatch.get(0).getChangedEntityIdentifiers().get(0).getId());
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
    List<JobBatch> batch = jobIterator.getNextPortion();
    assertEquals(3, batch.size());
    assertEquals(2, batch.get(0).getSize());
    assertEquals(null, batch.get(0).getTimestamp());
    assertEquals(2, batch.get(1).getSize());
    assertEquals(null, batch.get(1).getTimestamp());
    assertEquals(1, batch.get(2).getSize());
    assertEquals(timestamp, batch.get(2).getTimestamp());
  }

  private ChangedEntityIdentifier createEmptyIdentifier() {
    return new ChangedEntityIdentifier("testId",
        RecordChangeOperation.I,
        null);
  }

  private JobBatchIterator prepareBatchIterator(int batchSize,
      ChangedEntitiesIdentifiersService identifiersProvider) {
    JobBatchIteratorImpl jobIterator = new JobBatchIteratorImpl();
    jobIterator.setPageRequest(new PageRequest(0, batchSize));
    jobIterator.setJobMode(JobMode.INITIAL_LOAD);
    jobIterator.setChangedEntitiesIdentifiersService(identifiersProvider);
    return jobIterator;
  }

}