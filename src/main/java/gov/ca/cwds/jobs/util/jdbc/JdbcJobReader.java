package gov.ca.cwds.jobs.util.jdbc;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.jobs.util.JobReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by dmitry.rudenko on 4/28/2017.
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

    public void init() throws Exception {
        Connection connection = sessionFactory.getSessionFactoryOptions().getServiceRegistry()
                .getService(ConnectionProvider.class).getConnection();
        connection.setAutoCommit(false);
        connection.setReadOnly(true);
        try {
            statement = connection.createStatement();
            statement.setFetchSize(5000);
            statement.setMaxRows(0);
            statement.setQueryTimeout(100000);
            resultSet = statement.executeQuery(query);
        } catch (Exception e) {
            destroy();
            throw e;
        }
    }

    @Override
    public T read() throws Exception {
        if (resultSet.next()) {
            return rowMapper.mapRow(resultSet);
        } else {
            return null;
        }
    }

    @SuppressWarnings("ThrowFromFinallyBlock")
    public void destroy() throws Exception {
        try {
            if (statement != null) {
                statement.close();
                statement = null;
            }
        } finally {
            sessionFactory.close();
        }
    }
}
