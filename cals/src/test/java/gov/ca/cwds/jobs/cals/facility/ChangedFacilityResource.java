package gov.ca.cwds.jobs.cals.facility;

import com.google.inject.Inject;
import gov.ca.cwds.cals.service.dto.rfa.collection.CollectionDTO;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

import static gov.ca.cwds.cals.util.DateTimeUtils.toDate;
import static gov.ca.cwds.jobs.cals.facility.ChangedFacilityResourceTest.DATE_AFTER;
import static gov.ca.cwds.jobs.cals.facility.ChangedFacilityResourceTest.LIS_DATE_AFTER;
import static gov.ca.cwds.jobs.cals.facility.ChangedFacilityResourceTest.PATH_CHANGED_FACILITY;
import static gov.ca.cwds.jobs.cals.facility.ChangedFacilityResourceTest.PATH_INITIAL;

/**
 * @author CWDS TPT-2
 */
@Path(PATH_CHANGED_FACILITY)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChangedFacilityResource {

  @Inject
  private ChangedFacilityService changedFacilityService;

  @GET
  public CollectionDTO<ChangedFacilityDTO> getChangedFacilities(
      @QueryParam(DATE_AFTER)
          String stringDateAfter,
      @QueryParam(LIS_DATE_AFTER)
          String stringLisDateAfter
  ) throws ParseException {
    List<ChangedFacilityDTO> changedFacilityDTOList = changedFacilityService
            .changedFacilitiesStream(
                    toDate(stringDateAfter), toDate(stringLisDateAfter)).collect(Collectors.toList());
    return new CollectionDTO<>(changedFacilityDTOList);
  }

  @GET
  @Path("/" + PATH_INITIAL)
  public CollectionDTO<ChangedFacilityDTO> initialLLoadedFacilities(
          @QueryParam(LIS_DATE_AFTER)
                  String stringLisDateAfter
  ) throws ParseException {
    List<ChangedFacilityDTO> changedFacilityDTOList = changedFacilityService
            .changedFacilitiesStream(
                    null, toDate(stringLisDateAfter)).collect(Collectors.toList());
    return new CollectionDTO<>(changedFacilityDTOList);
  }

}
