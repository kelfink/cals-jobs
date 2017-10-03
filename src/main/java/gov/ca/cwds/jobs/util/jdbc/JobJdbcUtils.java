package gov.ca.cwds.jobs.util.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.jobs.component.AtomShared;
import gov.ca.cwds.jobs.config.JobOptions;

/**
 * JDBC utilities for Neutron jobs.
 * 
 * @author CWDS API Team
 */
public class JobJdbcUtils {

  private static final class PrepWork implements Work {
    private final Date lastRunTime;
    private final String sqlInsertLastChange;

    private PrepWork(Date lastRunTime, String sqlInsertLastChange) {
      this.lastRunTime = lastRunTime;
      this.sqlInsertLastChange = sqlInsertLastChange;
    }

    @Override
    public void execute(Connection con) throws SQLException {
      con.setSchema(getDBSchemaName());
      con.setAutoCommit(false);
      JobDB2Utils.enableParallelism(con);

      final StringBuilder buf = new StringBuilder();
      buf.append(makeTimestampString(lastRunTime));

      final String sql = sqlInsertLastChange.replaceAll("#SCHEMA#", getDBSchemaName())
          .replaceAll("##TIMESTAMP##", buf.toString());
      LOGGER.info("Prep SQL: {}", sql);

      try (final Statement stmt =
          con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
        LOGGER.info("Find keys new/changed since {}", lastRunTime);
        final int cntNewChanged = stmt.executeUpdate(sql);
        LOGGER.info("Total keys new/changed: {}", cntNewChanged);
      }
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(JobJdbcUtils.class);

  public static String makeTimestampString(final Date date) {
    final StringBuilder buf = new StringBuilder();
    buf.append("TIMESTAMP('")
        .append(new SimpleDateFormat(AtomShared.LEGACY_TIMESTAMP_FORMAT).format(date)).append("')");
    return buf.toString();
  }

  public static String makeSimpleTimestampString(final Date date) {
    return new SimpleDateFormat(AtomShared.LEGACY_TIMESTAMP_FORMAT).format(date);
  }

  /**
   * @return default CMS schema name
   */
  public static String getDBSchemaName() {
    return System.getProperty("DB_CMS_SCHEMA");
  }

  public static void prepHibernateLastChange(final Session session, final Transaction txn,
      final Date lastRunTime, final String sqlInsertLastChange) throws SQLException {
    final Work work = new PrepWork(lastRunTime, sqlInsertLastChange);
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
