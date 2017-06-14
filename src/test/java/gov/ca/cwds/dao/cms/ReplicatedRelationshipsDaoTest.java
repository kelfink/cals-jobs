package gov.ca.cwds.dao.cms;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.hibernate.SessionFactory;
import org.junit.Test;
import org.mockito.Mockito;

public class ReplicatedRelationshipsDaoTest {

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedRelationshipsDao.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    SessionFactory sessionFactory = Mockito.mock(SessionFactory.class);
    ReplicatedRelationshipsDao target = new ReplicatedRelationshipsDao(sessionFactory);
    assertThat(target, notNullValue());
  }

}
