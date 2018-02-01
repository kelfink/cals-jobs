package gov.ca.cwds.jobs.cals.facility;

import gov.ca.cwds.cals.service.dto.ExpandedFacilityDTO;
import gov.ca.cwds.dto.BaseDTO;
import gov.ca.cwds.jobs.cals.RecordChangeOperation;

import java.io.Serializable;

/**
 * Created by TPT2 on 12/12/2017.
 */
public class TestChangedFacilityDTO extends BaseDTO implements Serializable {
  private String id;

  private ExpandedFacilityDTO dto;

  private RecordChangeOperation recordChangeOperation;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ExpandedFacilityDTO getDto() {
    return dto;
  }

  public void setDto(ExpandedFacilityDTO dto) {
    this.dto = dto;
  }

  public RecordChangeOperation getRecordChangeOperation() {
    return recordChangeOperation;
  }

  public void setRecordChangeOperation(RecordChangeOperation recordChangeOperation) {
    this.recordChangeOperation = recordChangeOperation;
  }
}
