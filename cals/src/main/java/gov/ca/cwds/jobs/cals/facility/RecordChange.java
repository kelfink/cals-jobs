package gov.ca.cwds.jobs.cals.facility;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import org.hibernate.annotations.NamedNativeQuery;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author CWDS TPT-2
 */
@NamedNativeQuery(
    name = "RecordChange.findChangedFacilityRecordsInCWSCMS",
    query =
        "SELECT PlacementHome.IDENTIFIER AS ID, PlacementHome.IBMSNAP_OPERATION AS CHANGE_OPERATION"
            + " FROM {h-schema}PLC_HM_T PlacementHome"
            + " WHERE PlacementHome.LICENSE_NO IS NULL AND PlacementHome.IBMSNAP_LOGMARKER > :dateAfter"
            + " UNION"
            + " SELECT DISTINCT PlacementHome.IDENTIFIER AS ID, 'U' AS CHANGE_OPERATION"
            + " FROM {h-schema}CLIENT_T Client"
            + " INNER JOIN {h-schema}PLC_EPST PlacementEpisode ON Client.IDENTIFIER=PlacementEpisode.FKCLIENT_T"
            + " INNER JOIN {h-schema}O_HM_PLT OutOfHomePlacement ON PlacementEpisode.THIRD_ID=OutOfHomePlacement.FKPLC_EPS0"
            + " INNER JOIN {h-schema}PLC_HM_T PlacementHome ON OutOfHomePlacement.FKPLC_HM_T=PlacementHome.IDENTIFIER"
            + " WHERE PlacementHome.LICENSE_NO IS NULL AND ("
            + " Client.IBMSNAP_LOGMARKER > :dateAfter OR"
            + " PlacementEpisode.IBMSNAP_LOGMARKER > :dateAfter OR"
            + " OutOfHomePlacement.IBMSNAP_LOGMARKER > :dateAfter)",
    resultClass = RecordChange.class,
    readOnly = true
)
@NamedNativeQuery(
    name = "RecordChange.findChangedFacilityRecordsInLIS",
    query = "SELECT f.fac_nbr as ID, 'U' AS CHANGE_OPERATION"
        + " FROM {h-schema}lis_fac_file f WHERE f.fac_last_upd_date > :dateAfter",
    resultClass = RecordChange.class,
    readOnly = true
)
@NamedNativeQuery(
    name = "RecordChange.findChangedFacilityRecordsInFAS",
    query = "SELECT DISTINCT f.facility_number_text AS ID, 'U' AS CHANGE_OPERATION"
        + " FROM {h-schema}facility_information f"
        + " LEFT JOIN Rr809Dn rr809dn ON TRIM(rr809dn.facility_number_text) = TRIM(CAST (f.facility_number_text AS VARCHAR(254)))"
        + " LEFT JOIN Rrcpoc rrcpoc ON TRIM(rrcpoc.facility_number_text) = TRIM(CAST (f.facility_number_text AS VARCHAR(254)))"
        + " LEFT JOIN complaint_report_lic802 compl ON TRIM(compl.facility_number_text) = TRIM(CAST (f.facility_number_text AS VARCHAR(254)))"
        + " WHERE"
        + " (:initialLoad = 1 AND f.dt_modified IS NULL) OR f.dt_modified > :dateAfter"
        + " OR rr809dn.dt_created > :dateAfter OR rr809dn.dt_modified > :dateAfter"
        + " OR rrcpoc.dt_created > :dateAfter OR rrcpoc.dt_modified > :dateAfter"
        + " OR compl.dt_created > :dateAfter OR compl.dt_modified > :dateAfter",
    resultClass = RecordChange.class,
    readOnly = true
)
@Entity
public class RecordChange implements PersistentObject {

  @Id
  @Column(name = "ID")
  private String id;

  @Enumerated(EnumType.STRING)
  @Column(name = "CHANGE_OPERATION", updatable = false)
  private RecordChangeOperation recordChangeOperation;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public RecordChangeOperation getRecordChangeOperation() {
    return recordChangeOperation;
  }

  public void setRecordChangeOperation(RecordChangeOperation recordChangeOperation) {
    this.recordChangeOperation = recordChangeOperation;
  }

  @Override
  public Serializable getPrimaryKey() {
    return getId();
  }
}
