package gov.ca.cwds.dao;

import java.util.Date;
import java.util.List;

import gov.ca.cwds.data.Dao;
import gov.ca.cwds.data.model.cms.DocumentMetadata;

/**
 * {@link Dao} for DocumentMetadata
 * 
 * @author CWDS API Team
 */
@FunctionalInterface
public interface DocumentMetadataDao extends Dao {

  public List<DocumentMetadata> findByLastJobRunTimeMinusOneMinute(Date lastJobRunTime);

}
