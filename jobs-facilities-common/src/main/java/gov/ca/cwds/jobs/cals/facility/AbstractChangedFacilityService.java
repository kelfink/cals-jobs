package gov.ca.cwds.jobs.cals.facility;

import gov.ca.cwds.cals.service.dto.FacilityDTO;
import gov.ca.cwds.jobs.common.entity.ChangedEntityService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 3/28/2018.
 */
public abstract class AbstractChangedFacilityService implements
    ChangedEntityService<ChangedFacilityDto> {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractChangedFacilityService.class);

  @Override
  public ChangedFacilityDto loadEntity(ChangedEntityIdentifier identifier) {
    try {
      FacilityDTO facilityDTO = loadEntityById(identifier);
      if (facilityDTO == null) {
        LOG.error("Can't get facility by id {}", identifier.getId());
        throw new IllegalStateException("FacilityDTO must not be null!!!");
      } else {
        if (LOG.isInfoEnabled()) {
          LOG.info("Found facility by ID {}", facilityDTO.getId());
        }
      }
      return new ChangedFacilityDto(facilityDTO, identifier.getRecordChangeOperation());
    } catch (Exception e) {
      LOG.error("Can't get facility by id {}", identifier.getId(), e);
      throw new IllegalStateException(
          String.format("Can't get facility by id %s", identifier.getId()), e);
    }
  }

  protected abstract FacilityDTO loadEntityById(ChangedEntityIdentifier identifier);

}

