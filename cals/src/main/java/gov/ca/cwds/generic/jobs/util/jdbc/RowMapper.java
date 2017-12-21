package gov.ca.cwds.generic.jobs.util.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Simple interface to map results from a JDBC ResultSet to an entity bean.
 *
 * @param <T> persistence type to extract from ResultSet
 * @author CWDS TPT-2
 */
@FunctionalInterface
public interface RowMapper<T> {

  /**
   * Extract a persistence object from the ResultSet.
   *
   * @param resultSet SQL result set to process
   * @return persistence object extracted from ResultSet
   * @throws SQLException on SQL error or disconnect
   */
  T mapRow(ResultSet resultSet) throws SQLException;

}
