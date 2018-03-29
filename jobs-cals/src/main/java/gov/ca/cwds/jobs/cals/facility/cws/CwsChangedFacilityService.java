package gov.ca.cwds.jobs.cals.facility.cws;

import com.google.inject.Inject;
import gov.ca.cwds.cals.service.CwsFacilityService;
import gov.ca.cwds.cals.service.builder.FacilityParameterObjectBuilder;
import gov.ca.cwds.cals.service.dto.FacilityDTO;
import gov.ca.cwds.jobs.cals.facility.AbstractChangedFacilityService;
import gov.ca.cwds.jobs.cals.facility.ChangedFacilityDTO;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.job.ChangedEntityService;

/**
 * @author CWDS TPT-2
 */

public class CwsChangedFacilityService extends AbstractChangedFacilityService implements
    ChangedEntityService<ChangedFacilityDTO> {

  @Inject
  private CwsFacilityService cwsFacilityService;

  @Inject
  private FacilityParameterObjectBuilder facilityParameterObjectBuilder;

  @Override
  protected FacilityDTO loadEntityById(ChangedEntityIdentifier identifier) {
    return cwsFacilityService.loadFacilityFromCwsCms(
        facilityParameterObjectBuilder.createFacilityParameterObject(identifier.getId()));
  }
}
