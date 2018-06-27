package gov.ca.cwds.jobs.cals.facility.cws.service;

import com.google.inject.Inject;
import gov.ca.cwds.cals.service.CwsFacilityService;
import gov.ca.cwds.cals.service.dto.FacilityDTO;
import gov.ca.cwds.jobs.cals.facility.AbstractChangedFacilityService;
import gov.ca.cwds.jobs.cals.facility.ChangedFacilityDto;
import gov.ca.cwds.jobs.common.entity.ChangedEntityService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;

/**
 * @author CWDS TPT-2
 */

public class CwsChangedFacilityService extends AbstractChangedFacilityService implements
    ChangedEntityService<ChangedFacilityDto> {

  @Inject
  private CwsFacilityService cwsFacilityService;

  @Override
  protected FacilityDTO loadEntityById(ChangedEntityIdentifier identifier) {
    return cwsFacilityService.loadFacilityFromCwsCms(identifier.getId());
  }
}
