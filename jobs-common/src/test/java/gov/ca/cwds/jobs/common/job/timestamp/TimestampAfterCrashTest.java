package gov.ca.cwds.jobs.common.job.timestamp;

import gov.ca.cwds.jobs.common.job.JobReader;
import gov.ca.cwds.jobs.common.job.JobWriter;
import gov.ca.cwds.jobs.common.job.TestModule;
import gov.ca.cwds.jobs.common.job.impl.JobRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static gov.ca.cwds.jobs.common.job.timestamp.LastRunDirHelper.getLastRunDir;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander Serbin on 2/14/2018.
 */
public class TimestampAfterCrashTest {

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
        String configFilePath = Paths.get("src","test", "resources", "config.yaml").normalize().toAbsolutePath().toString();
        String[] args = new String[] {"-c", configFilePath, "-l", getLastRunDir().toString()};
        JobReader jobReader = () -> null;
        JobWriter jobWriter = items -> {};
        JobRunner.run(new TestModule(args, jobReader, jobWriter));
        assertTrue(timestampOperator.timeStampExists());
        LocalDateTime timestamp = timestampOperator.readTimestamp();
        assertTrue(timestamp.isBefore(LocalDateTime.now()));
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

    private void runCrashingJob() {
        String configFilePath = Paths.get("src", "test", "resources", "config.yaml").normalize().toAbsolutePath().toString();
        String[] args = new String[]{"-c", configFilePath, "-l", getLastRunDir().toString()};
        JobReader jobReader = () -> {
            if (1 == 1) {
                throw new IllegalStateException();
            }
            return null;
        };
        JobWriter jobWriter = items -> {};
        JobRunner.run(new TestModule(args, jobReader, jobWriter));
    }

}
