package gov.ca.cwds.jobs.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.jobs.exception.JobsException;

/**
 * Represents batch job options from the command line.
 * 
 * @author CWDS API Team
 */
public class JobOptions implements ApiMarker {

  /**
   * Base serialization version. Increment by class change.
   */
  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(JobOptions.class);

  public static final String CMD_LINE_ES_CONFIG = "config";
  public static final String CMD_LINE_INDEX_NAME = "index-name";
  public static final String CMD_LINE_LAST_RUN_TIME = "last-run-time";
  public static final String CMD_LINE_LAST_RUN_FILE = "last-run-file";
  public static final String CMD_LINE_ALT_INPUT_FILE = "alt-input-file";
  public static final String CMD_LINE_BUCKET_RANGE = "bucket-range";
  public static final String CMD_LINE_BUCKET_TOTAL = "total-buckets";
  public static final String CMD_LINE_THREADS = "thread-num";
  public static final String CMD_LINE_MIN_ID = "min_id";
  public static final String CMD_LINE_MAX_ID = "max_id";
  public static final String CMD_LINE_LOAD_SEALED_AND_SENSITIVE = "load-sealed-sensitive";

  /**
   * Location of Elasticsearch configuration file.
   */
  final String esConfigLoc;

  /**
   * Name of index to create or use. If this is not provided then alias is used from ES Config file.
   */
  private String indexName;

  /**
   * Last time job was executed in format 'yyyy-MM-dd HH.mm.ss' If this is provided then time stamp
   * given in last run time file is ignored.
   */
  private final Date lastRunTime;

  /**
   * Location of last run file.
   */
  final String lastRunLoc;

  /**
   * Location of alternate input file.
   */
  String altInputFile = "junk";

  /**
   * Whether to run in periodic "last run" mode or "initial" mode. Defaults to true.
   */
  boolean lastRunMode;

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
  private long totalBuckets;

  /**
   * Total threads to allocate to this batch run. Defaults to all available cores.
   */
  private long threadCount;

  private String minId;

  private String maxId;

  /**
   * If true then load sealed and sensitive data.
   */
  private boolean loadSealedAndSensitive;

  private boolean rangeGiven;

  /**
   * Construct from all settings.
   * 
   * @param esConfigLoc location of Elasticsearch configuration file
   * @param indexName Name of index to use. If not provided then alias is used from es config.
   * @param lastRunTime Last run time to use
   * @param lastRunLoc location of last run file
   * @param lastRunMode is last run mode or not
   * @param startBucket starting bucket number
   * @param endBucket ending bucket number
   * @param totalBuckets total buckets
   * @param threadCount number of simultaneous threads
   * @param loadSealedAndSensitive If true then load sealed and sensitive data
   * @param altInputFile alternate input file
   */
  JobOptions(String esConfigLoc, String indexName, Date lastRunTime, String lastRunLoc,
      boolean lastRunMode, long startBucket, long endBucket, long totalBuckets, long threadCount,
      String minId, String maxId, boolean loadSealedAndSensitive, String altInputFile,
      boolean rangeGiven) {
    this.esConfigLoc = esConfigLoc;
    this.indexName = StringUtils.isBlank(indexName) ? null : indexName;
    this.lastRunTime = lastRunTime;
    this.lastRunLoc = lastRunLoc;
    this.lastRunMode = lastRunMode;
    this.startBucket = startBucket;
    this.endBucket = endBucket;
    this.totalBuckets = totalBuckets;
    this.threadCount = threadCount;
    this.minId = minId;
    this.maxId = maxId;
    this.loadSealedAndSensitive = loadSealedAndSensitive;
    this.altInputFile = altInputFile;
    this.rangeGiven = rangeGiven;
  }

  /**
   * Getter for location of Elasticsearch configuration file.
   * 
   * @return location of Elasticsearch configuration file
   */
  public String getEsConfigLoc() {
    return esConfigLoc;
  }

  /**
   * Get name of the index to create or use.
   * 
   * @return Name of the index to use.
   */
  public String getIndexName() {
    return indexName;
  }

  /**
   * Get last run time override in format 'yyyy-MM-dd HH.mm.ss'. If this is non-null then time
   * provided in last run time file is ignored.
   * 
   * @return Last run time
   */
  public Date getLastRunTime() {
    return lastRunTime;
  }

  /**
   * Getter for location of last run date/time file.
   * 
   * @return location of last run file
   */
  public String getLastRunLoc() {
    return lastRunLoc;
  }

  /**
   * Getter for last run mode.
   * 
   * @return last run mode
   */
  public boolean isLastRunMode() {
    return lastRunMode;
  }

