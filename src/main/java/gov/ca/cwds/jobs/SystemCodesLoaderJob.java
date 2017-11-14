package gov.ca.cwds.jobs;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.neutron.jetpack.JobLogs;
import gov.ca.cwds.neutron.rocket.syscode.NsSystemCode;
import gov.ca.cwds.neutron.rocket.syscode.NsSystemCodeDao;
import gov.ca.cwds.neutron.rocket.syscode.SystemCodesLoaderModule;
import gov.ca.cwds.rest.api.domain.cms.SystemCode;
import gov.ca.cwds.rest.api.domain.cms.SystemCodeCache;
import gov.ca.cwds.rest.api.domain.cms.SystemMeta;

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

  protected void handleSystemMeta(Map<String, SystemMeta> systemMetaMap, NsSystemCode nsc,
      String categoryId, final SystemCode systemCode) {
    SystemMeta systemMeta = systemMetaMap.get(categoryId);
    if (systemMeta != null) {
      nsc.setCategoryDescription(StringUtils.trim(systemMeta.getShortDescriptionName()));
    } else {
      LOGGER.warn("Missing system meta for category {} for system code {}", categoryId,
          systemCode.getSystemId());
    }
  }

  protected void handleLogicalId(final NsSystemCode nsc, final SystemCode systemCode) {
    String logicalId = StringUtils.trim(systemCode.getLogicalId());
    if (!StringUtils.isBlank(logicalId)) {
      nsc.setLogicalId(logicalId);
    } else {
      LOGGER.warn("Missing logical ID for system code {}", systemCode.getSystemId());
    }
  }

  protected void handleSubCategory(Map<Short, SystemCode> systemCodeMap, final NsSystemCode nsc,
      final SystemCode systemCode) {
    Short subCategoryId = systemCode.getCategoryId();
    if (subCategoryId != null && subCategoryId != 0) {
      nsc.setSubCategoryId(subCategoryId.intValue());
      SystemCode subCategoySystemCode = systemCodeMap.get(subCategoryId);
      if (subCategoySystemCode != null) {
        nsc.setSubCategoryDescription(StringUtils.trim(subCategoySystemCode.getShortDescription()));
      } else {
        LOGGER.warn("Missing system code for sub-category ID {} for system code {}", subCategoryId,
            systemCode.getSystemId());
      }
    }
  }

  /**
   * Delete and load system codes into new system.
   * 
   * @return Map of newly loaded system codes.
   * @throws NeutronException oops!
   */
  public Map<Integer, NsSystemCode> load() throws NeutronException {
    Map<Integer, NsSystemCode> loadedSystemCodes = new HashMap<>();

    Set<SystemMeta> allSystemMetas = SystemCodeCache.global().getAllSystemMetas();
    LOGGER.info("Found total {} system metas in legacy", allSystemMetas.size());

    Set<SystemCode> allSystemCodes = SystemCodeCache.global().getAllSystemCodes();
    LOGGER.info("Found total {} system codes in legacy", allSystemCodes.size());

    Map<String, SystemMeta> systemMetaMap = new HashMap<>();
    for (SystemMeta systemMeta : allSystemMetas) {
      systemMetaMap.put(StringUtils.trim(systemMeta.getLogicalTableDsdName()), systemMeta);
    }

    Map<Short, SystemCode> systemCodeMap = new HashMap<>();
    for (SystemCode systemCode : allSystemCodes) {
      systemCodeMap.put(systemCode.getSystemId(), systemCode);
    }

    final Session session = systemCodeDao.getSessionFactory().getCurrentSession();
    final Transaction tx = session.beginTransaction();

    try {
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
          nsc.setOtherCode(StringUtils.trim(systemCode.getOtherCd()));

          String categoryId = StringUtils.trim(systemCode.getForeignKeyMetaTable());
          nsc.setCategoryId(categoryId);

          handleSystemMeta(systemMetaMap, nsc, categoryId, systemCode);
          handleSubCategory(systemCodeMap, nsc, systemCode);
          handleLogicalId(nsc, systemCode);
          systemCodeDao.createOrUpdate(nsc);
          loadedSystemCodes.put(nsc.getId(), nsc);
        }
      }
    } catch (Exception e) {
      tx.rollback();
      throw JobLogs.checked(LOGGER, e, "ERROR loading system codes, rolling back...",
          e.getMessage());
    }

    tx.commit();
    LOGGER.info("Loaded total {} active system codes from legacy into new system",
        loadedSystemCodes.size());
    return loadedSystemCodes;
  }

  protected void deleteNsSystemCodes(Connection conn) throws SQLException {
    try (Statement stmt = conn.createStatement()) {
      LOGGER.info("Deleting system codes from new system...");
      stmt.execute("DELETE FROM SYSTEM_CODES");
      LOGGER.info("Deleting system codes from new system");
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

      final NsSystemCodeDao systemCodeDao = injector.getInstance(NsSystemCodeDao.class);
      final SystemCodesLoaderJob systemCodesJob = new SystemCodesLoaderJob(systemCodeDao);
      systemCodesJob.load();
      LOGGER.info("DONE loading system codes from legacy to new system.");
    } catch (Exception e) {
      LOGGER.error("SYSTEM CODES LOADER FAILED: {}", e.getMessage(), e);
      System.exit(-1);
    }

    System.exit(0);
  }
}
