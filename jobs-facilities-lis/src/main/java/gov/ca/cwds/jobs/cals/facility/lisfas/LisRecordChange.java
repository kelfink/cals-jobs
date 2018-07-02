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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Created by Alexander Serbin on 3/6/2018.
 */
@NamedQueries({
    @NamedQuery(
        name = LisRecordChange.LIS_GET_MAX_TIMESTAMP_QUERY_NAME,
        query = LisRecordChange.LIS_GET_MAX_TIMESTAMP_SQL
    ),
    @NamedQuery(
        name = LisRecordChange.LIS_INITIAL_LOAD_QUERY_NAME,
        query = LisRecordChange.INITIAL_LOAD_SQL
    ),
    @NamedQuery(
        name = LisRecordChange.LIS_INCREMENTAL_LOAD_QUERY_NAME,
        query = LisRecordChange.INCREMENTAL_LOAD_SQL
    )
})
@Entity
public class LisRecordChange implements PersistentObject {

  private static final String SHARED_PART =
      " from LisFacFile where facilityTypeCode in (400, 403, 430, 431, 433, 710, 711, 720, 721, "
          + "722, 726, 728, 729, 730, 731, 732, 733) ";

  public static final String LIS_GET_MAX_TIMESTAMP_SQL =
      "select max(timestamp) "
          + SHARED_PART;

  public static final String INITIAL_LOAD_SQL =
      "select new LisRecordChange(facNbr, timestamp) "
          + SHARED_PART + " and facNbr > :facNbr order by facNbr";

  public static final String INCREMENTAL_LOAD_SQL =
      "select new LisRecordChange(facNbr, timestamp)"
          + SHARED_PART + " and timestamp > :dateAfter " +
          "order by timestamp, facNbr";


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
