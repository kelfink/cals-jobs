package gov.ca.cwds.jobs.util.transform;

import static gov.ca.cwds.data.persistence.cms.CmsPersistentObject.CMS_ID_LEN;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.dao.ApiClientCountyAware;
import gov.ca.cwds.dao.ApiLegacyAware;
import gov.ca.cwds.dao.ApiMultiplePersonAware;
import gov.ca.cwds.dao.ApiScreeningAware;
import gov.ca.cwds.data.ApiTypedIdentifier;
import gov.ca.cwds.data.es.ElasticSearchCounty;
import gov.ca.cwds.data.es.ElasticSearchLegacyDescriptor;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPerson.ESOptionalCollection;
import gov.ca.cwds.data.es.ElasticSearchPersonAddress;
import gov.ca.cwds.data.es.ElasticSearchPersonLanguage;
import gov.ca.cwds.data.es.ElasticSearchPersonPhone;
import gov.ca.cwds.data.es.ElasticSearchPersonScreening;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.CmsKeyIdGenerator;
import gov.ca.cwds.data.persistence.cms.rep.CmsReplicatedEntity;
import gov.ca.cwds.data.std.ApiAddressAware;
import gov.ca.cwds.data.std.ApiLanguageAware;
import gov.ca.cwds.data.std.ApiMultipleAddressesAware;
import gov.ca.cwds.data.std.ApiMultipleLanguagesAware;
import gov.ca.cwds.data.std.ApiMultiplePhonesAware;
import gov.ca.cwds.data.std.ApiPersonAware;
import gov.ca.cwds.data.std.ApiPhoneAware;
import gov.ca.cwds.jobs.component.AtomPersonDocPrep;
import gov.ca.cwds.jobs.component.JobProgressTrack;
import gov.ca.cwds.jobs.util.JobLogs;
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

  public static void pushToBulkProcessor(final JobProgressTrack track, final BulkProcessor bp,
      final DocWriteRequest<?> t) {
    JobLogs.logEvery(track.trackQueuedToIndex(), "add to es bulk", "push doc");
    bp.add(t);
  }

  protected static String determineId(final ApiLegacyAware l, final ElasticSearchPerson esp) {
    String id;
    final String legacyId = StringUtils.isNotBlank(l.getLegacyId()) ? l.getLegacyId().trim() : "";
    final boolean hasLegacyId = legacyId.length() == CMS_ID_LEN;

    if (hasLegacyId) {
      id = legacyId;
      esp.setLegacyId(id);
    } else {
      id = esp.getId();
    }

    return id;
  }

  protected static String determineId(CmsReplicatedEntity l, final ElasticSearchPerson esp) {
    final String id = l.getPrimaryKey().toString();
    esp.setLegacyId(id);
    return id;
  }


  /**
   * Prepare sections of a document for update. Elasticsearch automatically updates the provided
   * sections. Some jobs should only write sub-documents, such as screenings or allegations, from a
   * new data source, like Intake PostgreSQL, but should NOT overwrite document details from legacy.
   * 
   * <p>
   * Default handler just serializes the whole ElasticSearchPerson instance to JSON and returns the
   * same JSON for both insert and update. Child classes should override this method and null out
   * any fields that should not be updated.
   * </p>
   * 
   * @param <T> normalized persistent type
   * @param docPrep document handler
   * @param alias ES index alias
   * @param docType ES document type
   * @param esp ES document, already prepared by
   *        {@link ElasticTransformer#buildElasticSearchPersonDoc(ApiPersonAware)}
   * @param t target ApiPersonAware instance
   * @return left = insert JSON, right = update JSON throws JsonProcessingException on JSON parse
   *         error
   * @throws JsonProcessingException on JSON parse error
   * @throws IOException on Elasticsearch disconnect
   */
  public static <T extends PersistentObject> UpdateRequest prepareUpsertRequest(
      AtomPersonDocPrep<T> docPrep, String alias, String docType, final ElasticSearchPerson esp,
      T t) throws IOException {
    String id = esp.getId();

    // Set id and legacy id.
    if (t instanceof ApiLegacyAware) {
      ApiLegacyAware l = (ApiLegacyAware) t;
      final boolean hasLegacyId =
          StringUtils.isNotBlank(l.getLegacyId()) && l.getLegacyId().trim().length() == CMS_ID_LEN;

      if (hasLegacyId) {
        id = l.getLegacyId();
        esp.setLegacyId(id);
      } else {
        id = esp.getId();
      }
    } else if (t instanceof CmsReplicatedEntity) {
      esp.setLegacyId(t.getPrimaryKey().toString());
    }

    // Child classes may override these methods as needed.
    // left = update, right = insert.
    final Pair<String, String> json =
        ElasticTransformer.prepareUpsertJson(docPrep, esp, t, docPrep.getOptionalElementName(),
            docPrep.getOptionalCollection(esp, t), docPrep.keepCollections());

    // "Upsert": update if doc exists, insert if it does not.
    return new UpdateRequest(alias, docType, id).doc(json.getLeft())
        .upsert(new IndexRequest(alias, docType, id).source(json.getRight()));
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
      AtomPersonDocPrep<T> docPrep, ElasticSearchPerson esp, T t, String elementName,
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
      AtomPersonDocPrep<T> docPrep, ElasticSearchPerson esp, T t, String elementName,
      List<? extends ApiTypedIdentifier<String>> list, ESOptionalCollection... keep)
      throws JsonProcessingException {

    // Clear out optional collections for updates.
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
   * Create legacy descriptor.
   * 
   * <p>
   * Legacy ID should always be 10 characters long, otherwise we can not parse it to get UI ID. For
   * LegacyTable.STFPERST it is usually 3 characters long.
   * </p>
   * 
   * @param legacyId Legacy ID
   * @param legacyLastUpdated Legacy last updated time stamp
   * @param legacyTable Legacy table
   * @return Legacy descriptor
   */
  public static ElasticSearchLegacyDescriptor createLegacyDescriptor(String legacyId,
      Date legacyLastUpdated, LegacyTable legacyTable) {
    final ElasticSearchLegacyDescriptor ret = new ElasticSearchLegacyDescriptor();

    if (!StringUtils.isBlank(legacyId)) {
      ret.setLegacyId(legacyId.trim());
      ret.setLegacyLastUpdated(DomainChef.cookStrictTimestamp(legacyLastUpdated));

      final String cleanLegacyId = legacyId.trim();
      if (cleanLegacyId.length() == CMS_ID_LEN) {
        ret.setLegacyUiId(CmsKeyIdGenerator.getUIIdentifierFromKey(cleanLegacyId));
      } else {
        ret.setLegacyUiId(cleanLegacyId);
      }

      if (legacyTable != null) {
        ret.setLegacyTableName(legacyTable.getName());
        ret.setLegacyTableDescription(legacyTable.getDescription());
      }
    }

    return ret;
  }

  protected static List<ElasticSearchPersonLanguage> handleLanguage(ApiPersonAware p) {
    List<ElasticSearchPersonLanguage> ret = null;

    if (p instanceof ApiMultipleLanguagesAware) {
      ApiMultipleLanguagesAware mlx = (ApiMultipleLanguagesAware) p;
      ret = new ArrayList<>();
      for (ApiLanguageAware lx : mlx.getLanguages()) {
        Integer languageId = lx.getLanguageSysId();
        ElasticSearchPersonLanguage lang = new ElasticSearchPersonLanguage(languageId.toString(),
            SystemCodeCache.global().getSystemCodeShortDescription(languageId), lx.getPrimary());
        ret.add(lang);
      }
    } else if (p instanceof ApiLanguageAware) {
      ret = new ArrayList<>();
      ApiLanguageAware lx = (ApiLanguageAware) p;
      Integer languageId = lx.getLanguageSysId();
      ElasticSearchPersonLanguage lang = new ElasticSearchPersonLanguage(languageId.toString(),
          SystemCodeCache.global().getSystemCodeShortDescription(languageId), lx.getPrimary());
      ret.add(lang);
    }

    return ret;
  }

  protected static List<ElasticSearchPersonPhone> handlePhone(ApiPersonAware p) {
    List<ElasticSearchPersonPhone> ret = null;
    if (p instanceof ApiMultiplePhonesAware) {
      ret = new ArrayList<>();
      ApiMultiplePhonesAware mphx = (ApiMultiplePhonesAware) p;
      for (ApiPhoneAware phx : mphx.getPhones()) {
        ret.add(new ElasticSearchPersonPhone(phx));
      }
    } else if (p instanceof ApiPhoneAware) {
      ret = new ArrayList<>();
      ApiPhoneAware phx = (ApiPhoneAware) p;
      ret.add(new ElasticSearchPersonPhone(phx));
    }

    return ret;
  }

  protected static List<ElasticSearchPersonAddress> handleAddress(ApiPersonAware p) {
    List<ElasticSearchPersonAddress> ret = null;

    if (p instanceof ApiMultipleAddressesAware) {
      ret = new ArrayList<>();
      ApiMultipleAddressesAware madrx = (ApiMultipleAddressesAware) p;
      for (ApiAddressAware adrx : madrx.getAddresses()) {
        ElasticSearchPersonAddress esAddress = new ElasticSearchPersonAddress(adrx);
        if (adrx instanceof ApiLegacyAware) {
          esAddress.setLegacyDescriptor(((ApiLegacyAware) adrx).getLegacyDescriptor());
        }
        ret.add(esAddress);
      }
    } else if (p instanceof ApiAddressAware) {
      ret = new ArrayList<>();
      ElasticSearchPersonAddress esAddress = new ElasticSearchPersonAddress((ApiAddressAware) p);
      if (p instanceof ApiLegacyAware) {
        esAddress.setLegacyDescriptor(((ApiLegacyAware) p).getLegacyDescriptor());
      }
      ret.add(esAddress);
    }

    return ret;
  }

  protected static List<ElasticSearchPersonScreening> handleScreening(ApiPersonAware p) {
    List<ElasticSearchPersonScreening> ret = null;
    if (p instanceof ApiScreeningAware) {
      ret = new ArrayList<>();
      for (ElasticSearchPersonScreening scr : ((ApiScreeningAware) p).getEsScreenings()) {
        ret.add(scr);
      }
    }
    return ret;
  }

  protected static ElasticSearchLegacyDescriptor handleLegacyDescriptor(ApiPersonAware p) {
    ElasticSearchLegacyDescriptor ret = null;
    if (p instanceof ApiLegacyAware) {
      ret = ((ApiLegacyAware) p).getLegacyDescriptor();
    }
    return ret;
  }

  protected static ElasticSearchCounty handleClientCountyC(ApiPersonAware p) {
    ElasticSearchCounty esCounty = null;
    if (p instanceof ApiClientCountyAware) {
      ApiClientCountyAware countyAware = (ApiClientCountyAware) p;
      esCounty = new ElasticSearchCounty();
      esCounty.setId(countyAware.getClientCounty().toString());
      esCounty.setName(
          SystemCodeCache.global().getSystemCodeShortDescription(countyAware.getClientCounty()));
    }
    return esCounty;
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
      LOGGER.debug("NULL PRIMARY KEY: " + p);
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

    // WARNING: not yet in RSQ.
    // Set client county
    ret.setClientCounty(handleClientCountyC(p));

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
