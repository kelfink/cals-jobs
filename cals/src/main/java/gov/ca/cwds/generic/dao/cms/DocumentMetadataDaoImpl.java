package gov.ca.cwds.generic.dao.cms;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import gov.ca.cwds.data.DaoException;
import gov.ca.cwds.generic.dao.DocumentMetadataDao;
import gov.ca.cwds.generic.data.model.cms.DocumentMetadata;
import gov.ca.cwds.inject.CmsSessionFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link DocumentMetadataDao}, backed by Hibernate.
 * 
 * @author CWDS API Team
 */
@Singleton
public class DocumentMetadataDaoImpl implements DocumentMetadataDao {

  @SuppressWarnings("unused")
  private static final Logger LOGGER = LoggerFactory.getLogger(
      DocumentMetadataDaoImpl.class);

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
   * @see DocumentMetadataDao#findByLastJobRunTimeMinusOneMinute(Date)
   */
  @SuppressWarnings({"unchecked"})
  @Override
  public List<DocumentMetadata> findByLastJobRunTimeMinusOneMinute(Date lastJobRunTime) {
    // TODO - abstract out transaction management.
    // See story #134542407.
    final Session session = sessionFactory.getCurrentSession();
    Transaction txn = session.beginTransaction();
    try {
      Query query = session.getNamedQuery("findByLastJobRunTimeMinusOneMinute")
          .setString("lastJobRunTime", dateFormat.format(lastJobRunTime));
      ImmutableList.Builder<DocumentMetadata> documentMetadatas = new ImmutableList.Builder<>();
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
