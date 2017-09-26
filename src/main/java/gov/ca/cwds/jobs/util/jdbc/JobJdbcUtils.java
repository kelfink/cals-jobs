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

public class JobJdbcUtils {

  /**
   * Common timestamp format for legacy DB.
   */
  public static final String LEGACY_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

  private static final Logger LOGGER = LoggerFactory.getLogger(JobJdbcUtils.class);

  /**
   * @return default CMS schema name
   */
  public static String getDBSchemaName() {
    return System.getProperty("DB_CMS_SCHEMA");
  }

  public static void prepHibernateLastChange(final Session session, final Transaction txn,
      final Date lastRunTime, final String sqlInsertLastChange) throws SQLException {
    final Work work = new Work() {
      @Override
      public void execute(Connection con) throws SQLException {
        con.setSchema(getDBSchemaName());
        con.setAutoCommit(false);
        JobDB2Utils.enableParallelism(con);

        final StringBuilder buf = new StringBuilder();
        buf.append("TIMESTAMP('")
            .append(new SimpleDateFormat(LEGACY_TIMESTAMP_FORMAT).format(lastRunTime)).append("')");

        final String sql = sqlInsertLastChange.replaceAll("#SCHEMA#", getDBSchemaName())
            .replaceAll("##TIMESTAMP##", buf.toString());
        LOGGER.info("Prep SQL: {}", sql);

        try (final Statement stmt =
            con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
          LOGGER.info("Find referrals new/changed since {}", lastRunTime);
          final int cntInsClientReferral = stmt.executeUpdate(sql);
          LOGGER.info("Total relationships new/changed: {}", cntInsClientReferral);
        } finally {
          // The statement closes automatically.
        }
      }
    };
    session.doWork(work);
  }

}
