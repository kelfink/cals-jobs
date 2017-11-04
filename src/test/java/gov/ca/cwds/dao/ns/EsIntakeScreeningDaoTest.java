package gov.ca.cwds.dao.ns;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.jobs.Goddard;

public class EsIntakeScreeningDaoTest extends Goddard {

  private EsIntakeScreeningDao target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    target = new EsIntakeScreeningDao(sessionFactory);
  }

  @Test
  public void type() throws Exception {
    assertThat(EsIntakeScreeningDao.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

}
