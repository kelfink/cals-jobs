package gov.ca.cwds.neutron.util.shrinkray;

import java.util.Date;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.data.std.ApiPersonAware;

/**
 * Empty person implementation. Useful for jobs which POJOs that return no personal information but
 * still implement ApiPersonAware.
 * 
 * Named after the Jimmy's home town.
 * 
 * @author CWDS API Team
 */
public interface RetrovillePerson extends ApiPersonAware, PersistentObject, ApiMarker {

  @Override
  default Date getBirthDate() {
    return null;
  }

  @Override
  default String getFirstName() {
    return null;
  }

  @Override
  default String getGender() {
    return null;
  }

  @Override
  default String getLastName() {
    return null;
  }

  @Override
  default String getMiddleName() {
    return null;
  }

  @Override
  default String getNameSuffix() {
    return null;
  }

  @Override
  default String getSsn() {
    return null;
  }

}
