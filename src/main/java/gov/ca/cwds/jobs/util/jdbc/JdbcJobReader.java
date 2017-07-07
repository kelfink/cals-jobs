package gov.ca.cwds.jobs.util.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.jobs.exception.JobsException;
import gov.ca.cwds.jobs.util.JobReader;

/**
 * @param <T> persistence class
 * @author CWDS Elasticsearch Team
 */
public class JdbcJobReader<T extends PersistentObject> implements JobReader<T> {

  private static final Logger LOGGER = LogManager.getLogger(JdbcJobReader.class);

  private SessionFactory sessionFactory;
  private ResultSet resultSet;
  private RowMapper<T> rowMapper;
  private Statement statement;
  private String query;

  public JdbcJobReader(SessionFactory sessionFactory, RowMapper<T> rowMapper, String query) {
    this.sessionFactory = sessionFactory;
    this.rowMapper = rowMapper;
    this.query = query;
  }

  @Override
  public void init() {
    try {
      Connection connection = sessionFactory.getSessionFactoryOptions().getServiceRegistry()
          .getService(ConnectionProvider.class).getConnection();
      connection.setAutoCommit(false);
      connection.setReadOnly(true);


      statement = connection.createStatement();
      statement.setFetchSize(5000);
      statement.setMaxRows(0);
      statement.setQueryTimeout(100000);
      resultSet = statement.executeQuery(query);
    } catch (SQLException e) {
      destroy();
      throw new JobsException(e);
    }

  }

  @Override
  public T read() {
    try {
      if (resultSet.next()) {
        return rowMapper.mapRow(resultSet);
      } else {
        return null;
      }
    } catch (SQLException e) {
      throw new JobsException(e);
    }
  }

  @Override
  @SuppressWarnings("ThrowFromFinallyBlock")
  public void destroy() {
    try {
      if (statement != null) {
        statement.close();
        statement = null;
      }
    } catch (SQLException e) {
      // Do nothing
    } finally {
      sessionFactory.close();
    }
  }
}
