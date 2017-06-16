package gov.ca.cwds.data.persistence.cms;

import java.io.Serializable;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.data.std.ApiObjectIdentity;

/**
 * Entity bean for Materialized Query Table (MQT), ES_CASE_HIST.
 * 
 * <p>
 * Implements {@link ApiGroupNormalizer} and converts to {@link ReplicatedPersonCases}.
 * </p>
 * 
 * @author CWDS API Team
 */
public abstract class EsPersonCase extends ApiObjectIdentity
    implements PersistentObject, ApiGroupNormalizer<ReplicatedPersonCases> {

  private static final long serialVersionUID = 2896950873299112269L;

  @Override
  public Class<ReplicatedPersonCases> getNormalizationClass() {
    return ReplicatedPersonCases.class;
  }

  @Override
  public Serializable getPrimaryKey() {
    return null;
  }

}
