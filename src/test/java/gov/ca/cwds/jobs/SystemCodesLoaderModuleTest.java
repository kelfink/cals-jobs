package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.data.cms.SystemCodeDao;
import gov.ca.cwds.data.cms.SystemMetaDao;
import gov.ca.cwds.rest.api.domain.cms.SystemCodeCache;

public class SystemCodesLoaderModuleTest {

  SystemCodesLoaderModule target;

  @Before
  public void setup() throws Exception {
    target = new SystemCodesLoaderModule();
  }

  @Test
  public void type() throws Exception {
    assertThat(SystemCodesLoaderModule.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  // @Test
  // public void configure_Args__() throws Exception {
  // Injector injector =
  // Guice.createInjector(new SystemCodesLoaderModule("test-h2-cms.xml", "test-h2-ns.xml"));
  // // target.configure(); // Can only call indirectly.
  // }

  @Test
  public void provideSystemCodeCache_Args__SystemCodeDao__SystemMetaDao() throws Exception {
    SystemCodeDao systemCodeDao = mock(SystemCodeDao.class);
    SystemMetaDao systemMetaDao = mock(SystemMetaDao.class);
    SystemCodeCache actual = target.provideSystemCodeCache(systemCodeDao, systemMetaDao);
    assertThat(actual, is(notNullValue()));
  }

}
