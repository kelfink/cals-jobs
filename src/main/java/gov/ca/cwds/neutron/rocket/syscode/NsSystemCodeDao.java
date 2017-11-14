package gov.ca.cwds.neutron.rocket.syscode;

import org.hibernate.SessionFactory;

import com.google.inject.Inject;

import gov.ca.cwds.data.CrudsDaoImpl;
import gov.ca.cwds.inject.NsSessionFactory;

//
// ============================================================================
// System codes DAO for new system
// ============================================================================
//
/**
 * System codes DAO for new system
 */
public class NsSystemCodeDao extends CrudsDaoImpl<NsSystemCode> {

  /**
   * Constructor
   * 
   * @param sessionFactory The PostgreSQL sessionFactory
   */
  @Inject
  public NsSystemCodeDao(@NsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  /**
   * Create or update system code record.
   * 
   * @param systemCode System code
   * @return Created or updated system code.
   */
  public NsSystemCode createOrUpdate(NsSystemCode systemCode) {
    return persist(systemCode);
  }
}
