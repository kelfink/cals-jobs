package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.update.UpdateRequest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import gov.ca.cwds.dao.cms.ReplicatedRelationshipsDao;
import gov.ca.cwds.data.persistence.cms.EsRelationship;
import gov.ca.cwds.data.persistence.cms.ReplicatedRelationships;

public class RelationshipIndexerJobTest
    extends PersonJobTester<ReplicatedRelationships, EsRelationship> {

  ReplicatedRelationshipsDao dao;
  RelationshipIndexerJob target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();

    dao = new ReplicatedRelationshipsDao(sessionFactory);
    target = new RelationshipIndexerJob(dao, esDao, lastJobRunTimeFilename, MAPPER, sessionFactory);
    target.setOpts(opts);
  }

  @Test
  public void type() throws Exception {
    assertThat(RelationshipIndexerJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void threadExtractJdbc_Args__() throws Exception {
    target.threadRetrieveByJdbc();
  }

  // @Test
  public void extract_Args__ResultSet() throws Exception {
    EsRelationship actual = target.extract(rs);
    EsRelationship expected = new EsRelationship();
    assertThat(actual, is(equalTo(expected)));
  }

  // @Test
  public void extract_Args__ResultSet_T__SQLException() throws Exception {
    try {
      target.extract(rs);
      fail("Expected exception was not thrown!");
    } catch (SQLException e) {
    }
  }

  @Test
  public void getDenormalizedClass_Args__() throws Exception {
    Object actual = target.getDenormalizedClass();
    Object expected = EsRelationship.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getViewName_Args__() throws Exception {
    String actual = target.getInitialLoadViewName();
    String expected = "VW_MQT_BI_DIR_RELATION";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getJdbcOrderBy_Args__() throws Exception {
    String actual = target.getJdbcOrderBy();
    String expected = " ORDER BY THIS_LEGACY_ID, RELATED_LEGACY_ID ";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void prepareUpsertRequest_Args__ElasticSearchPerson__ReplicatedRelationships()
      throws Exception {
    ReplicatedRelationships p = new ReplicatedRelationships(DEFAULT_CLIENT_ID);
    UpdateRequest actual = target.prepareUpsertRequest(esp, p);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  @Ignore
  public void prepareUpsertRequest_Args__ElasticSearchPerson__ReplicatedRelationships_T__IOException()
      throws Exception {
    ReplicatedRelationships p = new ReplicatedRelationships(DEFAULT_CLIENT_ID);
    try {
      target.prepareUpsertRequest(esp, p);
      fail("Expected exception was not thrown!");
    } catch (IOException e) {
    }
  }

  @Test
  public void pullRange_Args__Pair() throws Exception {
    final Pair<String, String> p = Pair.of("aaaaaaaaaa", "9999999999");
    target.pullRange(p);
  }

  @Test
  public void getPartitionRanges_Args() throws Exception {
    final List actual = target.getPartitionRanges();
    final List expected = new ArrayList<>();
    expected.add(Pair.of("aaaaaaaaaa", "9999999999"));
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getPartitionRanges_RSQ() throws Exception {
    System.setProperty("DB_CMS_SCHEMA", "CWSRSQ");
    final List actual = target.getPartitionRanges();
    assertThat(actual.size(), is(equalTo(64)));
  }

  @Test
  public void normalizeSingle_Args__List() throws Exception {
    List<EsRelationship> recs = new ArrayList<EsRelationship>();
    EsRelationship rel = new EsRelationship();
    rel.setRelatedLegacyId(DEFAULT_CLIENT_ID);
    recs.add(rel);

    ReplicatedRelationships actual = target.normalizeSingle(recs);
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void normalize_Args__List() throws Exception {
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
