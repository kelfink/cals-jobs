package gov.ca.cwds.jobs.util;

import java.text.MessageFormat;

import org.apache.logging.log4j.Logger;

import gov.ca.cwds.jobs.JobsException;

/**
 * Logging utilities for Neutron job classes.
 * 
 * @author CWDS API Team
 */
public class JobLogUtils {

  private JobLogUtils() {
    // No class instantiation.
  }

  /**
   * Format message and throw a runtime {@link JobsException}.
   * 
   * @param log class logger
   * @param e any Throwable
   * @param pattern MessageFormat pattern
   * @param args error message, excluding throwable message
   * @throws JobsException runtime exception
   */
  public static void throwFatalError(final Logger log, Throwable e, String pattern, Object... args)
      throws JobsException {
    final String msg = MessageFormat.format(pattern, args);
    log.fatal(msg, e);
    throw new JobsException(msg, e);
  }

  /**
   * Format message and throw a runtime {@link JobsException}.
   * 
   * @param log class logger
   * @param e any Throwable
   * @param message error message, excluding throwable message
   * @throws JobsException runtime exception
   */
  public static void throwFatalError(final Logger log, Throwable e, String message)
      throws JobsException {
    final String msg = MessageFormat.format("ERROR: {}: MSG: {}", message, e.getMessage()); // NOSONAR
    log.fatal(msg, e);
    throw new JobsException(msg, e);
  }

}
