package gov.ca.cwds.jobs.common.job.timestamp;

import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.batch.JobBatchPreProcessor;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.job.TestModule;
import gov.ca.cwds.jobs.common.job.impl.JobRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static gov.ca.cwds.jobs.common.job.timestamp.LastRunDirHelper.getLastRunDir;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander Serbin on 2/14/2018.
 */
public class JobTimestampTest {

    private FilesystemTimestampOperator timestampOperator;

    @Before
    public void beforeMethod() throws IOException {
        timestampOperator = new FilesystemTimestampOperator(getLastRunDir().toString());
        LastRunDirHelper.createTimestampDirectory();
    }

    @After
    public void afterMethod() throws IOException {
        LastRunDirHelper.deleteTimestampDirectory();
    }

    @Test
    public void test_timestamp_is_created_if_job_successful() throws IOException {
        assertFalse(timestampOperator.timeStampExists());
        JobRunner.run(new TestModule(getModuleArgs()));
        assertTrue(timestampOperator.timeStampExists());
        LocalDateTime timestamp = timestampOperator.readTimestamp();
        assertTrue(timestamp.until(LocalDateTime.now(), ChronoUnit.SECONDS) < 1);
    }

    @Test
    public void test_timestamp_is_not_created_if_crash_happens() throws IOException {
        assertFalse(timestampOperator.timeStampExists());
        runCrashingJob();
        assertFalse(timestampOperator.timeStampExists());
    }

    @Test
    public void test_timestamp_is_not_updated_if_crash_happens() throws IOException {
        assertFalse(timestampOperator.timeStampExists());
        LocalDateTime timestamp = LocalDateTime.of(2017, 1, 20, 5, 25);
        timestampOperator.writeTimestamp(timestamp);
        runCrashingJob();
        assertTrue(timestampOperator.timeStampExists());
        assertTrue(timestamp.equals(timestampOperator.readTimestamp()));
    }

    @Test
    public void test_all_timestamps_null() {
        assertFalse(timestampOperator.timeStampExists());
        TestModule testModule = new TestModule(getModuleArgs());
        testModule.setJobBatchPreProcessorClass(EmptyTimestampTestPreProcessor.class);
        JobRunner.run(testModule);
        assertTrue(timestampOperator.timeStampExists());
        LocalDateTime timestamp = timestampOperator.readTimestamp();
        assertTrue(timestamp.until(LocalDateTime.now(), ChronoUnit.SECONDS) < 1);
    }

    private void runCrashingJob() {
        TestModule testModule = new TestModule(getModuleArgs());
        testModule.setJobWriter(items -> {
            if (1 == 1) {
                throw new IllegalStateException();
            }
        });
        JobRunner.run(testModule);
    }

    private String[] getModuleArgs() {
        String configFilePath = Paths.get("src", "test", "resources", "config.yaml").normalize().toAbsolutePath().toString();
        return new String[]{"-c", configFilePath, "-l", getLastRunDir().toString()};
    }

    private static class EmptyTimestampTestPreProcessor implements JobBatchPreProcessor {

        @Override
        public List<JobBatch> buildJobBatches(Stream<ChangedEntityIdentifier> identifiers) {
            return Collections.singletonList(new JobBatch(identifiers, null));
        }
    }



}
