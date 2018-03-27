package gov.ca.cwds.jobs.common.batch;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.inject.JobBatchSize;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.Validate;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public class BatchPreProcessorImpl implements BatchPreProcessor {

  @Inject
  @JobBatchSize
  private int batchSize;

  @Override
  public List<JobBatch> buildJobBatches(Stream<ChangedEntityIdentifier> identifiers) {
    Validate.isTrue(batchSize > 0, "Batch size is not passed");
    Map<Boolean, List<ChangedEntityIdentifier>> timestampEmptyAndAndNotEmptyIdentifiers =
        identifiers.collect(Collectors.partitioningBy(o -> o.getTimestamp() == null));
    List<JobBatch> jobBatches = handleEmptyIdentifiers(
        timestampEmptyAndAndNotEmptyIdentifiers.get(Boolean.TRUE));
    jobBatches.addAll(
        handleNotEmptyIdentifiers(timestampEmptyAndAndNotEmptyIdentifiers.get(Boolean.FALSE)));
    return jobBatches;
  }

  private <T, K extends Comparable<K>> Collector<T, ?, TreeMap<K, List<T>>> sortedGroupingBy(
      Function<T, K> function) {
    return Collectors.groupingBy(function,
        TreeMap::new, Collectors.toList());
  }

  private List<JobBatch> handleNotEmptyIdentifiers(List<ChangedEntityIdentifier> identifiers) {
    List<JobBatch> jobBatches = new ArrayList<>(identifiers.size() / batchSize);
    TreeMap<ChronoLocalDateTime<?>, List<ChangedEntityIdentifier>> identifiersMap = identifiers
        .stream().
            collect(sortedGroupingBy(ChangedEntityIdentifier::getTimestamp));
    OpenedJobBatchHolder openedJobBatchHolder = new OpenedJobBatchHolder(jobBatches);
    for (ChronoLocalDateTime timestamp : identifiersMap.keySet()) {
      List<List<ChangedEntityIdentifier>> partisionedIdentifiers = Lists
          .partition(identifiersMap.get(timestamp), batchSize);
      if (partisionedIdentifiers.size() > 1) {
        openedJobBatchHolder.encloseJobBatch();
        jobBatches
            .addAll(handleManyJobBatchesCase((LocalDateTime) timestamp, partisionedIdentifiers));
      }
      if (partisionedIdentifiers.size() == 1) {
        handleOneJobBatchCase(openedJobBatchHolder, (LocalDateTime) timestamp,
            partisionedIdentifiers.get(0));
      }
    }
    openedJobBatchHolder.encloseJobBatch();
    return jobBatches;
  }

  private void handleOneJobBatchCase(OpenedJobBatchHolder openedJobBatchHolder,
      LocalDateTime timestamp,
      List<ChangedEntityIdentifier> identifiers) {
    final List<JobBatch> jobBatches = openedJobBatchHolder.jobBatches;
    int newBatchSize = identifiers.size();
    if (newBatchSize == batchSize) {
      openedJobBatchHolder.encloseJobBatch();
      jobBatches.add(new JobBatch(identifiers, timestamp));
    } else {
      if (openedJobBatchHolder.existsOpenedJobBatch()) {
        if (openedJobBatchHolder.getOpenedJobBatch().getSize() + newBatchSize < batchSize) {
          openedJobBatchHolder.getOpenedJobBatch().addIdentifiers(identifiers);
        } else if (openedJobBatchHolder.getOpenedJobBatch().getSize() + newBatchSize == batchSize) {
          openedJobBatchHolder.getOpenedJobBatch().addIdentifiers(identifiers);
          openedJobBatchHolder.encloseJobBatch();
        } else {
          openedJobBatchHolder.encloseJobBatch();
          openedJobBatchHolder.push(new JobBatch(identifiers));
        }
      } else {
        openedJobBatchHolder.push(new JobBatch(identifiers));
      }
    }
  }

  private List<JobBatch> handleManyJobBatchesCase(LocalDateTime timestamp,
      List<List<ChangedEntityIdentifier>> partisionedIdentifiers) {
    List<JobBatch> timeStampBatch = partisionedIdentifiers.stream().
        map(list -> new JobBatch(list)).collect(Collectors.toList());
    if (timeStampBatch.size() > 1) {
      timeStampBatch.get(timeStampBatch.size() - 1).setTimestamp(timestamp);
    } else {
      timeStampBatch.get(0).setTimestamp(timestamp);
    }
    ;
    return timeStampBatch;
  }

  private List<JobBatch> handleEmptyIdentifiers(
      List<ChangedEntityIdentifier> changedEntityIdentifiers) {
    return Lists.partition(changedEntityIdentifiers, batchSize).stream().map(
        list -> new JobBatch(list)).collect(Collectors.toList());
  }

  private Stream<ChangedEntityIdentifier> getSortedStream(
      Stream<ChangedEntityIdentifier> identifiers) {
    return identifiers.sorted((id1, id2) -> {
      if (id1.getTimestamp() == null || id2.getTimestamp() == null) {
        if (id1.getTimestamp() == null && id2.getTimestamp() == null) {
          return 0;
        } else if (id1.getTimestamp() == null && id2.getTimestamp() != null) {
          return -1;
        } else {
          return 1;
        }
      } else {
        return id1.getTimestamp().compareTo(id2.getTimestamp());
      }
    });
  }

  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }

  private static class OpenedJobBatchHolder {

    private Queue<JobBatch> stack = new LinkedList<>();
    ;
    private List<JobBatch> jobBatches;

    public OpenedJobBatchHolder(List<JobBatch> jobBatches) {
      this.jobBatches = jobBatches;
    }

    public JobBatch encloseJobBatch() {
      JobBatch jobBatch = stack.poll();
      if (jobBatch != null) {
        jobBatch.calculateTimestamp();
        jobBatches.add(jobBatch);
      }
      return jobBatch;
    }

    public void push(JobBatch jobBatch) {
      stack.add(jobBatch);
    }

    public boolean existsOpenedJobBatch() {
      return !stack.isEmpty();
    }

    public JobBatch getOpenedJobBatch() {
      if (!existsOpenedJobBatch()) {
        throw new IllegalStateException("Batch preprocessing error: there is no opened job batch");
      }
      return stack.peek();
    }
  }

}
