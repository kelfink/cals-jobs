package gov.ca.cwds.jobs.util.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

  private static final String Z_OS_START = "aaaaaaaaaa";
  private static final String Z_OS_END = "9999999999";

  private static final String[] BASE_PARTITIONS = {"ACZAVXC2PZ", "A6y48OH3Ut", "BzaK4AmDfS",
      "B26CwOn9qX", "CvIYhuyCJt", "CY8r2uwEBI", "DsNAQ1W4l6", "DWQGOmFAzi", "EpKrO9a9xb",
      "ESH4wdQ02u", "Fmy2cMI0kW", "FN4JbQ0BFV", "GjekFiPEuJ", "GMSl4aH4wm", "HfvKXzA7rJ",
      "HJtGK9J07S", "IbMKNtL5Dk", "IEwHjr9FXO", "I6RlOno3qC", "JBHjcBU74E", "J5p3IOTEaC",
      "Kybpi693es", "K1KqHJ7AUR", "LuIBpq0Ftl", "LXN8vc87GE", "Mq5VGFd5Tk", "MUOYtgq2Mq",
      "NnZWsz1AzZ", "NRQqLqM5xF", "OjJmdJW5Dm", "ONwCecl5DQ", "Pf5jLGj9UX", "PI0rahL9yH",
      "Qd7xBSt3sQ", "QG0okqi0AR", "RaKSN5wCaR", "REwFaZn4iv", "R62gv73C4Z", "SzBubpb5sR",
      "S2pw19Q2Br", "Txj1LpTFgm", "T0eZIQpDjK", "0fXmBiY5Ch", "0JGoWelDYN", "1a6ExS95Ch",
      "1Fbwaaw3b4", "17PbXTH73o", "2AzE4ua8rX", "23rHzlZ30A", "3yq0BFP7Nw", "32qlKK2ICi",
      "4u0U0MECwr", "4YRF9Dd70O", "5rOfwNO3gC", "5T7PS2j37S", "6oipPRSKDX", "6R6kaia0SL",
      "7ki4MYoAzi", "7NYwtxJ7Lu", "8guC2hG4ak", "8JpJrxB37S", "9cRG3VmH6i", "9GwwRzY7D3", Z_OS_END};

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

  private static <T> Function<T, Stream<T>> everyNth(int n) {
    return new Function<T, Stream<T>>() {
      int i = 0;

      @Override
      public Stream<T> apply(T t) {
        if (i++ % n == 0) {
          return Stream.of(t);
        }
        return Stream.empty();
      }
    };
  }

  private static List<Pair<String, String>> buildPartitionsRanges(int partitions) {
    final int len = BASE_PARTITIONS.length;
    final int skip = len / partitions;
    return IntStream.rangeClosed(0, BASE_PARTITIONS.length - 1).boxed().flatMap(everyNth(skip))
        .collect(Collectors.toList()).stream().sorted().sequential()
        .map(i -> Pair.of(i > 0 ? BASE_PARTITIONS[i - 1] : Z_OS_START, BASE_PARTITIONS[i]))
        .collect(Collectors.toList());
  }

  public static List<Pair<String, String>> getPartitionRanges64() {
    return buildPartitionsRanges(64);
  }

  public static List<Pair<String, String>> getPartitionRanges16() {
    return buildPartitionsRanges(16);
  }

  public static List<Pair<String, String>> getPartitionRanges4() {
    return buildPartitionsRanges(4);
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
      ret = initialLoad.limitRange(buildPartitionsRanges(numPartitions));
    } else if (initialLoad.isDB2OnZOS()) {
      // ----------------------------
      // z/OS, small data set:
      // ORDER: a,z,A,Z,0,9
      // ----------------------------
      ret.add(Pair.of(Z_OS_START, Z_OS_END));
    } else {
      // ----------------------------
      // Linux or small data set:
      // ORDER: 0,9,a,A,z,Z
      // ----------------------------
      ret.add(Pair.of("0000000000", "ZZZZZZZZZZ"));
    }

    return ret;
  }

  public static List<Pair<String, String>> getCommonPartitionRanges4(
      @SuppressWarnings("rawtypes") AtomHibernate initialLoad) {
    return getCommonPartitionRanges(initialLoad, 4);
  }

  public static List<Pair<String, String>> getCommonPartitionRanges16(
      @SuppressWarnings("rawtypes") AtomHibernate initialLoad) {
    return getCommonPartitionRanges(initialLoad, 16);
  }

  public static List<Pair<String, String>> getCommonPartitionRanges64(
      @SuppressWarnings("rawtypes") AtomHibernate initialLoad) {
    return getCommonPartitionRanges(initialLoad, 64);
  }

  public static void main(String[] args) {
    final List<Pair<String, String>> result = buildPartitionsRanges(4);
    System.out.println("result size: " + result.size());
    System.out.println(result);
  }

}
