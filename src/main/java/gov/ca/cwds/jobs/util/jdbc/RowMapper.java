package gov.ca.cwds.jobs.util.jdbc;

import java.sql.ResultSet;

/**
 * @author CWDS Elasticsearch Team
 */
public interface RowMapper<T> {
    T mapRow(ResultSet resultSet) throws Exception;
}
