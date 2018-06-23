package gov.ca.cwds.jobs.cals.facility;

import gov.ca.cwds.cals.service.dto.FacilityDTO;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import org.junit.Assert;
import org.junit.Test;

public class ChangedFacilityDTOTest {

  @Test
  public void equals() {
    FacilityDTO facilityDTO = new FacilityDTO();
    facilityDTO.setName("Some Facility");
    ChangedFacilityDto changedFacilityDTO1 = new ChangedFacilityDto(facilityDTO,
        RecordChangeOperation.U);

    ChangedFacilityDto changedFacilityDTO2 = new ChangedFacilityDto(facilityDTO,
        RecordChangeOperation.U);

    Assert.assertEquals(changedFacilityDTO1, changedFacilityDTO2);
  }

  @Test
  public void getDTO() {
    FacilityDTO facilityDTO = new FacilityDTO();
    facilityDTO.setId("FacilityId");
    ChangedFacilityDto changedFacilityDTO = new ChangedFacilityDto(facilityDTO,
        RecordChangeOperation.U);
    Assert.assertEquals(facilityDTO, changedFacilityDTO.getDTO());
  }

  @Test
  public void getId() {
    FacilityDTO facilityDTO = new FacilityDTO();
    String facilityId = "FacilityId";
    facilityDTO.setId(facilityId);
    ChangedFacilityDto changedFacilityDTO = new ChangedFacilityDto(facilityDTO,
        RecordChangeOperation.U);
    Assert.assertEquals(facilityId, changedFacilityDTO.getId());
  }
}