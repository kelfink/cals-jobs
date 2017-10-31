package gov.ca.cwds.jobs.schedule;

public class NeutronServerSettings {

  /**
   * For unit tests where resources either may not close properly or where expensive resources
   * should be mocked.
   */
  private boolean testMode = false;

  /**
   * Run a single server for all jobs. Launch one JVM, serve many jobs.
   */
  private boolean continuousMode = false;

  /**
   * Launch one JVM, run initial load jobs sequentially, and exit.
   */
  private boolean initialMode = false;

  public boolean isTestMode() {
    return testMode;
  }

  public void setTestMode(boolean testMode) {
    this.testMode = testMode;
  }

  public boolean isContinuousMode() {
    return continuousMode;
  }

  public void setContinuousMode(boolean continuousMode) {
    this.continuousMode = continuousMode;
  }

  public boolean isInitialMode() {
    return initialMode;
  }

  public void setInitialMode(boolean initialMode) {
    this.initialMode = initialMode;
  }

}
