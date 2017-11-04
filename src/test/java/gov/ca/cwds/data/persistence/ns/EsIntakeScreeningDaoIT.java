package gov.ca.cwds.data.persistence.ns;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.junit.Before;

import gov.ca.cwds.dao.ns.EsIntakeScreeningDao;
import gov.ca.cwds.jobs.Goddard;

/**
 * 
 * @author CWDS API Team
 */
public class EsIntakeScreeningDaoIT extends Goddard {

  private static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  /**
   * Expect to not throw exceptions.
   */
  // @Rule
  // public ExpectedException thrown = ExpectedException.none();

  private EsIntakeScreeningDao dao;

  // @SuppressWarnings("javadoc")
  // @BeforeClass
  // public static void beforeClass() throws Exception {
  // sessionFactory =
  // new Configuration().configure("test-ns-hibernate.cfg.xml").buildSessionFactory();
  // dao = new EsIntakeScreeningDao(sessionFactory);
  // }
  //
  // @SuppressWarnings("javadoc")
  // @AfterClass
  // public static void afterClass() throws Exception {
  // if (sessionFactory != null) {
  // sessionFactory.close();
  // }
  // }

  @Override
  @Before
  public void setup() throws Exception {
    // session = sessionFactory.getCurrentSession();
    // session.beginTransaction();
    super.setup();
    dao = new EsIntakeScreeningDao(sessionFactory);
  }

  // @After
  // public void teardown() {
  // session.getTransaction().rollback();
  // }

  // @Test
  // public void testFindAllNamedQueryExist() throws Exception {
  // Query query = session
  // .getNamedQuery("gov.ca.cwds.data.persistence.ns.EsIntakeScreening.findAllUpdatedAfter");
  // assertThat(query, is(notNullValue()));
  // }

  // @SuppressWarnings("javadoc")
  // @Test
  // public void testfindAllUpdatedAfterNamedQueryExists() throws Exception {
  // Query query = session
  // .getNamedQuery("gov.ca.cwds.data.persistence.ns.EsIntakeScreening.findAllUpdatedAfter");
  // assertThat(query, is(notNullValue()));
  // }

  // @SuppressWarnings("javadoc")
  // @Test
  // public void testfindAllUpdatedAfterReturnsCorrectList() throws Exception {
  // Query query = session
  // .getNamedQuery("gov.ca.cwds.data.persistence.ns.EsIntakeScreening.findAllUpdatedAfter")
  // .setDate("after", TIMESTAMP_FORMAT.parse("2016-11-02 00:00:00"));
  // // assertThat(query.list().size(), greaterThan(0));
  // }

}
