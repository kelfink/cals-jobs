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

import gov.ca.cwds.jobs.BasePersonIndexerJob.JobCmdLineOption;

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
  private final long startBucket;

  /**
   * When running in "initial load" mode, specifies the ending bucket of records to be processed by
   * this job.
   * <p>
   * Required for "initial load" mode.
   * </p>
   */
  private final long endBucket;

  /**
   * When running in "initial load" mode, specifies the total number buckets for all related batch
   * runs.
   * <p>
   * Required for "initial load" mode.
   * </p>
   */
  private final long totalBuckets;

  /**
   * Total threads to allocate to this batch run. Defaults to all available cores.
   */
  private final long threadCount;

  private JobOptions(String esConfigLoc, String lastRunLoc, boolean lastRunMode, long startBucket,
      long endBucket, long totalBuckets, long threadCount) {
    this.esConfigLoc = esConfigLoc;
    this.lastRunLoc = lastRunLoc;
    this.lastRunMode = lastRunMode;
    this.startBucket = startBucket;
    this.endBucket = endBucket;
    this.totalBuckets = totalBuckets;
    this.threadCount = threadCount;
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
      BasePersonIndexerJob.LOGGER.error(sw.toString());
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

    try {
      Options options = buildCmdLineOptions();
      CommandLineParser parser = new DefaultParser();
      CommandLine cmd = parser.parse(options, args);

      // Java clincher: case statements only take constants. Even compile-time constants, like
      // enum members (evaluated at compile time), are not considered "constants."
      for (Option opt : cmd.getOptions()) {
        switch (opt.getArgName()) {
          case BasePersonIndexerJob.CMD_LINE_ES_CONFIG:
            BasePersonIndexerJob.LOGGER.info("ES config file  = " + opt.getValue());
            esConfigLoc = opt.getValue().trim();
            break;

          case BasePersonIndexerJob.CMD_LINE_LAST_RUN:
            lastRunMode = true;
            lastRunLoc = opt.getValue().trim();
            BasePersonIndexerJob.LOGGER.info("last run file = " + lastRunLoc);
            break;

          case BasePersonIndexerJob.CMD_LINE_BUCKET_TOTAL:
            BasePersonIndexerJob.LOGGER.info("INITIAL LOAD!");
            lastRunMode = false;
            totalBuckets = Long.parseLong(opt.getValue());
            break;

          case BasePersonIndexerJob.CMD_LINE_BUCKET_RANGE:
            lastRunMode = false;
            startBucket = Long.parseLong(opt.getValues()[0]);
            endBucket = Long.parseLong(opt.getValues()[1]);
            break;

          case BasePersonIndexerJob.CMD_LINE_THREADS:
            threadCount = Long.parseLong(opt.getValue());
            break;

          default:
            break;
        }
      }
    } catch (ParseException e) {
      printUsage();
      BasePersonIndexerJob.LOGGER.error("Error parsing command line: {}", e.getMessage(), e);
      throw new JobsException("Error parsing command line: " + e.getMessage(), e);
    }

    return new JobOptions(esConfigLoc, lastRunLoc, lastRunMode, startBucket, endBucket,
        totalBuckets, threadCount);
  }

}
