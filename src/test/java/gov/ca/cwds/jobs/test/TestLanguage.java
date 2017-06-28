package gov.ca.cwds.jobs.test;

import java.io.Serializable;

import gov.ca.cwds.data.std.ApiLanguageAware;

public class TestLanguage implements ApiLanguageAware, Serializable {

  @Override
  public Integer getLanguageSysId() {
    return 1249;
  }

}
