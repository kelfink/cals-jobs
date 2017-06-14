package gov.ca.cwds.dao.cms;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.hibernate.SessionFactory;
import org.junit.Test;
import org.mockito.Mockito;

public class ReplicatedClientDaoTest {

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedClientDao.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    SessionFactory sessionFactory = Mockito.mock(SessionFactory.class);
    ReplicatedClientDao target = new ReplicatedClientDao(sessionFactory);
    assertThat(target, notNullValue());
  }

}
