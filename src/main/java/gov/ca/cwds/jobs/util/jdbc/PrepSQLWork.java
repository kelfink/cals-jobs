package gov.ca.cwds.jobs.util.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Execute SQL prior to retrieving records, typically for last change runs. Examples include
 * populating a global temporary table prior to reading from a view.
 * 
 * @author CWDS API Team
 */
public class PrepSQLWork implements Work {

  private static final Logger LOGGER = LoggerFactory.getLogger(PrepSQLWork.class);

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

  private PreparedStatement createPreparedStatement(Connection con) {
    return prepStmtMaker.apply(con);
  }

  @Override
  public void execute(Connection con) throws SQLException {
    con.setSchema(NeutronJdbcUtils.getDBSchemaName());
    con.setAutoCommit(false);
    NeutronDB2Utils.enableParallelism(con);

    final String strLastRunTime = NeutronJdbcUtils.makeSimpleTimestampString(lastRunTime);

    try (final PreparedStatement stmt = createPreparedStatement(con)) {
      for (int i = 1; i <= StringUtils.countMatches(sql, "?"); i++) {
        stmt.setString(i, strLastRunTime);
      }

      LOGGER.info("Find keys new/changed since {}", lastRunTime);
      final int cntNewChanged = stmt.executeUpdate();
      LOGGER.info("Total keys new/changed: {}", cntNewChanged);
    }
  }

}
