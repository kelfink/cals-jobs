package gov.ca.cwds.jobs.common.job;

import static org.junit.Assert.assertEquals;

import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.batch.BatchPreProcessorImpl;
import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import org.junit.Test;

/**
 * Created by Alexander Serbin on 3/9/2018.
 */
public class BatchPreprocessorTest {

  public static final LocalDateTime TIMESTAMP_4 = LocalDateTime.of(2011, 1, 4, 10, 6, 20);

  @Test
  public void test_batch_size_1() {
    BatchPreProcessorImpl jobBatchPreProcessor = new BatchPreProcessorImpl();
    int batchSize = 1;
    jobBatchPreProcessor.setBatchSize(batchSize);
    ChangedEntityIdentifier changedEntityIdentifier = new ChangedEntityIdentifier("testId",
        RecordChangeOperation.I,
        LocalDateTime.of(2015, 3, 4, 5, 6, 20));
    Stream<ChangedEntityIdentifier> identifiers = Stream.of(changedEntityIdentifier);
    List<JobBatch> jobBatches = jobBatchPreProcessor.buildJobBatches(identifiers);
    assertEquals(1, jobBatches.size());
    assertEquals(1, jobBatches.get(0).getChangedEntityIdentifiers().size());
    assertEquals(LocalDateTime.of(2015, 3, 4, 5, 6, 20), jobBatches.get(0).getTimestamp());
  }

  @Test
  public void test_empty_dates_and_less_items_then_batch_number() {
    BatchPreProcessorImpl jobBatchPreProcessor = new BatchPreProcessorImpl();
    int batchSize = 5;
    jobBatchPreProcessor.setBatchSize(batchSize);
    Stream<ChangedEntityIdentifier> identifiers = Stream.of(
        createNoDateChangedEntityIdentifier(),
        createNoDateChangedEntityIdentifier(),
        createNoDateChangedEntityIdentifier());
    List<JobBatch> jobBatches = jobBatchPreProcessor.buildJobBatches(identifiers);
    assertEquals(1, jobBatches.size());
    assertEquals(3, jobBatches.get(0).getChangedEntityIdentifiers().size());
    assertEquals(null, jobBatches.get(0).getTimestamp());
  }

  @Test
  public void test_empty_dates_and_exact_count_as_then_batch_number() {
    BatchPreProcessorImpl jobBatchPreProcessor = new BatchPreProcessorImpl();
    final int batchSize = 2;
    jobBatchPreProcessor.setBatchSize(batchSize);
    Stream<ChangedEntityIdentifier> identifiers = Stream.of(
        createNoDateChangedEntityIdentifier(),
        createNoDateChangedEntityIdentifier()
    );
    List<JobBatch> jobBatches = jobBatchPreProcessor.buildJobBatches(identifiers);
    assertEquals(1, jobBatches.size());
    assertEquals(batchSize, jobBatches.get(0).getChangedEntityIdentifiers().size());
    assertEquals(null, jobBatches.get(0).getTimestamp());
  }

  @Test
  public void test_empty_dates_and_items_count_more_then_batch_number() {
    BatchPreProcessorImpl jobBatchPreProcessor = new BatchPreProcessorImpl();
    int batchSize = 2;
    jobBatchPreProcessor.setBatchSize(batchSize);
    Stream<ChangedEntityIdentifier> identifiers = Stream.of(
        createNoDateChangedEntityIdentifier(),
        createNoDateChangedEntityIdentifier(),
        createNoDateChangedEntityIdentifier()
    );
    List<JobBatch> jobBatches = jobBatchPreProcessor.buildJobBatches(identifiers);
    assertEquals(batchSize, jobBatches.size());
    assertEquals(null, jobBatches.get(0).getTimestamp());
    assertEquals(batchSize, jobBatches.get(0).getChangedEntityIdentifiers().size());
    assertEquals(null, jobBatches.get(1).getTimestamp());
    assertEquals(1, jobBatches.get(1).getChangedEntityIdentifiers().size());
  }

