package gov.ca.cwds.jobs.util.jdbc;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.jobs.component.NeutronDateTimeFormat;
import gov.ca.cwds.jobs.config.JobOptions;

/**
 * JDBC utilities for Neutron jobs.
 * 
 * @author CWDS API Team
 */
public class JobJdbcUtils {

  static final Logger LOGGER = LoggerFactory.getLogger(JobJdbcUtils.class);

  private JobJdbcUtils() {
    // Default, no-op. Static utility class.
  }

  public static String makeTimestampString(final Date date) {
    final StringBuilder buf = new StringBuilder();
    buf.append("TIMESTAMP('")
        .append(
            new SimpleDateFormat(NeutronDateTimeFormat.LEGACY_TIMESTAMP_FORMAT.getFormat()).format(date))
        .append("')");
    return buf.toString();
  }

  public static String makeSimpleTimestampString(final Date date) {
    return new SimpleDateFormat(NeutronDateTimeFormat.LEGACY_TIMESTAMP_FORMAT.getFormat()).format(date);
  }

  /**
   * @return default CMS schema name
   */
  public static String getDBSchemaName() {
    return System.getProperty("DB_CMS_SCHEMA");
  }

  public static void prepHibernateLastChange(final Session session, final Transaction txn,
      final Date lastRunTime, final String sqlInsertLastChange) throws SQLException {
    final Work work = new PrepSQLWork(lastRunTime, sqlInsertLastChange);
    session.doWork(work);
  }

  /**
   * Calculate the number of reader threads to run from incoming job options and available
   * processors.
   * 
   * @param opts job options
   * @return number of reader threads to run
   */
  public static int calcReaderThreads(final JobOptions opts) {
    final int ret = opts.getThreadCount() != 0L ? (int) opts.getThreadCount()
        : Math.max(Runtime.getRuntime().availableProcessors() - 4, 4);
    LOGGER.warn(">>>>>>>> # OF READER THREADS: {} <<<<<<<<", ret);
    return ret;
  }

}
