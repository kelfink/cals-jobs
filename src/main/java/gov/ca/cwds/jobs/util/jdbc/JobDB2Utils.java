package gov.ca.cwds.jobs.util.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.db2.jcc.DB2Connection;
import com.ibm.db2.jcc.DB2SystemMonitor;

import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.jobs.util.JobLogs;

/**
 * Miscellaneous DB2 utilities for Neutron jobs.
 * 
 * @author CWDS API Team
 */
public class JobDB2Utils {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobDB2Utils.class);

  private JobDB2Utils() {
    // Default no-op.
  }

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
   * @param dao DAO
   * 
   * @return true if DB2 is running on a mainframe
   */
  public static boolean isDB2OnZOS(final BaseDaoImpl<?> dao) {
    boolean ret = false;

    try (final Connection con = dao.getSessionFactory().getSessionFactoryOptions()
        .getServiceRegistry().getService(ConnectionProvider.class).getConnection()) {

      final DatabaseMetaData meta = con.getMetaData();
      LOGGER.info("meta:\nproduct name: {}\nproduction version: {}\nmajor: {}\nminor: {}",
          meta.getDatabaseProductName(), meta.getDatabaseProductVersion(),
          meta.getDatabaseMajorVersion(), meta.getDatabaseMinorVersion());

      ret = meta.getDatabaseProductVersion().startsWith("DSN");
    } catch (Exception e) {
      JobLogs.raiseError(LOGGER, e, "FAILED TO FIND DB2 PLATFORM! {}", e.getMessage());
    }

    return ret;
  }

  /**
   * Get a DB2 monitor and start it for this transaction.
   * 
   * @param con database connection
   * @return DB2 monitor
   */
  public static DB2SystemMonitor monitorStart(final Connection con) {
    try {
      final com.ibm.db2.jcc.t4.b nativeCon =
          (com.ibm.db2.jcc.t4.b) ((com.mchange.v2.c3p0.impl.NewProxyConnection) con)
              .unwrap(Class.forName("com.ibm.db2.jcc.t4.b")); // NOSONAR
      final DB2Connection db2Con = nativeCon;
      LOGGER.info("sendDataAsIs_: {}, enableRowsetSupport_: {}", nativeCon.sendDataAsIs_,
          nativeCon.enableRowsetSupport_);

      final DB2SystemMonitor monitor = db2Con.getDB2SystemMonitor();
      monitor.enable(true);
      monitor.start(DB2SystemMonitor.RESET_TIMES);
      return monitor;
    } catch (Exception e) {
      LOGGER.warn("UNABLE TO GRAB DB2 MONITOR: {}", e.getMessage(), e);
    }

    return null;
  }

  /**
   * Stop the DB2 monitor and report stats.
   * 
   * @param monitor current monitor instance
   * @throws SQLException on JDBC error
   */
  public static void monitorStopAndReport(final DB2SystemMonitor monitor) throws SQLException {
    if (monitor != null) {
      monitor.stop();
      LOGGER.info("Server elapsed time (microseconds)={}", monitor.getServerTimeMicros());
      LOGGER.info("Network I/O elapsed time (microseconds)={}", monitor.getNetworkIOTimeMicros());
      LOGGER.info("Core driver elapsed time (microseconds)={}", monitor.getCoreDriverTimeMicros());
      LOGGER.info("Application elapsed time (milliseconds)={}", monitor.getApplicationTimeMillis());
      LOGGER.info("monitor.moreData: 0: {}", monitor.moreData(0));
      LOGGER.info("monitor.moreData: 1: {}", monitor.moreData(1));
      LOGGER.info("monitor.moreData: 2: {}", monitor.moreData(2));
    }
  }

}
