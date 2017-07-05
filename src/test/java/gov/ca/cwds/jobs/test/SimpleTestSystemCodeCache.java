package gov.ca.cwds.jobs.test;

import java.util.Set;

import gov.ca.cwds.rest.api.domain.cms.SystemCode;
import gov.ca.cwds.rest.api.domain.cms.SystemCodeCache;
import gov.ca.cwds.rest.api.domain.cms.SystemMeta;

@SuppressWarnings("serial")
public class SimpleTestSystemCodeCache implements SystemCodeCache {

  private static SimpleTestSystemCodeCache instance;

  public static synchronized void init() {
    if (instance == null) {
      instance = new SimpleTestSystemCodeCache();
    }
  }

  public SimpleTestSystemCodeCache() {
    register();
  }

  @Override
  public Set<SystemCode> getAllSystemCodes() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<SystemMeta> getAllSystemMetas() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SystemCode getSystemCode(Number id) {
    if (196 == id.intValue()) {
      return new SystemCode(id.shortValue(), null, null, null, "Daughter/Mother (Birth)", null,
          null, "CLNTRELC", null);
    }
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getSystemCodeShortDescription(Number arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<SystemCode> getSystemCodesForMeta(String arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean verifyActiveSystemCodeDescriptionForMeta(String arg0, String arg1) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean verifyActiveSystemCodeIdForMeta(Number arg0, String arg1) {
    // TODO Auto-generated method stub
    return false;
  }
}
