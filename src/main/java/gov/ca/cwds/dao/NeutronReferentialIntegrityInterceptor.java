package gov.ca.cwds.dao;

import java.io.Serializable;
import java.util.Iterator;

import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.data.ApiHibernateInterceptor;

/**
 * Hibernate interceptor traps referential integrity errors.
 * 
 * @author CWDS API Team
 */
public class NeutronReferentialIntegrityInterceptor extends ApiHibernateInterceptor {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER =
      LoggerFactory.getLogger(NeutronReferentialIntegrityInterceptor.class);

  /**
   * <p>
   * Called on entity delete.
   * </p>
   * 
   * {@inheritDoc}
   */
  @Override
  public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames,
      Type[] types) {
    LOGGER.debug("on delete");
  }

  /**
   * <p>
   * Called on entity update.
   * </p>
   * 
   * {@inheritDoc}
   */
  @Override
  public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState,
      Object[] previousState, String[] propertyNames, Type[] types) {
    LOGGER.debug("on flush dirty");
    return false;
  }

  /**
   * <p>
   * Called on entity load.
   * </p>
   * 
   * {@inheritDoc}
   */
  @Override
  public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames,
      Type[] types) {
    LOGGER.debug("Load Operation");
    return true;
  }

  /**
   * <p>
   * Called on entity save.
   * </p>
   * 
   * {@inheritDoc}
   */
  @Override
  public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames,
      Type[] types) {
    LOGGER.debug("on save");
    return false;
  }

  /**
   * <p>
   * Called on <strong>before</strong> commit.
   * </p>
   * 
   * {@inheritDoc}
   */
  @Override
  public void preFlush(@SuppressWarnings("rawtypes") Iterator iterator) {
    LOGGER.debug("Before commiting");
  }

  // Called after committed to database.
  @Override
  public void postFlush(Iterator iterator) {
    LOGGER.debug("After commiting");
  }
}
