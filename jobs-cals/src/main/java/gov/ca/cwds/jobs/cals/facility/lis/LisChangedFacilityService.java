package gov.ca.cwds.jobs.cals.facility.lis;

import com.google.inject.Inject;
import gov.ca.cwds.cals.service.LisFacilityService;
import gov.ca.cwds.cals.service.builder.FacilityParameterObjectBuilder;
import gov.ca.cwds.cals.service.dto.FacilityDTO;
import gov.ca.cwds.jobs.cals.facility.AbstractChangedFacilityService;
import gov.ca.cwds.jobs.cals.facility.ChangedFacilityDTO;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.job.ChangedEntityService;

/**
 * @author CWDS TPT-2
 */

public class LisChangedFacilityService extends AbstractChangedFacilityService implements
    ChangedEntityService<ChangedFacilityDTO> {

  @Inject
  private LisFacilityService lisFacilityService;

  @Inject
  private FacilityParameterObjectBuilder facilityParameterObjectBuilder;

  @Override
  protected FacilityDTO loadEntityById(ChangedEntityIdentifier identifier) {
    return lisFacilityService.loadFacilityFromLis(
        facilityParameterObjectBuilder.createFacilityParameterObject(identifier.getId()));
  }
}
