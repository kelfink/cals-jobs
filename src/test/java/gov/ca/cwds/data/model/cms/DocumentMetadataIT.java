package gov.ca.cwds.data.model.cms;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DocumentMetadataIT {

  private SessionFactory sessionFactory;

  @Before
  public void setup() {
    sessionFactory = new Configuration().configure().buildSessionFactory();
    sessionFactory.getCurrentSession().beginTransaction();
  }

  @Test
  public void failedTest() {

    Session session = sessionFactory.getCurrentSession();
    session.beginTransaction();
    Query query = session.getNamedQuery("findByLastJobRunTimeMinusOneMinute")
        .setString("lastJobRunTime", "2010-02-01 16:35:00"); // YYYY-MM-DD HH24:MI:SS
    query.list();
    Assert.fail("Implement This Test");
  }
}
