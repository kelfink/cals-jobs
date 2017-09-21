package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.update.UpdateRequest;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.NativeQuery;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import gov.ca.cwds.dao.cms.BatchBucket;
import gov.ca.cwds.dao.cms.ReplicatedAkaDao;
import gov.ca.cwds.dao.cms.ReplicatedOtherClientNameDao;
import gov.ca.cwds.data.es.ElasticSearchPerson;
import gov.ca.cwds.data.es.ElasticSearchPersonAka;
import gov.ca.cwds.data.persistence.cms.ReplicatedAkas;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherClientName;

/**
 * 
 * @author CWDS API Team
 */
@SuppressWarnings("javadoc")
public class OtherClientNameIndexerJobTest extends PersonJobTester {

  ReplicatedAkaDao normDao;
  ReplicatedOtherClientNameDao denormDao;
  OtherClientNameIndexerJob target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    normDao = new ReplicatedAkaDao(sessionFactory);
    denormDao = new ReplicatedOtherClientNameDao(sessionFactory);
    target = new OtherClientNameIndexerJob(normDao, denormDao, esDao, lastJobRunTimeFilename,
        MAPPER, sessionFactory);
    target.setOpts(opts);
  }

  @Test
  public void testType() throws Exception {
    assertThat(OtherClientNameIndexerJob.class, notNullValue());
  }

  @Test
  public void testInstantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  @Ignore
  public void testfindAllUpdatedAfterNamedQueryExists() throws Exception {
    // NOTE: Add as an integration test. Doesn't work with mocks.
    final Query query = session.getNamedQuery(
        "gov.ca.cwds.data.persistence.cms.rep.ReplicatedOtherClientName.findAllUpdatedAfter");
    assertThat(query, is(notNullValue()));
  }

  @Test
  public void type() throws Exception {
    assertThat(OtherClientNameIndexerJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void getPartitionRanges_Args__() throws Exception {
    final javax.persistence.Query q = mock(javax.persistence.Query.class);
    when(em.createNativeQuery(any(String.class), any(Class.class))).thenReturn(q);

    final List<BatchBucket> buckets = new ArrayList<>();
    final BatchBucket b = new BatchBucket();
    b.setBucket(1);
    b.setBucketCount(2);
    b.setMaxId("1");
    b.setMaxId("2");
    buckets.add(b);
    when(q.getResultList()).thenReturn(buckets);

    final NativeQuery<ReplicatedOtherClientName> qn = mock(NativeQuery.class);
    when(session.getNamedNativeQuery(any(String.class))).thenReturn(qn);
    when(qn.setString(any(String.class), any(String.class))).thenReturn(qn);
    when(qn.setFlushMode(any(FlushMode.class))).thenReturn(qn);
    when(qn.setReadOnly(any(Boolean.class))).thenReturn(qn);
    when(qn.setCacheMode(any(CacheMode.class))).thenReturn(qn);
    when(qn.setFetchSize(any(Integer.class))).thenReturn(qn);
    when(qn.setCacheable(any(Boolean.class))).thenReturn(qn);

    final ScrollableResults results = mock(ScrollableResults.class);
    when(qn.scroll(any(ScrollMode.class))).thenReturn(results);

    final List<ReplicatedOtherClientName> denorms = new ArrayList<>();
    final ReplicatedOtherClientName m = new ReplicatedOtherClientName();
    denorms.add(m);
    when(qn.list()).thenReturn(denorms);

    final List<?> actual = target.getPartitionRanges();
    assertThat(actual, notNullValue());
  }

  @Test
  public void getLegacySourceTable_Args__() throws Exception {
    final String actual = target.getLegacySourceTable();
    final String expected = "OCL_NM_T";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void extract_Args__ResultSet() throws Exception {
    final ReplicatedOtherClientName actual = target.extract(rs);
    assertThat(actual, notNullValue());
  }

  @Test
  public void getDenormalizedClass_Args__() throws Exception {
    final Object actual = target.getDenormalizedClass();
    final Object expected = ReplicatedOtherClientName.class;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void normalizeSingle_Args__List() throws Exception {
    final List<ReplicatedOtherClientName> recs = new ArrayList<ReplicatedOtherClientName>();
    ReplicatedOtherClientName m = new ReplicatedOtherClientName();
    m.setClientId(DEFAULT_CLIENT_ID);
    recs.add(m);

    final ReplicatedAkas actual = target.normalizeSingle(recs);
    assertThat(actual, notNullValue());
  }

  @Test
  public void normalize_Args__List() throws Exception {
    final List<ReplicatedOtherClientName> recs = new ArrayList<ReplicatedOtherClientName>();
    final List<ReplicatedAkas> actual = target.normalize(recs);
    assertThat(actual, notNullValue());
  }

  @Test
  public void prepareUpsertRequest_Args__ElasticSearchPerson__ReplicatedAkas() throws Exception {
    ElasticSearchPerson esp = new ElasticSearchPerson();
    final ReplicatedAkas p = new ReplicatedAkas(DEFAULT_CLIENT_ID);
    ElasticSearchPersonAka aka = new ElasticSearchPersonAka();
    aka.setFirstName("Albert");
    aka.setLastName("Einstein");
    p.addAka(aka);

    final UpdateRequest actual = target.prepareUpsertRequest(esp, p);
    assertThat(actual, notNullValue());
  }

  @Test
  public void getInitialLoadViewName_Args__() throws Exception {
    final String actual = target.getInitialLoadViewName();
    assertThat(actual, notNullValue());
  }

  @Test
  public void getJdbcOrderBy_Args__() throws Exception {
    final String actual = target.getJdbcOrderBy().trim();
    final String expected = "ORDER BY x.FKCLIENT_T";
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getInitialLoadQuery_Args__String() throws Exception {
    final String dbSchemaName = "CWSRS1";
    final String actual = target.getInitialLoadQuery(dbSchemaName);
    assertThat(actual, notNullValue());
  }

  @Test
  @Ignore
  public void main_Args__StringArray() throws Exception {
    String[] args = new String[] {};
    OtherClientNameIndexerJob.main(args);
  }

}
