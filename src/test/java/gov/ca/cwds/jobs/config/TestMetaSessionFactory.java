package gov.ca.cwds.jobs.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class TestMetaSessionFactory {

  private static SessionFactory sessionFactory;

  static {
    sessionFactory =
        new Configuration().configure("test-cms-hibernate.cfg.xml").buildSessionFactory();
  }

  public static SessionFactory getSessionFactory() {
    return sessionFactory;
  }

}
