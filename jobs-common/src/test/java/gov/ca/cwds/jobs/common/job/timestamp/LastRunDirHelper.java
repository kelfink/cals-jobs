package gov.ca.cwds.jobs.common.job.timestamp;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Alexander Serbin on 2/14/2018.
 */
public final class LastRunDirHelper {

    private LastRunDirHelper() {}

    public static void createTimestampDirectory() throws IOException {
        FileUtils.forceMkdir(getLastRunDir().toFile());
    }

    public static void deleteTimestampDirectory() throws IOException {
        FileUtils.deleteDirectory(getLastRunDir().toFile());
    }

    public static Path getLastRunDir() {
        return Paths.get("temp").normalize().toAbsolutePath();
    }

}
