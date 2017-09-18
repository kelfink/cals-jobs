package gov.ca.cwds.jobs.util.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

public class Db2JdbcUtils {

  /**
   * Enable DB2 parallelism. Ignored for other databases.
   * 
   * @param con connection
   * @throws SQLException connection error
   */
  public static void enableParallelism(Connection con) throws SQLException {
    String dbProductName = con.getMetaData().getDatabaseProductName();
    if (StringUtils.containsIgnoreCase(dbProductName, "db2")) {
      con.nativeSQL("SET CURRENT DEGREE = 'ANY'");
    }
  }

}
