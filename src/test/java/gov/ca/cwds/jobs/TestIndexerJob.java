package gov.ca.cwds.jobs;

import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.inject.LastRunFile;

public class TestIndexerJob
    extends BasePersonIndexerJob<TestNormalizedEntity, TestDenormalizedEntity> {

  public TestIndexerJob(final TestNormalizedEntityDao mainDao,
      final ElasticsearchDao elasticsearchDao, @LastRunFile final String lastJobRunTimeFilename,
      final ObjectMapper mapper, @CmsSessionFactory SessionFactory sessionFactory) {
    super(mainDao, elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  @Override
  protected String getLegacySourceTable() {
    return "NOBUENO";
  }

  @Override
  public String getViewName() {
    return "VW_NUTTIN";
  }

}
