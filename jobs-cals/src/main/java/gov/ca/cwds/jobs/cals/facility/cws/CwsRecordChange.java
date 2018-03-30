package gov.ca.cwds.jobs.cals.facility.cws;

import gov.ca.cwds.jobs.cals.facility.RecordChange;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import org.hibernate.annotations.NamedNativeQuery;

/**
 * Created by Alexander Serbin on 3/6/2018.
 */

@NamedNativeQuery(
    name = CwsRecordChange.CWSCMS_INITIAL_LOAD_QUERY_NAME,
    query = CwsRecordChange.CWS_CMS_INITIAL_LOAD_QUERY,
    resultClass = CwsRecordChange.class,
    readOnly = true
)

@NamedNativeQuery(
    name = CwsRecordChange.CWSCMS_RESUME_INCREMENTAL_LOAD_QUERY_NAME,
    query = CwsRecordChange.CWS_CMS_RESUME_INITIAL_LOAD_QUERY,
    resultClass = CwsRecordChange.class,
    readOnly = true
)

@NamedNativeQuery(
    name = CwsRecordChange.CWSCMS_INCREMENTAL_LOAD_QUERY_NAME,
    query = CwsRecordChange.CWS_CMS_INCREMENTAL_LOAD_QUERY,
    resultClass = CwsRecordChange.class,
    readOnly = true
)

@Entity
public class CwsRecordChange extends RecordChange {

  static final String CWS_CMS_INITIAL_LOAD_QUERY = "SELECT PlacementHome.IDENTIFIER AS ID" +
      ",'I' AS CHANGE_OPERATION" +
      ",PlacementHome.LST_UPD_TS AS TIME_STAMP" +
      " FROM {h-schema}PLC_HM_T PlacementHome" +
      " WHERE PlacementHome.LICENSE_NO IS NULL";

  public static final String CWS_CMS_RESUME_INITIAL_LOAD_QUERY = CWS_CMS_INITIAL_LOAD_QUERY +
      " AND PlacementHome.LST_UPD_TS > :dateAfter";

  static final String CWS_CMS_INCREMENTAL_LOAD_QUERY = "SELECT PlacementHome.IDENTIFIER AS ID" +
      ",PlacementHome.IBMSNAP_OPERATION AS CHANGE_OPERATION" +
      ",PlacementHome.IBMSNAP_LOGMARKER AS TIME_STAMP" +
      " FROM {h-schema}PLC_HM_T PlacementHome" +
      " WHERE PlacementHome.LICENSE_NO IS NULL" +
      " AND PlacementHome.IBMSNAP_LOGMARKER > :dateAfter";

  public static final String CWSCMS_INITIAL_LOAD_QUERY_NAME = "RecordChange.cwscmsInitialLoadQuery";
  public static final String CWSCMS_INCREMENTAL_LOAD_QUERY_NAME = "RecordChange.cwscmsIncrementalLoadQuery";
  public static final String CWSCMS_RESUME_INCREMENTAL_LOAD_QUERY_NAME = "RecordChange.cwscmsResumeInitialLoadQuery";


  @Column(name = "TIME_STAMP")
  private LocalDateTime timestamp;

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
