package gov.ca.cwds.jobs.config;

import org.apache.commons.cli.Option;

import gov.ca.cwds.neutron.flight.FlightPlan;

/**
 * Neutron command line options.
 * 
 * @author CWDS API Team
 */
public enum CmdLineOption {

  /**
   * ElasticSearch configuration file.
   */
  ES_CONFIG(FlightPlan.makeOpt("c", FlightPlan.CMD_LINE_ES_CONFIG, "ElasticSearch configuration file", true, 1, String.class, ',')),

  /**
   * ElasticSearch index name to create or use. If not provided then ES Config alias is used.
   */
  INDEX_NAME(FlightPlan.makeOpt("i", FlightPlan.CMD_LINE_INDEX_NAME, "ElasticSearch index name", false, 1, String.class, ',')),

  /**
   * Last run time in format 'yyyy-MM-dd HH:mm:ss'
   */
  LAST_RUN_TIME(FlightPlan.makeOpt("a", FlightPlan.CMD_LINE_LAST_RUN_TIME, "last run time (yyyy-MM-dd HH:mm:ss)", false, 1, String.class, ',')),

  /**
   * Last run date file (yyyy-MM-dd HH:mm:ss)
   */
  LAST_RUN_FILE(FlightPlan.makeOpt("l", FlightPlan.CMD_LINE_LAST_RUN_FILE, "last run date file (yyyy-MM-dd HH:mm:ss)", false, 1, String.class, ',')),

  /**
   * Alternate input file
   */
  BASE_DIRECTORY(FlightPlan.makeOpt("b", FlightPlan.CMD_LINE_BASE_DIRECTORY, "base directory", false, 1, String.class, ',')),

  /**
   * Bucket range (-r 20-24).
   */
  BUCKET_RANGE(FlightPlan.makeOpt("r", FlightPlan.CMD_LINE_BUCKET_RANGE, "bucket range (-r 20-24)", false, 2, Integer.class, '-')),

  /**
   * Total buckets.
   */
  BUCKET_TOTAL(FlightPlan.makeOpt("B", FlightPlan.CMD_LINE_BUCKET_TOTAL, "total buckets", false, 1, Integer.class, ',')),

  /**
   * Number of threads (optional).
   */
  THREADS(FlightPlan.makeOpt("t", FlightPlan.CMD_LINE_THREADS, "# of threads", false, 1, Integer.class, ',')),

  /**
   * Minimum key, inclusive.
   */
  MIN_ID(FlightPlan.makeOpt("m", FlightPlan.CMD_LINE_MIN_ID, "minimum identifier, inclusive", false, 1, String.class, ',')),

  /**
   * Maximum key, inclusive.
   */
  MAX_ID(FlightPlan.makeOpt("x", FlightPlan.CMD_LINE_MAX_ID, "maximum identifier, exclusive", false, 1, String.class, ',')),

  /**
   * Indicate if sealed and sensitive data should be loaded
   */
  LOAD_SEALED_SENSITIVE(FlightPlan.makeOpt("s", FlightPlan.CMD_LINE_LOAD_SEALED_AND_SENSITIVE, "true or false - load sealed and sensitive data, default is false", false, 1, Boolean.class, ',')),

  /**
   * Run full (initial) load.
   */
  FULL_LOAD(FlightPlan.makeOpt("F", FlightPlan.CMD_LINE_INITIAL_LOAD, "Run full (initial) load", false, 0, Boolean.class, ',')),

  /**
   * Refresh materialized query tables for full (initial) load.
   */
  REFRESH_MQT(FlightPlan.makeOpt("M", FlightPlan.CMD_LINE_REFRESH_MQT, "Refresh MQT for initial load", false, 0, Boolean.class, ',')),

  /**
   * Drop index before running full (initial) load.
   */
  DROP_INDEX(FlightPlan.makeOpt("D", FlightPlan.CMD_LINE_DROP_INDEX, "Drop index for full (initial) load", false, 0, Boolean.class, ',')),

  /**
   * Test mode!
   */
  SIMULATE_LAUNCH(FlightPlan.makeOpt("S", FlightPlan.CMD_LINE_SIMULATE_LAUNCH, "Simulate launch (test mode)", false, 0, Boolean.class, ','))

  ;

  private final Option opt;

  CmdLineOption(Option opt) {
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
