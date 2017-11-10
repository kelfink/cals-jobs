package gov.ca.cwds.neutron.jetpack;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.jobs.exception.JobsException;
import gov.ca.cwds.jobs.exception.NeutronException;

/**
 * Logging utilities for Neutron.
 * 
 * @author CWDS API Team
 */
public final class JobLogs {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobLogs.class);

  private static final int DEFAULT_LOG_EVERY = 5000;

  private JobLogs() {
    // Static methods only; do not instantiate.
    // Evil singleton, blah, blah, blah ... I can't hear you ...
  }

  /**
   * Log every N records.
   * 
   * @param log Logger
   * @param logEvery log every N records
   * @param cntr record count
   * @param action action message (extract, transform, load, etc)
   * @param args variable message arguments
   */
  public static void logEvery(Logger log, int logEvery, int cntr, String action, Object... args) {
    if (cntr > 0 && (cntr % logEvery) == 0) {
      log.info("{} {} {}", action, cntr, args);
    }
  }

  /**
   * Log every {@link #DEFAULT_LOG_EVERY} records.
   * 
   * @param log Logger
   * @param cntr record count
   * @param action action message (extract, transform, load, etc)
   * @param args variable message arguments
   */
  public static void logEvery(Logger log, int cntr, String action, Object... args) {
    logEvery(log, DEFAULT_LOG_EVERY, cntr, action, args);
  }

  /**
   * Log every {@link #DEFAULT_LOG_EVERY} records.
   * 
   * @param cntr record count
   * @param action action message (extract, transform, load, etc)
   * @param args variable message arguments
   */
  public static void logEvery(int cntr, String action, Object... args) {
    logEvery(LOGGER, cntr, action, args);
  }

  /**
   * Format message and return a runtime {@link JobsException}.
   * 
   * @param log class logger
   * @param e any Throwable
   * @param pattern MessageFormat pattern
   * @param args error message, excluding throwable message
   * @return JobsException runtime exception
   */
  public static JobsException buildRuntimeException(final Logger log, Throwable e, String pattern,
      Object... args) {
    JobsException ret;
    final boolean hasArgs = args == null || args.length == 0;
    final boolean hasPattern = !StringUtils.isEmpty(pattern);
    final Logger logger = log != null ? log : LOGGER;

    // Build message:
    final Object[] objs = hasArgs ? new Object[0] : args;
    final String pat = hasPattern ? pattern : StringUtils.join(objs, "{}");
    final String msg = hasPattern && hasArgs ? MessageFormat.format(pat, objs) : "";

    if (e != null) {
      logger.error(msg, e);
      ret = new JobsException(msg, e);
    } else {
      logger.error(msg);
      ret = new JobsException(msg);
    }

    return ret;
  }

  /**
   * Format message and return a runtime {@link JobsException}.
   * 
   * @param log class logger
   * @param e any Throwable
   * @param pattern MessageFormat pattern
   * @param args error message, excluding throwable message
   * @return NeutronException checked exception
   */
  public static NeutronException buildCheckedException(final Logger log, Throwable e,
      String pattern, Object... args) {
    final boolean hasArgs = args == null || args.length == 0;
    final boolean hasPattern = !StringUtils.isEmpty(pattern);
    final Logger logger = log != null ? log : LOGGER;

    // Build message:
    final Object[] objs = hasArgs ? new Object[0] : args;
    final String pat = hasPattern ? pattern : StringUtils.join(objs, "{}");
    final String msg = hasPattern && hasArgs ? MessageFormat.format(pat, objs) : "";

    logger.error(msg, e);
    return new NeutronException(msg, e);
  }

  public static NeutronException checked(final Logger log, Throwable e, String pattern,
      Object... args) {
    return buildCheckedException(log, e, pattern, args);
  }

  public static JobsException runtime(final Logger log, Throwable e, String pattern,
      Object... args) {
    return buildRuntimeException(log, e, pattern, args);
  }

  public static JobsException runtime(final Logger log, String pattern, Object... args) {
    return buildRuntimeException(log, null, pattern, args);
  }

  public static String stackToString(Exception e) {
    return ExceptionUtils.getStackTrace(e);
  }

}
