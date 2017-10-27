package gov.ca.cwds.jobs.component;

import gov.ca.cwds.jobs.schedule.JobRunner;

public interface AtomValidateLastChg {

  default void validate() {

    if (JobRunner.isSchedulerMode() && !JobRunner.isInitialMode()) {
      // Validate affected documents.
      // JobRunner.getInstance().getTrackHistory().get(this.defaultSchedule.getKlazz()).stream()
      // .forEach(buf::append);
    }

  }

}
