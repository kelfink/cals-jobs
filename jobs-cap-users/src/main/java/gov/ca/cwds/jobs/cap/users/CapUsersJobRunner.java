package gov.ca.cwds.jobs.cap.users;

import gov.ca.cwds.jobs.common.inject.JobRunner;

/**
 * @author CWDS TPT-3
 */
public final class CapUsersJobRunner {

  public static void main(String[] args) {
    JobRunner.run(new CapUsersJobModule(args));
  }
}
