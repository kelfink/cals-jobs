package gov.ca.cwds.jobs.cals.facility.lisfas;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.jobs.cals.facility.lisfas.identifier.LicenseNumberIdentifier;
import gov.ca.cwds.jobs.cals.facility.lisfas.identifier.LisTimestampIdentifier;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LicenseNumberSavePoint;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LisTimestampSavePoint;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.identifier.TimestampIdentifier;
import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import org.hibernate.annotations.NamedNativeQuery;

/**
 * Created by Alexander Serbin on 3/6/2018.
 */
@NamedNativeQuery(
    name = LisRecordChange.LIS_GET_MAX_TIMESTAMP_QUERY_NAME,
    query = LisRecordChange.LIS_GET_MAX_TIMESTAMP_SQL,
    resultSetMapping = "LisRecordChangeMaxValue"
)
@NamedNativeQuery(
    name = LisRecordChange.LIS_INITIAL_LOAD_QUERY_NAME,
    query = LisRecordChange.INITIAL_LOAD_SQL,
    resultSetMapping = "LisRecordChangeMapping"
)
@NamedNativeQuery(
    name = LisRecordChange.LIS_INCREMENTAL_LOAD_QUERY_NAME,
    query = LisRecordChange.INCREMENTAL_LOAD_SQL,
    resultSetMapping = "LisRecordChangeMapping"
)
@SqlResultSetMappings({
    @SqlResultSetMapping(name = "LisRecordChangeMaxValue",
        columns = {@ColumnResult(name = "m")})
    , @SqlResultSetMapping(
    name = "LisRecordChangeMapping",
    entities = {
        @EntityResult(
            entityClass = LisRecordChange.class,
            fields = {
                @FieldResult(name = "id", column = "fac_nbr"),
                @FieldResult(name = "timestamp", column = "system_datetime_1")
            }
        )
    }
)}
)
@Entity
public class LisRecordChange implements PersistentObject {

  private static final String SHARED_PART =
      " from lis_fac_file where fac_type in (400, 403, 430, 431, 433, 710, 711, 720, 721, "
          + "722, 726, 728, 729, 730, 731, 732, 733) ";

  public static final String LIS_GET_MAX_TIMESTAMP_SQL =
      "select max(system_datetime_1) as m "
          + SHARED_PART;

  public static final String INITIAL_LOAD_SQL =
      "select fac_nbr, system_datetime_1 "
          + SHARED_PART + " and fac_nbr > :facNbr order by fac_nbr";

  public static final String INCREMENTAL_LOAD_SQL =
      "select fac_nbr, system_datetime_1 "
          + SHARED_PART + " and system_datetime_1 > :dateAfter " +
          "order by system_datetime_1, fac_nbr";

  public static final String LIS_GET_MAX_TIMESTAMP_QUERY_NAME = "RecordChange.lisGetMaxTimestamp";
  public static final String LIS_INITIAL_LOAD_QUERY_NAME = "RecordChange.lisInitialLoadQuery";
  public static final String LIS_INCREMENTAL_LOAD_QUERY_NAME = "RecordChange.lisIncrementalLoadQuery";

  @Column(name = "TIME_STAMP")
  private BigInteger timestamp;

  @Id
  @Column(name = "ID")
  private int id;

  public LisRecordChange() {
    // Default constructor
  }

  public LisRecordChange(int id,
      BigInteger timestamp) {
    this.id = id;
    this.timestamp = timestamp;
  }

  public BigInteger getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(BigInteger timestamp) {
    this.timestamp = timestamp;
  }

  public static LicenseNumberIdentifier toLicenseNumberIdentifier(
      LisRecordChange recordChange) {
    return new LicenseNumberIdentifier(recordChange.getId(),
        RecordChangeOperation.U, new LicenseNumberSavePoint(recordChange.getId()));
  }

  public static TimestampIdentifier<BigInteger> toLisTimestampIdentifier(
      LisRecordChange recordChange) {
    return new LisTimestampIdentifier(recordChange.getId(),
        RecordChangeOperation.U, new LisTimestampSavePoint(recordChange.getTimestamp()));
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Override
  public Serializable getPrimaryKey() {
    return getId();
  }
}
