package gov.ca.cwds.data.persistence.ns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

  /**
   * Convert to ElasticSearchPersonAllegation for ES person document allegation element.
   * 
   * @return ES person allegation element.
   */
  public ElasticSearchPersonAllegation toEsAllegation() {
    ElasticSearchPersonAllegation ret = new ElasticSearchPersonAllegation();

    ret.setAllegationDescription(allegationDescription);
    ret.setDispositionDescription(dispositionDescription);
    ret.setId(id);

    ret.setPerpetratorFirstName(this.perpetrator.getFirstName());
    ret.setPerpetratorId(this.perpetrator.getId());
    ret.setPerpetratorLastName(this.perpetrator.getLastName());
    ret.setPerpetratorLegacyClientId(this.perpetrator.getLegacyId());

    ret.setVictimFirstName(this.victim.getFirstName());
    ret.setVictimId(this.victim.getId());
    ret.setVictimLastName(this.victim.getLastName());
    ret.setVictimLegacyClientId(this.victim.getLegacyId());

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

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public final int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, false);
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public final boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj, false);
  }

}
