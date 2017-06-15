package gov.ca.cwds.dao.ns;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.hibernate.SessionFactory;
import org.junit.Test;

public class IntakeParticipantDaoTest {

  @Test
  public void type() throws Exception {
    assertThat(IntakeParticipantDao.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    SessionFactory sessionFactory = mock(SessionFactory.class);
    IntakeParticipantDao target = new IntakeParticipantDao(sessionFactory);
    assertThat(target, notNullValue());
  }

}
