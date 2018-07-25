package gov.ca.cwds.jobs.cap.users.service;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cap.users.dto.ChangedUserDto;

import java.util.Collections;
import java.util.List;

public class CapChangedUsersService {

  @Inject
  private IdmService idmService;

  public List<ChangedUserDto> getCapChanges() {
    //TODO COG-333
    idmService.getCapChanges();
    return Collections.emptyList();
  }
}
