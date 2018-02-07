package gov.ca.cwds.jobs.common.job.timestamp;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander Serbin on 2/6/2018.
 */
public class FilesystemTimestampOperatorTest {

    @Test
    public void readWriteTimestampTest() throws Exception {
        LocalDateTime timestamp = LocalDateTime.of(2018, 2, 6, 4, 14, 20);
        FilesystemTimestampOperator timestampOperator = new FilesystemTimestampOperator(getLastRunDir().toString());
        assertFalse(timestampOperator.timeStampExists());
        timestampOperator.writeTimestamp(timestamp);
        assertEquals(timestamp, timestampOperator.readTimestamp());
        assertTrue(timestampOperator.timeStampExists());
    }

    @Before
    public void beforeMethod() throws IOException {
        FileUtils.forceMkdir(getLastRunDir().toFile());
    }

    @After
    public void afterMethod() throws IOException {
        FileUtils.deleteDirectory(getLastRunDir().toFile());
    }

    private Path getLastRunDir() {
        return Paths.get("temp").normalize().toAbsolutePath();
    }





}