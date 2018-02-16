package gov.ca.cwds.jobs.common.exception;

import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 2/15/2018.
 */

public class JobExceptionHandler {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(JobExceptionHandler.class);

    private static volatile boolean exceptionHappened;

    public static synchronized void handleException(String message, Exception e) {
       LOGGER.error(message, e);
       exceptionHappened = true;
       throw new JobsException(message, e);
    }

    public static synchronized void handleException(Exception e) {
        handleException("Exception occured ", e);
    }

    public static boolean isExceptionHappened() {
        return exceptionHappened;
    }

    public static synchronized void reset() {
        exceptionHappened = false;
    }
}
