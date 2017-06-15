package gov.ca.cwds.dao.ns;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.hibernate.SessionFactory;
import org.junit.Test;

public class IntakeScreeningDaoTest {

  @Test
  public void type() throws Exception {
    assertThat(IntakeScreeningDao.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    SessionFactory sessionFactory = mock(SessionFactory.class);
    IntakeScreeningDao target = new IntakeScreeningDao(sessionFactory);
    assertThat(target, notNullValue());
  }

}
