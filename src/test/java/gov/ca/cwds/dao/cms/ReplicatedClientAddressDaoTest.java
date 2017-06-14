package gov.ca.cwds.dao.cms;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.hibernate.SessionFactory;
import org.junit.Test;
import org.mockito.Mockito;

public class ReplicatedClientAddressDaoTest {

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedClientAddressDao.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    SessionFactory sessionFactory = Mockito.mock(SessionFactory.class);
    ReplicatedClientAddressDao target = new ReplicatedClientAddressDao(sessionFactory);
    assertThat(target, notNullValue());
  }

}
