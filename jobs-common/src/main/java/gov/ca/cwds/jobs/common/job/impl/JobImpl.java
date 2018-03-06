package gov.ca.cwds.jobs.common.job.impl;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.exception.JobExceptionHandler;
import gov.ca.cwds.jobs.common.job.Job;
import gov.ca.cwds.jobs.common.job.JobReader;
import gov.ca.cwds.jobs.common.job.JobWriter;
import gov.ca.cwds.jobs.common.job.timestamp.TimestampOperator;
import gov.ca.cwds.jobs.common.job.utils.ConsumerCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public class JobImpl implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobImpl.class);

    @Inject
    private TimestampOperator timestampOperator;

    @Inject
    private JobReader jobReader;

    @Inject
    private JobWriter jobWriter;

    @Override
    public void run() {
        try {
            //Stream<ChangedEntitiesInformation> changedEntities = changedEntitiesProvider.findChangedentities
            //List<JobBatch> jobPreProcessor.buildJobBatches();
            //for (batch: batches) {
            //   run job(batch)
            //   set batch timestamp
            //}
            new AsyncReadWriteJob(jobReader, jobWriter).run();
            if (!JobExceptionHandler.isExceptionHappened()) {
                timestampOperator.writeTimestamp(LocalDateTime.now());
            }
            LOGGER.info(String.format("Added %s entities to ES bulk uploader", ConsumerCounter.getCounter()));
        } catch (RuntimeException e) {
            LOGGER.error("ERROR: ", e.getMessage(), e);
            System.exit(1);
        } finally {
            JobExceptionHandler.reset();
            close();
        }
    }

}
