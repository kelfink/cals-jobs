package gov.ca.cwds.jobs.common.job.timestamp;

import gov.ca.cwds.jobs.common.job.TestModule;
import gov.ca.cwds.jobs.common.job.impl.JobRunner;
import gov.ca.cwds.jobs.common.job.preprocessor.BatchSavePointTestPreprocessor;
import gov.ca.cwds.jobs.common.job.preprocessor.EmptyTimestampTestPreProcessor;
import gov.ca.cwds.jobs.common.job.preprocessor.SingleBatchPreprocessor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static gov.ca.cwds.jobs.common.job.preprocessor.BatchSavePointTestPreprocessor.BROKEN_ENTITY;
import static gov.ca.cwds.jobs.common.job.preprocessor.BatchSavePointTestPreprocessor.SECOND_TIMESTAMP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander Serbin on 2/14/2018.
 */
public class JobTimestampTest {

    private FilesystemTimestampOperator timestampOperator;
    private LastRunDirHelper lastRunDirHelper = new LastRunDirHelper("temp");

    @Before
    public void beforeMethod() throws IOException {
        timestampOperator = new FilesystemTimestampOperator(lastRunDirHelper.getLastRunDir().toString());
        lastRunDirHelper.createTimestampDirectory();
    }

    @After
    public void afterMethod() throws IOException {
        lastRunDirHelper.deleteTimestampDirectory();
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
    public void last_successfull_batch_save_point_test() {
        assertFalse(timestampOperator.timeStampExists());
        TestModule testModule = new TestModule(getModuleArgs());
        testModule.setJobBatchPreProcessorClass(BatchSavePointTestPreprocessor.class);
        testModule.setChangedEntityService(identifier -> {
            if (identifier == BROKEN_ENTITY) {
                return BROKEN_ENTITY;
            } else {
                return new Object();
            }
        });
        testModule.setBulkWriter(items -> {
            if (!items.isEmpty() && items.get(0) == BROKEN_ENTITY) {
                throw new IllegalStateException("Broken batch");
            };
        });
        JobRunner.run(testModule);
        assertTrue(timestampOperator.timeStampExists());
        assertEquals(SECOND_TIMESTAMP, timestampOperator.readTimestamp());
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
        testModule.setJobBatchPreProcessorClass(SingleBatchPreprocessor.class);
        testModule.setBulkWriter(items -> {
            if (1 == 1) {
                throw new IllegalStateException();
            }
        });
        JobRunner.run(testModule);
    }

    private String[] getModuleArgs() {
        String configFilePath = Paths.get("src", "test", "resources", "config.yaml").normalize().toAbsolutePath().toString();
        return new String[]{"-c", configFilePath, "-l", lastRunDirHelper.getLastRunDir().toString()};
    }

}