  @Test
  public void test_empty_dates_less_then_batch_number_and_some_not_empty_dates_more_then_batch_number() {
    BatchPreProcessorImpl jobBatchPreProcessor = new BatchPreProcessorImpl();
    int batchSize = 3;
    jobBatchPreProcessor.setBatchSize(batchSize);
    ChangedEntityIdentifier changedEntityIdentifier1 = new ChangedEntityIdentifier("testId1",
        RecordChangeOperation.I,
        LocalDateTime.of(2015, 3, 4, 5, 6, 20));
    ChangedEntityIdentifier changedEntityIdentifier2 = new ChangedEntityIdentifier("testId2",
        RecordChangeOperation.I,
        LocalDateTime.of(2013, 5, 8, 1, 10, 25));
    ChangedEntityIdentifier changedEntityIdentifier3 = new ChangedEntityIdentifier("testId3",
        RecordChangeOperation.I,
        LocalDateTime.of(2017, 8, 5, 5, 6, 10));
    ChangedEntityIdentifier changedEntityIdentifier4 = new ChangedEntityIdentifier("testId4",
        RecordChangeOperation.I,
        LocalDateTime.of(2011, 1, 4, 10, 6, 20));
    Stream<ChangedEntityIdentifier> identifiers = Stream.of(
        createNoDateChangedEntityIdentifier()
        , changedEntityIdentifier1
        , createNoDateChangedEntityIdentifier()
        , changedEntityIdentifier2
        , changedEntityIdentifier3
        , changedEntityIdentifier4
        , createNoDateChangedEntityIdentifier()
        , createNoDateChangedEntityIdentifier()
    );
    List<JobBatch> jobBatches = jobBatchPreProcessor.buildJobBatches(identifiers);
    assertEquals(4, jobBatches.size());
    assertEquals(null, jobBatches.get(0).getTimestamp());
    assertEquals(batchSize, jobBatches.get(0).getChangedEntityIdentifiers().size());
    assertEquals(null, jobBatches.get(1).getTimestamp());
    assertEquals(1, jobBatches.get(1).getChangedEntityIdentifiers().size());
    assertEquals(createNoDateChangedEntityIdentifier(),
        jobBatches.get(1).getChangedEntityIdentifiers().stream().findAny().get());
    assertEquals(LocalDateTime.of(2015, 3, 4, 5, 6, 20),
        jobBatches.get(2).getTimestamp());
    assertEquals(batchSize, jobBatches.get(2).getChangedEntityIdentifiers().size());
    assertEquals(batchSize,
        getIdentifiersCount(jobBatches.get(2), changedEntityIdentifier1, changedEntityIdentifier2,
            changedEntityIdentifier4));
    assertEquals(LocalDateTime.of(2017, 8, 5, 5, 6, 10),
        jobBatches.get(3).getTimestamp());
    assertEquals(1, jobBatches.get(3).getChangedEntityIdentifiers().size());
    assertEquals(1, getIdentifiersCount(jobBatches.get(3), changedEntityIdentifier3));
  }

  @Test
  public void test_not_empty_dates_less_then_batch_number() {
    BatchPreProcessorImpl jobBatchPreProcessor = new BatchPreProcessorImpl();
    int batchSize = 3;
    jobBatchPreProcessor.setBatchSize(batchSize);
    ChangedEntityIdentifier changedEntityIdentifier1 = new ChangedEntityIdentifier("testId1",
        RecordChangeOperation.I,
        LocalDateTime.of(2015, 3, 4, 5, 6, 20));
    ChangedEntityIdentifier changedEntityIdentifier2 = new ChangedEntityIdentifier("testId2",
        RecordChangeOperation.I,
        LocalDateTime.of(2013, 5, 8, 1, 10, 25));
    Stream<ChangedEntityIdentifier> identifiers = Stream
        .of(changedEntityIdentifier1, changedEntityIdentifier2);
    List<JobBatch> jobBatches = jobBatchPreProcessor.buildJobBatches(identifiers);
    assertEquals(1, jobBatches.size());
    assertEquals(LocalDateTime.of(2015, 3, 4, 5, 6, 20),
        jobBatches.get(0).getTimestamp());
    assertEquals(2, jobBatches.get(0).getChangedEntityIdentifiers().size());
    assertEquals(2,
        getIdentifiersCount(jobBatches.get(0), changedEntityIdentifier1, changedEntityIdentifier2));
  }

