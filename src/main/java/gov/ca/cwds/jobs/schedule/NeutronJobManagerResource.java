package gov.ca.cwds.jobs.schedule;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.data.std.ApiMarker;

@Path("/neutron")
public class NeutronJobManagerResource implements ApiMarker {

  private static final Logger LOGGER = LoggerFactory.getLogger(NeutronJobManagerResource.class);

  public NeutronJobManagerResource() {
    // default, no-op
  }

  @GET
  @Path("test")
  @Produces(MediaType.TEXT_PLAIN)
  public String testIsServerAlive() {
    return "\n\nCurrent server time: " + new SimpleDateFormat().format(new Date()) + "\n\n";
  }

  /**
   * <pre>
   * {@code curl -X POST "http://localhost:9999/job/client/run_initial" -k -d @in.txt}
   * </pre>
   * 
   * @param jobName job name
   * @param command management command
   * @param body configuration file content in request body
   * @return JSON acknowledgement
   */
  @Path("/{jobName}/{command}")
  @POST
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public String masterAndCommander(@PathParam("jobName") String jobName,
      @PathParam("command") String command, String body) {
    LOGGER.info("job name: {}, command: {}, POST body len: {}", jobName, command, body.length());
    final StringBuilder buf = new StringBuilder();
    buf.append("{\"result\":\"ACK\",\"job_name\":\"").append(jobName).append("\",\"command\":\"")
        .append(command).append("\"}");
    final NeutronJobManagementBean ufb = new NeutronJobManagementBean(jobName, command, body);
    // NOTE: Handle command.
    return buf.toString();
  }

}
