package gov.ca.cwds.generic.data.persistence.ns;

import gov.ca.cwds.data.es.ElasticSearchPersonAny;
import gov.ca.cwds.data.es.ElasticSearchPersonNestedPerson;
import gov.ca.cwds.data.es.ElasticSearchPersonReporter;

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
   * For ElasticSearchPersonSocialWorker.
   */
  SOCIAL_WORKER,

  /**
   * For ElasticSearchPersonStaff.
   */
  STAFF,

  /**
   * For {@link ElasticSearchPersonAny}.
   */
  ALL;
}