  @Test
  public void complexBatchPrepossessingTest() {
    BatchPreProcessorImpl jobBatchPreProcessor = new BatchPreProcessorImpl();
    int batchSize = 2;

    jobBatchPreProcessor.setBatchSize(batchSize);
    ChangedEntityIdentifier changedEntityIdentifier1 = new ChangedEntityIdentifier("testId1",
        RecordChangeOperation.I,
        LocalDateTime.of(2015, 3, 4, 5, 6, 20));
    ChangedEntityIdentifier changedEntityIdentifier2 = new ChangedEntityIdentifier("testId2",
        RecordChangeOperation.I,
        LocalDateTime.of(2013, 5, 8, 1, 10, 25));
    ChangedEntityIdentifier changedEntityIdentifier3 = new ChangedEntityIdentifier("testId3",
        RecordChangeOperation.I,
        LocalDateTime.of(2017, 8, 5, 5, 6, 10));
    ChangedEntityIdentifier changedEntityIdentifier4_1 = new ChangedEntityIdentifier("testId41",
        RecordChangeOperation.I,
        TIMESTAMP_4);
    ChangedEntityIdentifier changedEntityIdentifier4_2 = new ChangedEntityIdentifier("testId42",
        RecordChangeOperation.I,
        TIMESTAMP_4);
    ChangedEntityIdentifier changedEntityIdentifier4_3 = new ChangedEntityIdentifier("testId43",
        RecordChangeOperation.I,
        TIMESTAMP_4);
    ChangedEntityIdentifier changedEntityIdentifier5 = new ChangedEntityIdentifier("testId5",
        RecordChangeOperation.I,
        LocalDateTime.of(2017, 11, 5, 10, 6, 20));

    Stream<ChangedEntityIdentifier> identifiers = Stream.of(
        changedEntityIdentifier5,
        createNoDateChangedEntityIdentifier(),
        changedEntityIdentifier1,
        createNoDateChangedEntityIdentifier(),
        changedEntityIdentifier2,
        createNoDateChangedEntityIdentifier(),
        changedEntityIdentifier3,
        changedEntityIdentifier4_1,
        changedEntityIdentifier4_2,
        changedEntityIdentifier4_3,
        createNoDateChangedEntityIdentifier(),
        createNoDateChangedEntityIdentifier()
    );
    List<JobBatch> jobBatches = jobBatchPreProcessor.buildJobBatches(identifiers);
    assertEquals(7, jobBatches.size());
    assertEquals(null, jobBatches.get(0).getTimestamp());
    assertEquals(batchSize, jobBatches.get(0).getChangedEntityIdentifiers().size());
    assertEquals(null, jobBatches.get(1).getTimestamp());
    assertEquals(batchSize, jobBatches.get(1).getChangedEntityIdentifiers().size());
    assertEquals(null, jobBatches.get(2).getTimestamp());
    assertEquals(1, jobBatches.get(2).getChangedEntityIdentifiers().size());

    assertEquals(null, jobBatches.get(3).getTimestamp());
    assertEquals(batchSize, jobBatches.get(3).getChangedEntityIdentifiers().size());
    assertEquals(batchSize, getIdentifiersCount(jobBatches.get(3), changedEntityIdentifier4_1,
        changedEntityIdentifier4_2, changedEntityIdentifier4_3));

    assertEquals(TIMESTAMP_4, jobBatches.get(4).getTimestamp());
    assertEquals(1, jobBatches.get(4).getChangedEntityIdentifiers().size());
    assertEquals(1, getIdentifiersCount(jobBatches.get(4), changedEntityIdentifier4_1,
        changedEntityIdentifier4_2, changedEntityIdentifier4_3));

    assertEquals(LocalDateTime.of(2015, 3, 4, 5, 6, 20), jobBatches.get(5).getTimestamp());
    assertEquals(batchSize, jobBatches.get(5).getChangedEntityIdentifiers().size());
    assertEquals(batchSize,
        getIdentifiersCount(jobBatches.get(5), changedEntityIdentifier1, changedEntityIdentifier2));

    assertEquals(LocalDateTime.of(2017, 11, 5, 10, 6, 20), jobBatches.get(6).getTimestamp());
    assertEquals(batchSize, jobBatches.get(6).getChangedEntityIdentifiers().size());
    assertEquals(batchSize,
        getIdentifiersCount(jobBatches.get(6), changedEntityIdentifier3, changedEntityIdentifier5));

  }

  private ChangedEntityIdentifier createNoDateChangedEntityIdentifier() {
    return new ChangedEntityIdentifier("testEmtyDateId", RecordChangeOperation.I, null);
  }

  private long getIdentifiersCount(JobBatch batch, ChangedEntityIdentifier... identifiers) {
    return batch.getChangedEntityIdentifiers().stream().filter(o ->
        {
          for (ChangedEntityIdentifier identifier : identifiers) {
            if (identifier == o) {
              return true;
            }
          }
          return false;
        }
    ).count();
  }

}
