package gov.ca.cwds.generic.jobs.component;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.generic.data.persistence.cms.rep.CmsReplicatedEntity;
import gov.ca.cwds.generic.jobs.config.JobOptions;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common functions and features for initial load.
 * 
 * @author CWDS API Team
 *
 * @param <T> normalized type
 */
public interface AtomInitialLoad<T extends PersistentObject> extends AtomShared {

  static final Logger LOGGER = LoggerFactory.getLogger(AtomInitialLoad.class);

  /**
   * Restrict initial load key ranges from command line.
   * 
   * @param allKeyPairs all key ranges for this job
   * @return list of key pairs to execute
   */
  default List<Pair<String, String>> limitRange(final List<Pair<String, String>> allKeyPairs) {
    List<Pair<String, String>> ret;
    final JobOptions opts = getOpts();
    if (opts != null && opts.isRangeGiven()) {
      final List<Pair<String, String>> list = new ArrayList<>();

      final int start = ((int) opts.getStartBucket()) - 1;
      final int end = ((int) opts.getEndBucket()) - 1;

      LOGGER.info("Limit key range to: {} to {}", start + 1, end + 1);
      for (int i = start; i <= end; i++) {
        list.add(allKeyPairs.get(i));
      }

      ret = list;
    } else {
      ret = allKeyPairs;
    }

    return ret;
  }

  /**
   * @return true if the job provides its own key ranges
   */
  default boolean providesInitialKeyRanges() {
    return false;
  }

  /**
   * Get the view or materialized query table name, if used. Any child classes relying on a
   * de-normalized view must define the name.
   * 
   * @return name of view or materialized query table or null if none
   */
  default String getInitialLoadViewName() {
    return null;
  }

  /**
   * Get initial load SQL query.
   * 
   * @param dbSchemaName The DB schema name
   * @return Initial load query
   */
  default String getInitialLoadQuery(String dbSchemaName) {
    return null;
  }

  /**
   * Override to customize the default number of buckets by job.
   * 
   * @return default total buckets
   */
  default int getJobTotalBuckets() {
    return NeutronIntegerDefaults.DEFAULT_BUCKETS.getValue();
  }

  /**
   * Mark a record for deletion. Intended for replicated records with deleted flag.
   * 
   * @param t bean to check
   * @return true if marked for deletion
   */
  default boolean isDelete(T t) {
    return t instanceof CmsReplicatedEntity ? CmsReplicatedEntity.isDelete((CmsReplicatedEntity) t)
        : false;
  }

}
