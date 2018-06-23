package gov.ca.cwds.jobs.cals.facility;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.dropwizard.jackson.Jackson;
import java.util.Optional;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * Created by Alexander Serbin on 3/28/2018.
 */
public final class AssertFacilityHelper {

  private AssertFacilityHelper() {
  }

  public static void assertFacility(String fixturePath, String facilityId)
      throws JSONException, JsonProcessingException {
    JSONAssert.assertEquals(
        fixture(fixturePath),
        Jackson.newObjectMapper().writeValueAsString(getFacilityById(facilityId)),
        JSONCompareMode.STRICT);
  }

  private static ChangedFacilityDto getFacilityById(String facilityId) {
    Optional<ChangedFacilityDto> optional = TestWriter.getItems().stream()
        .filter(o -> facilityId.equals(o.getId())).findAny();
    assertTrue(optional.isPresent());
    return optional.orElse(null);
  }

}
