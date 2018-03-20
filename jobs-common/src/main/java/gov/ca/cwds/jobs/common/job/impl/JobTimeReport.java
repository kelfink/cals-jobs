package gov.ca.cwds.jobs.common.job.impl;

import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.job.utils.TimeSpentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Alexander Serbin on 3/19/2018.
 */
public class JobTimeReport {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobTimeReport.class);

    private final List<JobBatch> jobBatches;
    private LocalDateTime jobStartTime;


    public JobTimeReport(List<JobBatch> jobBatches) {
        this.jobBatches = jobBatches;
        this.jobStartTime = LocalDateTime.now();
    }

    public void printTimeSpent() {
        TimeSpentUtil.printTimeSpent("Overall batch processing", jobStartTime);
    }

    public void printTimeReport(int finishedBatchNumber) {
        LOGGER.info(new DecimalFormat("#0.00").format(getCompletionPercent(finishedBatchNumber)) + "% complete");
        TimeLeftEstimationProvider timeLeftEstimationProvider
                = new TimeLeftEstimationProvider(jobBatches, jobStartTime, finishedBatchNumber);
        long timeLeftEstimation = timeLeftEstimationProvider.get();
        LOGGER.info("Estimated time left in milliseconds - " + timeLeftEstimation);
        LOGGER.info("Estimated time left - " +  String.format("%d hours %d min %d sec",
                TimeUnit.MILLISECONDS.toHours(timeLeftEstimation),
                TimeUnit.MILLISECONDS.toMinutes(timeLeftEstimation),
                TimeUnit.MILLISECONDS.toSeconds(timeLeftEstimation) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeLeftEstimation))
        ));
    }

    float getCompletionPercent(int finishedBatchNumber) {
        return ((float)finishedBatchNumber + 1)/jobBatches.size() * 100;
    }

}
