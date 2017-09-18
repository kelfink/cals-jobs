package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.ca.cwds.jobs.SystemCodesLoaderJob.NsSystemCode;
import gov.ca.cwds.jobs.SystemCodesLoaderJob.NsSystemCodeDao;
import gov.ca.cwds.jobs.test.SimpleTestSystemCodeCache;
import gov.ca.cwds.rest.api.domain.cms.SystemCode;
import gov.ca.cwds.rest.api.domain.cms.SystemCodeCache;

public class SystemCodesLoaderJobTest {

  private static SystemCodesLoaderJob job;

  @BeforeClass
  public static void setupClass() throws Exception {
    SessionFactory sessionFactory = mock(SessionFactory.class);
    Session session = mock(Session.class);

    Transaction transaction = mock(Transaction.class);
    when(sessionFactory.getCurrentSession()).thenReturn(session);
    when(session.beginTransaction()).thenReturn(transaction);

    NsSystemCodeDao dao = new NsSystemCodeDao(sessionFactory);
    job = new SystemCodesLoaderJob(dao);
    SimpleTestSystemCodeCache.init();
  }

  @Test
  public void type() throws Exception {
    assertThat(SystemCodesLoaderJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(job, notNullValue());
  }

  @Test
  public void testLoading() throws Exception {
    Map<Integer, NsSystemCode> loadedSystemCode = job.load();
    Assert.assertNotNull(loadedSystemCode);
    Assert.assertEquals(6, loadedSystemCode.size());
    Assert.assertTrue(loadedSystemCode.containsKey(1));
    Assert.assertTrue(loadedSystemCode.containsKey(2));
    Assert.assertTrue(loadedSystemCode.containsKey(3));
    Assert.assertTrue(loadedSystemCode.containsKey(7));
    Assert.assertTrue(loadedSystemCode.containsKey(8));
    Assert.assertTrue(loadedSystemCode.containsKey(9));
    Assert.assertFalse(loadedSystemCode.containsKey(4));
    Assert.assertFalse(loadedSystemCode.containsKey(5));
    Assert.assertFalse(loadedSystemCode.containsKey(6));

    for (SystemCode systemCode : SystemCodeCache.global().getAllSystemCodes()) {
      if (systemCode.getSystemId().equals(1)) {
        Assert.assertEquals(createNsSystemCode(systemCode, "META_A_DESC"), loadedSystemCode.get(1));
      }

      if (systemCode.getSystemId().equals(2)) {
        Assert.assertEquals(createNsSystemCode(systemCode, "META_A_DESC"), loadedSystemCode.get(2));
      }

      if (systemCode.getSystemId().equals(3)) {
        Assert.assertEquals(createNsSystemCode(systemCode, "META_A_DESC"), loadedSystemCode.get(3));
      }

      if (systemCode.getSystemId().equals(7)) {
        Assert.assertEquals(createNsSystemCode(systemCode, "META_C_DESC"), loadedSystemCode.get(7));
      }

      if (systemCode.getSystemId().equals(8)) {
        Assert.assertEquals(createNsSystemCode(systemCode, "META_C_DESC"), loadedSystemCode.get(8));
      }

      if (systemCode.getSystemId().equals(9)) {
        Assert.assertEquals(createNsSystemCode(systemCode, "META_C_DESC"), loadedSystemCode.get(9));
      }
    }
  }

  private NsSystemCode createNsSystemCode(SystemCode systemCode, String categoryDesc) {
    NsSystemCode nsc = new NsSystemCode();
    nsc.setId(systemCode.getSystemId().intValue());
    nsc.setDescription(systemCode.getShortDescription());
    nsc.setCategoryId(systemCode.getForeignKeyMetaTable());
    nsc.setCategoryDescription(categoryDesc);
    return nsc;
  }

}
