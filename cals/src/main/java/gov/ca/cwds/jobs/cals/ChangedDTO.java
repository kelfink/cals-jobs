package gov.ca.cwds.jobs.cals;

import gov.ca.cwds.cals.Identifiable;
import gov.ca.cwds.dto.BaseDTO;

/**
 * @author CWDS TPT-2
 */
public interface ChangedDTO<T extends BaseDTO> extends Identifiable<String> {

  RecordChangeOperation getRecordChangeOperation();

  T getDTO();
}
