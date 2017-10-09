package gov.ca.cwds.dao.cms;

import java.util.Date;
import java.util.List;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import gov.ca.cwds.data.BaseDao;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.DaoException;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.BatchBucketDao;
import gov.ca.cwds.jobs.component.NeutronIntegerDefaults;

/**
 * Base class for DAO with some common methods.
 * 
 * @author CWDS API Team
 * @param <T> type of {@link PersistentObject}
 */
public abstract class BatchDaoImpl<T extends PersistentObject> extends BaseDaoImpl<T>
    implements BaseDao<T>, BatchBucketDao<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(BatchDaoImpl.class);

  /**
   * Constructor
   * 
   * @param sessionFactory The session factory
   */
  public BatchDaoImpl(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  /**
   * {@inheritDoc}
   * 
   * @see gov.ca.cwds.data.BaseDao#findAll()
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<T> findAll() {
    final String namedQueryName = constructNamedQueryName("findAll");
    Session session = getSessionFactory().getCurrentSession();

    Transaction txn = session.beginTransaction();
    try {
      final Query query = session.getNamedQuery(namedQueryName);
      ImmutableList.Builder<T> entities = new ImmutableList.Builder<>();
      entities.addAll(query.list());
      txn.commit();
      return entities.build();
    } catch (HibernateException h) {
      txn.rollback();
      throw new DaoException(h);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see gov.ca.cwds.data.BaseDao#findAllUpdatedAfter(java.util.Date)
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<T> findAllUpdatedAfter(Date datetime) {
    final String namedQueryName = constructNamedQueryName("findAllUpdatedAfter");
    Session session = getSessionFactory().getCurrentSession();

    Transaction txn = session.beginTransaction();
    final java.sql.Timestamp ts = new java.sql.Timestamp(datetime.getTime());
    try {
      // Cross platform DB2 (both z/OS and Linux).
      final Query query =
          session.getNamedQuery(namedQueryName).setCacheable(false).setFlushMode(FlushMode.MANUAL)
              .setReadOnly(true).setCacheMode(CacheMode.IGNORE).setTimestamp("after", ts);

      // Iterate, process, flush.
      query.setFetchSize(NeutronIntegerDefaults.DEFAULT_FETCH_SIZE.getValue());
      final ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);
      final ImmutableList.Builder<T> ret = new ImmutableList.Builder<>();
      int cnt = 0;

      while (results.next()) {
        Object[] row = results.get();
        ret.add((T) row[0]);

        if (((++cnt) % NeutronIntegerDefaults.DEFAULT_FETCH_SIZE.getValue()) == 0) {
          LOGGER.info("find updated after {}. recs read: {}", ts, cnt);
          session.flush();
        }
      }

      session.flush();
      results.close();
      txn.commit();
      return ret.build();

    } catch (HibernateException h) {
      txn.rollback();
      throw new DaoException(h);
    }
  }

  /**
   * Retrieve all records for batch processing for a single bucket. PostgreSQL queries would likely
   * rely on the <a href="https://www.postgresql.org/docs/9.6/static/functions-window.html">NTILE
   * analytic function</a>, whereas DB2 10.5, lacking modern analytics, would likely rely on nested
   * or correlated queries. Note that DB2 doesn't even provide basic pseudo-columns, like ROWNUM,
   * without enabling <a href=
   * "http://www.ibm.com/support/knowledgecenter/SSEPGG_10.5.0/com.ibm.db2.luw.apdv.porting.doc/doc/r0052867.html">
   * "compatibility vectors"</a> or writing a
   * <a href="http://hoteljavaopensource.blogspot.com/2011/06/ntile-and-db2.html">user-defined
   * function</a>.
   * 
   * <p>
   * The following DB2 SQL demonstrates how to simulate the NTILE analytic without installing
   * additional packages or non-standard functions.
   * </p>
   * 
   * <pre>
   * {@code 
   * select z.identifier, z.bucket, z.rn, row_number() over (partition by z.bucket order by 1) as bucket_rn 
   * from ( 
   *    select mod(y.rn,10) + 1 as bucket, y.rn, y.identifier 
   *    from (
   *       select row_number() over (order by 1) as rn, x.identifier 
   *       from ( select c.identifier from client_t c 
   *       WHERE x.identifier >= 'B3bMRWu8NW' and x.identifier < 'DW5GzxJ30A') x 
   *    ) y 
   * ) z 
   * where z.bucket = 3 for read only;
   * }
   * </pre>
   * 
   * <p>
   * Most batch bucket queries are defined Hibernate named queries in the persistence class itself.
   * </p>
   * 
   * @param bucketNum current bucket for this batch
   * @param totalBuckets total buckets for this batch run only, NOT the total for all batches
   * @param minId minimum key value, inclusive
   * @param maxId maximum key value, exclusive
   * @return ordered list of referral/client document records
   */
  @Override
  @SuppressWarnings("unchecked")
  public List<T> partitionedBucketList(long bucketNum, long totalBuckets, String minId,
      String maxId) {
    final String namedQueryName = getEntityClass().getName() + ".findPartitionedBuckets";
    final Session session = getSessionFactory().getCurrentSession();
    final Transaction txn = session.beginTransaction();

    try {
      final Query query = session.getNamedQuery(namedQueryName)
          .setInteger("bucket_num", (int) bucketNum).setInteger("total_buckets", (int) totalBuckets)
          .setString("min_id", minId).setString("max_id", maxId).setCacheable(false)
          .setFlushMode(FlushMode.MANUAL).setReadOnly(true).setCacheMode(CacheMode.IGNORE)
          .setFetchSize(NeutronIntegerDefaults.DEFAULT_FETCH_SIZE.getValue());

      // Iterate, process, flush.
      final ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);
      final ImmutableList.Builder<T> ret = new ImmutableList.Builder<>();
      int cnt = 0;

      while (results.next()) {
        Object[] row = results.get();
        ret.add((T) row[0]);

        if (((++cnt) % NeutronIntegerDefaults.DEFAULT_FETCH_SIZE.getValue()) == 0) {
          LOGGER.info("recs read: {}", cnt);
          session.flush();
        }
      }

      session.flush();
      results.close();
      txn.commit();
      return ret.build();
    } catch (HibernateException h) {
      txn.rollback();
      throw new DaoException(h);
    }
  }

  /**
   * Retrieve all records for batch processing for a single "bucket" (an arbitrary number of
   * records). PostgreSQL queries would likely rely on the
   * <a href="https://www.postgresql.org/docs/9.6/static/functions-window.html">NTILE analytic
   * function</a>, whereas DB2 10.5, lacking modern analytics, would likely rely on nested or
   * correlated queries. Note that DB2 doesn't even provide common pseudo-columns, like ROWNUM,
   * without enabling <a href=
   * "http://www.ibm.com/support/knowledgecenter/SSEPGG_10.5.0/com.ibm.db2.luw.apdv.porting.doc/doc/r0052867.html">
   * "compatibility vectors"</a> or writing a
   * <a href="http://hoteljavaopensource.blogspot.com/2011/06/ntile-and-db2.html">user-defined
   * function</a>.
   * 
   * <p>
   * The following DB2 SQL demonstrates how to simulate the NTILE analytic without installing
   * additional packages or non-standard functions.
   * </p>
   * 
   * <pre>
   * select z.identifier, z.bucket, z.rn, row_number() over (partition by z.bucket order by 1) as bucket_rn 
   * from ( 
   *    select mod(y.rn, :total_buckets) + 1 as bucket, y.rn, y.identifier 
   *    from (
   *       select row_number() over (order by 1) as rn, x.identifier 
   *       from ( select c.identifier from client_t c ) x 
   *    ) y 
   * ) z 
   * where z.bucket = :bucket_num for read only;
   * </pre>
   * 
   * <p>
   * Batch bucket queries are defined Hibernate named queries in the persistence class itself.
   * </p>
   * 
   * <p>
   * Since the large CMS DB2 tables are usually partitioned by primary key (column IDENTIFIER)
   * across 16 partitions, this method takes a minimum and maximum key to enhance performance via
   * partition pruning.
   * </p>
   * 
   * @param bucketNum current bucket for this batch
   * @param totalBuckets total buckets in batch run
   * @return ordered list of referral/client document records
   */
  @Override
  @SuppressWarnings("unchecked")
  public List<T> bucketList(long bucketNum, long totalBuckets) {
    final String namedQueryName = constructNamedQueryName("findAllByBucket");
    final Session session = getSessionFactory().getCurrentSession();
    final Transaction txn = session.beginTransaction();

    try {
      final Query query = session.getNamedQuery(namedQueryName).setCacheable(false)
          .setFlushMode(FlushMode.MANUAL).setReadOnly(true).setCacheMode(CacheMode.IGNORE)
          .setInteger("bucket_num", (int) bucketNum)
          .setInteger("total_buckets", (int) totalBuckets);

      // Iterate, process, flush.
      query.setFetchSize(NeutronIntegerDefaults.DEFAULT_FETCH_SIZE.getValue());
      final ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);
      ImmutableList.Builder<T> ret = new ImmutableList.Builder<>();
      int cnt = 0;

      while (results.next()) {
        Object[] row = results.get();
        ret.add((T) row[0]);

        if (((++cnt) % NeutronIntegerDefaults.DEFAULT_FETCH_SIZE.getValue()) == 0) {
          LOGGER.info("recs read: {}", cnt);
          session.flush();
        }
      }

      session.flush();
      results.close();
      txn.commit();
      return ret.build();

    } catch (HibernateException h) {
      txn.rollback();
      throw new DaoException(h);
    }
  }

  /**
   * Builds named query by the naming convention of "entity class.suffix".
   * 
   * @param suffix suffix of the named query
   * @return named query for lookup
   */
  private String constructNamedQueryName(String suffix) {
    return getEntityClass().getName() + "." + suffix;
  }

}
