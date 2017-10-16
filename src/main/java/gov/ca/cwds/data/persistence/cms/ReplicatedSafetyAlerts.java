package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import gov.ca.cwds.data.es.ElasticSearchSafetyAlert;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiPersonAware;

/**
 * Pseudo-normalized container for CMS safety alerts.
 * 
 * @author CWDS API Team
 */
public class ReplicatedSafetyAlerts implements PersistentObject, ApiPersonAware {

  private static final long serialVersionUID = 8733181688462933133L;

  private String clientId;
  private List<ElasticSearchSafetyAlert> safetyAlerts = new ArrayList<>();

  /**
   * Default constructor.
   */
  public ReplicatedSafetyAlerts() {
    // Default, no-op
  }

  /**
   * Construct the object
   * 
   * @param clientId The referral client id.
   */
  public ReplicatedSafetyAlerts(String clientId) {
    this.clientId = clientId;
  }

  /**
   * Adds a safety alert to this container.
   * 
   * @param safetyAlert The safety alert to add.
   */
  public void addSafetyAlert(ElasticSearchSafetyAlert safetyAlert) {
    safetyAlerts.add(safetyAlert);
  }

  public List<ElasticSearchSafetyAlert> getSafetyAlerts() {
    return safetyAlerts;
  }

  public void setSafetyAlerts(List<ElasticSearchSafetyAlert> safetyAlerts) {
    this.safetyAlerts = safetyAlerts;
  }

  @Override
  public Serializable getPrimaryKey() {
    return this.clientId;
  }

  @Override
  public Date getBirthDate() {
    return null;
  }

  @Override
  public String getFirstName() {
    return null;
  }

  @Override
  public String getGender() {
    return null;
  }

  @Override
  public String getLastName() {
    return null;
  }

  @Override
  public String getMiddleName() {
    return null;
  }

  @Override
  public String getNameSuffix() {
    return null;
  }

  @Override
  public String getSsn() {
    return null;
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
