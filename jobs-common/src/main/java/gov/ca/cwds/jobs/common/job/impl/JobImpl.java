package gov.ca.cwds.jobs.common.job.impl;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.batch.BatchPreProcessor;
import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.exception.JobExceptionHandler;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.identifier.impl.ChangedIdentifiersProvider;
import gov.ca.cwds.jobs.common.job.ChangedEntityService;
import gov.ca.cwds.jobs.common.job.Job;
import gov.ca.cwds.jobs.common.job.timestamp.TimestampOperator;
import gov.ca.cwds.jobs.common.job.utils.ConsumerCounter;
import gov.ca.cwds.jobs.common.job.utils.TimeSpentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
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
    private ChangedEntityService<T> changedEntitiesService;

    @Inject
    private ChangedIdentifiersProvider changedIdentifiersProvider;

    @Inject
    private BatchPreProcessor jobBatchPreProcessor;

    @Inject
    private BatchProcessor<T> batchProcessor;

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
        JobTimeReport jobTimeReport = new JobTimeReport(jobBatches);
        batchProcessor.init();
        for (int batchNumber = 0; batchNumber < jobBatches.size(); batchNumber ++) {
            batchProcessor.process(jobBatches.get(batchNumber));
            if (!JobExceptionHandler.isExceptionHappened()) {
               timestampOperator.writeTimestamp(jobBatches.get(batchNumber).getTimestamp());
               if (LOGGER.isInfoEnabled()) {
                   jobTimeReport.printTimeReport(batchNumber);
               }
               LOGGER.info("Save point has been reached. Save point batch timestamp is " + jobBatches.get(batchNumber).getTimestamp());
            } else {
               LOGGER.error("Exception occured during batch processing. Job has been terminated." +
                       " Batch timestamp " + jobBatches.get(batchNumber).getTimestamp() + "has not been recorded");
               throw new RuntimeException("Exception occured during batch processing");
            }
        }
        jobTimeReport.printTimeSpent();
    }

    private List<JobBatch> splitEntitiesByBatches() {
        LocalDateTime startTime = LocalDateTime.now();
        Stream<ChangedEntityIdentifier> identifiers = changedIdentifiersProvider.get();
        List<JobBatch> jobBatches = jobBatchPreProcessor.buildJobBatches(identifiers);
        printJobBatchesInformation(jobBatches, startTime);
        return jobBatches;
    }

    private void printJobBatchesInformation(List<JobBatch> jobBatches, LocalDateTime startTime) {
        if (LOGGER.isInfoEnabled() && !jobBatches.isEmpty()) {
            LOGGER.info("*** Batches ***");
            jobBatches.forEach(batch->LOGGER.info(batch.toString()));
            LOGGER.info("*** End of Batches");
            TimeSpentUtil.printTimeSpent("Batches distribution", startTime);
        }
    }

    @Override
    public void close() {
       batchProcessor.destroy();
    }
}
