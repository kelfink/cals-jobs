package gov.ca.cwds.jobs.facility;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.jobs.exception.JobsException;

public class FacilityRowMapperTest {

  private FacilityRowMapper target = new FacilityRowMapper();

  @Before
  public void setup() {
    target = new FacilityRowMapper();
  }

  @Test
  public void type() throws Exception {
    assertThat(FacilityRowMapper.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test(expected = JobsException.class)
  public void mapRow_Args__ResultSet() throws Exception {
    ResultSet rs = mock(ResultSet.class);
    FacilityRow actual = target.mapRow(rs);
    assertThat(actual, is(notNullValue()));
  }

  @Test(expected = JobsException.class)
  public void mapRow_Args__ResultSet_T__SQLException() throws Exception {
    ResultSet rs = mock(ResultSet.class);
    when(rs.next()).thenThrow(SQLException.class);
    when(rs.getString(any())).thenThrow(SQLException.class);
    target.mapRow(rs);
  }

}
