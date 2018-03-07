package gov.ca.cwds.jobs.cals.rfa;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import gov.ca.cwds.cals.service.dto.rfa.collection.RFA1aFormCollectionDTO;
import gov.ca.cwds.rest.api.Response;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static gov.ca.cwds.cals.Constants.RFA;
import static gov.ca.cwds.cals.Constants.UnitOfWork.CALSNS;
import static gov.ca.cwds.jobs.cals.rfa.ChangedRFA1aFormsResourceTest.PATH_CHANGED_RFA_1A_FORMS;
import static gov.ca.cwds.jobs.cals.rfa.ChangedRFA1aFormsResourceTest.PATH_PARAM_DATE_AFTER;

/**
 * @author CWDS TPT-2
 */
@Api(tags = {RFA})
@Path(PATH_CHANGED_RFA_1A_FORMS)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChangedRFA1aFormsResource {

  @Inject
  private ChangedRFA1aFormsService changedRFAFormsService;

  @UnitOfWork(CALSNS)
  @GET
  @Path("/{" + PATH_PARAM_DATE_AFTER + "}")
  @Timed
  @ApiResponses(
      value = {
          @ApiResponse(code = 401, message = "Not Authorized"),
          @ApiResponse(code = 404, message = "Not found"),
          @ApiResponse(code = 406, message = "Accept Header not supported")
      }
  )
  @ApiOperation(
      value = "Returns all RFA 1A Forms that were changed after given time",
      response = RFA1aFormCollectionDTO.class
  )
  public Response getChangedApplicationForms(
      @PathParam(PATH_PARAM_DATE_AFTER)
      @ApiParam(required = true, name = PATH_PARAM_DATE_AFTER, value = "date/time")
          String dateAfter
  ) {
    // TODO uncomment when RFA 1a job is ready
/*
    List<ChangedRFA1aFormDTO> changedRFA1aFormDTOList = changedRFAFormsService
            .doIncrementalLoad(DateTimeUtils.toLocalDateTime(dateAfter)).collect(Collectors.toList());
    return new CollectionDTO<>(changedRFA1aFormDTOList);
*/
   return null;
  }
}
