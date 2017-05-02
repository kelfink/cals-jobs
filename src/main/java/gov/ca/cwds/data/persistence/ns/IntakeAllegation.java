package gov.ca.cwds.data.persistence.ns;

import java.io.Serializable;

import gov.ca.cwds.data.persistence.PersistentObject;

public class IntakeAllegation implements PersistentObject {

  private String id;

  private String allegationTypes;

  private IntakeParticipant victim = new IntakeParticipant();

  private IntakeParticipant perpetrator = new IntakeParticipant();

  @Override
  public Serializable getPrimaryKey() {
    return id;
  }

}
