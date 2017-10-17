package gov.ca.cwds.jobs.util.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.jobs.exception.JobsException;
import gov.ca.cwds.jobs.util.JobReader;

/**
 * @param <T> persistence class
 * @author CWDS Elasticsearch Team
 */
public class JdbcJobReader<T extends PersistentObject> implements JobReader<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(JdbcJobReader.class);

  private SessionFactory sessionFactory;
  private ResultSet resultSet;
  private RowMapper<T> rowMapper;
  private PreparedStatement statement;
  private String query;

  /**
   * @param sessionFactory Hibernate session factor
   * @param rowMapper row mapper
   * @param query SQL query
   */
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

      statement = connection.prepareStatement(query);
      statement.setFetchSize(5000);
      statement.setMaxRows(0);
      statement.setQueryTimeout(100000);
      resultSet = statement.executeQuery();
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
  public void destroy() {
    try {
      if (statement != null) {
        statement.close();
        statement = null;
      }
    } catch (SQLException e) {
      LOGGER.error(e.getMessage(), e);
    } finally {
      sessionFactory.close();
    }
  }

}
