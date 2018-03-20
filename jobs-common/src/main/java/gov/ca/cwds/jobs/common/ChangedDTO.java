package gov.ca.cwds.jobs.common;

import gov.ca.cwds.Identifiable;

/**
 * @author CWDS TPT-2
 */
public interface ChangedDTO<T> extends Identifiable<String> {

  RecordChangeOperation getRecordChangeOperation();

  T getDTO();
}
