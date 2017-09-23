package gov.ca.cwds.data.persistence.ns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gov.ca.cwds.dao.ApiLegacyAware;
import gov.ca.cwds.dao.ApiScreeningAware;
import gov.ca.cwds.data.es.ElasticSearchLegacyDescriptor;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonSocialWorker;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonStaff;
import gov.ca.cwds.data.es.ElasticSearchPersonAddress;
import gov.ca.cwds.data.es.ElasticSearchPersonAny;
import gov.ca.cwds.data.es.ElasticSearchPersonNestedPerson;
import gov.ca.cwds.data.es.ElasticSearchPersonPhone;
import gov.ca.cwds.data.es.ElasticSearchPersonReporter;
import gov.ca.cwds.data.es.ElasticSearchPersonScreening;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiAddressAware;
import gov.ca.cwds.data.std.ApiMultipleAddressesAware;
import gov.ca.cwds.data.std.ApiMultiplePhonesAware;
import gov.ca.cwds.data.std.ApiObjectIdentity;
import gov.ca.cwds.data.std.ApiPersonAware;
import gov.ca.cwds.data.std.ApiPhoneAware;
import gov.ca.cwds.jobs.util.transform.ElasticTransformer;
import gov.ca.cwds.rest.api.domain.cms.LegacyTable;

/**
 * Represents an Intake Participant or Person.
 * 
 * @author CWDS API Team
 */
public class IntakeParticipant extends ApiObjectIdentity
    implements PersistentObject, ApiPersonAware, ApiMultipleAddressesAware, ApiMultiplePhonesAware,
    ApiScreeningAware, ApiLegacyAware {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  private String id;

  private String legacyId;

  private Date legacyLastUpdated;

  private String legacyTable;

  private String firstName;

  private String lastName;

  private Date birthDate;

  private String gender;

  private String ssn;

  @JsonIgnore
  private Map<String, ElasticSearchPersonAddress> addresses = new LinkedHashMap<>();

  @JsonIgnore
  private Map<String, ElasticSearchPersonPhone> phones = new LinkedHashMap<>();

  /**
   * Can't convert to ES screening unless all of screening's data area available.
   */
  @JsonIgnore
  private Map<String, IntakeScreening> screenings = new LinkedHashMap<>();

  /**
   * Update section JSON is the participant's screenings.
   * 
   * @return JSON to update document only
   */
  public String buildUpdateJson() {
    return "";
  }

  @Override
  public Serializable getPrimaryKey() {
    return StringUtils.isNotBlank(legacyId) ? legacyId : id;
  }

  @Override
  public Date getBirthDate() {
    return this.birthDate;
  }

  @Override
  public String getFirstName() {
    return this.firstName;
  }

  @Override
  public String getGender() {
    return this.gender;
  }

  @Override
  public String getLastName() {
    return this.lastName;
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
    return this.ssn;
  }

  @Override
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public void setSsn(String ssn) {
    this.ssn = ssn;
  }

  @Override
  public String getLegacyId() {
    return legacyId;
  }

  public void setLegacyId(String legacyId) {
    this.legacyId = legacyId;
  }

  @Override
  public ElasticSearchLegacyDescriptor getLegacyDescriptor() {
    return ElasticTransformer.createLegacyDescriptor(legacyId, legacyLastUpdated,
        LegacyTable.lookupByName(legacyTable));
  }

  @Override
  public ApiPhoneAware[] getPhones() {
    return phones.values().toArray(new ApiPhoneAware[0]);
  }

  @Override
  public ApiAddressAware[] getAddresses() {
    return addresses.values().toArray(new ApiAddressAware[0]);
  }

  @Override
  public ElasticSearchPersonScreening[] getEsScreenings() {
    List<ElasticSearchPersonScreening> ret = new ArrayList<>();

    for (IntakeScreening ess : screenings.values()) {
      ret.add(ess.toEsScreening());
    }

    return ret.toArray(new ElasticSearchPersonScreening[0]);
  }

  /**
   * Convert this participant to an appropriate Elasticsearch nested person element.
   * 
   * @param esType type of Elasticsearch nested person element
   * @param screening optional screening, required for "all_people".
   * @return Elasticsearch nested person object
   */
  public ElasticSearchPersonNestedPerson toEsPerson(EsPersonType esType,
      IntakeScreening screening) {
    ElasticSearchPersonNestedPerson ret;

    switch (esType) {
      case STAFF:
        ret = new ElasticSearchPersonStaff();
        break;

      case REPORTER:
        ret = new ElasticSearchPersonReporter();
        break;

      case SOCIAL_WORKER:
        ret = new ElasticSearchPersonSocialWorker();
        break;

      case ALL:
      default:
        ElasticSearchPersonAny any = new ElasticSearchPersonAny();
        any.getRoles()
            .addAll(screening.findParticipantRoles(id).stream().collect(Collectors.toList()));
        ret = any;
        break;
    }

    ret.setFirstName(firstName);
    ret.setLastName(lastName);
    ret.setLegacyClientId(legacyId);
    ret.setId(id);
    ret.setLegacyDescriptor(getLegacyDescriptor());

    return ret;
  }

  public void addPhone(ElasticSearchPersonPhone ph) {
    if (!phones.containsKey(ph.getPhoneId())) {
      phones.put(ph.getPhoneId(), ph);
    }
  }

  public void addAddress(ElasticSearchPersonAddress addr) {
    if (!addresses.containsKey(addr.getAddressId())) {
      addresses.put(addr.getAddressId(), addr);
    }
  }

  public void addScreening(IntakeScreening screening) {
    if (!screenings.containsKey(screening.getId())) {
      screenings.put(screening.getId(), screening);
    }
  }

  public Map<String, IntakeScreening> getScreenings() {
    return screenings;
  }

  public void setScreenings(Map<String, IntakeScreening> screenings) {
    this.screenings = screenings;
  }

  public void setAddresses(Map<String, ElasticSearchPersonAddress> addresses) {
    this.addresses = addresses;
  }

  public void setPhones(Map<String, ElasticSearchPersonPhone> phones) {
    this.phones = phones;
  }

  public Date getLegacyLastUpdated() {
    return legacyLastUpdated;
  }

  public void setLegacyLastUpdated(Date legacyLastUpdated) {
    this.legacyLastUpdated = legacyLastUpdated;
  }

  public String getLegacyTable() {
    return legacyTable;
  }

  public void setLegacyTable(String legacyTable) {
    this.legacyTable = legacyTable;
  }
}
