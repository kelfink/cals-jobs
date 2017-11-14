package gov.ca.cwds.data.persistence.ns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import gov.ca.cwds.data.es.ElasticSearchPersonAllegation;
import gov.ca.cwds.data.es.ElasticSearchPersonNestedPerson;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.neutron.util.transform.ElasticTransformer;
import gov.ca.cwds.rest.api.domain.cms.LegacyTable;

/**
 * Represents a screening allegation.
 * 
 * @author CWDS API Team
 */
public class IntakeAllegation implements PersistentObject {

  private static final long serialVersionUID = 1L;

  private String id;

  private List<String> allegationTypes = new ArrayList<>();

  /**
   * <strong>FUTURE:</strong> Not yet available from Intake PostgreSQL.
   */
  private String allegationDescription;

  /**
   * <strong>FUTURE:</strong> Not yet available from Intake PostgreSQL.
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
    ret.setLegacyId(id);
    ret.setLegacyDescriptor(
        ElasticTransformer.createLegacyDescriptor(id, null, LegacyTable.ALLEGATION));

    ElasticSearchPersonNestedPerson perpet = new ElasticSearchPersonNestedPerson();
    perpet.setId(this.perpetrator.getId());
    perpet.setFirstName(this.perpetrator.getFirstName());
    perpet.setLastName(this.perpetrator.getLastName());
    perpet.setLegacyDescriptor(
        ElasticTransformer.createLegacyDescriptor(this.perpetrator.getLegacyId(),
            this.perpetrator.getLegacyLastUpdated(), LegacyTable.CLIENT));
    ret.setPerpetrator(perpet);

    ret.setPerpetratorFirstName(this.perpetrator.getFirstName());
    ret.setPerpetratorId(this.perpetrator.getId());
    ret.setPerpetratorLastName(this.perpetrator.getLastName());
    ret.setPerpetratorLegacyClientId(this.perpetrator.getLegacyId());

    ElasticSearchPersonNestedPerson vict = new ElasticSearchPersonNestedPerson();
    vict.setId(this.victim.getId());
    vict.setFirstName(this.victim.getFirstName());
    vict.setLastName(this.victim.getLastName());
    vict.setLegacyDescriptor(ElasticTransformer.createLegacyDescriptor(this.victim.getLegacyId(),
        this.victim.getLegacyLastUpdated(), LegacyTable.CLIENT));
    ret.setVictim(vict);

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
