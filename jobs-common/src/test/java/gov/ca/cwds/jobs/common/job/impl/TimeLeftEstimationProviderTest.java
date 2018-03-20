package gov.ca.cwds.jobs.common.job.impl;

import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander Serbin on 3/19/2018.
 */
public class TimeLeftEstimationProviderTest {
    @Test
    public void getEstimationTest() throws Exception {
        TimeLeftEstimationProvider timeLeftEstimationProvider = createEstimationProvider(0);
        Thread.sleep(10);
        long timeLeftAfterBatch1 = timeLeftEstimationProvider.get();
        assertTrue(timeLeftAfterBatch1 > 10);
        timeLeftEstimationProvider = createEstimationProvider(1);
        Thread.sleep(10);
        long timeLeftAfterBatch2 = timeLeftEstimationProvider.get();
        assertTrue(timeLeftAfterBatch2 < timeLeftAfterBatch1);
        timeLeftEstimationProvider = createEstimationProvider(2);
        Thread.sleep(10);
        assertEquals(0, timeLeftEstimationProvider.get());
    }

    private TimeLeftEstimationProvider createEstimationProvider(int batchNumber) {
        ChangedEntityIdentifier id = new ChangedEntityIdentifier("1", RecordChangeOperation.I, null);
        List<ChangedEntityIdentifier> list = Arrays.asList(id, id, id);
        return new TimeLeftEstimationProvider(
                Arrays.asList(new JobBatch(list), new JobBatch(list), new JobBatch(list)),
                LocalDateTime.now(),
                batchNumber);
    }

}