package gov.ca.cwds.jobs.cap.users.entity;

import gov.ca.cwds.data.legacy.cms.CmsPersistentObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(
        name = "STFPERST"
)
public class StaffPerson extends CmsPersistentObject {

  private static final long serialVersionUID = 6483188919867162459L;

  @Id
  @Column(
          name = "IDENTIFIER"
  )
  private String id;

  @Column(
          name = "FKCWS_OFFT"
  )
  private String cwsOffice;


  @Override
  public Serializable getPrimaryKey() {
    return this.getId();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCwsOffice() {
    return cwsOffice;
  }

  public void setCwsOffice(String cwsOffice) {
    this.cwsOffice = cwsOffice;
  }
}
