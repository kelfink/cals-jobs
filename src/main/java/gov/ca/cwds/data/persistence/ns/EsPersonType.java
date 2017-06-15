package gov.ca.cwds.data.persistence.ns;

import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonAny;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonNestedPerson;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonReporter;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonSocialWorker;
import gov.ca.cwds.data.es.ElasticSearchPerson.ElasticSearchPersonStaff;

/**
 * Types for {@link ElasticSearchPersonNestedPerson} child classes.
 * 
 * @author CWDS API Team
 */
public enum EsPersonType {

  /**
   * For {@link ElasticSearchPersonReporter}.
   */
  REPORTER,

  /**
   * For {@link ElasticSearchPersonSocialWorker}.
   */
  SOCIAL_WORKER,

  /**
   * For {@link ElasticSearchPersonStaff}.
   */
  STAFF,

  /**
   * For {@link ElasticSearchPersonAny}.
   */
  ALL;
}