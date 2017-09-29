package gov.ca.cwds.jobs.component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import gov.ca.cwds.dao.cms.BatchDaoImpl;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.util.jdbc.JobDB2Utils;
import gov.ca.cwds.jobs.util.jdbc.JobJdbcUtils;
import gov.ca.cwds.jobs.util.jdbc.JobResultSetAware;

public interface JobAtomHibernate<T extends PersistentObject, M extends ApiGroupNormalizer<?>>
    extends JobAtomShared, JobResultSetAware<M> {

  static final int DEFAULT_BATCH_WAIT = 25;
  static final int SLEEP_MILLIS = 2500;
  static final int POLL_MILLIS = 3000;
  static final int DEFAULT_FETCH_SIZE = BatchDaoImpl.DEFAULT_FETCH_SIZE;

  static final String SQL_COLUMN_AFTER = "after";

  /**
   * @return default CMS schema name
   */
  default String getDBSchemaName() {
    return System.getProperty("DB_CMS_SCHEMA");
  }

  /**
   * @return default CMS schema name
   */
  static String databaseSchemaName() {
    return System.getProperty("DB_CMS_SCHEMA");
  }

  /**
   * @return the job's main DAO
   */
  public BaseDaoImpl<T> getJobDao();

  /**
   * Identifier column for this table. Defaults to "IDENTIFIER", the most common key name in legacy
   * DB2.
   * 
   * @return Identifier column
   */
  default String getIdColumn() {
    return "IDENTIFIER";
  }

  /**
   * Get the legacy source table for this job, if any.
   * 
   * @return legacy source table
   * @deprecated Logic moved to ApiLegacyAware implementation classes
   */
  @Deprecated
  default String getLegacySourceTable() {
    return null;
  }

  /**
   * Get the table or view used to allocate bucket ranges. Called on full load only.
   * 
   * @return the table or view used to allocate bucket ranges
   */
  default String getDriverTable() {
    String ret = null;
    final Table tbl = getJobDao().getEntityClass().getDeclaredAnnotation(Table.class);
    if (tbl != null) {
      ret = tbl.name();
    }

    return ret;
  }

  /**
   * Optional method to customize JDBC ORDER BY clause on initial load.
   * 
   * @return custom ORDER BY clause for JDBC query
   */
  default String getJdbcOrderBy() {
    return null;
  }

  /**
   * Return SQL to run before SELECTing from a last change view.
   * 
   * @return prep SQL
   */
  default String getPrepLastChangeSQL() {
    return null;
  }

  @Override
  default M extract(final ResultSet rs) throws SQLException {
    return null;
  }

  /**
   * @see JobDB2Utils#isDB2OnZOS(BaseDaoImpl)
   * @return true if DB2 on mainframe
   */
  default boolean isDB2OnZOS() {
    return JobDB2Utils.isDB2OnZOS(getJobDao());
  }

  /**
   * Execute JDBC prior to calling method
   * {@link BasePersonIndexerJob#pullBucketRange(String, String)}.
   * 
   * <blockquote>
   * 
   * <pre>
   * final Work work = new Work() {
   *   &#64;Override
   *   public void execute(Connection connection) throws SQLException {
   *     // Run JDBC here.
   *   }
   * };
   * session.doWork(work);
   * </pre>
   * 
   * </blockquote>
   * 
   * @param session current Hibernate session
   * @param txn current transaction
   * @param lastRunTime last successful run datetime
   * @throws SQLException on disconnect, invalid parameters, etc.
   */
  default void prepHibernateLastChange(final Session session, final Transaction txn,
      final Date lastRunTime) throws SQLException {
    if (StringUtils.isNotBlank(getPrepLastChangeSQL())) {
      JobJdbcUtils.prepHibernateLastChange(session, txn, lastRunTime, getPrepLastChangeSQL());
    }
  }

}
