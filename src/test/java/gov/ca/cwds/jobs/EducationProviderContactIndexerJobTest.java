package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.dao.cms.ReplicatedEducationProviderContactDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.rep.ReplicatedEducationProviderContact;

/**
 * @author CWDS API Team
 */
@SuppressWarnings("javadoc")
public class EducationProviderContactIndexerJobTest extends
    PersonJobTester<ReplicatedEducationProviderContact, ReplicatedEducationProviderContact> {

  ReplicatedEducationProviderContactDao dao;
  EducationProviderContactIndexerJob target;

  @Override
  @Before
  public void setup() throws Exception {
    super.setup();
    dao = new ReplicatedEducationProviderContactDao(this.sessionFactory);
    target = new EducationProviderContactIndexerJob(dao, esDao, lastJobRunTimeFilename, MAPPER,
        sessionFactory);
  }


  @Test
  public void testType() throws Exception {
    assertThat(EducationProviderContactIndexerJob.class, notNullValue());
  }

  @Test
  public void testInstantiation() throws Exception {
    ReplicatedEducationProviderContactDao educationProviderContactDao = null;
    ElasticsearchDao elasticsearchDao = null;
    String lastJobRunTimeFilename = null;
    ObjectMapper mapper = null;
    SessionFactory sessionFactory = null;
    EducationProviderContactIndexerJob target =
        new EducationProviderContactIndexerJob(educationProviderContactDao, elasticsearchDao,
            lastJobRunTimeFilename, mapper, sessionFactory);
    assertThat(target, notNullValue());
  }

  @Test
  @Ignore
  public void testfindAllUpdatedAfterNamedQueryExists() throws Exception {
    Query query = session.getNamedQuery(
        "gov.ca.cwds.data.persistence.cms.rep.ReplicatedEducationProviderContact.findAllUpdatedAfter");
    assertThat(query, is(notNullValue()));
  }

}
