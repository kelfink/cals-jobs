package gov.ca.cwds.jobs.cals.rfa;

import static org.junit.Assert.assertEquals;

import gov.ca.cwds.cals.DatabaseHelper;
import gov.ca.cwds.jobs.cals.BaseCalsIndexerJobTest;
import java.util.Collections;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author CWDS TPT-2
 */
public class RFA1aFormIndexerJobTest extends BaseCalsIndexerJobTest {

  private static final String TEST_CONFIG = "src/test/resources/config/cals/rfa/CALS_RFA1aForm.yaml";

  private static final String CALSNS_SCHEMA = "calsns";
  private static final String CALSNS_JDBC_URL_TEMPLATE = "jdbc:h2:%s;INIT=create schema if not exists %s\\;set schema %s;autocommit=true";

  private static final String CALSNS_TEST_DATA_SCRIPT = "liquibase/calsns_database_master_for_tests.xml";

  private static final RFA1aFormIncrementalLoadDateStrategy RFA1A_FORM_LOAD_DATE_STRATEGY = new RFA1aFormIncrementalLoadDateStrategy();

  private static final String SEARCH_URI_PATH = "/rfa1aforms/rfa1aform/_search";
  private static final String QUERY_FILE_APPLICANT_FIRST_NAME = "cals/rfa/query_ApplicantFirstName.json";
  private static final String QUERY_FILE_APPLICANT_FIRST_NAME_PHONETIC = "cals/rfa/query_ApplicantFirstName_phonetic.json";

  private static String tempDbFile;

  private static void cleanUp() throws Exception {
    RFA1A_FORM_LOAD_DATE_STRATEGY.reset(TIME_FILES_DIR);
  }

  @BeforeClass
  public static void beforeClass() throws Exception {
    cleanUp();
    tempDbFile = createTestDbFile(CALSNS_SCHEMA);
    setUpCalsns();
    System.setProperty("DB_CALSNS_JDBC_URL", getCalsnsJdbcUrl());
    System.setProperty("DB_CALSNS_USER", CALSNS_USER);
    System.setProperty("DB_CALSNS_PASSWORD", CALSNS_PASSWORD);

    RFA1aFormIndexerJob.main(new String[]{
        "-c", TEST_CONFIG, "-l", TIME_FILES_DIR
    });
  }

  @AfterClass
  public static void afterClass() throws Exception {
    cleanUp();
  }

  //ignore test for now in order to be able to test locally
  @Ignore
  @Test(expected = ResponseException.class)
  public void testUnauthorized() throws Exception {
    anonymousRestClient.performRequest("GET", "/");
  }

  @Test
  public void testTotalIndexedDocuments() throws Exception {
    Response response = restClient.performRequest("GET", SEARCH_URI_PATH);
    assertEquals(200, response.getStatusLine().getStatusCode());

    assertTotalHits(response, 2);
  }

  @Test
  public void testSearchByApplicantFirstName() throws Exception {
    HttpEntity queryEntity = new NStringEntity(
        readResource(QUERY_FILE_APPLICANT_FIRST_NAME), ContentType.APPLICATION_JSON);
    Response response = restClient
        .performRequest("POST", SEARCH_URI_PATH, Collections.emptyMap(), queryEntity);
    assertEquals(200, response.getStatusLine().getStatusCode());

    assertTotalHits(response, 1);
  }

  @Test
  public void testSearchByApplicantFirstNamePhonetic() throws Exception {
    HttpEntity queryEntity = new NStringEntity(
        readResource(QUERY_FILE_APPLICANT_FIRST_NAME_PHONETIC), ContentType.APPLICATION_JSON);
    Response response = restClient
        .performRequest("POST", SEARCH_URI_PATH, Collections.emptyMap(), queryEntity);
    assertEquals(200, response.getStatusLine().getStatusCode());

    assertTotalHits(response, 2);
  }

  private static String getCalsnsJdbcUrl() throws Exception {
    return String.format(CALSNS_JDBC_URL_TEMPLATE, tempDbFile, CALSNS_SCHEMA, CALSNS_SCHEMA);
  }

  private static void setUpCalsns() throws Exception {
    new DatabaseHelper(getCalsnsJdbcUrl(), CALSNS_USER, CALSNS_PASSWORD)
        .runScript(CALSNS_TEST_DATA_SCRIPT, CALSNS_SCHEMA);
  }
}
