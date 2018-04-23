package gov.ca.cwds.jobs.cals.facility.lis;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Id;
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
        @FieldResult(name = "timestamp", column = "system_datetime_1")
      }
    )
  }
)
@Entity
public class LisRecordChange implements PersistentObject {

  public static final String INITIAL_LOAD_SQL = "select fac_nbr, system_datetime_1 from "
      + "(select fac_nbr , system_datetime_1 from lis_fac_file "
      + "where fac_nbr > :facNbr order by fac_nbr)";

  public static final String INCREMENTAL_LOAD_SQL = "select fac_nbr, system_datetime_1 from "
      + "(select fac_nbr , system_datetime_1 from lis_fac_file "
      + "where system_datetime_1 > :dateAfter order by system_datetime_1)";


  public static final String LIS_INITIAL_LOAD_QUERY_NAME = "RecordChange.lisInitialLoadQuery";
  public static final String LIS_INCREMENTAL_LOAD_QUERY_NAME = "RecordChange.lisIncrementalLoadQuery";

  public static final DateTimeFormatter lisTimestampFormatter = DateTimeFormatter
      .ofPattern("yyyyMMddHHmmss");

  public LisRecordChange() {
    // Default constructor
  }

  public LisRecordChange(String id,
      BigInteger timestamp) {
    this.id = id;
    this.timestamp = timestamp;
  }

  @Column(name = "TIME_STAMP")
  private BigInteger timestamp;

  @Id
  @Column(name = "ID")
  private String id;


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
        RecordChangeOperation.U, timestamp);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public Serializable getPrimaryKey() {
    return getId();
  }
}
