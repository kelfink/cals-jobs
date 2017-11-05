package gov.ca.cwds.data.persistence.cms.rep;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.Type;

import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.jobs.util.JobDateUtil;

@Embeddable
public class EmbeddableStaffWorker implements ApiMarker {

  private static final long serialVersionUID = 1L;

  @Column(name = "WORKER_ID")
  private String workerId;

  @Column(name = "WORKER_FIRST_NM")
  @ColumnTransformer(read = "trim(WORKER_FIRST_NM)")
  private String workerFirstName;

  @Column(name = "WORKER_LAST_NM")
  @ColumnTransformer(read = "trim(WORKER_LAST_NM)")
  private String workerLastName;

  @Column(name = "WORKER_LAST_UPDATED")
  @Type(type = "timestamp")
  private Date workerLastUpdated;

  public String getWorkerId() {
    return workerId;
  }

  public void setWorkerId(String workerId) {
    this.workerId = workerId;
  }

  public String getWorkerFirstName() {
    return workerFirstName;
  }

  public void setWorkerFirstName(String workerFirstName) {
    this.workerFirstName = workerFirstName;
  }

  public String getWorkerLastName() {
    return workerLastName;
  }

  public void setWorkerLastName(String workerLastName) {
    this.workerLastName = workerLastName;
  }

  public Date getWorkerLastUpdated() {
    return JobDateUtil.freshDate(workerLastUpdated);
  }

  public void setWorkerLastUpdated(Date workerLastUpdated) {
    this.workerLastUpdated = JobDateUtil.freshDate(workerLastUpdated);
  }

}
