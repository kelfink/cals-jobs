package gov.ca.cwds.jobs.common.job.timestamp;

import gov.ca.cwds.jobs.common.BaseIndexerJob;
import gov.ca.cwds.jobs.common.job.TestIndexerJob;
import gov.ca.cwds.jobs.common.job.impl.AsyncReadWriteJob;
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
        BaseIndexerJob job = new TestIndexerJob(new AsyncReadWriteJob(() -> null, items -> {}));
        String configFilePath = Paths.get("src","test", "resources", "config.yaml").normalize().toAbsolutePath().toString();
        job.run(new String[] {"-c", configFilePath, "-l", getLastRunDir().toString()});
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
        TestIndexerJob job = new TestIndexerJob(new AsyncReadWriteJob(() -> {
            if (1 == 1) {
                throw new IllegalStateException();
            }
            return null;
        }, items -> {
        }));
        String configFilePath = Paths.get("src", "test", "resources", "config.yaml").normalize().toAbsolutePath().toString();
        job.run(new String[]{"-c", configFilePath, "-l", getLastRunDir().toString()});
    }

}
