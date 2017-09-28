package gov.ca.cwds.jobs.component;

public interface JobCurrentStatus {

  boolean isRunning();

  boolean isFailed();

  void doneIndexing();

  void doneExtracting();

  void doneTransforming();

  NeutronJobStatus status();

}
