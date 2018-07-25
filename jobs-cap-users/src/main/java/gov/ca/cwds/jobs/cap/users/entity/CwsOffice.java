package gov.ca.cwds.jobs.cap.users.entity;

import gov.ca.cwds.data.legacy.cms.CmsPersistentObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


@Entity
@Table(name = "CWS_OFFT")
public class CwsOffice extends CmsPersistentObject {
  private static final long serialVersionUID = 2458409600546521184L;

  @Id
  @Column(name = "IDENTIFIER")
  private String officeId;


  @Override
  public Serializable getPrimaryKey() {
    return this.getOfficeId();
  }

  public String getOfficeId() {
    return officeId;
  }

  public void setOfficeId(String officeId) {
    this.officeId = officeId;
  }

}
