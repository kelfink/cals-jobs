package gov.ca.cwds.jobs.inject;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import gov.ca.cwds.dao.DocumentMetadataDao;
import gov.ca.cwds.dao.cms.DocumentMetadataDaoImpl;

public class JobsGuiceInjector extends AbstractModule {

  /*
   * (non-Javadoc)
   * 
   * @see com.google.inject.AbstractModule#configure()
   */
  @Override
  protected void configure() {
    bind(DocumentMetadataDao.class).to(DocumentMetadataDaoImpl.class);


  }

  @Provides
  @CmsSessionFactory
  SessionFactory cmsSessionFactory() {
    return new Configuration().configure("cms-hibernate.cfg.xml").buildSessionFactory();
  }

  @Provides
  @NsSessionFactory
  SessionFactory nsSessionFactory() {
    return new Configuration().configure("ns-hibernate.cfg.xml").buildSessionFactory();
  }

}