  /**
   * Getter for starting bucket.
   * 
   * @return starting bucket
   */
  public long getStartBucket() {
    return startBucket;
  }

  /**
   * Getter for last bucket.
   * 
   * @return last bucket
   */
  public long getEndBucket() {
    return endBucket;
  }

  /**
   * Getter for total buckets.
   * 
   * @return total buckets
   */
  public long getTotalBuckets() {
    return totalBuckets;
  }

  /**
   * Getter for thread count.
   * 
   * @return thread count
   */
  public long getThreadCount() {
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
   * Get if sealed and sensitive data should be loaded.
   * 
   * @return true if sealed and sensitive data should be loaded, false otherwise.
   */
  public boolean isLoadSealedAndSensitive() {
    return loadSealedAndSensitive;
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
    ret.addOption(JobCmdLineOption.INDEX_NAME.getOpt());
    ret.addOption(JobCmdLineOption.LAST_RUN_TIME.getOpt());
    ret.addOption(JobCmdLineOption.THREADS.getOpt());
    ret.addOption(JobCmdLineOption.BUCKET_RANGE.getOpt());
    ret.addOption(JobCmdLineOption.MIN_ID.getOpt());
    ret.addOption(JobCmdLineOption.MAX_ID.getOpt());
    ret.addOption(JobCmdLineOption.LOAD_SEALED_SENSITIVE.getOpt());
    ret.addOption(JobCmdLineOption.ALT_INPUT_FILE.getOpt());

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
      final String msg = "ERROR PRINTING HELP! How ironic. :-)";
      LOGGER.error(msg, e);
      throw new JobsException(msg, e);
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
    String indexName = null;
    Date lastRunTime = null;
    String lastRunLoc = null;
    String altInputLoc = "junk";
    boolean lastRunMode = false;
    long startBucket = 0L;
    long endBucket = 0L;
    long totalBuckets = 0L;
    long threadCount = 0L;
    boolean loadSealedAndSensitive = false;
    boolean rangeGiven = false;

    String minId = " ";
    String maxId = "9999999999";

    try {
      final Options options = buildCmdLineOptions();
      final CommandLineParser parser = new DefaultParser();
      final CommandLine cmd = parser.parse(options, args);

      // Java clincher: case statements only take constants. Even compile-time constants, like
      // enum members (evaluated at compile time), are not considered "constants."
      for (Option opt : cmd.getOptions()) {
        switch (opt.getArgName()) {
          case CMD_LINE_ES_CONFIG:
            esConfigLoc = opt.getValue().trim();
            break;

          case CMD_LINE_INDEX_NAME:
            indexName = opt.getValue().trim();
            LOGGER.info("index name = " + indexName);
            break;

          case CMD_LINE_LAST_RUN_TIME:
            lastRunMode = true;
            String lastRunTimeStr = opt.getValue().trim();
            lastRunTime = createDate(lastRunTimeStr);
            break;

          case CMD_LINE_ALT_INPUT_FILE:
            altInputLoc = opt.getValue().trim();
            break;

          case CMD_LINE_LAST_RUN_FILE:
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
            rangeGiven = true;
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

          case CMD_LINE_LOAD_SEALED_AND_SENSITIVE:
            loadSealedAndSensitive = Boolean.parseBoolean(opt.getValue().trim());
            break;

          default:
            break;
        }
      }
    } catch (Exception e) { // NOSONAR
      printUsage();
      LOGGER.error("Error parsing command line: {}", e.getMessage(), e);
      throw new JobsException("Error parsing command line: " + e.getMessage(), e);
    }

    return new JobOptions(esConfigLoc, indexName, lastRunTime, lastRunLoc, lastRunMode, startBucket,
        endBucket, totalBuckets, threadCount, minId, maxId, loadSealedAndSensitive, altInputLoc,
        rangeGiven);
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

  public void setIndexName(String indexName) {
    this.indexName = indexName;
  }

  public String getAltInputFile() {
    return altInputFile;
  }

  private static Date createDate(String timestamp) throws java.text.ParseException {
    Date date = null;
    String trimTimestamp = StringUtils.trim(timestamp);
    if (StringUtils.isNotEmpty(trimTimestamp)) {
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      date = df.parse(trimTimestamp);
    }
    return date;
  }

  public boolean isRangeGiven() {
    return rangeGiven;
  }

  public void setLoadSealedAndSensitive(boolean loadSealedAndSensitive) {
    this.loadSealedAndSensitive = loadSealedAndSensitive;
  }

  public void setRangeGiven(boolean rangeGiven) {
    this.rangeGiven = rangeGiven;
  }

  public void setLastRunMode(boolean flag) {
    this.lastRunMode = flag;
  }

}
