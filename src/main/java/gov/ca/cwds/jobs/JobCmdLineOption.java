package gov.ca.cwds.jobs;

import org.apache.commons.cli.Option;

/**
 * Definitions of batch job command line options.
 * 
 * @author CWDS API Team
 */
public enum JobCmdLineOption {

  /**
   * ElasticSearch configuration file.
   */
  ES_CONFIG(JobOptions.makeOpt("c", JobOptions.CMD_LINE_ES_CONFIG, "ElasticSearch configuration file", true, 1, String.class, ',')),

  /**
   * Last run date file (yyyy-MM-dd HH:mm:ss)
   */
  LAST_RUN_FILE(JobOptions.makeOpt("l", JobOptions.CMD_LINE_LAST_RUN, "last run date file (yyyy-MM-dd HH:mm:ss)", false, 1, String.class, ',')),

  /**
   * Bucket range (-r 20-24).
   */
  BUCKET_RANGE(JobOptions.makeOpt("r", JobOptions.CMD_LINE_BUCKET_RANGE, "bucket range (-r 20-24)", false, 2, Integer.class, '-')),

  /**
   * Total buckets.
   */
  BUCKET_TOTAL(JobOptions.makeOpt("b", JobOptions.CMD_LINE_BUCKET_TOTAL, "total buckets", false, 1, Integer.class, ',')),

  /**
   * Number of threads (optional).
   */
  THREADS(JobOptions.makeOpt("t", JobOptions.CMD_LINE_THREADS, "# of threads", false, 1, Integer.class, ',')),

  /**
   * Minimum key, inclusive.
   */
  MIN_ID(JobOptions.makeOpt("m", JobOptions.CMD_LINE_MIN_ID, "minimum identifier, inclusive", false, 1, String.class, ',')),

  /**
   * Maximum key, inclusive.
   */
  MAX_ID(JobOptions.makeOpt("x", JobOptions.CMD_LINE_MAX_ID, "maximum identifier, exclusive", false, 1, String.class, ','));

  private final Option opt;

  JobCmdLineOption(Option opt) {
    this.opt = opt;
  }

  /**
   * Getter for the type's command line option definition.
   * 
   * @return command line option definition
   */
  public final Option getOpt() {
    return opt;
  }

}
