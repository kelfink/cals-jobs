package gov.ca.cwds.jobs;

import org.hibernate.SessionFactory;

import gov.ca.cwds.data.BaseDaoImpl;

/**
 * DAO
 */
public class TestNormalizedEntityDao extends BaseDaoImpl<TestNormalizedEntity> {

  public TestNormalizedEntityDao(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

}
