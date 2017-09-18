package gov.ca.cwds.jobs.test;

import java.util.List;

import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.inject.LastRunFile;

public class TestIndexerJob
    extends BasePersonIndexerJob<TestNormalizedEntity, TestDenormalizedEntity> {

  public TestIndexerJob(final TestNormalizedEntityDao mainDao,
      final ElasticsearchDao elasticsearchDao, @LastRunFile final String lastJobRunTimeFilename,
      final ObjectMapper mapper, @CmsSessionFactory SessionFactory sessionFactory) {
    super(mainDao, elasticsearchDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  @Override
  public String getLegacySourceTable() {
    return super.getLegacySourceTable();
  }

  @Override
  public String getInitialLoadViewName() {
    return "VW_NUTTIN";
  }

  @Override
  public String getDriverTable() {
    return "GOOBER_T";
  }

  @Override
  public TestNormalizedEntity normalizeSingle(List<TestDenormalizedEntity> recs) {
    return new TestNormalizedEntity((String) recs.get(0).getPrimaryKey());
  }

  @Override
  public Class<? extends ApiGroupNormalizer<? extends PersistentObject>> getDenormalizedClass() {
    return TestDenormalizedEntity.class;
  }

}
