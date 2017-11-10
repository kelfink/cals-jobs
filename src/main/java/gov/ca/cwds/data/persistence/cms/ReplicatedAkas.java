package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import gov.ca.cwds.data.es.ElasticSearchPersonAka;
import gov.ca.cwds.neutron.util.shrinkray.RetrovillePerson;

/**
 * Pseudo-normalized container for CMS legacy "other client names" (name aliases) by legacy client
 * id.
 * 
 * @author CWDS API Team
 */
public class ReplicatedAkas implements RetrovillePerson {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Legacy person id, the client id.
   */
  private String id;

  private List<ElasticSearchPersonAka> akas = new ArrayList<>();

  /**
   * Default constructor.
   */
  public ReplicatedAkas() {
    // Default, no-op.
  }

  /**
   * Preferred constructor.
   * 
   * @param id legacy id
   */
  public ReplicatedAkas(String id) {
    this.id = id;
  }

  @Override
  public Serializable getPrimaryKey() {
    return id;
  }

  @SuppressWarnings("javadoc")
  public String getId() {
    return id;
  }

  @SuppressWarnings("javadoc")
  public void setId(String id) {
    this.id = id;
  }

  @SuppressWarnings("javadoc")
  public List<ElasticSearchPersonAka> getAkas() {
    return akas;
  }

  @SuppressWarnings("javadoc")
  public void setAkas(List<ElasticSearchPersonAka> akas) {
    this.akas = akas;
  }

  @SuppressWarnings("javadoc")
  public void addAka(ElasticSearchPersonAka aka) {
    this.akas.add(aka);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, false);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj, false);
  }

}
