package gov.ca.cwds.jobs.cals.facility.lis;

import gov.ca.cwds.jobs.cals.facility.RecordChange;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.persistence.Column;
import javax.persistence.Entity;
import org.hibernate.annotations.NamedNativeQuery;

/**
 * Created by Alexander Serbin on 3/6/2018.
 */

@NamedNativeQuery(
    name = LisRecordChange.LIS_INITIAL_LOAD_QUERY_NAME,
    query = LisRecordChange.LIS_BASE_QUERY,
    resultClass = LisRecordChange.class,
    readOnly = true
)
@NamedNativeQuery(
    name = LisRecordChange.LIS_INCREMENTAL_LOAD_QUERY_NAME,
    query = LisRecordChange.LIS_BASE_QUERY +
        " WHERE system_datetime_1 > :dateAfter ",
    resultClass = LisRecordChange.class,
    readOnly = true
)

@Entity
public class LisRecordChange extends RecordChange {

  public static final String LIS_INITIAL_LOAD_QUERY_NAME = "RecordChange.lisInitialLoadQuery";
  public static final String LIS_INCREMENTAL_LOAD_QUERY_NAME = "RecordChange.lisIncrementalLoadQuery";

  public static final DateTimeFormatter lisTimestampFormatter = DateTimeFormatter
      .ofPattern("yyyyMMddHHmmss");

  static final String LIS_BASE_QUERY = "SELECT " +
      "fac_nbr as ID" +
      ", 'U' AS CHANGE_OPERATION" +
      ", system_datetime_1 as TIME_STAMP "
      + " FROM {h-schema}lis_fac_file";

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
