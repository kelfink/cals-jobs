package gov.ca.cwds.jobs.cals.facility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import gov.ca.cwds.DataSourceName;
import gov.ca.cwds.cals.service.dto.rfa.collection.CollectionDTO;
import gov.ca.cwds.jobs.cals.TestCalsJobsApplication;
import gov.ca.cwds.jobs.cals.TestCalsJobsConfiguration;
import gov.ca.cwds.jobs.common.job.timestamp.TimestampOperator;
import gov.ca.cwds.test.support.BaseApiTest;
import gov.ca.cwds.test.support.BaseDropwizardApplication;
import gov.ca.cwds.test.support.DatabaseHelper;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jackson.Jackson;
import liquibase.exception.LiquibaseException;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static gov.ca.cwds.jobs.cals.facility.ChangedFacilityResource.DATE_AFTER;
import static gov.ca.cwds.jobs.cals.facility.ChangedFacilityResource.PATH_CHANGED_FACILITY;
import static gov.ca.cwds.jobs.cals.facility.ChangedFacilityResource.PATH_INITIAL;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.junit.Assert.assertEquals;

/**
 * Created by Alexander Serbin on 2/27/2018.
 */
public class InitialAndIncrementalFacilityLoadTest extends BaseApiTest<TestCalsJobsConfiguration> {

    static final String PATH_INITIAL_FACILITY_LOAD = PATH_CHANGED_FACILITY + "/" + PATH_INITIAL;

    static final String LIS_INITIAL_LOAD_FACILITY_ID = "9069";
    static final String CWSCMS_INITIAL_LOAD_FACILITY_ID = "3w6sOO50Ki";

    static final String CWSCMS_INCREMENTAL_LOAD_NEW_FACILITY_ID = "AAAAAAAAAA";
    static final String CWSCMS_INCREMENTAL_LOAD_UPDATED_FACILITY_ID = "AP9Ewb409u";
    static final String CWSCMS_INCREMENTAL_LOAD_DELETED_FACILITY_ID = "AyT7r860AB";
    static final String LIS_INCREMENTAL_LOAD_UPDATED_FACILITY_ID = "193600001";

    @ClassRule
    public static final BaseDropwizardApplication<TestCalsJobsConfiguration> application
            = new BaseDropwizardApplication<>(TestCalsJobsApplication.class, "config/test-application.yml");

    @Override
    protected BaseDropwizardApplication<TestCalsJobsConfiguration> getApplication() {
        return application;
    }


    @BeforeClass
    public static void beforeClass() throws Exception {
        System.out.println("Setup database has been started!!!");
        DatabaseHelper.setUpDatabase(application.getConfiguration().getLisDataSourceFactory(), DataSourceName.LIS);
        DatabaseHelper.setUpDatabase(application.getConfiguration().getFasDataSourceFactory(), DataSourceName.FAS);
        DatabaseHelper.setUpDatabase(application.getConfiguration().getCmsrsDataSourceFactory(), DataSourceName.CWSRS);
        System.out.println("Setup database has been finished!!!");
    }

    @Test
    public void initialAndIncrementalLoadTest() throws Exception {
        initialLoadTest();
        incrementalLoadTest();
    }

    private void incrementalLoadTest() throws IOException, LiquibaseException, JSONException {
        WebTarget target = clientTestRule.target(PATH_CHANGED_FACILITY + "/?" + DATE_AFTER + "="
                + LocalDateTime.now().format(TimestampOperator.DATE_TIME_FORMATTER));
        assertEquals(0, getFacilities(target).getCollection().size());
        LocalDateTime dateAfter = LocalDateTime.now();
        addCwsDataForIncrementalLoad();
        addLisDataForIncrementalLoad();
        target = clientTestRule.target(PATH_CHANGED_FACILITY + "/?" + DATE_AFTER + "=" + dateAfter.format(TimestampOperator.DATE_TIME_FORMATTER));
        CollectionDTO<TestChangedFacilityDTO> facilities = getFacilities(target);
        assertEquals(4, facilities.getCollection().size());
        assertFacility(facilities, CWSCMS_INCREMENTAL_LOAD_NEW_FACILITY_ID, "fixtures/cwsrs_new_facility.json");
        assertFacility(facilities, CWSCMS_INCREMENTAL_LOAD_UPDATED_FACILITY_ID, "fixtures/cwsrs_updated_facility.json");
        assertFacility(facilities, CWSCMS_INCREMENTAL_LOAD_DELETED_FACILITY_ID, "fixtures/cwsrs_deleted_facility.json");
        assertFacility(facilities, LIS_INCREMENTAL_LOAD_UPDATED_FACILITY_ID, "fixtures/lis_updated_facility.json");
    }

