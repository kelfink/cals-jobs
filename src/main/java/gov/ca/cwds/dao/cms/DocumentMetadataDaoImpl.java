package gov.ca.cwds.dao.cms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import gov.ca.cwds.dao.DocumentMetadataDao;
import gov.ca.cwds.data.DaoException;
import gov.ca.cwds.data.model.cms.DocumentMetadata;
import gov.ca.cwds.inject.CmsSessionFactory;

/**
 * Implementation of {@link DocumentMetadataDao}, backed by Hibernate.
 * 
 * @author CWDS API Team
 */
@Singleton
public class DocumentMetadataDaoImpl implements DocumentMetadataDao {

  @SuppressWarnings("unused")
  private static final Logger LOGGER = LoggerFactory.getLogger(DocumentMetadataDaoImpl.class);

  private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH24:mm:ss";

  /**
   * WARNING: NOT thread safe!
   */
  private final DateFormat dateFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);

  private SessionFactory sessionFactory;

  /**
   * Constructor
   * 
   * @param sessionFactory the sessionFactory
   */
  @Inject
  public DocumentMetadataDaoImpl(@CmsSessionFactory SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  /**
   * {@inheritDoc}
   * 
   * @see DocumentMetadataDao#findByLastJobRunTimeMinusOneMinute(java.util.Date)
   */
  @SuppressWarnings({"unchecked"})
  @Override
  public List<DocumentMetadata> findByLastJobRunTimeMinusOneMinute(Date lastJobRunTime) {
    final Session session = sessionFactory.getCurrentSession();
    Transaction txn = session.beginTransaction();
    try {
      final NativeQuery<DocumentMetadata> query =
          session.getNamedNativeQuery("findByLastJobRunTimeMinusOneMinute").setParameter(
              "lastJobRunTime", dateFormat.format(lastJobRunTime), StringType.INSTANCE);
      final ImmutableList.Builder<DocumentMetadata> documentMetadatas =
          new ImmutableList.Builder<>();
      documentMetadatas.addAll(query.list());
      txn.commit();
      return documentMetadatas.build();
    } catch (HibernateException h) {
      txn.rollback();
      throw new DaoException(h);
    } finally {
      session.close();
    }
  }

}
