package gov.ca.cwds.jobs;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.Type;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import gov.ca.cwds.rest.api.domain.cms.SystemMeta;
import gov.ca.cwds.rest.services.cms.CachingSystemCodeService;

/**
 * Loads system codes from DB2 to PostgreSQL.
 * 
 * @author CWDS API Team
 */
public class SystemCodesLoaderJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(SystemCodesLoaderJob.class);

  private NsSystemCodeDao systemCodeDao;

  /**
   * Constructor
   * 
   * @param systemCodeDao system codes DAO
   */
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

    Set<SystemMeta> allSystemMetas = SystemCodeCache.global().getAllSystemMetas();
    LOGGER.info("Found total " + allSystemMetas.size() + " system metas in legacy");

    Set<SystemCode> allSystemCodes = SystemCodeCache.global().getAllSystemCodes();
    LOGGER.info("Found total " + allSystemCodes.size() + " system codes in legacy");

    Map<String, SystemMeta> systemMetaMap = new HashMap<>();
    for (SystemMeta systemMeta : allSystemMetas) {
      systemMetaMap.put(systemMeta.getLogicalTableDsdName(), systemMeta);
    }

    Map<Short, SystemCode> systemCodeMap = new HashMap<>();
    for (SystemCode systemCode : allSystemCodes) {
      systemCodeMap.put(systemCode.getSystemId(), systemCode);
    }

    final Session session = systemCodeDao.getSessionFactory().getCurrentSession();
    final Transaction tx = session.beginTransaction();

    try {
      // Delete all existing system codes from new system.
      session.doWork(new Work() {
        @Override
        public void execute(Connection connection) throws SQLException {
          deleteNsSystemCodes(connection);
        }
      });

      for (SystemCode systemCode : allSystemCodes) {
        boolean active =
            StringUtils.equalsIgnoreCase("N", systemCode.getInactiveIndicator().trim());

        if (active) {
          NsSystemCode nsc = new NsSystemCode();
          nsc.setId(systemCode.getSystemId().intValue());
          nsc.setDescription(systemCode.getShortDescription());
          nsc.setOtherCode(systemCode.getOtherCd());

          String categoryId = systemCode.getForeignKeyMetaTable();
          nsc.setCategoryId(categoryId);

          SystemMeta systemMeta = systemMetaMap.get(categoryId);
          if (systemMeta != null) {
            nsc.setCategoryDescription(systemMeta.getShortDescriptionName());
          } else {
            LOGGER.warn("Missing system meta for category " + categoryId + " for system code "
                + systemCode.getSystemId());
          }

          Short subCategoryId = systemCode.getCategoryId();
          if (subCategoryId != null && subCategoryId != 0) {
            nsc.setSubCategoryId(subCategoryId.intValue());
            SystemCode subCategoySystemCode = systemCodeMap.get(subCategoryId);
            if (subCategoySystemCode != null) {
              nsc.setSubCategoryDescription(subCategoySystemCode.getShortDescription());
            } else {
              LOGGER.warn("Missing system code for sub-category ID " + subCategoryId
                  + " for system code " + systemCode.getSystemId());
            }
          }

          String logicalId = systemCode.getLogicalId();
          if (!StringUtils.isBlank(logicalId)) {
            nsc.setLogicalId(logicalId);
          } else {
            LOGGER.warn("Missing logical ID for system code " + systemCode.getSystemId());
          }

          systemCodeDao.createOrUpdate(nsc);
          loadedSystemCodes.put(nsc.getId(), nsc);
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

  private void deleteNsSystemCodes(Connection conn) throws SQLException {
    Statement stmt = null;
    try {
      LOGGER.info("Deleting system codes from new system...");
      final String query = "DELETE FROM SYSTEM_CODES";
      stmt = conn.createStatement();
      stmt.execute(query);
      LOGGER.info("Deleting system codes from new system");
    } finally {
      if (stmt != null) {
        stmt.close();
      }
    }
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    LOGGER.info("Loading system codes from legacy to new system...");

    try {
      Injector injector = Guice.createInjector(new SystemCodesLoaderModule());

      // Initialize system code cache.
      injector.getInstance(SystemCodeCache.class);

      NsSystemCodeDao systemCodeDao = injector.getInstance(NsSystemCodeDao.class);
      SystemCodesLoaderJob systemCodesJob = new SystemCodesLoaderJob(systemCodeDao);
      systemCodesJob.load();
      LOGGER.info("DONE loading system codes from legacy to new system.");
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
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
   * System codes persistence class for new system.
   */
  @Entity
  @Table(name = "system_codes")
  static class NsSystemCode implements PersistentObject {

    private static final long serialVersionUID = 8370500764130606101L;

    @Id
    @Column(name = "ID")
    @Type(type = "int")
    private Integer id;

    @Column(name = "CATEGORY_ID")
    private String categoryId;

    @Column(name = "SUB_CATEGORY_ID")
    @Type(type = "int")
    private Integer subCategoryId;

    @Column(name = "DESCRIPTION")
    @ColumnTransformer(read = "trim(DESCRIPTION)")
    private String description;

    @Column(name = "CATEGORY_DESCRIPTION")
    @ColumnTransformer(read = "trim(CATEGORY_DESCRIPTION)")
    private String categoryDescription;

    @Column(name = "SUB_CATEGORY_DESCRIPTION")
    @ColumnTransformer(read = "trim(SUB_CATEGORY_DESCRIPTION)")
    private String subCategoryDescription;

    @Column(name = "OTHER_CODE")
    @ColumnTransformer(read = "trim(OTHER_CODE)")
    private String otherCode;

    @Column(name = "LOGICAL_ID")
    @ColumnTransformer(read = "trim(LOGICAL_ID)")
    private String logicalId;

    /**
     * Default constructor.
     */
    public NsSystemCode() {
      // Default constructor
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

    public Integer getSubCategoryId() {
      return subCategoryId;
    }

    public void setSubCategoryId(Integer subCategoryId) {
      this.subCategoryId = subCategoryId;
    }

    public String getCategoryDescription() {
      return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
      this.categoryDescription = categoryDescription;
    }

    public String getSubCategoryDescription() {
      return subCategoryDescription;
    }

    public void setSubCategoryDescription(String subCategoryDescription) {
      this.subCategoryDescription = subCategoryDescription;
    }

    public String getOtherCode() {
      return otherCode;
    }

    public void setOtherCode(String otherCode) {
      this.otherCode = otherCode;
    }

    public String getLogicalId() {
      return logicalId;
    }

    public void setLogicalId(String logicalId) {
      this.logicalId = logicalId;
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
