package gov.ca.cwds.jobs.component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import gov.ca.cwds.dao.cms.BatchDaoImpl;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.persistence.cms.rep.CmsReplicatedEntity;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.util.JobLogs;
import gov.ca.cwds.jobs.util.jdbc.JobDB2Utils;
import gov.ca.cwds.jobs.util.jdbc.JobJdbcUtils;
import gov.ca.cwds.jobs.util.jdbc.JobResultSetAware;
import gov.ca.cwds.jobs.util.transform.EntityNormalizer;

public interface JobFeatureHibernate<T extends PersistentObject, M extends ApiGroupNormalizer<?>>
    extends JobShared, JobResultSetAware<M> {

  static final int DEFAULT_BATCH_WAIT = 25;
  static final int DEFAULT_BUCKETS = 1;

  static final int ES_BULK_SIZE = 5000;
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
   * Mark a record for deletion. Intended for replicated records with deleted flag.
   * 
   * @param t bean to check
   * @return true if marked for deletion
   */
  default boolean isDelete(T t) {
    return t instanceof CmsReplicatedEntity ? CmsReplicatedEntity.isDelete((CmsReplicatedEntity) t)
        : false;
  }

  /**
   * Return the job's entity class for its de-normalized source view or materialized query table, if
   * any, or null if not using a de-normalized source.
   * 
   * @return entity class of view or materialized query table
   */
  default Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    return null;
  }

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
   * Override to customize the default number of buckets by job.
   * 
   * @return default total buckets
   */
  default int getJobTotalBuckets() {
    return DEFAULT_BUCKETS;
  }

  /**
   * True if the Job class reduces de-normalized results to normalized ones.
   * 
   * @return true if class overrides {@link #normalize(List)}
   */
  default boolean isViewNormalizer() {
    return getDenormalizedClass() != null;
  }

  /**
   * Default normalize method just returns the input. Child classes may customize this method to
   * normalize de-normalized result sets (view records) to normalized entities (parent/child)
   * records.
   * 
   * @param recs entity records
   * @return unmodified entity records
   * @see EntityNormalizer
   */
  @SuppressWarnings("unchecked")
  default List<T> normalize(List<M> recs) {
    return (List<T>) recs;
  }

  default void incrementNormalizeCount() {
    JobLogs.logEvery(getTrack().trackNormalized(), "Normalize", "single");
  }

  /**
   * Normalize view records for a single grouping (such as all the same client) into a normalized
   * entity bean, consisting of a parent object and its child objects.
   * 
   * @param recs de-normalized view beans
   * @return normalized entity bean instance
   */
  default T normalizeSingle(List<M> recs) {
    incrementNormalizeCount();
    final List<T> list = normalize(recs);
    return list != null && !list.isEmpty() ? list.get(0) : null;
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
