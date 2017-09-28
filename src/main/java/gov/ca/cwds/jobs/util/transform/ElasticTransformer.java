package gov.ca.cwds.jobs.util.transform;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.dao.ApiLegacyAware;
import gov.ca.cwds.dao.ApiMultiplePersonAware;
import gov.ca.cwds.dao.ApiScreeningAware;
import gov.ca.cwds.data.ApiTypedIdentifier;
import gov.ca.cwds.data.es.ElasticSearchLegacyDescriptor;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPerson.ESOptionalCollection;
import gov.ca.cwds.data.es.ElasticSearchPersonAddress;
import gov.ca.cwds.data.es.ElasticSearchPersonLanguage;
import gov.ca.cwds.data.es.ElasticSearchPersonPhone;
import gov.ca.cwds.data.es.ElasticSearchPersonScreening;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.CmsKeyIdGenerator;
import gov.ca.cwds.data.std.ApiAddressAware;
import gov.ca.cwds.data.std.ApiLanguageAware;
import gov.ca.cwds.data.std.ApiMultipleAddressesAware;
import gov.ca.cwds.data.std.ApiMultipleLanguagesAware;
import gov.ca.cwds.data.std.ApiMultiplePhonesAware;
import gov.ca.cwds.data.std.ApiPersonAware;
import gov.ca.cwds.data.std.ApiPhoneAware;
import gov.ca.cwds.rest.api.domain.DomainChef;
import gov.ca.cwds.rest.api.domain.cms.LegacyTable;
import gov.ca.cwds.rest.api.domain.cms.SystemCodeCache;

/**
 * Methods to transform {@link ApiPersonAware} into {@link ElasticSearchPerson}.
 * 
 * @author CWDS API Team
 */
