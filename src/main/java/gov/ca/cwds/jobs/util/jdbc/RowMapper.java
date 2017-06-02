package gov.ca.cwds.jobs.util.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author CWDS Elasticsearch Team
 * @param <T> persistence type to extract from ResultSet
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
