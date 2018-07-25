package gov.ca.cwds.jobs.cap.users.iterator;

import com.google.inject.Inject;
import gov.ca.cwds.idm.dto.UsersPage;
import gov.ca.cwds.jobs.cap.users.dto.ChangedUserDto;
import gov.ca.cwds.jobs.cap.users.service.IdmService;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CapUsersInitialJobIterator {

  private String paginationToken;
  private boolean allProcessed;


  @Inject
  private IdmService idmService;

  public List<ChangedUserDto> getNextPortion() {
    if (allProcessed) {
      return Collections.emptyList();
    }
    UsersPage page = idmService.getUserPage(paginationToken);
    paginationToken = page.getPaginationToken();
    if (paginationToken == null) {
      allProcessed = true;
    }
    if (CollectionUtils.isEmpty(page.getUserList())) {
      return Collections.emptyList();
    }
    return page.getUserList().stream()
            .map(u -> new ChangedUserDto(u, RecordChangeOperation.I))
            .collect(Collectors.toList());
  }
}
