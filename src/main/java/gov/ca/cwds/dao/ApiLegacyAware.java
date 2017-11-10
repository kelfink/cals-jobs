package gov.ca.cwds.dao;

import gov.ca.cwds.data.es.ElasticSearchLegacyDescriptor;
import gov.ca.cwds.data.std.ApiMarker;

/**
 * Means that the implementing entity persistence bean can handle legacy identifiers. For new
 * databases, such as Postgres, {@link #getId()} may return a different value from
 * {@link #getLegacyId()}. For legacy systems, they may be the same.
 * 
 * @author CWDS API Team
 */
public interface ApiLegacyAware extends ApiMarker {

  /**
   * Get the unique identifier of this record.
   * 
   * @return primary key
   */
  String getId();

  /**
   * Get the legacy identifier of this record.
   * 
   * @return primary key
   */
  String getLegacyId();

  /**
   * Get legacy descriptor.
   * 
   * @return Legacy descriptor.
   */
  ElasticSearchLegacyDescriptor getLegacyDescriptor();

}
