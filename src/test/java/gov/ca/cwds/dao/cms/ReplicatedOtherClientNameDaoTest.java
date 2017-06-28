package gov.ca.cwds.dao.cms;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.hibernate.SessionFactory;
import org.junit.Test;

public class ReplicatedOtherClientNameDaoTest {

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedOtherClientNameDao.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    SessionFactory sessionFactory = mock(SessionFactory.class);
    ReplicatedOtherClientNameDao target = new ReplicatedOtherClientNameDao(sessionFactory);
    assertThat(target, notNullValue());
  }

}
