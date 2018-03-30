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

  static void assertFacility(String fixturePath, String facilityId)
      throws JSONException, JsonProcessingException {
    JSONAssert.assertEquals(
        fixture(fixturePath),
        Jackson.newObjectMapper().writeValueAsString(getFacilityById(facilityId)),
        JSONCompareMode.STRICT);
  }

  private static ChangedFacilityDTO getFacilityById(String facilityId) {
    Optional<ChangedFacilityDTO> optional = TestWriter.getItems().stream()
        .filter(o -> facilityId.equals(o.getId())).findAny();
    assertTrue(optional.isPresent());
    return optional.orElse(null);
  }

}
