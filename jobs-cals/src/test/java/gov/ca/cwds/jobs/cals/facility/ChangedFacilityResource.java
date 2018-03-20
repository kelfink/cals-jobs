package gov.ca.cwds.jobs.cals.facility;

import com.google.inject.Inject;
import gov.ca.cwds.cals.service.dto.rfa.collection.CollectionDTO;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.identifier.ChangedIdentifiersService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static gov.ca.cwds.cals.Constants.API.FACILITIES;
import static gov.ca.cwds.jobs.cals.facility.ChangedFacilityResource.PATH_CHANGED_FACILITY;
import static gov.ca.cwds.jobs.common.job.timestamp.TimestampOperator.DATE_TIME_FORMATTER;

/**
 * @author CWDS TPT-2
 */
@Path(PATH_CHANGED_FACILITY)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChangedFacilityResource {

  static final String PATH_CHANGED_FACILITY = "changed-" + FACILITIES;
  static final String PATH_INITIAL = "initial";
  static final String DATE_AFTER = "dateAfter";

  @Inject
  private ChangedFacilityService changedFacilityService;

  @Inject
  private ChangedIdentifiersService changedFacilityIdentifiersService;

  @GET
  public CollectionDTO<ChangedFacilityDTO> incrementalLoad(
          @QueryParam(DATE_AFTER)
                  String stringDateAfter) throws ParseException {
    LocalDateTime dateAfter = LocalDateTime.parse(stringDateAfter, DATE_TIME_FORMATTER);
    Stream<ChangedEntityIdentifier> identifiers = changedFacilityIdentifiersService.getIdentifiersForIncrementalLoad(dateAfter);
    List<ChangedFacilityDTO> changedFacilityDTOList = identifiers.map(changedFacilityService::loadEntity).collect(Collectors.toList());
    return new CollectionDTO<>(changedFacilityDTOList);
  }

  @GET
  @Path("/" + PATH_INITIAL)
  public CollectionDTO<ChangedFacilityDTO> initialLLoadedFacilities() throws ParseException {
    Stream<ChangedEntityIdentifier> identifiers = changedFacilityIdentifiersService.getIdentifiersForInitialLoad();
    List<ChangedFacilityDTO> changedFacilityDTOList = identifiers.map(changedFacilityService::loadEntity).collect(Collectors.toList());
    return new CollectionDTO<>(changedFacilityDTOList);
  }

}
