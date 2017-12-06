package gov.ca.cwds.dao.cms;

import org.hibernate.SessionFactory;

import com.google.inject.Inject;

import gov.ca.cwds.data.CrudsDaoImpl;
import gov.ca.cwds.data.persistence.cms.StaffPerson;
import gov.ca.cwds.inject.CmsSessionFactory;

/**
 * Hibernate DAO for DB2 {@link StaffPerson}.
 * 
 * @author CWDS API Team
 */
public class StaffPersonDao extends CrudsDaoImpl<StaffPerson> {

  /**
   * Constructor
   * 
   * @param sessionFactory The session factory
   */
  @Inject
  public StaffPersonDao(@CmsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

}
