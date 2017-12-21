package gov.ca.cwds.generic.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.generic.dao.cms.ReplicatedSubstituteCareProviderDao;
import gov.ca.cwds.generic.data.persistence.cms.rep.ReplicatedSubstituteCareProvider;
import gov.ca.cwds.generic.jobs.inject.JobRunner;
import gov.ca.cwds.generic.jobs.inject.LastRunFile;
import gov.ca.cwds.generic.jobs.util.jdbc.JobJdbcUtils;
import gov.ca.cwds.inject.CmsSessionFactory;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.SessionFactory;

/**
 * Job to load Substitute Care Providers from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class SubstituteCareProviderIndexJob extends
    BasePersonIndexerJob<ReplicatedSubstituteCareProvider, ReplicatedSubstituteCareProvider> {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param dao main DAO
   * @param esDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public SubstituteCareProviderIndexJob(final ReplicatedSubstituteCareProviderDao dao,
      final ElasticsearchDao esDao, @LastRunFile final String lastJobRunTimeFilename,
      final ObjectMapper mapper, @CmsSessionFactory SessionFactory sessionFactory) {
    super(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  @Override
  protected List<Pair<String, String>> getPartitionRanges() {
    return JobJdbcUtils.getCommonPartitionRanges(this);
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    JobRunner.runStandalone(SubstituteCareProviderIndexJob.class, args);
  }

}
