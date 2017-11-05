package gov.ca.cwds.neutron.shrinkray;

import java.util.Date;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.data.std.ApiPersonAware;

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
