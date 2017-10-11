package gov.ca.cwds.jobs.util.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

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
      final Date lastRunTime, final String sqlInsertLastChange,
      final Function<Connection, PreparedStatement> func) throws HibernateException {
    final Work work = new PrepSQLWork(lastRunTime, sqlInsertLastChange, func);
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
    LOGGER.info(">>>>>>>> # OF READER THREADS: {} <<<<<<<<", ret);
    return ret;
  }

  public static List<Pair<String, String>> getPartitionRanges64() {
    final List<Pair<String, String>> ret = new ArrayList<>(64);
    ret.add(Pair.of("aaaaaaaaaa", "ACZAVXC2PZ"));
    ret.add(Pair.of("ACZA5MZAR1", "A6y48OH3Ut"));
    ret.add(Pair.of("A6y49C3046", "BzaK4AmDfS"));
    ret.add(Pair.of("BzaLz7EAXU", "B26CwOn9qX"));
    ret.add(Pair.of("B26CABM7D3", "CvIYhuyCJt"));
    ret.add(Pair.of("CvIYnyi046", "CY8r2uwEBI"));
    ret.add(Pair.of("CY8skTZK8I", "DsNAQ1W4l6"));
    ret.add(Pair.of("DsNAT5Q5Cn", "DWQGOmFAzi"));
    ret.add(Pair.of("DWQJMNtBVn", "EpKrO9a9xb"));
    ret.add(Pair.of("EpKsibQGUs", "ESH4wdQ02u"));
    ret.add(Pair.of("ESH6xJD5Dg", "Fmy2cMI0kW"));
    ret.add(Pair.of("Fmy2xT72L9", "FN4JbQ0BFV"));
    ret.add(Pair.of("FN4JHu74n3", "GjekFiPEuJ"));
    ret.add(Pair.of("GjekNyLCtT", "GMSl4aH4wm"));
    ret.add(Pair.of("GMSmdt6EcW", "HfvKXzA7rJ"));
    ret.add(Pair.of("HfvLp2RBFX", "HJtGK9J07S"));
    ret.add(Pair.of("HJtMdwiJ9x", "IbMKNtL5Dk"));
    ret.add(Pair.of("IbML33BCKo", "IEwHjr9FXO"));
    ret.add(Pair.of("IEwHwyZBMb", "I6RlOno3qC"));
    ret.add(Pair.of("I6Rytb53Lr", "JBHjcBU74E"));
    ret.add(Pair.of("JBHjhuiGmy", "J5p3IOTEaC"));
    ret.add(Pair.of("J5p3PS5AUp", "Kybpi693es"));
    ret.add(Pair.of("KybpkUu83S", "K1KqHJ7AUR"));
    ret.add(Pair.of("K1KqNIk5Ds", "LuIBpq0Ftl"));
    ret.add(Pair.of("LuIBA4v07S", "LXN8vc87GE"));
    ret.add(Pair.of("LXN9PW15O4", "Mq5VGFd5Tk"));
    ret.add(Pair.of("Mq5V2Gk046", "MUOYtgq2Mq"));
    ret.add(Pair.of("MUOYVNp6fK", "NnZWsz1AzZ"));
    ret.add(Pair.of("NnZWZbP8tp", "NRQqLqM5xF"));
    ret.add(Pair.of("NRQqN6xCPl", "OjJmdJW5Dm"));
    ret.add(Pair.of("OjJmrdICcq", "ONwCecl5DQ"));
    ret.add(Pair.of("ONwCl195Dl", "Pf5jLGj9UX"));
    ret.add(Pair.of("Pf5jRzm6V1", "PI0rahL9yH"));
    ret.add(Pair.of("PI0rfcq0KD", "Qd7xBSt3sQ"));
    ret.add(Pair.of("Qd7xIBy9yH", "QG0okqi0AR"));
    ret.add(Pair.of("QG0oFocA17", "RaKSN5wCaR"));
    ret.add(Pair.of("RaKTbwy1CN", "REwFaZn4iv"));
    ret.add(Pair.of("REwFbVrFj8", "R62gv73C4Z"));
    ret.add(Pair.of("R62gHYM9Nb", "SzBubpb5sR"));
    ret.add(Pair.of("SzBuWRC722", "S2pw19Q2Br"));
    ret.add(Pair.of("S2pyMvq7NP", "Txj1LpTFgm"));
    ret.add(Pair.of("Txj3EIxBeO", "T0eZIQpDjK"));
    ret.add(Pair.of("T0eZ92P37S", "0fXmBiY5Ch"));
    ret.add(Pair.of("0fXnkHf9q0", "0JGoWelDYN"));
    ret.add(Pair.of("0JGoYmm06Q", "1a6ExS95Ch"));
    ret.add(Pair.of("1a6EK1pCPy", "1Fbwaaw3b4"));
    ret.add(Pair.of("1Fbwe7q9Ma", "17PbXTH73o"));
    ret.add(Pair.of("17Pb3di8fe", "2AzE4ua8rX"));
    ret.add(Pair.of("2AzE86j5Dn", "23rHzlZ30A"));
    ret.add(Pair.of("23rIcb6MUf", "3yq0BFP7Nw"));
    ret.add(Pair.of("3yq0XgrAR1", "32qlKK2ICi"));
    ret.add(Pair.of("32qlKSGF39", "4u0U0MECwr"));
    ret.add(Pair.of("4u0VaS8B5d", "4YRF9Dd70O"));
    ret.add(Pair.of("4YRGE555th", "5rOfwNO3gC"));
    ret.add(Pair.of("5rOfCtA4bm", "5T7PS2j37S"));
    ret.add(Pair.of("5T7P0oDEyI", "6oipPRSKDX"));
    ret.add(Pair.of("6oip1GXBEM", "6R6kaia0SL"));
    ret.add(Pair.of("6R6kaRt33A", "7ki4MYoAzi"));
    ret.add(Pair.of("7ki5lfa996", "7NYwtxJ7Lu"));
    ret.add(Pair.of("7NYwQaO8SW", "8guC2hG4ak"));
    ret.add(Pair.of("8guDdze6Z2", "8JpJrxB37S"));
    ret.add(Pair.of("8JpTu2j3K2", "9cRG3VmH6i"));
    ret.add(Pair.of("9cRHCIm6xC", "9GwwRzY7D3"));
    ret.add(Pair.of("9Gwxhdx41S", "9999999999"));
    return ret;
  }

  public static List<Pair<String, String>> getPartitionRanges16() {
    final List<Pair<String, String>> ret = new ArrayList<>(16);
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
    return ret;
  }

  @SuppressWarnings("unchecked")
  private static List<Pair<String, String>> getCommonPartitionRanges(
      @SuppressWarnings("rawtypes") AtomHibernate initialLoad, int numPartitions) {
    List<Pair<String, String>> ret = new ArrayList<>(numPartitions);
    if (initialLoad.isLargeDataSet()) {
      // ----------------------------
      // z/OS, large data set:
      // ORDER: a,z,A,Z,0,9
      // ----------------------------
      ret = initialLoad
          .limitRange(numPartitions == 64 ? getPartitionRanges64() : getPartitionRanges16());
    } else if (initialLoad.isDB2OnZOS()) {
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

  public static List<Pair<String, String>> getCommonPartitionRanges16(
      @SuppressWarnings("rawtypes") AtomHibernate initialLoad) {
    return getCommonPartitionRanges(initialLoad, 16);
  }

  public static List<Pair<String, String>> getCommonPartitionRanges64(
      @SuppressWarnings("rawtypes") AtomHibernate initialLoad) {
    return getCommonPartitionRanges(initialLoad, 64);
  }

}
