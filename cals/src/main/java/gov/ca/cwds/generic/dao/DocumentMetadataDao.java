package gov.ca.cwds.generic.dao;

import gov.ca.cwds.data.Dao;
import gov.ca.cwds.generic.data.model.cms.DocumentMetadata;
import java.util.Date;
import java.util.List;

/**
 * {@link Dao} for DocumentMetadata
 * 
 * @author CWDS API Team
 */
@FunctionalInterface
public interface DocumentMetadataDao extends Dao {

  public List<DocumentMetadata> findByLastJobRunTimeMinusOneMinute(Date lastJobRunTime);

}
