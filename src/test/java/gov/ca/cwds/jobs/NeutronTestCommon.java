package gov.ca.cwds.jobs;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class NeutronTestCommon {

  private static final SessionFactory sessionFactory;

  static {
    sessionFactory =
        new Configuration().configure("test-cms-hibernate.cfg.xml").buildSessionFactory();
  }

  static SessionFactory getSessionfactory() {
    return sessionFactory;
  }

}
