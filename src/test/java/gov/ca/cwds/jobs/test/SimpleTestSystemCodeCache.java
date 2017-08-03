package gov.ca.cwds.jobs.test;

import java.util.HashSet;
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
    Set<SystemCode> systemCodes = new HashSet<>();
    // Add META_A_ active codes
    for (int i = 1; i < 4; i++) {
      systemCodes.add(new SystemCode(new Integer(i).shortValue(), null, "N", null,
          ("DESCRIPTION_A" + i), null, null, "META_A", null));
    }

    // Add META_A_ inactive codes
    for (int i = 4; i < 7; i++) {
      systemCodes.add(new SystemCode(new Integer(i).shortValue(), null, "Y", null,
          ("DESCRIPTION_A" + i), null, null, "META_A", null));
    }

    // Add META_B_ active codes
    for (int i = 7; i < 10; i++) {
      systemCodes.add(new SystemCode(new Integer(i).shortValue(), null, "N", null,
          ("DESCRIPTION_B" + i), null, null, "META_B", null));
    }

    return systemCodes;
  }

  @Override
  public Set<SystemMeta> getAllSystemMetas() {
    Set<SystemMeta> systemMetas = new HashSet<>();
    systemMetas.add(new SystemMeta("META_A", "META_A", "META_A_DESC"));
    systemMetas.add(new SystemMeta("META_B", "META_B", "META_B_DESC"));
    systemMetas.add(new SystemMeta("META_C", "META_C", "META_C_DESC"));
    return systemMetas;
  }

  @Override
  public SystemCode getSystemCode(Number id) {
    if (196 == id.intValue()) {
      return new SystemCode(id.shortValue(), null, null, null, "Daughter/Mother (Birth)", null,
          null, "CLNTRELC", null);
    }

    return null;
  }

  @Override
  public String getSystemCodeShortDescription(Number arg0) {
    return null;
  }

  @Override
  public Set<SystemCode> getSystemCodesForMeta(String arg0) {
    return null;
  }

  @Override
  public boolean verifyActiveSystemCodeDescriptionForMeta(String arg0, String arg1) {
    return false;
  }

  @Override
  public boolean verifyActiveSystemCodeIdForMeta(Number arg0, String arg1) {
    return false;
  }

  @Override
  public boolean verifyActiveLogicalIdForMeta(String arg0, String arg1) {
    return false;
  }

}
