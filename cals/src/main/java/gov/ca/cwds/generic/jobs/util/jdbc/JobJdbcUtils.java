package gov.ca.cwds.generic.jobs.util.jdbc;

import gov.ca.cwds.generic.jobs.component.AtomHibernate;
import gov.ca.cwds.generic.jobs.component.NeutronDateTimeFormat;
import gov.ca.cwds.generic.jobs.config.JobOptions;
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
      ret.add(Pair.of("aaaaaaaaaa", "J5p3IOTEaC"));
      ret.add(Pair.of("J5p3IOTEaC", "QG0okqi0AR"));
      ret.add(Pair.of("QG0okqi0AR", "0JGoWelDYN"));
      ret.add(Pair.of("0JGoYmm06Q", "1a6ExS95Ch"));
      ret.add(Pair.of("1a6ExS95Ch", "4u0U0MECwr"));
      ret.add(Pair.of("4u0VaS8B5d", "7NYwtxJ7Lu"));
      ret.add(Pair.of("7NYwtxJ7Lu", "9999999999"));

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
