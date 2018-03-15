package gov.ca.cwds.jobs.cals.facility.recordchange;

import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import org.hibernate.annotations.NamedNativeQuery;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDateTime;

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
        name = CwsRecordChange.CWSCMS_INCREMENTAL_LOAD_QUERY_NAME,
        query = CwsRecordChange.CWS_CMS_INCREMENTAL_LOAD_QUERY,
        resultClass = CwsRecordChange.class,
        readOnly = true
)

@Entity
public class CwsRecordChange extends RecordChange  {

    final static String CWS_CMS_INITIAL_LOAD_QUERY = "SELECT PlacementHome.IDENTIFIER AS ID" +
            ",'I' AS CHANGE_OPERATION" +
            ",PlacementHome.LST_UPD_TS AS TIME_STAMP" +
            " FROM {h-schema}PLC_HM_T PlacementHome" +
            " WHERE PlacementHome.LICENSE_NO IS NULL";

    final static String CWS_CMS_INCREMENTAL_LOAD_QUERY = "SELECT PlacementHome.IDENTIFIER AS ID" +
            ",PlacementHome.IBMSNAP_OPERATION AS CHANGE_OPERATION" +
            ",PlacementHome.IBMSNAP_LOGMARKER AS TIME_STAMP" +
            " FROM {h-schema}PLC_HM_T PlacementHome" +
            " WHERE PlacementHome.LICENSE_NO IS NULL" +
            " AND PlacementHome.IBMSNAP_LOGMARKER >= :dateAfter";

    public final static String CWSCMS_INITIAL_LOAD_QUERY_NAME = "RecordChange.cwscmsInitialLoadQuery";
    public final static String CWSCMS_INCREMENTAL_LOAD_QUERY_NAME = "RecordChange.cwscmsIncrementalLoadQuery";

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
