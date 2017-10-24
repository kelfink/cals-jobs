package gov.ca.cwds.jobs.util.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.jobs.exception.JobsException;
import gov.ca.cwds.jobs.util.JobLogs;
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
  private final String query;

  /**
   * @param sessionFactory Hibernate session factory
   * @param rowMapper row mapper
   * @param query SQL query
   */
  public JdbcJobReader(SessionFactory sessionFactory, RowMapper<T> rowMapper, String query) {
    this.sessionFactory = sessionFactory;
    this.rowMapper = rowMapper;
    this.query = query;
  }

  Function<Connection, PreparedStatement> makePreparedStatementProducer() {
    return c -> {
      try {
        return c.prepareStatement(query);
      } catch (SQLException e) {
        throw JobLogs.buildRuntimeException(LOGGER, e, "FAILED TO PREPARE STATEMENT",
            e.getMessage());
      }
    };
  }

  @Override
  public void init() {
    try {
      final Connection con = sessionFactory.getSessionFactoryOptions().getServiceRegistry()
          .getService(ConnectionProvider.class).getConnection();
      con.setAutoCommit(false); // connection pool should set this ...
      con.setReadOnly(true); // may fail in some situations.

      // SonarQube complains loudly about this "vulnerability."
      statement = makePreparedStatementProducer().apply(con);
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
      return resultSet.next() ? rowMapper.mapRow(resultSet) : null;
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

  public String getQuery() {
    return query;
  }

}
