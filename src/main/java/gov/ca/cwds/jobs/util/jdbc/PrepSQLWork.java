package gov.ca.cwds.jobs.util.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.hibernate.jdbc.Work;

public class PrepSQLWork implements Work {

  private final Date lastRunTime;
  private final String sqlInsertLastChange;

  public PrepSQLWork(Date lastRunTime, String sqlInsertLastChange) {
    this.lastRunTime = lastRunTime;
    this.sqlInsertLastChange = sqlInsertLastChange;
  }

  @Override
  public void execute(Connection con) throws SQLException {
    con.setSchema(JobJdbcUtils.getDBSchemaName());
    con.setAutoCommit(false);
    JobDB2Utils.enableParallelism(con);

    final StringBuilder buf = new StringBuilder();
    buf.append(JobJdbcUtils.makeTimestampString(lastRunTime));

    final String sql = sqlInsertLastChange.replaceAll("#SCHEMA#", JobJdbcUtils.getDBSchemaName())
        .replaceAll("##TIMESTAMP##", buf.toString());
    JobJdbcUtils.LOGGER.info("Prep SQL: {}", sql);

    try (final Statement stmt =
        con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
      JobJdbcUtils.LOGGER.info("Find keys new/changed since {}", lastRunTime);
      final int cntNewChanged = stmt.executeUpdate(sql);
      JobJdbcUtils.LOGGER.info("Total keys new/changed: {}", cntNewChanged);
    }
  }

}