public class ElasticTransformer {

  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticTransformer.class);

  private static ObjectMapper mapper;

  private ElasticTransformer() {
    // Static methods, don't instantiate.
  }

  /**
   * Serialize object to JSON.
   * 
   * @param obj object to serialize
   * @return JSON for this screening
   */
  public static String jsonify(Object obj) {
    String ret = "";
    try {
      ret = mapper.writeValueAsString(obj);
    } catch (Exception e) { // NOSONAR
      LOGGER.warn("ERROR SERIALIZING OBJECT {} TO JSON", obj);
    }
    return ret;
  }

  /**
   * Prepare "upsert" JSON (update and insert). Child classes do not normally override this method.
   * 
   * @param <T> normalized persistent type
   * @param docPrep optional handling to set collections before serializing JSON
   * @param esp ES document, already prepared by
   *        {@link ElasticTransformer#buildElasticSearchPersonDoc(ApiPersonAware)}
   * @param t target ApiPersonAware instance
   * @param elementName target ES element for update
   * @param list list of ES child objects
   * @param keep ES sections to keep
   * @return Pair of JSON, left = update, right = insert
   * @throws JsonProcessingException on JSON processing error
   */
  public static <T extends PersistentObject> Pair<String, String> prepareUpsertJson(
      JobElasticPersonDocPrep<T> docPrep, ElasticSearchPerson esp, T t, String elementName,
      List<? extends ApiTypedIdentifier<String>> list, ESOptionalCollection... keep)
      throws JsonProcessingException {

    // Child classes: Set optional collections before serializing the insert JSON.
    prepareInsertCollections(docPrep, esp, t, elementName, list, keep);
    final String insertJson = mapper.writeValueAsString(esp);

    String updateJson;
    if (StringUtils.isNotBlank(elementName)) {
      StringBuilder buf = new StringBuilder();
      buf.append("{\"").append(elementName).append("\":[");

      if (list != null && !list.isEmpty()) {
        buf.append(list.stream().map(ElasticTransformer::jsonify).sorted(String::compareTo)
            .collect(Collectors.joining(",")));
      }

      buf.append("]}");
      updateJson = buf.toString();
    } else {
      updateJson = insertJson;
    }

    return Pair.of(updateJson, insertJson);
  }

  /**
   * Set optional ES person collections to null so that they are not overwritten by accident. Child
   * classes do not normally override this method.
   * 
   * @param <T> normalized persistent type
   * @param docPrep optional handling to set collections before serializing JSON
   * @param esp ES document, already prepared by
   *        {@link #buildElasticSearchPersonDoc(ApiPersonAware)}
   * @param t target ApiPersonAware instance
   * @param elementName target ES element for update
   * @param list list of ES child objects
   * @param keep ES sections to keep
   * @throws JsonProcessingException on JSON processing error
   */
  public static <T extends PersistentObject> void prepareInsertCollections(
      JobElasticPersonDocPrep<T> docPrep, ElasticSearchPerson esp, T t, String elementName,
      List<? extends ApiTypedIdentifier<String>> list, ESOptionalCollection... keep)
      throws JsonProcessingException {

    // Null out optional collections for updates.
    esp.clearOptionalCollections(keep);

    // Child classes: Set optional collections before serializing the insert JSON.
    docPrep.setInsertCollections(esp, t, list);
  }

  /**
   * Handle both {@link ApiMultiplePersonAware} and {@link ApiPersonAware} implementations of type
   * T.
   * 
   * @param p instance of type T
   * @return array of person documents
   * @throws JsonProcessingException on parse error
   */
  public static ElasticSearchPerson[] buildElasticSearchPersons(final PersistentObject p)
      throws JsonProcessingException {
    ElasticSearchPerson[] ret;
    if (p instanceof ApiMultiplePersonAware) {
      final ApiPersonAware[] persons = ((ApiMultiplePersonAware) p).getPersons();
      ret = new ElasticSearchPerson[persons.length];
      int i = 0;
      for (ApiPersonAware px : persons) {
        ret[i++] = ElasticTransformer.buildElasticSearchPersonDoc(px);
      }
    } else {
      ret = new ElasticSearchPerson[] {buildElasticSearchPerson((ApiPersonAware) p)};
    }
    return ret;
  }

  /**
   * Produce an ElasticSearchPerson suitable as an Elasticsearch person document.
   * 
   * @param p ApiPersonAware persistence object
   * @return populated ElasticSearchPerson
   * @throws JsonProcessingException if unable to serialize JSON
   */
  public static ElasticSearchPerson buildElasticSearchPerson(ApiPersonAware p)
      throws JsonProcessingException {
    return ElasticTransformer.buildElasticSearchPersonDoc(p);
  }

  /**
   * Create legacy descriptor
   * 
   * @param legacyId Legacy ID
   * @param legacyLastUpdated Legacy last updated time stamp
   * @param legacyTable Legacy table
   * @return Legacy descriptor
   */
  public static ElasticSearchLegacyDescriptor createLegacyDescriptor(String legacyId,
      Date legacyLastUpdated, LegacyTable legacyTable) {
    ElasticSearchLegacyDescriptor legacyDesc = new ElasticSearchLegacyDescriptor();

    if (!StringUtils.isBlank(legacyId)) {
      legacyDesc.setLegacyId(legacyId.trim());
      legacyDesc.setLegacyLastUpdated(DomainChef.cookStrictTimestamp(legacyLastUpdated));

      /**
       * Legacy ID should always be 10 characters long, otherwise we can not parse it to get UI ID.
       * For LegacyTable.STFPERST it is usually 3 characters long.
       */
      if (legacyId.trim().length() == 10) {
        legacyDesc.setLegacyUiId(CmsKeyIdGenerator.getUIIdentifierFromKey(legacyId.trim()));
      } else {
        legacyDesc.setLegacyUiId(legacyId.trim());
      }

      if (legacyTable != null) {
        legacyDesc.setLegacyTableName(legacyTable.getName());
        legacyDesc.setLegacyTableDescription(legacyTable.getDescription());
      }
    }

    return legacyDesc;
  }

  protected static List<ElasticSearchPersonLanguage> handleLanguage(ApiPersonAware p) {
    List<ElasticSearchPersonLanguage> languages = null;

    if (p instanceof ApiMultipleLanguagesAware) {
      ApiMultipleLanguagesAware mlx = (ApiMultipleLanguagesAware) p;
      languages = new ArrayList<>();
      for (ApiLanguageAware lx : mlx.getLanguages()) {
        Integer languageId = lx.getLanguageSysId();
        ElasticSearchPersonLanguage lang = new ElasticSearchPersonLanguage(languageId.toString(),
            SystemCodeCache.global().getSystemCodeShortDescription(languageId), lx.getPrimary());
        languages.add(lang);
      }
    } else if (p instanceof ApiLanguageAware) {
      languages = new ArrayList<>();
      ApiLanguageAware lx = (ApiLanguageAware) p;
      Integer languageId = lx.getLanguageSysId();
      ElasticSearchPersonLanguage lang = new ElasticSearchPersonLanguage(languageId.toString(),
          SystemCodeCache.global().getSystemCodeShortDescription(languageId), lx.getPrimary());
      languages.add(lang);
    }

    return languages;
  }

  protected static List<ElasticSearchPersonPhone> handlePhone(ApiPersonAware p) {
    List<ElasticSearchPersonPhone> phones = null;
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
   * @param p ApiPersonAware persistence object
   * @return populated ElasticSearchPerson
   * @throws JsonProcessingException if unable to serialize JSON
   */
  public static ElasticSearchPerson buildElasticSearchPersonDoc(ApiPersonAware p)
      throws JsonProcessingException {
    return buildElasticSearchPersonDoc(mapper, p);
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
    Serializable primaryKey = p.getPrimaryKey();

    if (primaryKey == null) {
      LOGGER.warn("NULL PRIMARY KEY: " + p);
      primaryKey = "MISSING_ID";
    }

    // Write persistence object to Elasticsearch Person document.
    ret = new ElasticSearchPerson(primaryKey.toString(), // id
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

    return ret;
  }

  public static ObjectMapper getMapper() { // NOSONAR
    return mapper;
  }

  public static synchronized void setMapper(final ObjectMapper mapper) {
    if (ElasticTransformer.mapper == null) {
      ElasticTransformer.mapper = mapper;
    }
  }

}
