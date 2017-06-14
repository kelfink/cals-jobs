package gov.ca.cwds.dao.cms;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.hibernate.SessionFactory;
import org.junit.Test;
import org.mockito.Mockito;

public class ReplicatedPersonCasesDaoTest {

  @Test
  public void type() throws Exception {
    assertThat(ReplicatedPersonCasesDao.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    SessionFactory sessionFactory = Mockito.mock(SessionFactory.class);
    ReplicatedPersonCasesDao target = new ReplicatedPersonCasesDao(sessionFactory);
    assertThat(target, notNullValue());
  }

}
