package gov.ca.cwds.jobs.cals.facility.cws;

import gov.ca.cwds.jobs.cals.facility.RecordChange;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Created by Alexander Serbin on 3/6/2018.
 */
@NamedQueries({@NamedQuery(
    name = CwsRecordChange.CWSCMS_INITIAL_LOAD_QUERY_NAME,
    query = CwsRecordChange.CWS_CMS_INITIAL_LOAD_QUERY
), @NamedQuery(
    name = CwsRecordChange.CWSCMS_INCREMENTAL_LOAD_QUERY_NAME,
    query = CwsRecordChange.CWS_CMS_INCREMENTAL_LOAD_QUERY
)
})
@Entity
public class CwsRecordChange extends RecordChange {

  static final String CWS_CMS_INITIAL_LOAD_QUERY =
      "select distinct new CwsRecordChange(home.identifier,"
          + "home.lastUpdatedTime) "
          + "from ReplicationPlacementHome as home "
          + "where home.licensrCd <> 'CL' "
          + "and home.lastUpdatedTime >= :dateAfter "
          + "order by home.lastUpdatedTime";

  static final String CWS_CMS_INCREMENTAL_LOAD_QUERY =
      "select new CwsRecordChange(home.identifier,"
          + "home.recordChangeOperation, "
          + "home.timestamp) "
          + " from ReplicationPlacementHome as home "
          + " where home.licensrCd <> 'CL' "
          + " and home.timestamp >= :dateAfter"
          + " order by home.timestamp";


  public static final String CWSCMS_INITIAL_LOAD_QUERY_NAME = "RecordChange.cwscmsInitialLoadQuery";
  public static final String CWSCMS_INCREMENTAL_LOAD_QUERY_NAME = "RecordChange.cwscmsIncrementalLoadQuery";

  public CwsRecordChange(String id, RecordChangeOperation recordChangeOperation,
      LocalDateTime timestamp) {
    super(id, recordChangeOperation);
    this.timestamp = timestamp;
  }

  public CwsRecordChange(String id,
      LocalDateTime timestamp) {
    super(id, RecordChangeOperation.I);
    this.timestamp = timestamp;
  }

  @Column(name = "TIME_STAMP")
  private transient LocalDateTime timestamp;

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public static ChangedEntityIdentifier valueOf(CwsRecordChange recordChange) {
    return new ChangedEntityIdentifier(recordChange.getId(),
        recordChange.getRecordChangeOperation(),
        recordChange.getTimestamp());
  }

}
