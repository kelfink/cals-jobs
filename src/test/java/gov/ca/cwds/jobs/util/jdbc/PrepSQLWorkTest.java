package gov.ca.cwds.jobs.util.jdbc;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.data.persistence.cms.EsPersonCase;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonCases;
import gov.ca.cwds.jobs.PersonJobTester;
import gov.ca.cwds.jobs.util.JobLogs;

public class PrepSQLWorkTest extends PersonJobTester<ReplicatedPersonCases, EsPersonCase> {

  private static final Logger LOGGER = LoggerFactory.getLogger(PrepSQLWorkTest.class);

  protected PreparedStatement prepStmt;
  PrepSQLWork target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();

    prepStmt = mock(PreparedStatement.class);
    when(con.prepareStatement(any(String.class))).thenReturn(prepStmt);
    when(prepStmt.executeUpdate()).thenReturn(10);
  }

  @Test
  public void type() throws Exception {
    assertThat(PrepSQLWork.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    Date lastRunTime = new Date();
    String sql = null;

    target = new PrepSQLWork(lastRunTime, sql, null);
    assertThat(target, notNullValue());
  }

  @Test
  public void execute_Args__Connection_function() throws Exception {
    final Date lastRunTime = new Date();
    final String sql = "SELECT C.* FROM CLIENT_T C WHERE c.LST_UPD_TS > :ts";

    target = new PrepSQLWork(lastRunTime, sql, c -> {
      try {
        return c.prepareStatement(sql);
      } catch (SQLException e) {
        throw JobLogs.buildRuntimeException(LOGGER, e, "FAILED TO PREPARE STATEMENT", e.getMessage());
      }
    });
    target.execute(con);
  }

  @Test(expected = SQLException.class)
  public void execute_Args__Connection_T__SQLException() throws Exception {
    final Date lastRunTime = new Date();
    final String sql = "SELECT C.* FROM CLIENT_T C WHERE c.LST_UPD_TS > '2017-08-28 11:54:40'";
    when(prepStmt.executeUpdate()).thenThrow(SQLException.class);

    target = new PrepSQLWork(lastRunTime, sql, c -> {
      try {
        return c.prepareStatement(sql);
      } catch (SQLException e) {
        throw JobLogs.buildRuntimeException(LOGGER, e, "FAILED TO PREPARE STATEMENT", e.getMessage());
      }
    });
    target.execute(con);
    fail("Expected exception was not thrown!");
  }

}
