package gov.ca.cwds.jobs.common.job.impl;

import com.google.inject.Inject;
import com.google.inject.Injector;
import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.batch.JobBatchPreProcessor;
import gov.ca.cwds.jobs.common.exception.JobExceptionHandler;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.identifier.impl.ChangedIdentifiersProvider;
import gov.ca.cwds.jobs.common.job.ChangedEntitiesService;
import gov.ca.cwds.jobs.common.job.Job;
import gov.ca.cwds.jobs.common.job.JobReader;
import gov.ca.cwds.jobs.common.job.JobWriter;
import gov.ca.cwds.jobs.common.job.timestamp.TimestampOperator;
import gov.ca.cwds.jobs.common.job.utils.ConsumerCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public class JobImpl<T> implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobImpl.class);

    @Inject
    private TimestampOperator timestampOperator;

    @Inject
    private ChangedEntitiesService changedEntitiesService;

    @Inject
    private ChangedIdentifiersProvider changedIdentifiersProvider;

    @Inject
    private JobBatchPreProcessor jobBatchPreProcessor;

    @Inject
    private Injector injector;

    @Override
    public void run() {
        try {
            processBatches(splitEntitiesByBatches());
            LocalDateTime now = LocalDateTime.now();
            timestampOperator.writeTimestamp(now);
            LOGGER.info("Updating job timestamp to the current moment {}", now);
            LOGGER.info(String.format("Added %s entities to the Elastic Search index", ConsumerCounter.getCounter()));
        } catch (RuntimeException e) {
            LOGGER.error("ERROR: ", e.getMessage(), e);
        } finally {
            JobExceptionHandler.reset();
            close();
        }
    }

    private void processBatches(List<JobBatch> jobBatches) {
        for (int batchNumber = 0; batchNumber < jobBatches.size(); batchNumber ++) {
            createBatchJob(jobBatches.get(batchNumber)).run();
            if (!JobExceptionHandler.isExceptionHappened()) {
               timestampOperator.writeTimestamp(jobBatches.get(batchNumber).getTimestamp());
               LOGGER.info(getCompletionPercent(jobBatches, batchNumber) + "% complete");
               LOGGER.info("Save point has been reached. Save point batch timestamp is " + jobBatches.get(batchNumber).getTimestamp());
            } else {
               LOGGER.error("Exception occured during batch processing. Job has been terminated." +
                       " Batch timestamp " + jobBatches.get(batchNumber).getTimestamp() + "has not been recorded");
               throw new RuntimeException("Exception occured during batch processing");
            }
        }
    }

    private List<JobBatch> splitEntitiesByBatches() {
        LocalDateTime startTime = LocalDateTime.now();
        Stream<ChangedEntityIdentifier> identifiers = changedIdentifiersProvider.get();
        List<JobBatch> jobBatches = jobBatchPreProcessor.buildJobBatches(identifiers);
        printJobBatchesInformation(jobBatches, startTime);
        return jobBatches;
    }

    private String getCompletionPercent(List<JobBatch> jobBatches, float batchNumber) {
        return new DecimalFormat("#0.00").format((batchNumber + 1)/jobBatches.size() * 100);
    }

    private void printJobBatchesInformation(List<JobBatch> jobBatches, LocalDateTime startTime) {
        if (LOGGER.isInfoEnabled() && !jobBatches.isEmpty()) {
            LOGGER.info("*** Batches ***");
            jobBatches.forEach(batch->LOGGER.info(batch.toString()));
            LOGGER.info("*** End of Batches");
            printTimeSpent("batches distribution", startTime);
        }
    }

    private void printTimeSpent(String workDescription, LocalDateTime startTime) {
        long hours = startTime.until(LocalDateTime.now(), ChronoUnit.HOURS);
        long minutes = startTime.until(LocalDateTime.now(), ChronoUnit.MINUTES);
        long seconds = startTime.until(LocalDateTime.now(), ChronoUnit.SECONDS);
        long milliseconds = startTime.until(LocalDateTime.now(), ChronoUnit.MILLIS);
        if (hours > 0) {
            LOGGER.info(workDescription + " {} hr", hours);
        } else if (minutes > 0) {
            LOGGER.info(workDescription + " {} min", minutes);
        } else if (seconds > 0) {
            LOGGER.info(workDescription + " {} sec", seconds);
        } else {
            LOGGER.info(workDescription + " {} ms", milliseconds);
        }
    }

    private AsyncReadWriteJob createBatchJob(JobBatch batch) {
        return new AsyncReadWriteJob(createJobReader(batch.getChangedEntityIdentifiers()),
                injector.getInstance(JobWriter.class));
    }

    private JobReader<T> createJobReader(List<ChangedEntityIdentifier> identifiers) {
        Iterator<T> entitiesIterator = changedEntitiesService.loadEntities(identifiers).iterator();
        return () -> entitiesIterator.hasNext() ? entitiesIterator.next() : null;
    }

}
