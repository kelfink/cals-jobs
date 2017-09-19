package gov.ca.cwds.jobs.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gov.ca.cwds.dao.ApiLegacyAware;
import gov.ca.cwds.dao.ApiMultiplePersonAware;
import gov.ca.cwds.data.es.ElasticSearchLegacyDescriptor;
import gov.ca.cwds.data.std.ApiAddressAware;
import gov.ca.cwds.data.std.ApiAddressAwareWritable;
import gov.ca.cwds.data.std.ApiLanguageAware;
import gov.ca.cwds.data.std.ApiMultipleAddressesAware;
import gov.ca.cwds.data.std.ApiMultipleLanguagesAware;
import gov.ca.cwds.data.std.ApiMultiplePhonesAware;
import gov.ca.cwds.data.std.ApiPersonAware;
import gov.ca.cwds.data.std.ApiPersonAwareWritable;
import gov.ca.cwds.data.std.ApiPhoneAware;
import gov.ca.cwds.data.std.ApiPhoneAwareWritable;
import gov.ca.cwds.jobs.PersonJobTester;

public class TestOnlyApiPersonAware implements ApiPersonAwareWritable, ApiPhoneAwareWritable,
    ApiAddressAwareWritable, ApiMultiplePersonAware, ApiMultipleAddressesAware,
    ApiMultipleLanguagesAware, ApiMultiplePhonesAware, ApiLegacyAware {

  private String id = PersonJobTester.DEFAULT_CLIENT_ID;
  private String firstName = "Albert";
  private String lastName = "Einstein";
  private String middleName;
  private String gender;
  private Date birthDate = new Date();

  private List<ApiAddressAware> addresses = new ArrayList<>();
  private List<ApiPhoneAware> phones = new ArrayList<>();
  private List<ApiLanguageAware> languages = new ArrayList<>();

  @Override
  public Serializable getPrimaryKey() {
    return null;
  }

  @Override
  public String getFirstName() {
    return this.firstName;
  }

  @Override
  public String getMiddleName() {
    return this.middleName;
  }

  @Override
  public String getLastName() {
    return this.lastName;
  }

  @Override
  public String getGender() {
    return this.gender;
  }

  @Override
  public Date getBirthDate() {
    return this.birthDate;
  }

  @Override
  public String getSsn() {
    return null;
  }

  @Override
  public String getNameSuffix() {
    return null;
  }

  @Override
  public void setBirthDate(Date arg0) {}

  @Override
  public void setFirstName(String arg0) {}

  @Override
  public void setGender(String arg0) {}

  @Override
  public void setLastName(String arg0) {}

  @Override
  public void setMiddleName(String arg0) {}

  @Override
  public void setNameSuffix(String arg0) {}

  @Override
  public void setSsn(String arg0) {}

  @Override
  public String getAddressId() {
    return null;
  }

  @Override
  public String getCity() {
    return null;
  }

  @Override
  public String getCounty() {
    return null;
  }

  @Override
  public String getState() {
    return null;
  }

  @Override
  public Short getStateCd() {
    return null;
  }

  @Override
  public String getStreetAddress() {
    return null;
  }

  @Override
  public String getZip() {
    return null;
  }

  @Override
  public String getPhoneId() {
    return null;
  }

  @Override
  public String getPhoneNumber() {
    return null;
  }

  @Override
  public String getPhoneNumberExtension() {
    return null;
  }

  @Override
  public PhoneType getPhoneType() {
    return null;
  }

  @Override
  public ApiPhoneAware[] getPhones() {
    return this.phones.toArray(new ApiPhoneAware[0]);
  }

  @Override
  public ApiLanguageAware[] getLanguages() {
    return this.languages.toArray(new ApiLanguageAware[0]);
  }

  @Override
  public ApiAddressAware[] getAddresses() {
    return this.addresses.toArray(new ApiAddressAware[0]);
  }

  @Override
  public ApiPersonAware[] getPersons() {
    return null;
  }

  @Override
  public void setCity(String arg0) {}

  @Override
  public void setCounty(String arg0) {}

  @Override
  public void setState(String arg0) {}

  @Override
  public void setStreetAddress(String arg0) {}

  @Override
  public void setZip(String arg0) {}

  @Override
  public void getPhoneNumberExtension(String arg0) {}

  @Override
  public void setPhoneNumber(String arg0) {}

  @Override
  public void setPhoneType(PhoneType arg0) {}

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getLegacyId() {
    return id;
  }

  @Override
  public ElasticSearchLegacyDescriptor getLegacyDescriptor() {
    final ElasticSearchLegacyDescriptor ret = new ElasticSearchLegacyDescriptor();
    ret.setLegacyId(id);
    ret.setLegacyLastUpdated(new Date().toString());
    ret.setLegacyTableDescription("Client");
    ret.setLegacyTableName("CLIENT_T");

    return ret;
  }

  public void addAddress(ApiAddressAware addr) {
    addresses.add(addr);
  }

  public void addLanguage(ApiLanguageAware lang) {
    languages.add(lang);
  }

  public void addPhone(ApiPhoneAware phone) {
    phones.add(phone);
  }

}
