package gov.ca.cwds.jobs.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import gov.ca.cwds.dao.ApiLegacyAware;
import gov.ca.cwds.data.ApiTypedIdentifier;
import gov.ca.cwds.data.ReadablePhone;
import gov.ca.cwds.data.es.ElasticSearchLegacyDescriptor;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiAddressAware;
import gov.ca.cwds.data.std.ApiLanguageAware;
import gov.ca.cwds.data.std.ApiMultipleAddressesAware;
import gov.ca.cwds.data.std.ApiMultipleLanguagesAware;
import gov.ca.cwds.data.std.ApiMultiplePhonesAware;
import gov.ca.cwds.data.std.ApiPersonAware;
import gov.ca.cwds.data.std.ApiPhoneAware;
import gov.ca.cwds.data.std.ApiPhoneAware.PhoneType;
import gov.ca.cwds.jobs.util.transform.ElasticTransformer;

@JsonPropertyOrder(alphabetic = true)
public class TestNormalizedEntity
    implements PersistentObject, ApiPersonAware, ApiTypedIdentifier<String>, ApiLegacyAware,
    ApiMultiplePhonesAware, ApiMultipleAddressesAware, ApiMultipleLanguagesAware {

  private String id;

  private String firstName;

  private String lastName;

  private String title;

  private List<TestNormalizedEntry> entries = new ArrayList<>();

  public TestNormalizedEntity(String id) {
    this.id = id;
  }

  @Override
  public Serializable getPrimaryKey() {
    return id;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  @Override
  public Date getBirthDate() {
    return null;
  }

  @Override
  public String getFirstName() {
    return firstName;
  }

  @Override
  public String getGender() {
    return null;
  }

  @Override
  public String getLastName() {
    return lastName;
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

  public String getName() {
    return firstName;
  }

  public void setName(String name) {
    this.firstName = name;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void addEntry(TestNormalizedEntry entry) {
    this.entries.add(entry);
  }

  @Override
  public String getLegacyId() {
    return getId();
  }

  @Override
  public ElasticSearchLegacyDescriptor getLegacyDescriptor() {
    return ElasticTransformer.createLegacyDescriptor(null, null, null);
  }

  @Override
  public ApiPhoneAware[] getPhones() {
    ApiPhoneAware[] phones = new ApiPhoneAware[1];
    phones[0] = new ReadablePhone("abc1234567", "408-374-2790", "", PhoneType.Home);
    return phones;
  }

  @Override
  public ApiAddressAware[] getAddresses() {
    ApiAddressAware[] addresses = new ApiAddressAware[1];
    addresses[0] = new SimpleAddress("Sacramento", "Sacramento", "CA", "1234", "95660");
    return addresses;
  }

  @Override
  public ApiLanguageAware[] getLanguages() {
    ApiLanguageAware[] languages = new ApiLanguageAware[1];
    languages[0] = new TestLanguage();
    return languages;
  }

}
