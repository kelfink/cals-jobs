package gov.ca.cwds.jobs.test;

import org.hibernate.SessionFactory;

import com.google.inject.Inject;

import gov.ca.cwds.data.BaseDaoImpl;

/**
 * Test DAO.
 */
public class TestNormalizedEntityDao extends BaseDaoImpl<TestNormalizedEntity> {

  @Inject
  public TestNormalizedEntityDao(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

}
