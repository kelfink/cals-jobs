package gov.ca.cwds.jobs.common.job.impl;

import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.job.utils.TimeSpentUtil;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Created by Alexander Serbin on 3/19/2018.
 */
public class TimeLeftEstimationProvider {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TimeSpentUtil.class);

    private final List<JobBatch> jobBatches;
    private LocalDateTime jobStartTime;
    private int finishedBatchNumber;

    TimeLeftEstimationProvider(List<JobBatch> jobBatches, LocalDateTime jobStartTime, int finishedBatchNumber) {
        this.jobBatches = jobBatches;
        this.jobStartTime = jobStartTime;
        this.finishedBatchNumber = finishedBatchNumber;
    }

    public long get() {
        return (calculateTotalItems() - calculateTotalItemsProcessed()) * getEstimatedTimePerItem();
    }

    private int calculateTotalItems() {
        return jobBatches.parallelStream().mapToInt(batch -> batch.getChangedEntityIdentifiers().size()).sum();
    }

    private long getEstimatedTimePerItem() {
        long totalAlreadySpent = jobStartTime.until(LocalDateTime.now(), ChronoUnit.MILLIS);
        int totalItemsProcessed = calculateTotalItemsProcessed();
        return totalItemsProcessed != 0 ? totalAlreadySpent / totalItemsProcessed: 0;
    }

    private int calculateTotalItemsProcessed() {
        int totalItemsProcessed = 0;
        for (int i = 0; i <= finishedBatchNumber; i++) {
           totalItemsProcessed += jobBatches.get(i).getSize();
        }
        return totalItemsProcessed;
    }

}
