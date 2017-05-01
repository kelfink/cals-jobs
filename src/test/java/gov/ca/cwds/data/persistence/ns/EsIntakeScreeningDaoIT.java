package gov.ca.cwds.data.persistence.ns;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import gov.ca.cwds.dao.ns.EsIntakeScreeningDao;

/**
 * 
 * @author CWDS API Team
 */
public class EsIntakeScreeningDaoIT
// implements DaoTestTemplate
{

  private static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  /**
   * 
   */
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private static EsIntakeScreeningDao dao;
  private static SessionFactory sessionFactory;
  private Session session;

  @SuppressWarnings("javadoc")
  @BeforeClass
  public static void beforeClass() {
    sessionFactory = new Configuration().configure("ns-hibernate.cfg.xml").buildSessionFactory();
    dao = new EsIntakeScreeningDao(sessionFactory);
  }

  @SuppressWarnings("javadoc")
  @AfterClass
  public static void afterClass() {
    sessionFactory.close();
  }

  // @Override
  @Before
  public void setup() {
    session = sessionFactory.getCurrentSession();
    session.beginTransaction();
  }

  // @Override
  @After
  public void teardown() {
    session.getTransaction().rollback();
  }

  // @Override
  @Test
  public void testFindAllNamedQueryExist() throws Exception {
    Query query = session
        .getNamedQuery("gov.ca.cwds.data.persistence.ns.EsNsScreeningHistory.findAllUpdatedAfter");
    assertThat(query, is(notNullValue()));
  }

  // @Override
  @Test
  public void testFindAllReturnsCorrectList() {
    // Query query = session.getNamedQuery("gov.ca.cwds.rest.api.persistence.ns.Person.findAll");
    // assertThat(query.list().size(), is(2));
  }

  @SuppressWarnings("javadoc")
  @Test
  public void testfindAllUpdatedAfterNamedQueryExists() throws Exception {
    Query query = session
        .getNamedQuery("gov.ca.cwds.data.persistence.ns.EsNsScreeningHistory.findAllUpdatedAfter");
    assertThat(query, is(notNullValue()));
  }

  @SuppressWarnings("javadoc")
  @Test
  public void testfindAllUpdatedAfterReturnsCorrectList() throws Exception {
    // Query query =
    // session.getNamedQuery("gov.ca.cwds.rest.api.persistence.ns.Person.findAllUpdatedAfter")
    // .setDate("after", TIMESTAMP_FORMAT.parse("2016-11-02 00:00:00"));
    // assertThat(query.list().size(), is(1));
  }

  // @Override
  @Test
  public void testFind() {
    // long id = 1;
    // Person found = dao.find(id);
    // assertThat(found.getId(), is(id));
  }

  // @Override
  @Test
  public void testFindEntityNotFoundException() throws Exception {
    // long id = 99;
    // Person found = dao.find(id);
    // assertThat(found, is(nullValue()));
  }

}
