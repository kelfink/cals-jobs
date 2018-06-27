package gov.ca.cwds.jobs.cals.facility;

import static org.junit.Assert.assertEquals;

import gov.ca.cwds.cals.service.dto.FacilityDTO;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import org.junit.Test;

public class ChangedFacilityDtoTest {

  @Test
  public void equals() {
    FacilityDTO facilityDto = new FacilityDTO();
    facilityDto.setName("Some Facility");
    ChangedFacilityDto changedFacilityDTO1 = new ChangedFacilityDto(facilityDto,
        RecordChangeOperation.U);

    ChangedFacilityDto changedFacilityDTO2 = new ChangedFacilityDto(facilityDto,
        RecordChangeOperation.U);

    assertEquals(changedFacilityDTO1, changedFacilityDTO2);
  }

  @Test
  public void getDTO() {
    FacilityDTO facilityDTO = new FacilityDTO();
    facilityDTO.setId("FacilityId");
    ChangedFacilityDto changedFacilityDTO = new ChangedFacilityDto(facilityDTO,
        RecordChangeOperation.U);
    assertEquals(facilityDTO, changedFacilityDTO.getDTO());
  }

  @Test
  public void getId() {
    FacilityDTO facilityDto = new FacilityDTO();
    String facilityId = "FacilityId";
    facilityDto.setId(facilityId);
    ChangedFacilityDto changedFacilityDto = new ChangedFacilityDto(facilityDto,
        RecordChangeOperation.U);
    assertEquals(facilityId, changedFacilityDto.getId());
  }
}