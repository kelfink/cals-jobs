package gov.ca.cwds.dao.cms;

import java.util.Date;
import java.util.List;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.type.TimestampType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import gov.ca.cwds.data.BaseDao;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.DaoException;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.BatchBucketDao;
import gov.ca.cwds.neutron.enums.NeutronIntegerDefaults;

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
    final String namedQueryName = constructNamedQueryWithSuffix("findAll");
    Session session = getSessionFactory().getCurrentSession();
    Transaction txn = session.beginTransaction();

    try {
      @SuppressWarnings("rawtypes")
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
    final String namedQueryName = constructNamedQueryWithSuffix("findAllUpdatedAfter");
    final Session session = getSessionFactory().getCurrentSession();
    final Transaction txn = session.beginTransaction();
    final java.sql.Timestamp ts = new java.sql.Timestamp(datetime.getTime());
    try {
      // Cross platform DB2 (both z/OS and Linux).
      final Query<T> query = session.getNamedQuery(namedQueryName).setCacheable(false)
          .setHibernateFlushMode(FlushMode.MANUAL).setReadOnly(true).setCacheMode(CacheMode.IGNORE)
          .setParameter("after", ts, TimestampType.INSTANCE);

      // Iterate, process, flush.
      query.setFetchSize(NeutronIntegerDefaults.FETCH_SIZE.getValue());
      final ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);
      final ImmutableList.Builder<T> ret = new ImmutableList.Builder<>();
      int cnt = 0;

      while (results.next()) {
        Object[] row = results.get();
        for (Object obj : row) {
          ret.add((T) obj);
        }

        if (++cnt % NeutronIntegerDefaults.FETCH_SIZE.getValue() == 0) {
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
   * Builds named query by the naming convention of "entity class.suffix".
   * 
   * @param suffix suffix of the named query
   * @return named query for lookup
   */
  private String constructNamedQueryWithSuffix(String suffix) {
    return getEntityClass().getName() + "." + suffix;
  }

}
