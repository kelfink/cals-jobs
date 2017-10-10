package gov.ca.cwds.jobs.util.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.jdbc.Work;

/**
 * Execute SQL prior to retrieving records, typically for last change runs. Examples include
 * populating a global temporary table prior to reading from a view.
 * 
 * @author CWDS API Team
 */
public class PrepSQLWork implements Work {

  private final Date lastRunTime;
  private final String sql;
  private final Function<Connection, PreparedStatement> prepStmtMaker;

  /**
   * Constructor.
   * 
   * @param lastRunTime last successful run time
   * @param sql SQL to run
   * @param prepStmtMaker Function to produce prepared statement
   */
  public PrepSQLWork(Date lastRunTime, String sql,
      final Function<Connection, PreparedStatement> prepStmtMaker) {
    this.lastRunTime = lastRunTime != null ? new Date(lastRunTime.getTime()) : null;
    this.sql = sql;
    this.prepStmtMaker = prepStmtMaker;
  }

  private PreparedStatement createPreparedStatement(Connection con, String sql)
      throws SQLException {
    return prepStmtMaker != null ? prepStmtMaker.apply(con) : con.prepareStatement(sql);
  }

  @Override
  public void execute(Connection con) throws SQLException {
    con.setSchema(JobJdbcUtils.getDBSchemaName());
    con.setAutoCommit(false);
    JobDB2Utils.enableParallelism(con);

    final String strLastRunTime = JobJdbcUtils.makeSimpleTimestampString(lastRunTime);

    try (final PreparedStatement stmt = createPreparedStatement(con, this.sql)) {
      for (int i = 1; i <= StringUtils.countMatches(sql, "?"); i++) {
        stmt.setString(i, strLastRunTime);
      }

      JobJdbcUtils.LOGGER.info("Find keys new/changed since {}", lastRunTime);
      final int cntNewChanged = stmt.executeUpdate();
      JobJdbcUtils.LOGGER.info("Total keys new/changed: {}", cntNewChanged);
    }
  }

}
