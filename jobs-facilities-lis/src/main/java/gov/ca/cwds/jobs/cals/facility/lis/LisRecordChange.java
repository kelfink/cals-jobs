package gov.ca.cwds.jobs.cals.facility.lis;

import gov.ca.cwds.jobs.cals.facility.RecordChange;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SqlResultSetMapping;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

/** Created by Alexander Serbin on 3/6/2018. */
@NamedNativeQueries({
  @NamedNativeQuery(
    name = LisRecordChange.LIS_INITIAL_LOAD_QUERY_NAME,
    query = LisRecordChange.INITIAL_LOAD_SQL,
    resultSetMapping = "LisRecordChangeMapping"
  ),
  @NamedNativeQuery(
    name = LisRecordChange.LIS_INCREMENTAL_LOAD_QUERY_NAME,
    query = LisRecordChange.INCREMENTAL_LOAD_SQL,
    resultSetMapping = "LisRecordChangeMapping"
  )
})
@SqlResultSetMapping(
  name = "LisRecordChangeMapping",
  entities = {
    @EntityResult(
      entityClass = LisRecordChange.class,
      fields = {
        @FieldResult(name = "id", column = "fac_nbr"),
        @FieldResult(name = "recordChangeOperation", column = "op"),
        @FieldResult(name = "timestamp", column = "system_datetime_1")
      }
    )
  }
)
@Entity
public class LisRecordChange extends RecordChange {

  public static final String INITIAL_LOAD_SQL = "select fac_nbr, 'U' op, system_datetime_1 from "
      + "(select fac_nbr , system_datetime_1 from lis_fac_file "
      + "where fac_nbr > :facNbr order by fac_nbr)";

  public static final String INCREMENTAL_LOAD_SQL = "select fac_nbr as ID, 'U' as CHANGE_OPERATION, system_datetime_1 as TIME_STAMP from "
      + "(select fac_nbr , system_datetime_1 from lis_fac_file "
      + "where system_datetime_1 > :dateAfter order by system_datetime_1)";


  public static final String LIS_INITIAL_LOAD_QUERY_NAME = "RecordChange.lisInitialLoadQuery";
  public static final String LIS_INCREMENTAL_LOAD_QUERY_NAME = "RecordChange.lisIncrementalLoadQuery";

  public static final DateTimeFormatter lisTimestampFormatter = DateTimeFormatter
      .ofPattern("yyyyMMddHHmmss");

  public LisRecordChange() {
    // Default constructor
  }

  public LisRecordChange(int id, BigInteger timestamp) {
    this(String.valueOf(id), RecordChangeOperation.U, timestamp);
  }

  public LisRecordChange(String id,
      RecordChangeOperation recordChangeOperation,
      BigInteger timestamp) {
    super(id, recordChangeOperation);
    this.timestamp = timestamp;
  }

  @Column(name = "TIME_STAMP")
  private BigInteger timestamp;

  public BigInteger getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(BigInteger timestamp) {
    this.timestamp = timestamp;
  }

  public static ChangedEntityIdentifier valueOf(LisRecordChange recordChange) {
    LocalDateTime timestamp = recordChange.getTimestamp() == null ? null :
        LocalDateTime.parse(String.valueOf(recordChange.getTimestamp()),
            lisTimestampFormatter);
    return new ChangedEntityIdentifier(recordChange.getId(),
        recordChange.getRecordChangeOperation(), timestamp);
  }
}
