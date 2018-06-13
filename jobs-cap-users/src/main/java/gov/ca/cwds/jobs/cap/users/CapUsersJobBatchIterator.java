package gov.ca.cwds.jobs.cap.users;

import com.google.inject.Inject;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UsersPage;
import gov.ca.cwds.idm.service.IdmService;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CapUsersJobBatchIterator {

  private String paginationToken;
  private AtomicBoolean eol = new AtomicBoolean(false) ;

  @Inject
  private IdmService idmService;

  public List<User> getNextPortion() {
    if(eol.get()) {
      return Collections.emptyList();
    }
    UsersPage page = idmService.getUserPage(paginationToken);
    paginationToken = page.getPaginationToken();
    if (paginationToken == null) {
      eol.set(true);
    }
    return page.getUserList();
  }
}
