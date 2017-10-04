package gov.ca.cwds.data.model.cms;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NamedNativeQuery;

/**
 * 
 * @author CWDS API Team
 */
@NamedNativeQuery(name = "findByLastJobRunTimeMinusOneMinute",
    query = "SELECT CNT.DOC_HANDLE AS DOC_HANDLE, DECODE(CNT.CMPRS_PRG, 'DELETED', 'DELETED', 'DELETE02', 'DELETED', 'ACTIVE') AS DOC_STATUS,"
        + "CNT.LST_UPD_TS AS LST_UPD_TS  FROM {h-schema}TSCNTRLT CNT WHERE CNT.DOC_HANDLE <> 'DUMMY' AND CMPRS_PRG = 'CWSCMP01'"
        + "AND CNT.LST_UPD_TS > TIMESTAMP_FORMAT(:lastJobRunTime, 'YYYY-MM-DD HH24:MI:SS') - 1 MINUTE;",
    resultClass = DocumentMetadata.class)
@Entity
@Table(name = "TSCNTRLT")
public class DocumentMetadata {

  @Id
  @Column(name = "DOC_HANDLE")
  private String handle;

  @Column(name = "DOC_STATUS")
  private String status;

  @Column(name = "LST_UPD_TS")
  private Date lastUpdatedTimestamp;

  /**
   * Constructor
   */
  public DocumentMetadata() {
    super();
  }

  /**
   * @return the handle
   */
  public String getHandle() {
    return handle;
  }

  /**
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  /**
   * @return the lastUpdatedTimestamp
   */
  public Date getLastUpdatedTimestamp() {
    return lastUpdatedTimestamp;
  }
}
