package gov.ca.cwds.jobs.common.job.impl;

import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Alexander Serbin on 3/19/2018.
 */
public class JobTimeReportTest {

    @Test
    public void completionPercentTest() throws InterruptedException {
        ChangedEntityIdentifier id = new ChangedEntityIdentifier("1", RecordChangeOperation.I, null);
        List<ChangedEntityIdentifier> list = Arrays.asList(id, id, id);
        JobTimeReport jobTimeReport = new JobTimeReport(Arrays.asList(new JobBatch(list), new JobBatch(list)));
        assertEquals(50, jobTimeReport.getCompletionPercent(0), 0.0000001);
    }

}