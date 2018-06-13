package gov.ca.cwds.jobs.cap.users;

import com.google.inject.Inject;
import gov.ca.cwds.data.auth.CwsOfficeDao;
import gov.ca.cwds.data.persistence.auth.CwsOffice;
import gov.ca.cwds.inject.CmsSessionFactory;
import org.hibernate.SessionFactory;

import java.util.Collection;

public class CwsOfficeDaoImpl implements CwsOfficeDao {

  @Inject
  @CmsSessionFactory
  SessionFactory sessionFactory;


  @Override
  public Iterable<CwsOffice> findByOfficeIdIn(Collection<String> collection) {
    return sessionFactory.openSession().createQuery("SELECT U FROM CwsOffice U WHERE U.officeId in :ids", CwsOffice.class)
            .setParameter("ids", collection).list();
  }

  @Override
  public CwsOffice findOne(String s) {
    return null;
  }

  @Override
  public Iterable<CwsOffice> findAll() {
    return null;
  }
}
