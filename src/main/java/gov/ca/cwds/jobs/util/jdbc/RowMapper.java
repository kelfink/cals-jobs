package gov.ca.cwds.jobs.util.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by dmitry.rudenko on 4/28/2017.
 */
public interface RowMapper<T> {
    T mapRow(ResultSet resultSet) throws Exception;
}
