package gov.ca.cwds.jobs.cals.facility;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.job.impl.AbstractJobReader;
import gov.ca.cwds.jobs.common.job.timestamp.TimestampOperator;

/**
 * @author CWDS TPT-2
 */
public class FacilityReader extends AbstractJobReader<ChangedFacilityDTO> {

  @Inject
  public FacilityReader(ChangedFacilityService changedEntitiesService,
                        TimestampOperator timestampOperator) {
    super(changedEntitiesService, timestampOperator);
  }

}
