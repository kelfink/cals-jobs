package gov.ca.cwds.jobs.cap.users;

import com.google.inject.Inject;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UsersPage;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CapUsersJobBatchIterator implements CapUsersIterator {

  private String paginationToken;
  private AtomicBoolean eol = new AtomicBoolean(false);

  private static final String PAGINATION_TOKEN = "paginationToken";

  @Inject
  private Client client;

  @Inject
  @PerryApiUrl
  private String apiURL;

  public List<User> getNextPortion() {
    if (eol.get()) {
      return Collections.emptyList();
    }
    UsersPage page = client
            .target(apiURL)
            .queryParam(PAGINATION_TOKEN, paginationToken)
            .request(MediaType.APPLICATION_JSON)
            .get(UsersPage.class);

    paginationToken = page.getPaginationToken();
    if (paginationToken == null) {
      eol.set(true);
    }
    return page.getUserList();
  }
}
