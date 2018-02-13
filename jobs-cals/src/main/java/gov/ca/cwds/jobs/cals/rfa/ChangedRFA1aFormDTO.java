package gov.ca.cwds.jobs.cals.rfa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.ca.cwds.cals.service.dto.rfa.RFA1aFormDTO;
import gov.ca.cwds.dto.BaseDTO;
import gov.ca.cwds.jobs.common.ChangedDTO;
import gov.ca.cwds.jobs.common.RecordChangeOperation;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author CWDS TPT-2
 */
public class ChangedRFA1aFormDTO extends BaseDTO implements ChangedDTO<RFA1aFormDTO>, Serializable {

  private static final long serialVersionUID = 2L;

  @NotNull
  private RFA1aFormDTO rfa1aFormDTO;

  @NotNull
  private RecordChangeOperation recordChangeOperation;

  public ChangedRFA1aFormDTO() {
    // default constructor
  }

  public ChangedRFA1aFormDTO(RFA1aFormDTO rfa1aFormDTO,
                             RecordChangeOperation recordChangeOperation) {
    this.rfa1aFormDTO = rfa1aFormDTO;
    this.recordChangeOperation = recordChangeOperation;
  }

  @Override
  @JsonIgnore
  public String getId() {
    return String.valueOf(rfa1aFormDTO.getId());
  }

  @Override
  public RecordChangeOperation getRecordChangeOperation() {
    return recordChangeOperation;
  }

  public void setRecordChangeOperation(RecordChangeOperation recordChangeOperation) {
    this.recordChangeOperation = recordChangeOperation;
  }

  @Override
  public RFA1aFormDTO getDTO() {
    return rfa1aFormDTO;
  }

  public void setDTO(RFA1aFormDTO rfa1aFormDTO) {
    this.rfa1aFormDTO = rfa1aFormDTO;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChangedRFA1aFormDTO that = (ChangedRFA1aFormDTO) o;
    return recordChangeOperation == that.recordChangeOperation && Objects
        .equals(rfa1aFormDTO, that.rfa1aFormDTO);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rfa1aFormDTO, recordChangeOperation);
  }
}
