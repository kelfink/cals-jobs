package gov.ca.cwds.jobs.component;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ParameterMode;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.procedure.ProcedureCall;

import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.DaoException;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.rep.CmsReplicatedEntity;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.neutron.enums.NeutronIntegerDefaults;

/**
 * Common functions and features for initial load.
 * 
 * @author CWDS API Team
 *
 * @param <T> normalized type
 */
public interface AtomInitialLoad<T extends PersistentObject> extends AtomShared {

  BaseDaoImpl<T> getJobDao();

  /**
   * Restrict initial load key ranges from command line.
   * 
   * @param allKeyPairs all key ranges for this job
   * @return list of key pairs to execute
   */
  default List<Pair<String, String>> limitRange(final List<Pair<String, String>> allKeyPairs) {
    List<Pair<String, String>> ret;
    final FlightPlan opts = getOpts();
    if (opts != null && opts.isRangeGiven()) {
      final List<Pair<String, String>> list = new ArrayList<>();

      final int start = ((int) opts.getStartBucket()) - 1;
      final int end = ((int) opts.getEndBucket()) - 1;

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
  default boolean isInitialLoadJdbc() {
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

  default Transaction getOrCreateTransaction() {
    Transaction txn = null;
    final Session session = getJobDao().getSessionFactory().getCurrentSession();
    try {
      txn = session.beginTransaction();
    } catch (Exception e) { // NOSONAR
      txn = session.getTransaction();
    }
    return txn;
  }

  /**
   * Source Materialized Query Table to be refreshed before running initial load.
   * 
   * @return MQT name
   */
  default String getMQTName() {
    return null;
  }

  default void refreshMQT() {
    if (getOpts().isRefreshMqt() && StringUtils.isNotBlank(getMQTName())) {
      final Session session = getJobDao().getSessionFactory().getCurrentSession();
      getOrCreateTransaction();
      final String schema =
          (String) session.getSessionFactory().getProperties().get("hibernate.default_schema");

      try {
        final ProcedureCall proc = session.createStoredProcedureCall(schema + ".SPREFRSMQT");
        proc.registerStoredProcedureParameter("MQTNAME", String.class, ParameterMode.IN);
        proc.registerStoredProcedureParameter("RETMESSAG", String.class, ParameterMode.OUT);

        proc.setParameter("MQTNAME", getMQTName());
        proc.execute();

        final String returnMsg = (String) proc.getOutputParameterValue("RETCODE");
        getLogger().info("stored proc: returnCode: {}", returnMsg);

        if (StringUtils.isNotBlank(returnMsg)) {
          getLogger().error("Stored Procedure return message: {}", returnMsg);
          throw new DaoException("Stored Procedure returned with ERROR - " + returnMsg);
        }

      } catch (DaoException h) {
        throw new DaoException("Call to Stored Procedure failed - " + h, h);
      }
    }
  }

}
