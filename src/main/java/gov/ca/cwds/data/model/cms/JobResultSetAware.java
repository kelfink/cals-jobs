package gov.ca.cwds.data.model.cms;

import java.sql.ResultSet;
import java.sql.SQLException;

import gov.ca.cwds.data.std.ApiGroupNormalizer;

/**
 * Read from a JDBC ResultSet into an MQT entity bean.
 *
 * @author CWDS API Team
 * @param <M> MQT entity class instance
 */
public interface JobResultSetAware<M extends ApiGroupNormalizer<?>> {

  /**
   * Read from a JDBC ResultSet into an MQT entity bean.
   * 
   * @param rs ResultSet
   * @return populated MQT entity bean
   * @throws SQLException on disconnect or type conversion error
   */
  M pullFromResultSet(ResultSet rs) throws SQLException;

}
