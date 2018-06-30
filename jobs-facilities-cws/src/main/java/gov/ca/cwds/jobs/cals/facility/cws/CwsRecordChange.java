package gov.ca.cwds.jobs.cals.facility.cws;

import gov.ca.cwds.jobs.cals.facility.RecordChange;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
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
), @NamedQuery(
    name = CwsRecordChange.CWSCMS_GET_MAX_TIMESTAMP_QUERY_NAME,
    query = CwsRecordChange.CWS_CMS_GET_MAX_TIMESTAMP_QUERY
)
})
@Entity
public class CwsRecordChange extends RecordChange {

  private static final String SHARED_PART =
      " from ReplicationPlacementHome as home"
          + " where home.licensrCd <> 'CL' ";

  static final String CWS_CMS_GET_MAX_TIMESTAMP_QUERY =
      "select max(home.replicationLastUpdated)"
          + SHARED_PART;

  static final String CWS_CMS_INITIAL_LOAD_QUERY =
      "select new CwsRecordChange(home.identifier,"
          + "home.lastUpdatedTime) "
          + SHARED_PART
          + " and home.lastUpdatedTime > :dateAfter "
          + " order by home.lastUpdatedTime, home.identifier";

  static final String CWS_CMS_INCREMENTAL_LOAD_QUERY =
      "select new CwsRecordChange(home.identifier,"
          + "home.recordChangeOperation, "
          + "home.replicationLastUpdated) "
          + SHARED_PART
          + " and home.replicationLastUpdated > :dateAfter"
          + " order by home.replicationLastUpdated, home.identifier";

  public static final String CWSCMS_INITIAL_LOAD_QUERY_NAME = "RecordChange.cwscmsInitialLoadQuery";
  public static final String CWSCMS_INCREMENTAL_LOAD_QUERY_NAME = "RecordChange.cwscmsIncrementalLoadQuery";
  public static final String CWSCMS_GET_MAX_TIMESTAMP_QUERY_NAME = "RecordChange.cwscmsMaxTimestampQuery";

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

  public static ChangedEntityIdentifier<TimestampSavePoint> valueOf(CwsRecordChange recordChange) {
    return new ChangedEntityIdentifier<TimestampSavePoint>(recordChange.getId(),
        recordChange.getRecordChangeOperation(),
        new TimestampSavePoint(recordChange.getTimestamp())) {

      @Override
      public int compareTo(ChangedEntityIdentifier<TimestampSavePoint> o) {
        return getSavePoint().compareTo(o.getSavePoint());
      }
    };
  }

}
