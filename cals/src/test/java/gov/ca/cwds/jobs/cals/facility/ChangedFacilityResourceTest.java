package gov.ca.cwds.jobs.cals.facility;

import com.fasterxml.jackson.core.type.TypeReference;
import gov.ca.cwds.DataSourceName;
import gov.ca.cwds.cals.service.dto.rfa.collection.CollectionDTO;
import gov.ca.cwds.jobs.cals.TestCalsJobsApplication;
import gov.ca.cwds.jobs.cals.TestCalsJobsConfiguration;
import gov.ca.cwds.test.support.BaseApiTest;
import gov.ca.cwds.test.support.BaseDropwizardApplication;
import gov.ca.cwds.test.support.DatabaseHelper;
import io.dropwizard.jackson.Jackson;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import static gov.ca.cwds.cals.Constants.API.FACILITIES;
import static gov.ca.cwds.test.utils.AssertResponseHelper.assertEqualsResponse;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.junit.Assert.assertEquals;

/**
 * @author CWDS TPT-2
 */
@Ignore
public class ChangedFacilityResourceTest extends BaseApiTest<TestCalsJobsConfiguration> {

  static final String PATH_CHANGED_FACILITY = "changed-" + FACILITIES;
  static final String PATH_INITIAL = "initial";
  static final String PATH_INITIAL_FACILITY_LOAD = PATH_CHANGED_FACILITY + "/" + PATH_INITIAL;
  static final String DATE_AFTER = "dateAfter";
  static final String LIS_DATE_AFTER = "lisDateAfter";

  @ClassRule
  public static final BaseDropwizardApplication<TestCalsJobsConfiguration> application
          = new BaseDropwizardApplication<>(TestCalsJobsApplication.class, "config/test-application.yml");

  @Override
  protected BaseDropwizardApplication<TestCalsJobsConfiguration> getApplication() {
    return application;
  }

  @BeforeClass
  public static void beforeClass() throws Exception {
    DatabaseHelper.setUpDatabase(application.getConfiguration().getLisDataSourceFactory(), DataSourceName.LIS);
    DatabaseHelper.setUpDatabase(application.getConfiguration().getCmsrsDataSourceFactory(), DataSourceName.CWSRS);
  }

    @Test
    public void getChangedFacilitiesTest() throws Exception {
        WebTarget target = clientTestRule.target(PATH_CHANGED_FACILITY + "/?" + DATE_AFTER + "=2017-12-19&"
                + LIS_DATE_AFTER + "=2017-12-19");
        Invocation.Builder invocation = target.request(MediaType.APPLICATION_JSON);
        CollectionDTO<TestChangedFacilityDTO> facilities = Jackson.newObjectMapper().readValue(
                invocation.get(String.class), new TypeReference<CollectionDTO<TestChangedFacilityDTO>>() {
                });
        assertEquals(79, facilities.getCollection().size());
        assertResponse(facilities);
    }

  @Test
  public void initialLoadTest() throws Exception {
    WebTarget target = clientTestRule.target(PATH_INITIAL_FACILITY_LOAD + "/?" + LIS_DATE_AFTER + "=2017-12-19");
    Invocation.Builder invocation = target.request(MediaType.APPLICATION_JSON);
    CollectionDTO<TestChangedFacilityDTO> facilities = Jackson.newObjectMapper().readValue(
              invocation.get(String.class), new TypeReference<CollectionDTO<TestChangedFacilityDTO>>() { });
    assertEquals(79, facilities.getCollection().size());
    assertResponse(facilities);
  }

    private void assertResponse(CollectionDTO<TestChangedFacilityDTO> changedFacilities) throws Exception {
        String fixture = fixture("fixtures/changed-facility-service.json");
        //assertEqualsResponse(fixture, transformDTOtoJSON(changedFacilities));
    }

}
