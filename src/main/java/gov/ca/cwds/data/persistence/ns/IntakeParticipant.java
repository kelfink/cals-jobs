package gov.ca.cwds.data.persistence.ns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import gov.ca.cwds.dao.ApiLegacyAware;
import gov.ca.cwds.dao.ApiScreeningAware;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonAddress;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonPhone;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonScreening;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiAddressAware;
import gov.ca.cwds.data.std.ApiMultipleAddressesAware;
import gov.ca.cwds.data.std.ApiMultiplePhonesAware;
import gov.ca.cwds.data.std.ApiPersonAware;
import gov.ca.cwds.data.std.ApiPhoneAware;

/**
 * Represents an Intake Participant or Person.
 * 
 * @author CWDS API Team
 */
public class IntakeParticipant implements PersistentObject, ApiPersonAware,
    ApiMultipleAddressesAware, ApiMultiplePhonesAware, ApiScreeningAware, ApiLegacyAware {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  private String id;

  private String legacyId;

  private String firstName;

  private String lastName;

  private Date birthDate;

  private String gender;

  private String ssn;

  private Map<String, ElasticSearchPersonAddress> addresses = new LinkedHashMap<>();

  private Map<String, ElasticSearchPersonPhone> phones = new LinkedHashMap<>();

  /**
   * Can't convert to ES screening until all data for screening are loaded.
   */
  private Map<String, IntakeScreening> screenings = new LinkedHashMap<>();

  public String makeUpdateJson() {
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

}
