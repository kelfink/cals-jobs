package gov.ca.cwds.jobs.facility;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

public class FacilityRowMapperTest {

  @Test
  public void type() throws Exception {
    assertThat(FacilityRowMapper.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    FacilityRowMapper target = new FacilityRowMapper();
    assertThat(target, notNullValue());
  }

  @Test
  public void mapRow_Args__ResultSet() throws Exception {
    FacilityRowMapper target = new FacilityRowMapper();
    // given
    ResultSet rs = mock(ResultSet.class);
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    FacilityRow actual = target.mapRow(rs);
    // then
    // e.g. : verify(mocked).called();
    FacilityRow expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void mapRow_Args__ResultSet_T__SQLException() throws Exception {
    FacilityRowMapper target = new FacilityRowMapper();
    // given
    ResultSet rs = mock(ResultSet.class);
    // e.g. : given(mocked.called()).willReturn(1);
    try {
      // when
      target.mapRow(rs);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
      // then
    }
  }

}
