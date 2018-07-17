package gov.ca.cwds.jobs.cap.users;

import com.google.inject.Inject;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UsersPage;
import gov.ca.cwds.jobs.cap.users.inject.PerryApiPassword;
import gov.ca.cwds.jobs.cap.users.inject.PerryApiUrl;
import gov.ca.cwds.jobs.cap.users.inject.PerryApiUser;
import org.apache.commons.codec.binary.Base64;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;

public class CapUsersJobBatchIterator implements CapUsersIterator {

  private String paginationToken;
  private boolean eol;

  private static final String PAGINATION_TOKEN = "paginationToken";

  @Inject
  private Client client;

  @Inject
  @PerryApiUrl
  private String apiURL;

  @Inject
  @PerryApiUser
  private String perryApiUser;

  @Inject
  @PerryApiPassword
  private String perryApiPassword;

  private String basicAuthHeader;

  @Inject
  public void init() {
    String authString = perryApiUser + ":" + perryApiPassword;
    byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
    String authStringEnc = new String(authEncBytes);
    basicAuthHeader = "Basic " + authStringEnc;
  }

  public List<User> getNextPortion() {
    if (eol) {
      return Collections.emptyList();
    }
    UsersPage page = client
            .target(apiURL)
            .queryParam(PAGINATION_TOKEN, paginationToken)
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, basicAuthHeader)
            .get(UsersPage.class);

    paginationToken = page.getPaginationToken();
    if (paginationToken == null) {
      eol = true;
    }
    return page.getUserList();
  }
}
