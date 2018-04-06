package gov.ca.cwds.jobs.common.job;

/**
 * Created by Alexander Serbin on 4/6/2018.
 */
public class TotalCountInformation {

  private long totalToBeUpdated;
  private long totalToBeInserted;
  private long totalToBeDeleted;

  public long getTotalToBeUpdated() {
    return totalToBeUpdated;
  }

  public void setTotalToBeUpdated(long totalToBeUpdated) {
    this.totalToBeUpdated = totalToBeUpdated;
  }

  public long getTotalToBeInserted() {
    return totalToBeInserted;
  }

  public void setTotalToBeInserted(long totalToBeInserted) {
    this.totalToBeInserted = totalToBeInserted;
  }

  public long getTotalToBeDeleted() {
    return totalToBeDeleted;
  }

  public void setTotalToBeDeleted(long totalToBeDeleted) {
    this.totalToBeDeleted = totalToBeDeleted;
  }

}
