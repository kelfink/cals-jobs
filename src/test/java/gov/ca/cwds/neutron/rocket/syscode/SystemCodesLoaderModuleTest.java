package gov.ca.cwds.neutron.rocket.syscode;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import gov.ca.cwds.jobs.Goddard;
import gov.ca.cwds.neutron.rocket.syscode.SystemCodesLoaderModule;
import gov.ca.cwds.rest.api.domain.cms.SystemCodeCache;

public class SystemCodesLoaderModuleTest extends Goddard {

  SystemCodesLoaderModule target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
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

  @Test
  public void instantiation_ctor() throws Exception {
    target = new SystemCodesLoaderModule("one.xml", "two.xml");
    assertThat(target, notNullValue());
  }

  @Test
  public void provideSystemCodeCache_Args__SystemCodeDao__SystemMetaDao() throws Exception {
    SystemCodeCache actual = target.provideSystemCodeCache(systemCodeDao, systemMetaDao);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void getCmsHibernateConfig_Args__() throws Exception {
    final String actual = target.getCmsHibernateConfig();
    final String expected = "jobs-cms-hibernate.cfg.xml";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setCmsHibernateConfig_Args__String() throws Exception {
    String cmsHibernateConfig = null;
    target.setCmsHibernateConfig(cmsHibernateConfig);
  }

  @Test
  public void getNsHibernateConfig_Args__() throws Exception {
    final String actual = target.getNsHibernateConfig();
    final String expected = "jobs-ns-hibernate.cfg.xml";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setNsHibernateConfig_Args__String() throws Exception {
    String nsHibernateConfig = null;
    target.setNsHibernateConfig(nsHibernateConfig);
  }

  // @Test
  // public void configure_Args__() throws Exception {
  // Injector injector =
  // Guice.createInjector(new SystemCodesLoaderModule("test-h2-cms.xml", "test-h2-ns.xml"));
  // // target.configure(); // Can only call indirectly via Guice. :-(
  // }

}
