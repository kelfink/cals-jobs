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
        name = RecordChange.CWSCMS_INITIAL_LOAD_QUERY_NAME,
        query = RecordChange.CWS_CMS_BASE_QUERY,
        resultClass = RecordChange.class,
        readOnly = true
)

@NamedNativeQuery(
    name = RecordChange.CWSCMS_INCREMENTAL_LOAD_QUERY_NAME,
    query = RecordChange.CWS_CMS_BASE_QUERY +
            " AND PlacementHome.IBMSNAP_LOGMARKER >= :dateAfter",
    resultClass = RecordChange.class,
    readOnly = true
)

@NamedNativeQuery(
    name = RecordChange.LIS_INITIAL_LOAD_QUERY_NAME,
    query = RecordChange.LIS_BASE_QUERY,
    resultClass = RecordChange.class,
    readOnly = true
)
@NamedNativeQuery(
        name = RecordChange.LIS_INCREMENTAL_LOAD_QUERY_NAME,
        query = RecordChange.LIS_BASE_QUERY +
                " WHERE f.system_datetime_1 >= :dateAfter ",
        resultClass = RecordChange.class,
        readOnly = true
)
@Entity
public class RecordChange implements PersistentObject {

  public final static String CWSCMS_INITIAL_LOAD_QUERY_NAME = "RecordChange.cwscmsInitialLoadQuery";
  public final static String CWSCMS_INCREMENTAL_LOAD_QUERY_NAME = "RecordChange.cwscmsIncrementalLoadQuery";
  public final static String LIS_INITIAL_LOAD_QUERY_NAME = "RecordChange.lisInitialLoadQuery";
  public final static String LIS_INCREMENTAL_LOAD_QUERY_NAME = "RecordChange.lisIncrementalLoadQuery";

  final static String CWS_CMS_BASE_QUERY = "SELECT PlacementHome.IDENTIFIER AS ID, PlacementHome.IBMSNAP_OPERATION AS CHANGE_OPERATION"
          + " FROM {h-schema}PLC_HM_T PlacementHome"
          + " WHERE PlacementHome.LICENSE_NO IS NULL";

  final static String LIS_BASE_QUERY = "SELECT fac_nbr as ID, 'U' AS CHANGE_OPERATION"
          + " FROM {h-schema}lis_fac_file f";

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
