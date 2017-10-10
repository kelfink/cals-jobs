package gov.ca.cwds.jobs.util.jdbc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.jobs.component.AtomHibernate;
import gov.ca.cwds.jobs.config.JobOptions;
import gov.ca.cwds.jobs.defaults.NeutronDateTimeFormat;

/**
 * JDBC utilities for Neutron jobs.
 * 
 * @author CWDS API Team
 */
public class JobJdbcUtils {

  static final Logger LOGGER = LoggerFactory.getLogger(JobJdbcUtils.class);

  private JobJdbcUtils() {
    // Default, no-op. Static utility class.
  }

  public static String makeTimestampString(final Date date) {
    final StringBuilder buf = new StringBuilder();
    buf.append("TIMESTAMP('")
        .append(new SimpleDateFormat(NeutronDateTimeFormat.LEGACY_TIMESTAMP_FORMAT.getFormat())
            .format(date))
        .append("')");
    return buf.toString();
  }

  public static String makeSimpleTimestampString(final Date date) {
    return new SimpleDateFormat(NeutronDateTimeFormat.LEGACY_TIMESTAMP_FORMAT.getFormat())
        .format(date);
  }

  /**
   * @return default CMS schema name
   */
  public static String getDBSchemaName() {
    return System.getProperty("DB_CMS_SCHEMA");
  }

  public static void prepHibernateLastChange(final Session session, final Transaction txn,
      final Date lastRunTime, final String sqlInsertLastChange) throws HibernateException {
    final Work work = new PrepSQLWork(lastRunTime, sqlInsertLastChange);
    session.doWork(work);
  }

  /**
   * Calculate the number of reader threads to run from incoming job options and available
   * processors.
   * 
   * @param opts job options
   * @return number of reader threads to run
   */
  public static int calcReaderThreads(final JobOptions opts) {
    final int ret = opts.getThreadCount() != 0L ? (int) opts.getThreadCount()
        : Math.max(Runtime.getRuntime().availableProcessors() - 4, 4);
    LOGGER.warn(">>>>>>>> # OF READER THREADS: {} <<<<<<<<", ret);
    return ret;
  }

  public static List<Pair<String, String>> getCommonPartitionRanges(AtomHibernate initialLoad) {
    List<Pair<String, String>> ret = new ArrayList<>();

    final boolean isMainframe = initialLoad.isDB2OnZOS();
    if (isMainframe && (getDBSchemaName().toUpperCase().endsWith("RSQ")
        || getDBSchemaName().toUpperCase().endsWith("REP"))) {
      // ----------------------------
      // z/OS, large data set:
      // ORDER: a,z,A,Z,0,9
      // ----------------------------
      ret.add(Pair.of("aaaaaaaaaa", "B3bMRWu8NV"));
      ret.add(Pair.of("B3bMRWu8NV", "DW5GzxJ30A"));
      ret.add(Pair.of("DW5GzxJ30A", "FNOBbaG6qq"));
      ret.add(Pair.of("FNOBbaG6qq", "HJf1EJe25X"));
      ret.add(Pair.of("HJf1EJe25X", "JCoyq0Iz36"));
      ret.add(Pair.of("JCoyq0Iz36", "LvijYcj01S"));
      ret.add(Pair.of("LvijYcj01S", "Npf4LcB3Lr"));
      ret.add(Pair.of("Npf4LcB3Lr", "PiJ6a0H49S"));
      ret.add(Pair.of("PiJ6a0H49S", "RbL4aAL34A"));
      ret.add(Pair.of("RbL4aAL34A", "S3qiIdg0BN"));
      ret.add(Pair.of("S3qiIdg0BN", "0Ltok9y5Co"));
      ret.add(Pair.of("0Ltok9y5Co", "2CFeyJd49S"));
      ret.add(Pair.of("2CFeyJd49S", "4w3QDw136B"));
      ret.add(Pair.of("4w3QDw136B", "6p9XaHC10S"));
      ret.add(Pair.of("6p9XaHC10S", "8jw5J580MQ"));
      ret.add(Pair.of("8jw5J580MQ", "9999999999"));

      ret = initialLoad.limitRange(ret); // command line range restriction
    } else if (isMainframe) {
      // ----------------------------
      // z/OS, small data set:
      // ORDER: a,z,A,Z,0,9
      // ----------------------------
      ret.add(Pair.of("aaaaaaaaaa", "9999999999"));
    } else {
      // ----------------------------
      // Linux or small data set:
      // ORDER: 0,9,a,A,z,Z
      // ----------------------------
      ret.add(Pair.of("0000000000", "ZZZZZZZZZZ"));
    }

    return ret;
  }

}
