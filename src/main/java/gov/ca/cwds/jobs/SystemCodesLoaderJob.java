package gov.ca.cwds.jobs;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.Type;
import org.hibernate.cfg.Configuration;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provides;

import gov.ca.cwds.data.CrudsDaoImpl;
import gov.ca.cwds.data.cms.SystemCodeDao;
import gov.ca.cwds.data.cms.SystemMetaDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.inject.NsSessionFactory;
import gov.ca.cwds.jobs.exception.JobsException;
import gov.ca.cwds.rest.api.domain.cms.SystemCode;
import gov.ca.cwds.rest.api.domain.cms.SystemCodeCache;
import gov.ca.cwds.rest.services.cms.CachingSystemCodeService;

/**
 * Loads system codes from DB2 to Postgress.
 * 
 * @author CWDS API Team
 */
public class SystemCodesLoaderJob {

  private static final Logger LOGGER = LogManager.getLogger(SystemCodesLoaderJob.class);

  private NsSystemCodeDao systemCodeDao;

  @Inject
  public SystemCodesLoaderJob(NsSystemCodeDao systemCodeDao) {
    this.systemCodeDao = systemCodeDao;
  }

  /**
   * Load system codes into new system
   * 
   * @return Map of newly loaded system codes.
   */
  public Map<Integer, NsSystemCode> load() {
    Map<Integer, NsSystemCode> loadedSystemCodes = new HashMap<>();
    Set<SystemCode> allSystemCodes = SystemCodeCache.global().getAllSystemCodes();
    LOGGER.info("Found total " + allSystemCodes.size() + " system codes in legacy");

    Transaction tx = systemCodeDao.getSessionFactory().getCurrentSession().beginTransaction();

    try {
      for (SystemCode systemCode : allSystemCodes) {
        if (StringUtils.equalsIgnoreCase("N", systemCode.getInactiveIndicator().trim())) {
          NsSystemCode sc = new NsSystemCode(systemCode);
          systemCodeDao.createOrUpdate(sc);
          loadedSystemCodes.put(sc.getId(), sc);
        }
      }
    } catch (Exception e) {
      tx.rollback();
      LOGGER.error("ERROR loading system codes, rolling back...");
      throw new JobsException(e);
    }

    tx.commit();
    LOGGER.info("Loaded total " + loadedSystemCodes.size()
        + " active system codes from legacy into new system");
    return loadedSystemCodes;
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    LOGGER.info("Loading systen codes from legacy to new system...");

    try {
      Injector injector = Guice.createInjector(new SystemCodesLoaderModule());

      /**
       * Initialize system code cache
       */
      injector.getInstance(SystemCodeCache.class);

      NsSystemCodeDao systemCodeDao = injector.getInstance(NsSystemCodeDao.class);
      SystemCodesLoaderJob systemCodesJob = new SystemCodesLoaderJob(systemCodeDao);
      systemCodesJob.load();
      LOGGER.info("DONE loading system codes from legacy to new system.");
    } catch (Exception e) {
      LOGGER.error(e);
      System.exit(-1);
    }
    System.exit(0);
  }

  //
  // ============================================================================
  // System codes persistence class for new system
  // ============================================================================
  //
  /**
   * System codes persistence class for new system
   */
  @Entity
  @Table(name = "system_codes")
  static class NsSystemCode implements PersistentObject {

    private static final long serialVersionUID = 8370500764130606101L;

    @Id
    @Column(name = "ID")
    @Type(type = "int")
    private Integer id;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "description")
    private String description;

    /**
     * Default constructor.
     */
    public NsSystemCode() {
      // Default constructor
    }

    public NsSystemCode(SystemCode systemCode) {
      setId(systemCode.getSystemId().intValue());
      setCategoryId(systemCode.getForeignKeyMetaTable());
      setDescription(systemCode.getShortDescription());
    }

    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }

    public String getCategoryId() {
      return categoryId;
    }

    public void setCategoryId(String categoryId) {
      this.categoryId = categoryId;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    @Override
    public Serializable getPrimaryKey() {
      return getId();
    }

    @Override
    public int hashCode() {
      return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
      return EqualsBuilder.reflectionEquals(this, obj);
    }
  }

  //
  // ============================================================================
  // System codes DAO for new system
  // ============================================================================
  //
  /**
   * System codes DAO for new system
   */
  static class NsSystemCodeDao extends CrudsDaoImpl<NsSystemCode> {

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

  //
  // ============================================================================
  // System codes loader Guice module
  // ============================================================================
  //
  /**
   * System codes loader Guice module
   */
  static class SystemCodesLoaderModule extends AbstractModule {

    /**
     * Default constructor.
     */
    public SystemCodesLoaderModule() {
      // Default constructor
    }

    @Override
    protected void configure() {
      // DB2 session factory:
      bind(SessionFactory.class).annotatedWith(CmsSessionFactory.class)
          .toInstance(new Configuration().configure("jobs-cms-hibernate.cfg.xml")
              .addAnnotatedClass(gov.ca.cwds.data.persistence.cms.SystemCode.class)
              .addAnnotatedClass(gov.ca.cwds.data.persistence.cms.SystemMeta.class)
              .buildSessionFactory());

      // PostgreSQL session factory:
      bind(SessionFactory.class).annotatedWith(NsSessionFactory.class)
          .toInstance(new Configuration().configure("jobs-ns-hibernate.cfg.xml")
              .addAnnotatedClass(NsSystemCode.class).buildSessionFactory());

      // DB2 tables:
      bind(SystemCodeDao.class);
      bind(SystemMetaDao.class);

      // PostgreSQL:
      bind(NsSystemCodeDao.class);
    }

    @Provides
    public SystemCodeCache provideSystemCodeCache(SystemCodeDao systemCodeDao,
        SystemMetaDao systemMetaDao) {
      final long secondsToRefreshCache = 15 * 24 * 60 * (long) 60; // 15 days
      SystemCodeCache systemCodeCache =
          new CachingSystemCodeService(systemCodeDao, systemMetaDao, secondsToRefreshCache, true);
      systemCodeCache.register();
      return systemCodeCache;
    }
  }
}
