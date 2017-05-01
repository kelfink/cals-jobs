package gov.ca.cwds.data.persistence.ns;

import java.io.Serializable;

import gov.ca.cwds.data.persistence.PersistentObject;

public class IntakePerson implements PersistentObject {

  private String id;

  private String firstName;

  private String lastName;

  @Override
  public Serializable getPrimaryKey() {
    // TODO Auto-generated method stub
    return null;
  }

}
