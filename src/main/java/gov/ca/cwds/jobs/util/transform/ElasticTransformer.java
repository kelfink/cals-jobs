package gov.ca.cwds.jobs.util.transform;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.dao.ApiLegacyAware;
import gov.ca.cwds.dao.ApiScreeningAware;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchLegacyDescriptor;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonAddress;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonPhone;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonScreening;
import gov.ca.cwds.data.persistence.cms.CmsKeyIdGenerator;
import gov.ca.cwds.data.std.ApiAddressAware;
import gov.ca.cwds.data.std.ApiLanguageAware;
import gov.ca.cwds.data.std.ApiMultipleAddressesAware;
import gov.ca.cwds.data.std.ApiMultipleLanguagesAware;
import gov.ca.cwds.data.std.ApiMultiplePhonesAware;
import gov.ca.cwds.data.std.ApiPersonAware;
import gov.ca.cwds.data.std.ApiPhoneAware;
import gov.ca.cwds.rest.api.domain.DomainChef;

/**
 * Translate {@link ApiPersonAware} to {@link ElasticSearchPerson}.
 * 
 * @author CWDS API Team
 */
public class ElasticTransformer {

  private static final Logger LOGGER = LogManager.getLogger(ElasticTransformer.class);

  /**
   * Key: Legacy table name <br>
   * Value: Legacy table description (human readable name)
   */
  private static Map<String, String> legacyTableDescriptions = new HashMap<>();

  /**
   * Initialize legacyTableDescriptions
   */
  static {
    legacyTableDescriptions.put("CLIENT_T", "Client");
    legacyTableDescriptions.put("COLTRL_T", "Collateral individual");
    legacyTableDescriptions.put("EDPRVCNT", "Education provider");
    legacyTableDescriptions.put("ATTRNY_T", "Attorney");
    legacyTableDescriptions.put("CLN_RELT", "Relationship");
    legacyTableDescriptions.put("OTH_ADLT", "Adult in placement home");
    legacyTableDescriptions.put("OTH_KIDT", "Child in placement home");
    legacyTableDescriptions.put("OCL_NM_T", "Alias or other client name");
    legacyTableDescriptions.put("REPTR_T", "Reporter");
    legacyTableDescriptions.put("SVC_PVRT", "Service provider");
    legacyTableDescriptions.put("SB_PVDRT", "Substitute care provider");
    legacyTableDescriptions.put("CASE_T", "Case");
    legacyTableDescriptions.put("STFPERST", "Staff");
    legacyTableDescriptions.put("REFERL_T", "Referral");
    legacyTableDescriptions.put("ALLGTN_T", "Allegation");
    legacyTableDescriptions.put("ADDRS_T", "Address");
  }

  private ElasticTransformer() {
    // Static methods, don't instantiate.
  }

  /**
   * Create legacy descriptor
   * 
   * @param legacyId Legacy ID
   * @param legacyLastUpdated Legacy last updated time stamp
   * @param legacyTableName Legacy table name
   * @return Legacy descriptor
   */
  public static ElasticSearchLegacyDescriptor createLegacyDescriptor(String legacyId,
      Date legacyLastUpdated, String legacyTableName) {
    ElasticSearchLegacyDescriptor legacyDesc = new ElasticSearchLegacyDescriptor();

    if (!StringUtils.isBlank(legacyId)) {
      legacyDesc.setLegacyId(legacyId.trim());
      legacyDesc.setLegacyUiId(CmsKeyIdGenerator.getUIIdentifierFromKey(legacyId.trim()));
      legacyDesc.setLegacyLastUpdated(DomainChef.cookStrictTimestamp(legacyLastUpdated));
      legacyDesc.setLegacyTableName(legacyTableName);
      legacyDesc.setLegacyTableDescription(legacyTableDescriptions.get(legacyTableName));
    }

    return legacyDesc;
  }

  /**
   * Create staff person (STFPERST) legacy descriptor
   * 
   * @param legacyId Legacy ID
   * @param legacyLastUpdated Legacy last updated time stamp
   * @return Legacy descriptor
   */
  public static ElasticSearchLegacyDescriptor createStaffLegacyDescriptor(String legacyId,
      Date legacyLastUpdated) {
    ElasticSearchLegacyDescriptor legacyDesc = new ElasticSearchLegacyDescriptor();

    if (!StringUtils.isBlank(legacyId)) {
      String legacyTableName = "STFPERST";
      legacyDesc.setLegacyId(legacyId.trim());
      legacyDesc.setLegacyUiId(legacyId.trim());
      legacyDesc.setLegacyLastUpdated(DomainChef.cookStrictTimestamp(legacyLastUpdated));
      legacyDesc.setLegacyTableName(legacyTableName);
      legacyDesc.setLegacyTableDescription(legacyTableDescriptions.get(legacyTableName));
    }

    return legacyDesc;
  }

