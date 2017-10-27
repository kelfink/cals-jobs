package gov.ca.cwds.jobs.component;

import org.slf4j.Logger;

import gov.ca.cwds.jobs.exception.NeutronException;

public interface AtomValidateDocument {

  Logger getLog();

  JobProgressTrack getTrack();

  default void validate() throws NeutronException {
    getTrack().getAffectedDocumentIds();
  }

}
