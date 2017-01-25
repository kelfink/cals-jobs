package gov.ca.cwds.jobs.inject;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import gov.ca.cwds.dao.DocumentMetadataDao;
import gov.ca.cwds.dao.cms.DocumentMetadataDaoImpl;
import gov.ca.cwds.data.CmsSystemCodeSerializer;
import gov.ca.cwds.data.persistence.cms.CmsSystemCodeCacheService;
import gov.ca.cwds.data.persistence.cms.ISystemCodeCache;
import gov.ca.cwds.data.persistence.cms.ISystemCodeDao;
import gov.ca.cwds.data.persistence.cms.SystemCodeDaoFileImpl;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.inject.NsSessionFactory;

public class JobsGuiceInjector extends AbstractModule {

  /**
   * {@inheritDoc}
   * 
   * @see com.google.inject.AbstractModule#configure()
   */
  @Override
  protected void configure() {
    bind(DocumentMetadataDao.class).to(DocumentMetadataDaoImpl.class);

    // Must declare as singleton, otherwise Guice creates a new instance each time.
    bind(ObjectMapper.class).asEagerSingleton();

    // Register CMS system code translator.
    bind(ISystemCodeDao.class).to(SystemCodeDaoFileImpl.class);
    bind(ISystemCodeCache.class).to(CmsSystemCodeCacheService.class).asEagerSingleton();
    bind(CmsSystemCodeSerializer.class).asEagerSingleton();
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