  protected static List<String> handleLanguage(ApiPersonAware p) {
    List<String> languages = null;
    if (p instanceof ApiMultipleLanguagesAware) {
      ApiMultipleLanguagesAware mlx = (ApiMultipleLanguagesAware) p;
      languages = new ArrayList<>();
      for (ApiLanguageAware lx : mlx.getLanguages()) {
        final ElasticSearchPerson.ElasticSearchPersonLanguage lang =
            ElasticSearchPerson.ElasticSearchPersonLanguage.findBySysId(lx.getLanguageSysId());
        if (lang != null) {
          languages.add(lang.getDescription());
        }
      }
    } else if (p instanceof ApiLanguageAware) {
      languages = new ArrayList<>();
      ApiLanguageAware lx = (ApiLanguageAware) p;
      final ElasticSearchPerson.ElasticSearchPersonLanguage lang =
          ElasticSearchPerson.ElasticSearchPersonLanguage.findBySysId(lx.getLanguageSysId());
      if (lang != null) {
        languages.add(lang.getDescription());
      }
    }

    return languages;
  }

  protected static List<ElasticSearchPerson.ElasticSearchPersonPhone> handlePhone(
      ApiPersonAware p) {
    List<ElasticSearchPerson.ElasticSearchPersonPhone> phones = null;
    if (p instanceof ApiMultiplePhonesAware) {
      phones = new ArrayList<>();
      ApiMultiplePhonesAware mphx = (ApiMultiplePhonesAware) p;
      for (ApiPhoneAware phx : mphx.getPhones()) {
        phones.add(new ElasticSearchPersonPhone(phx));
      }
    } else if (p instanceof ApiPhoneAware) {
      phones = new ArrayList<>();
      ApiPhoneAware phx = (ApiPhoneAware) p;
      phones.add(new ElasticSearchPersonPhone(phx));
    }

    return phones;
  }

  protected static List<ElasticSearchPersonAddress> handleAddress(ApiPersonAware p) {
    List<ElasticSearchPersonAddress> addresses = null;

    if (p instanceof ApiMultipleAddressesAware) {
      addresses = new ArrayList<>();
      ApiMultipleAddressesAware madrx = (ApiMultipleAddressesAware) p;
      for (ApiAddressAware adrx : madrx.getAddresses()) {
        ElasticSearchPersonAddress esAddress = new ElasticSearchPersonAddress(adrx);
        if (adrx instanceof ApiLegacyAware) {
          esAddress.setLegacyDescriptor(((ApiLegacyAware) adrx).getLegacyDescriptor());
        }
        addresses.add(esAddress);
      }
    } else if (p instanceof ApiAddressAware) {
      addresses = new ArrayList<>();
      ElasticSearchPersonAddress esAddress = new ElasticSearchPersonAddress((ApiAddressAware) p);
      if (p instanceof ApiLegacyAware) {
        esAddress.setLegacyDescriptor(((ApiLegacyAware) p).getLegacyDescriptor());
      }
      addresses.add(esAddress);
    }

    return addresses;
  }

  protected static List<ElasticSearchPersonScreening> handleScreening(ApiPersonAware p) {
    List<ElasticSearchPersonScreening> screenings = null;
    if (p instanceof ApiScreeningAware) {
      screenings = new ArrayList<>();
      for (ElasticSearchPersonScreening scr : ((ApiScreeningAware) p).getEsScreenings()) {
        screenings.add(scr);
      }
    }
    return screenings;
  }

  protected static ElasticSearchLegacyDescriptor handleLegacyDescriptor(ApiPersonAware p) {
    ElasticSearchLegacyDescriptor legacyDescriptor = null;
    if (p instanceof ApiLegacyAware) {
      legacyDescriptor = ((ApiLegacyAware) p).getLegacyDescriptor();
    }
    return legacyDescriptor;
  }

  /**
   * Produce an ElasticSearchPerson objects suitable for an Elasticsearch person document.
   * 
   * @param mapper Jackson ObjectMapper
   * @param p ApiPersonAware persistence object
   * @return populated ElasticSearchPerson
   * @throws JsonProcessingException if unable to serialize JSON
   */
  public static ElasticSearchPerson buildElasticSearchPersonDoc(final ObjectMapper mapper,
      ApiPersonAware p) throws JsonProcessingException {
    ElasticSearchPerson ret;

    if (p.getPrimaryKey() == null) {
      LOGGER.warn("NO PRIMARY KEY!");
    }

    // Write persistence object to Elasticsearch Person document.
    ret = new ElasticSearchPerson(p.getPrimaryKey().toString(), // id
        p.getFirstName(), // first name
        p.getLastName(), // last name
        p.getMiddleName(), // middle name
        p.getNameSuffix(), // name suffix
        p.getGender(), // gender
        DomainChef.cookDate(p.getBirthDate()), // birth date
        p.getSsn(), // SSN
        p.getClass().getName(), // type
        mapper.writeValueAsString(p), // source
        null, // omit highlights
        handleAddress(p), handlePhone(p), handleLanguage(p), handleScreening(p));

    // Legacy descriptor
    ret.setLegacyDescriptor(handleLegacyDescriptor(p));

    // Sealed and sensitive.
    ret.setSensitivityIndicator(p.getSensitivityIndicator());
    ret.setSoc158SealedClientIndicator(p.getSoc158SealedClientIndicator());

    return ret;
  }

}
