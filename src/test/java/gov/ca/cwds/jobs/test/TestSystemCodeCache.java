package gov.ca.cwds.jobs.test;

import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.persistence.cms.ApiSystemCodeCache;
import gov.ca.cwds.data.persistence.cms.ApiSystemCodeDao;
import gov.ca.cwds.data.persistence.cms.CmsSystemCodeCacheService;
import gov.ca.cwds.data.persistence.cms.SystemCodeDaoFileImpl;
import gov.ca.cwds.jobs.BasePersonIndexerJob;

public class TestSystemCodeCache {

  private ApiSystemCodeDao sysCodeDao;
  private ApiSystemCodeCache sysCodeCache;

  private static TestSystemCodeCache instance;

  private TestSystemCodeCache() {
    sysCodeDao = new SystemCodeDaoFileImpl();
    sysCodeCache = new CmsSystemCodeCacheService(sysCodeDao);

    BasePersonIndexerJob.setSystemCodes(sysCodeCache);
    ElasticSearchPerson.setSystemCodes(sysCodeCache);
  }

  public static synchronized void init() {
    if (instance == null) {
      instance = new TestSystemCodeCache();
    }
  }

}
