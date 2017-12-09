package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import gov.ca.cwds.data.es.ElasticSearchPersonAllegation;
import gov.ca.cwds.data.es.ElasticSearchPersonReferral;
import gov.ca.cwds.neutron.util.shrinkray.RetrovillePerson;

/**
 * Normalized container for CMS person referral.
 * 
 * @author CWDS API Team
 */
public class ReplicatedPersonReferrals implements RetrovillePerson {

  private static final long serialVersionUID = -8746969311364544478L;

  private String clientId;
  private Map<String, ElasticSearchPersonReferral> referrals = new ConcurrentHashMap<>();

  /**
   * Key: Referral ID <br>
   * Value: ElasticSearchPersonAllegation objects for the keyed referral id.
   */
  private Map<String, List<ElasticSearchPersonAllegation>> referralAllegations =
      new ConcurrentHashMap<>();

  /**
   * Default constructor.
   */
  public ReplicatedPersonReferrals() {
    // Default, no-op
  }

  /**
   * Construct the object
   * 
   * @param clientId The referral client id.
   */
  public ReplicatedPersonReferrals(String clientId) {
    this.clientId = clientId;
  }

  /**
   * @return The referrals collected in this container.
   */
  public List<ElasticSearchPersonReferral> getReferrals() {
    return new ArrayList<>(referrals.values());
  }

  /**
   * @param referralId referral to look up
   * @return whether referral is here here
   */
  public boolean hasReferral(String referralId) {
    return referrals.containsKey(referralId);
  }

  @SuppressWarnings("javadoc")
  public ElasticSearchPersonReferral getReferral(String referralId) {
    return referrals.get(referralId);
  }

  /**
   * Adds a referral to this container with optional allegation. Note that a referral may have more
   * than one allegations.
   * 
   * @param incomingReferral The referral to add.
   * @param allegation The allegation to add.
   */
  public void addReferral(ElasticSearchPersonReferral incomingReferral,
      ElasticSearchPersonAllegation allegation) {

    final String refId = incomingReferral.getId();
    ElasticSearchPersonReferral referral;
    if (referrals.containsKey(refId)) {
      referral = referrals.get(refId);
    } else {
      referral = incomingReferral;
    }

    referrals.put(refId, referral);

    // Add allegation.
    if (allegation != null) {
      List<ElasticSearchPersonAllegation> allegations = referralAllegations.get(refId);
      if (allegations == null) {
        allegations = new ArrayList<>();
        referralAllegations.put(incomingReferral.getId(), allegations);
      }
      allegations.add(allegation);
      incomingReferral.setAllegations(allegations);
    }
  }

  @Override
  public Serializable getPrimaryKey() {
    return this.clientId;
  }

  @SuppressWarnings("javadoc")
  public String getClientId() {
    return clientId;
  }

  @SuppressWarnings("javadoc")
  public void setClientId(String clientId) {
    this.clientId = clientId;
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
