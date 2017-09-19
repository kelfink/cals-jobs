package gov.ca.cwds.jobs;

import java.sql.SQLException;

import com.ibm.db2.jcc.DB2SystemMonitor;

public class TestDB2SystemMonitor implements DB2SystemMonitor {

  @Override
  public void enable(boolean paramBoolean) throws SQLException {}

  @Override
  public void start(int paramInt) throws SQLException {}

  @Override
  public void stop() throws SQLException {}

  @Override
  public long getServerTimeMicros() throws SQLException {
    return 0;
  }

  @Override
  public long getNetworkIOTimeMicros() throws SQLException {
    return 0;
  }

  @Override
  public long getCoreDriverTimeMicros() throws SQLException {
    return 0;
  }

  @Override
  public long getApplicationTimeMillis() throws SQLException {
    return 0;
  }

  @Override
  public Object moreData(int paramInt) throws SQLException {
    return "nothin";
  }

}