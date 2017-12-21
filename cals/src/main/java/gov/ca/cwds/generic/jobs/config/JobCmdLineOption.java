package gov.ca.cwds.generic.jobs.config;

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
  ES_CONFIG(JobOptions
      .makeOpt("c", JobOptions.CMD_LINE_ES_CONFIG, "ElasticSearch configuration file", true, 1, String.class, ',')),

  /**
   * ElasticSearch index name to create or use. If not provided then ES Config alias is used.
   */
  INDEX_NAME(JobOptions
      .makeOpt("i", JobOptions.CMD_LINE_INDEX_NAME, "ElasticSearch index name", false, 1, String.class, ',')),

  /**
   * Last run time in format 'yyyy-MM-dd HH:mm:ss'
   */
  LAST_RUN_TIME(JobOptions
      .makeOpt("a", JobOptions.CMD_LINE_LAST_RUN_TIME, "last run time (yyyy-MM-dd HH:mm:ss)", false, 1, String.class, ',')),

  /**
   * Last run date file (yyyy-MM-dd HH:mm:ss)
   */
  LAST_RUN_FILE(JobOptions
      .makeOpt("l", JobOptions.CMD_LINE_LAST_RUN_FILE, "last run date file (yyyy-MM-dd HH:mm:ss)", false, 1, String.class, ',')),

  /**
   * Alternate input file
   */
  ALT_INPUT_FILE(JobOptions
      .makeOpt("f", JobOptions.CMD_LINE_ALT_INPUT_FILE, "Alternate input file", false, 1, String.class, ',')),

  /**
   * Bucket range (-r 20-24).
   */
  BUCKET_RANGE(JobOptions
      .makeOpt("r", JobOptions.CMD_LINE_BUCKET_RANGE, "bucket range (-r 20-24)", false, 2, Integer.class, '-')),

  /**
   * Total buckets.
   */
  BUCKET_TOTAL(JobOptions
      .makeOpt("b", JobOptions.CMD_LINE_BUCKET_TOTAL, "total buckets", false, 1, Integer.class, ',')),

  /**
   * Number of threads (optional).
   */
  THREADS(JobOptions
      .makeOpt("t", JobOptions.CMD_LINE_THREADS, "# of threads", false, 1, Integer.class, ',')),

  /**
   * Minimum key, inclusive.
   */
  MIN_ID(JobOptions
      .makeOpt("m", JobOptions.CMD_LINE_MIN_ID, "minimum identifier, inclusive", false, 1, String.class, ',')),

  /**
   * Maximum key, inclusive.
   */
  MAX_ID(JobOptions
      .makeOpt("x", JobOptions.CMD_LINE_MAX_ID, "maximum identifier, exclusive", false, 1, String.class, ',')),

  /**
   * Indicate if sealed and sensitive data should be loaded
   */
  LOAD_SEALED_SENSITIVE(JobOptions.makeOpt("s", JobOptions.CMD_LINE_LOAD_SEALED_AND_SENSITIVE, "true or false - load sealed and sensitive data, default is false", false, 1, Boolean.class, ','));

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