    private void assertFacility(CollectionDTO<TestChangedFacilityDTO> facilities, String facilityId, String fixturePath)
            throws JsonProcessingException, JSONException {
        Optional<TestChangedFacilityDTO> facility =
                facilities.getCollection().stream().filter((o) -> facilityId.equals(o.getId())).findAny();
        JSONAssert.assertEquals(
                fixture(fixturePath),
                clientTestRule.getMapper().writeValueAsString(facility), JSONCompareMode.STRICT);
    }

    private void addCwsDataForIncrementalLoad() throws LiquibaseException {
        DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        DataSourceFactory cwsDataSourceFactory = application.getConfiguration().getCmsrsDataSourceFactory();
        DatabaseHelper cwsDatabaseHelper = new DatabaseHelper(cwsDataSourceFactory.getUrl(),
                cwsDataSourceFactory.getUser(), cwsDataSourceFactory.getPassword());
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("now", datetimeFormatter.format(LocalDateTime.now()));
        cwsDatabaseHelper.runScript("liquibase/cwsrs_facility_incremental_load.xml", parameters, "CWSCMSRS");
    }

    private void addLisDataForIncrementalLoad() throws LiquibaseException {
        DataSourceFactory lisDataSourceFactory = application.getConfiguration().getLisDataSourceFactory();
        DatabaseHelper lisDatabaseHelper = new DatabaseHelper(lisDataSourceFactory.getUrl(),
                lisDataSourceFactory.getUser(), lisDataSourceFactory.getPassword());
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("now", ChangedFacilityService.lisTimestampFormatter.format(LocalDateTime.now()));
        lisDatabaseHelper.runScript("liquibase/lis_facility_incremental_load.xml", parameters, "lis");
    }

    private void initialLoadTest() throws IOException, JSONException {
        WebTarget target = clientTestRule.target(PATH_INITIAL_FACILITY_LOAD);
        CollectionDTO<TestChangedFacilityDTO> facilities = getFacilities(target);
        assertEquals(309, facilities.getCollection().size());
        List<TestChangedFacilityDTO> filteredList =
                facilities.getCollection().stream().filter(
                        (o) -> CWSCMS_INITIAL_LOAD_FACILITY_ID.equals(o.getId()) || LIS_INITIAL_LOAD_FACILITY_ID.equals(o.getId()))
                        .collect(Collectors.toList());
        Optional<TestChangedFacilityDTO> cwsCmsFacility =
                filteredList.stream().filter((o) -> CWSCMS_INITIAL_LOAD_FACILITY_ID.equals(o.getId())).findAny();
        Optional<TestChangedFacilityDTO> lisFacility =
                filteredList.stream().filter((o) -> LIS_INITIAL_LOAD_FACILITY_ID.equals(o.getId())).findAny();
        JSONAssert.assertEquals(
                fixture("fixtures/facilities-initial-load-cwscms.json"),
                clientTestRule.getMapper().writeValueAsString(cwsCmsFacility), JSONCompareMode.STRICT);
        JSONAssert.assertEquals(
                fixture("fixtures/facilities-initial-load-lis.json"),
                clientTestRule.getMapper().writeValueAsString(lisFacility), JSONCompareMode.STRICT);
    }

    private static CollectionDTO<TestChangedFacilityDTO> getFacilities(WebTarget target) throws IOException {
        Invocation.Builder invocation = target.request(MediaType.APPLICATION_JSON);
        return Jackson.newObjectMapper().readValue(
                invocation.get(String.class),
                new TypeReference<CollectionDTO<TestChangedFacilityDTO>>() {}
        );
    }

}
