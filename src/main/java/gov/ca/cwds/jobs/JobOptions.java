package gov.ca.cwds.jobs;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents batch job options from the command line.
 * 
 * @author CWDS API Team
 */
public final class JobOptions implements Serializable {

  /**
   * Base serialization version. Increment by class change.
   */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LogManager.getLogger(JobOptions.class);

  static final String CMD_LINE_ES_CONFIG = "config";
  static final String CMD_LINE_LAST_RUN = "last-run-file";
  static final String CMD_LINE_BUCKET_RANGE = "bucket-range";
  static final String CMD_LINE_BUCKET_TOTAL = "total-buckets";
  static final String CMD_LINE_THREADS = "thread-num";
  static final String CMD_LINE_MIN_ID = "min_id";
  static final String CMD_LINE_MAX_ID = "max_id";

  /**
   * Location of Elasticsearch configuration file.
   */
  final String esConfigLoc;

  /**
   * Location of last run file.
   */
  final String lastRunLoc;

  /**
   * Whether to run in periodic "last run" mode or "initial" mode. Defaults to true.
   */
  final boolean lastRunMode;

  /**
   * When running in "initial load" mode, specifies the starting bucket of records to be processed
   * by this job.
   * <p>
   * Required for "initial load" mode.
   * </p>
   */
  private long startBucket;

  /**
   * When running in "initial load" mode, specifies the ending bucket of records to be processed by
   * this job.
   * <p>
   * Required for "initial load" mode.
   * </p>
   */
  private long endBucket;

  /**
   * When running in "initial load" mode, specifies the total number buckets for all related batch
   * runs.
   * <p>
   * Required for "initial load" mode.
   * </p>
   */
  private long totalBuckets = 10;

  /**
   * Total threads to allocate to this batch run. Defaults to all available cores.
   */
  private long threadCount;

  private String minId;

  private String maxId;

  /**
   * Construct from all settings.
   * 
   * @param esConfigLoc location of Elasticsearch configuration file
   * @param lastRunLoc location of last run file
   * @param lastRunMode is last run mode or not
   * @param startBucket starting bucket number
   * @param endBucket ending bucket number
   * @param totalBuckets total buckets
   * @param threadCount number of simultaneous threads
   */
  JobOptions(String esConfigLoc, String lastRunLoc, boolean lastRunMode, long startBucket,
      long endBucket, long totalBuckets, long threadCount, String minId, String maxId) {
    this.esConfigLoc = esConfigLoc;
    this.lastRunLoc = lastRunLoc;
    this.lastRunMode = lastRunMode;
    this.startBucket = startBucket;
    this.endBucket = endBucket;
    this.totalBuckets = totalBuckets;
    this.threadCount = threadCount;
    this.minId = minId;
    this.maxId = maxId;
  }

  /**
   * Getter for location of Elasticsearch configuration file.
   * 
   * @return location of Elasticsearch configuration file
   */
  public final String getEsConfigLoc() {
    return esConfigLoc;
  }

  /**
   * Getter for location of last run date/time file.
   * 
   * @return location of last run file
   */
  public final String getLastRunLoc() {
    return lastRunLoc;
  }

  /**
   * Getter for last run mode.
   * 
   * @return last run mode
   */
  public final boolean isLastRunMode() {
    return lastRunMode;
  }

  /**
   * Getter for starting bucket.
   * 
   * @return starting bucket
   */
  public final long getStartBucket() {
    return startBucket;
  }

  /**
   * Getter for last bucket.
   * 
   * @return last bucket
   */
  public final long getEndBucket() {
    return endBucket;
  }

  /**
   * Getter for total buckets.
   * 
   * @return total buckets
   */
  public final long getTotalBuckets() {
    return totalBuckets;
  }

  /**
   * Getter for thread count.
   * 
   * @return thread count
   */
  public final long getThreadCount() {
    return threadCount;
  }

  /**
   * Getter for minimum key value for this batch run
   * 
   * @return minimum key value for this batch run
   */
  public String getMinId() {
    return minId;
  }

  /**
   * Getter for maximum key value for this batch run
   * 
   * @return maximum key value for this batch run
   */
  public String getMaxId() {
    return maxId;
  }

  /**
   * Define a command line option.
   * 
   * @param shortOpt single letter option name
   * @param longOpt long option name
   * @param description option description
   * @return command line option
   */
  protected static Option makeOpt(String shortOpt, String longOpt, String description) {
    return Option.builder(shortOpt).argName(longOpt).longOpt(longOpt).desc(description)
        .numberOfArgs(0).build();
  }

  /**
   * Define a command line option.
   * 
   * @param shortOpt single letter option name
   * @param longOpt long option name
   * @param description option description
   * @param required true if required
   * @param argc number of arguments to this option
   * @param type arguments' Java class
   * @param sep argument separator
   * @return command line option
   */
  protected static Option makeOpt(String shortOpt, String longOpt, String description,
      boolean required, int argc, Class<?> type, char sep) {
    return Option.builder(shortOpt).argName(longOpt).required(required).longOpt(longOpt)
        .desc(description).numberOfArgs(argc).type(type).valueSeparator(sep).build();
  }

