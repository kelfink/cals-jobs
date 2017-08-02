package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.update.UpdateRequest;
import org.hibernate.SessionFactory;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.dao.cms.ReplicatedRelationshipsDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.EsRelationship;
import gov.ca.cwds.data.persistence.cms.ReplicatedRelationships;

public class RelationshipIndexerJobTest {

  @Test
  public void type() throws Exception {
    assertThat(RelationshipIndexerJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ReplicatedRelationshipsDao clientDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    RelationshipIndexerJob target = new RelationshipIndexerJob(clientDao, elasticsearchDao,
        lastJobRunTimeFilename, mapper, sessionFactory);
    assertThat(target, notNullValue());
  }

  // @Test
  public void extract_Args__ResultSet() throws Exception {
    ReplicatedRelationshipsDao clientDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    RelationshipIndexerJob target = new RelationshipIndexerJob(clientDao, elasticsearchDao,
        lastJobRunTimeFilename, mapper, sessionFactory);
    ResultSet rs = mock(ResultSet.class);
    EsRelationship actual = target.extract(rs);
    EsRelationship expected = new EsRelationship();
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void extract_Args__ResultSet_T__SQLException() throws Exception {
    ReplicatedRelationshipsDao clientDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    RelationshipIndexerJob target = new RelationshipIndexerJob(clientDao, elasticsearchDao,
        lastJobRunTimeFilename, mapper, sessionFactory);
    ResultSet rs = mock(ResultSet.class);
    try {
      target.extract(rs);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
    }
  }

  @Test
  public void getDenormalizedClass_Args__() throws Exception {
    ReplicatedRelationshipsDao clientDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    RelationshipIndexerJob target = new RelationshipIndexerJob(clientDao, elasticsearchDao,
        lastJobRunTimeFilename, mapper, sessionFactory);
    Object actual = target.getDenormalizedClass();
    Object expected = EsRelationship.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getViewName_Args__() throws Exception {
    ReplicatedRelationshipsDao clientDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    RelationshipIndexerJob target = new RelationshipIndexerJob(clientDao, elasticsearchDao,
        lastJobRunTimeFilename, mapper, sessionFactory);
    String actual = target.getInitialLoadViewName();
    String expected = "VW_MQT_BI_DIR_RELATION";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getJdbcOrderBy_Args__() throws Exception {
    ReplicatedRelationshipsDao clientDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    RelationshipIndexerJob target = new RelationshipIndexerJob(clientDao, elasticsearchDao,
        lastJobRunTimeFilename, mapper, sessionFactory);
    String actual = target.getJdbcOrderBy();
    String expected = " ORDER BY THIS_LEGACY_ID, RELATED_LEGACY_ID ";
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void prepareUpsertRequest_Args__ElasticSearchPerson__ReplicatedRelationships()
      throws Exception {
    ReplicatedRelationshipsDao clientDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    RelationshipIndexerJob target = new RelationshipIndexerJob(clientDao, elasticsearchDao,
        lastJobRunTimeFilename, mapper, sessionFactory);
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    ReplicatedRelationships p = mock(ReplicatedRelationships.class);
    UpdateRequest actual = target.prepareUpsertRequest(esp, p);
    UpdateRequest expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void prepareUpsertRequest_Args__ElasticSearchPerson__ReplicatedRelationships_T__IOException()
      throws Exception {
    ReplicatedRelationshipsDao clientDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    RelationshipIndexerJob target = new RelationshipIndexerJob(clientDao, elasticsearchDao,
        lastJobRunTimeFilename, mapper, sessionFactory);
    ElasticSearchPerson esp = mock(ElasticSearchPerson.class);
    ReplicatedRelationships p = mock(ReplicatedRelationships.class);
    try {
      target.prepareUpsertRequest(esp, p);
      fail("Expected exception was not thrown!");
    } catch (IOException e) {
    }
  }

  // @Test
  public void normalizeSingle_Args__List() throws Exception {
    ReplicatedRelationshipsDao clientDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    RelationshipIndexerJob target = new RelationshipIndexerJob(clientDao, elasticsearchDao,
        lastJobRunTimeFilename, mapper, sessionFactory);
    List<EsRelationship> recs = new ArrayList<EsRelationship>();
    ReplicatedRelationships actual = target.normalizeSingle(recs);
    ReplicatedRelationships expected = new ReplicatedRelationships();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalize_Args__List() throws Exception {
    ReplicatedRelationshipsDao clientDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    RelationshipIndexerJob target = new RelationshipIndexerJob(clientDao, elasticsearchDao,
        lastJobRunTimeFilename, mapper, sessionFactory);
    List<EsRelationship> recs = new ArrayList<EsRelationship>();
    List<ReplicatedRelationships> actual = target.normalize(recs);
    List<ReplicatedRelationships> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void main_Args__StringArray() throws Exception {
    String[] args = new String[] {};
    RelationshipIndexerJob.main(args);
  }

}
