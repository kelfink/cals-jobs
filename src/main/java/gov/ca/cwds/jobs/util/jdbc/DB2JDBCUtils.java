package gov.ca.cwds.jobs.util.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.jobs.util.JobLogUtils;

public class DB2JDBCUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(DB2JDBCUtils.class);

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

  /**
   * Character sets differ by operating system, which affect ORDER BY and WHERE clauses.
   * 
   * <p>
   * SELECT statements using range partitions depend on sort order.
   * </p>
   * 
   * <p>
   * Database platforms and versions:
   * </p>
   * 
   * <table summary="">
   * <tr>
   * <th align="justify">Platform</th>
   * <th align="justify">Name</th>
   * <th align="justify">Version</th>
   * <th align="justify">Major</th>
   * <th align="justify">Minor</th>
   * <th align="justify">Catalog</th>
   * </tr>
   * <tr>
   * <td align="justify">z/OS</td>
   * <td align="justify">DB2</td>
   * <td align="justify">DSN11010</td>
   * <td align="justify">11</td>
   * <td align="justify">1</td>
   * <td align="justify">location</td>
   * </tr>
   * <tr>
   * <td align="justify">Linux</td>
   * <td align="justify">DB2/LINUXX8664</td>
   * <td align="justify">SQL10057</td>
   * <td align="justify">10</td>
   * <td align="justify">5</td>
   * <td align="justify">database</td>
   * </tr>
   * </table>
   * 
   * @return true if DB2 is running on a mainframe
   */
  public static boolean isDB2OnZOS(final BaseDaoImpl jobDao) {
    boolean ret = false;

    try (final Connection con = jobDao.getSessionFactory().getSessionFactoryOptions()
        .getServiceRegistry().getService(ConnectionProvider.class).getConnection()) {

      final DatabaseMetaData meta = con.getMetaData();
      LOGGER.info("meta:\nproduct name: {}\nproduction version: {}\nmajor: {}\nminor: {}",
          meta.getDatabaseProductName(), meta.getDatabaseProductVersion(),
          meta.getDatabaseMajorVersion(), meta.getDatabaseMinorVersion());

      ret = meta.getDatabaseProductVersion().startsWith("DSN");
    } catch (Exception e) {
      JobLogUtils.raiseError(LOGGER, e, "FAILED TO FIND DB2 PLATFORM! {}", e.getMessage());
    }

    return ret;
  }

}
