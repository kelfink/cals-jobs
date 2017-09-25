package gov.ca.cwds.jobs.facility;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

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

  @Test
  @Ignore
  public void mapRow_Args__ResultSet() throws Exception {
    ResultSet rs = mock(ResultSet.class);
    FacilityRow actual = target.mapRow(rs);
    FacilityRow expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  @Ignore
  public void mapRow_Args__ResultSet_T__SQLException() throws Exception {
    ResultSet rs = mock(ResultSet.class);
    try {
      target.mapRow(rs);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
    }
  }

}
