package gov.ca.cwds.jobs.cap.users;

import com.google.inject.Inject;
import gov.ca.cwds.data.auth.StaffPersonDao;
import gov.ca.cwds.data.persistence.auth.StaffPerson;
import gov.ca.cwds.data.persistence.auth.UserId;
import gov.ca.cwds.inject.CmsSessionFactory;
import org.hibernate.SessionFactory;

import java.util.Collection;

public class StaffPersonDaoImpl implements StaffPersonDao {

  @Inject
  @CmsSessionFactory
  SessionFactory sessionFactory;

  @Override
  public Iterable<StaffPerson> findByIdIn(Collection<String> collection) {
    return sessionFactory.openSession().createQuery("SELECT U FROM StaffPerson U WHERE U.id in :ids", StaffPerson.class)
            .setParameter("ids", collection).list();
  }

  @Override
  public StaffPerson findOne(String s) {
    return null;
  }

  @Override
  public Iterable<StaffPerson> findAll() {
    return null;
  }
}
