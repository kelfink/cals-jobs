package gov.ca.cwds.jobs.cals.facility.cws;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.engine.spi.RowSelection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class CustomDb2DialectTest {

  @Spy
  @InjectMocks
  private CustomDb2Dialect customDb2Dialect; // "Class Under Test"

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void type() {
    assertThat(CustomDb2Dialect.class, notNullValue());
  }

  @Test
  public void instantiation() {
    assertThat(customDb2Dialect, notNullValue());
  }

  @Test
  public void testProcessSqlHasFirstRow() {
    LimitHandler limitHandler = customDb2Dialect.getLimitHandler();
    Assert.assertNotNull(limitHandler);
    RowSelection rowSelection = new RowSelection();
    rowSelection.setFirstRow(1);
    rowSelection.setMaxRows(10);

    String resultSql =
        "select * from ( select inner2_.*, rownumber() over() as rownumber_ from ( test SQL fetch first 11 rows only ) as inner2_ ) "
            + "as inner1_ where rownumber_ > 1 order by rownumber_";
    Assert.assertEquals(resultSql, limitHandler.processSql("test SQL", rowSelection));
  }

  @Test
  public void testProcessSqlNoFirstRow() {
    LimitHandler limitHandler = customDb2Dialect.getLimitHandler();
    Assert.assertNotNull(limitHandler);
    RowSelection rowSelection = new RowSelection();
    rowSelection.setFirstRow(0);
    rowSelection.setMaxRows(10);
    String resultSql = "test SQL fetch first 10 rows only";
    Assert.assertEquals(resultSql, limitHandler.processSql("test SQL", rowSelection));
  }
}
