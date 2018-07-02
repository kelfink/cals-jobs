package gov.ca.cwds.jobs.common.mode;

/**
 * Created by Alexander Serbin on 4/6/2018.
 */
public enum DefaultJobMode implements JobMode {
  INITIAL_LOAD,
  INITIAL_LOAD_RESUME,
  INCREMENTAL_LOAD
}
