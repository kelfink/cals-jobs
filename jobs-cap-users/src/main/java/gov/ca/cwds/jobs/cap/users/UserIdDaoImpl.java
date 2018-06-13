package gov.ca.cwds.jobs.cap.users;

import com.google.inject.Inject;
import gov.ca.cwds.data.auth.UserIdDao;
import gov.ca.cwds.data.persistence.auth.UserId;
import gov.ca.cwds.inject.CmsSessionFactory;
import org.hibernate.SessionFactory;

import java.util.Collection;
import java.util.List;

public class UserIdDaoImpl implements UserIdDao {

  @Inject
  @CmsSessionFactory
  SessionFactory sessionFactory;


  @Override
  public List<UserId> findActiveByLogonId(String s) {
    return null;
  }

  @Override
  public List<UserId> findActiveByLogonIdIn(Collection<String> collection) {
    return sessionFactory.openSession().createQuery("SELECT U FROM UserId U WHERE U.logonId in :logonIds AND U.endDate is null", UserId.class)
            .setParameter("logonIds", collection).list();
  }

  @Override
  public UserId findOne(String s) {
    return null;
  }

  @Override
  public Iterable<UserId> findAll() {
    return null;
  }
}
