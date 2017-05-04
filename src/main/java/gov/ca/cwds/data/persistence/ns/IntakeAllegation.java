package gov.ca.cwds.data.persistence.ns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonAllegation;
import gov.ca.cwds.data.persistence.PersistentObject;

/**
 * Represents a screening allegation.
 * 
 * @author CWDS API Team
 */
public class IntakeAllegation implements PersistentObject {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  private String id;

  private List<String> allegationTypes = new ArrayList<>();

  /**
   * Not yet available from Intake PG.
   */
  private String allegationDescription;

  /**
   * Not yet available from Intake PG.
   */
  private String dispositionDescription;

  private IntakeParticipant victim = new IntakeParticipant();

  private IntakeParticipant perpetrator = new IntakeParticipant();

  public ElasticSearchPersonAllegation toEsAllegation() {
    ElasticSearchPersonAllegation ret = new ElasticSearchPersonAllegation();

    ret.allegationDescription = this.allegationDescription;
    ret.dispositionDescription = this.dispositionDescription;
    ret.id = this.id;

    ret.perpetratorFirstName = this.perpetrator.getFirstName();
    ret.perpetratorId = this.perpetrator.getId();
    ret.perpetratorLastName = this.perpetrator.getLastName();
    ret.perpetratorLegacyClientId = this.perpetrator.getLegacyId();

    ret.victimFirstName = this.victim.getFirstName();
    ret.victimId = this.victim.getId();
    ret.victimLastName = this.victim.getLastName();
    ret.victimLegacyClientId = this.victim.getLegacyId();

    return ret;
  }

  @Override
  public Serializable getPrimaryKey() {
    return getId();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<String> getAllegationTypes() {
    return allegationTypes;
  }

  public void setAllegationTypes(List<String> allegationTypes) {
    this.allegationTypes = allegationTypes;
  }

  public String getAllegationDescription() {
    return allegationDescription;
  }

  public void setAllegationDescription(String allegationDescription) {
    this.allegationDescription = allegationDescription;
  }

  public String getDispositionDescription() {
    return dispositionDescription;
  }

  public void setDispositionDescription(String dispositionDescription) {
    this.dispositionDescription = dispositionDescription;
  }

  public IntakeParticipant getVictim() {
    return victim;
  }

  public void setVictim(IntakeParticipant victim) {
    this.victim = victim;
  }

  public IntakeParticipant getPerpetrator() {
    return perpetrator;
  }

  public void setPerpetrator(IntakeParticipant perpetrator) {
    this.perpetrator = perpetrator;
  }

}
