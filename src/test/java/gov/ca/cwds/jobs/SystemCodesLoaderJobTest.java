package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.jobs.test.SimpleTestSystemCodeCache;
import gov.ca.cwds.neutron.rocket.syscode.NsSystemCode;
import gov.ca.cwds.neutron.rocket.syscode.NsSystemCodeDao;
import gov.ca.cwds.rest.api.domain.cms.SystemCode;
import gov.ca.cwds.rest.api.domain.cms.SystemCodeCache;
import gov.ca.cwds.rest.api.domain.cms.SystemMeta;

public class SystemCodesLoaderJobTest extends Goddard {

  public static class TestSystemCodesLoaderJob extends SystemCodesLoaderJob {

    public TestSystemCodesLoaderJob(NsSystemCodeDao systemCodeDao) {
      super(systemCodeDao);
    }

    @Override
    public void handleSystemMeta(Map<String, SystemMeta> systemMetaMap, NsSystemCode nsc,
        String categoryId, SystemCode systemCode) {
      super.handleSystemMeta(systemMetaMap, nsc, categoryId, systemCode);
    }

    @Override
    public void handleLogicalId(NsSystemCode nsc, SystemCode systemCode) {
      super.handleLogicalId(nsc, systemCode);
    }

    @Override
    public void handleSubCategory(Map<Short, SystemCode> systemCodeMap, NsSystemCode nsc,
        SystemCode systemCode) {
      super.handleSubCategory(systemCodeMap, nsc, systemCode);
    }

    @Override
    public void deleteNsSystemCodes(Connection conn) throws SQLException {
      super.deleteNsSystemCodes(conn);
    }

  }

  SystemCodesLoaderJob target;
  NsSystemCodeDao dao;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();

    dao = new NsSystemCodeDao(sessionFactory);
    target = new TestSystemCodesLoaderJob(dao);
    SimpleTestSystemCodeCache.init();
  }

  @Test
  public void type() throws Exception {
    assertThat(SystemCodesLoaderJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void handleSystemMeta() throws Exception {
    final Map<String, SystemMeta> systemMetaMap = new HashMap<>();
    final NsSystemCode nsc = mock(NsSystemCode.class);
    final String categoryId = DEFAULT_CLIENT_ID;
    final SystemCode systemCode = mock(SystemCode.class);

    target.handleSystemMeta(systemMetaMap, nsc, categoryId, systemCode);
    assertThat(target, notNullValue());
  }

  @Test
  public void handleLogicalId() throws Exception {
    final Map<String, SystemMeta> systemMetaMap = new HashMap<>();
    final NsSystemCode nsc = mock(NsSystemCode.class);
    final SystemCode systemCode = mock(SystemCode.class);

    when(systemCode.getLogicalId()).thenReturn("07");

    target.handleLogicalId(nsc, systemCode);
    assertThat(target, notNullValue());
  }

  @Test
  public void deleteNsSystemCodes() throws Exception {
    target.deleteNsSystemCodes(con);
    assertThat(target, notNullValue());
  }

  @Test
  public void handleSubCategory() throws Exception {
    final Map<Short, SystemCode> systemCodeMap = new HashMap<>();
    final NsSystemCode nsc = mock(NsSystemCode.class);
    final SystemCode systemCode = mock(SystemCode.class);
    when(systemCode.getCategoryId()).thenReturn((short) 1);

    target.handleSubCategory(systemCodeMap, nsc, systemCode);
    assertThat(target, notNullValue());
  }

  @Test
  public void testLoading() throws Exception {
    Map<Integer, NsSystemCode> loadedSystemCode = target.load();
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
    final NsSystemCode nsc = new NsSystemCode();
    nsc.setId(systemCode.getSystemId().intValue());
    nsc.setDescription(systemCode.getShortDescription());
    nsc.setCategoryId(systemCode.getForeignKeyMetaTable());
    nsc.setCategoryDescription(categoryDesc);
    return nsc;
  }

  @Test
  public void load_Args__() throws Exception {
    Map<Integer, NsSystemCode> actual = target.load();
    assertThat(actual, is(notNullValue()));
  }

  public void load_Args__error() throws Exception {
    Map<Integer, NsSystemCode> actual = target.load();
    assertThat(actual, is(notNullValue()));
  }

  // @Test
  // public void main_Args__StringArray() throws Exception {
  // String[] args = new String[] {};
  // SystemCodesLoaderJob.main(args);
  // }

}