  /**
   * Define command line options.
   * 
   * @return command line option definitions
   */
  protected static Options buildCmdLineOptions() {
    Options ret = new Options();
    ret.addOption(JobCmdLineOption.ES_CONFIG.getOpt());
    ret.addOption(JobCmdLineOption.THREADS.getOpt());
    ret.addOption(JobCmdLineOption.BUCKET_RANGE.getOpt());
    ret.addOption(JobCmdLineOption.MIN_ID.getOpt());
    ret.addOption(JobCmdLineOption.MAX_ID.getOpt());

    // RUN MODE: mutually exclusive choice.
    OptionGroup group = new OptionGroup();
    group.setRequired(true);
    group.addOption(JobCmdLineOption.LAST_RUN_FILE.getOpt());
    group.addOption(JobCmdLineOption.BUCKET_TOTAL.getOpt());
    ret.addOptionGroup(group);

    return ret;
  }

  /**
   * Print usage.
   */
  protected static void printUsage() {
    try (final StringWriter sw = new StringWriter()) {
      new HelpFormatter().printHelp(new PrintWriter(sw), 100, "Batch loader",
          StringUtils.leftPad("", 90, '=') + "\nUSAGE: java <job class> ...\n"
              + StringUtils.leftPad("", 90, '='),
          buildCmdLineOptions(), 4, 8, StringUtils.leftPad("", 90, '='), true);
      LOGGER.error(sw.toString());
    } catch (IOException e) {
      throw new JobsException("ERROR PRINTING HELP! How ironic. :-)", e);
    }
  }

  /**
   * Parse the command line return the job settings.
   * 
   * @param args command line to parse
   * @return JobOptions defining this job
   * @throws JobsException if unable to parse command line
   */
  public static JobOptions parseCommandLine(String[] args) throws JobsException {
    String esConfigLoc = null;
    String lastRunLoc = null;
    boolean lastRunMode = false;
    long startBucket = 0L;
    long endBucket = 0L;
    long totalBuckets = 0L;
    long threadCount = 0L;

    String minId = " ";
    String maxId = "9999999999";

    try {
      Options options = buildCmdLineOptions();
      CommandLineParser parser = new DefaultParser();
      CommandLine cmd = parser.parse(options, args);

      // Java clincher: case statements only take constants. Even compile-time constants, like
      // enum members (evaluated at compile time), are not considered "constants."
      for (Option opt : cmd.getOptions()) {
        switch (opt.getArgName()) {
          case CMD_LINE_ES_CONFIG:
            LOGGER.info("ES config file  = " + opt.getValue());
            esConfigLoc = opt.getValue().trim();
            break;

          case CMD_LINE_LAST_RUN:
            lastRunMode = true;
            lastRunLoc = opt.getValue().trim();
            LOGGER.info("last run file = " + lastRunLoc);
            break;

          case CMD_LINE_BUCKET_TOTAL:
            LOGGER.info("INITIAL LOAD!");
            lastRunMode = false;
            totalBuckets = Long.parseLong(opt.getValue());
            break;

          case CMD_LINE_BUCKET_RANGE:
            lastRunMode = false;
            startBucket = Long.parseLong(opt.getValues()[0]);
            endBucket = Long.parseLong(opt.getValues()[1]);
            break;

          case CMD_LINE_THREADS:
            threadCount = Long.parseLong(opt.getValue());
            break;

          case CMD_LINE_MIN_ID:
            minId = opt.getValue().trim();
            break;

          case CMD_LINE_MAX_ID:
            maxId = opt.getValue().trim();
            break;

          default:
            break;
        }
      }
    } catch (NumberFormatException e) {
      printUsage();
      LOGGER.error("Invalid numeric argument: {}", e.getMessage(), e);
      throw new JobsException("Invalid numeric argument: " + e.getMessage(), e);
    } catch (ParseException e) {
      printUsage();
      LOGGER.error("Error parsing command line: {}", e.getMessage(), e);
      throw new JobsException("Error parsing command line: " + e.getMessage(), e);
    }

    return new JobOptions(esConfigLoc, lastRunLoc, lastRunMode, startBucket, endBucket,
        totalBuckets, threadCount, minId, maxId);
  }

  public void setStartBucket(long startBucket) {
    this.startBucket = startBucket;
  }

  public void setEndBucket(long endBucket) {
    this.endBucket = endBucket;
  }

  public void setThreadCount(long threadCount) {
    this.threadCount = threadCount;
  }

  public void setMinId(String minId) {
    this.minId = minId;
  }

  public void setMaxId(String maxId) {
    this.maxId = maxId;
  }

  public void setTotalBuckets(long totalBuckets) {
    this.totalBuckets = totalBuckets;
  }

}
