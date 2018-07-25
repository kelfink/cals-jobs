package gov.ca.cwds.jobs.cap.users.iterator;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cap.users.dto.ChangedUserDto;
import gov.ca.cwds.jobs.cap.users.service.CapChangedUsersService;
import gov.ca.cwds.jobs.cap.users.service.CwsChangedUsersService;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CapUsersIncrementalJobIterator implements CapUsersIterator {

  private boolean cwsChecked;
  private boolean capChecked;
  private Set<String> updatedUserIds = new HashSet<>();

  @Inject
  private CwsChangedUsersService cwsChangedUsersService;

  @Inject
  private CapChangedUsersService capChangedUsersService;

  @Override
  public List<ChangedUserDto> getNextPortion() {
    List<ChangedUserDto> result = null;
    if (capChecked) {
      return Collections.emptyList();
    }
    if (!cwsChecked) {
      result = cwsChangedUsersService.getCwsChanges();
    }
    if (!CollectionUtils.isEmpty(result)) {
      result.forEach(e -> updatedUserIds.add(e.getDTO().getId()));
      cwsChecked = true;
      return result;
    }
    result = capChangedUsersService.getCapChanges();
    capChecked = true;
    return result.stream()
            .filter(e -> !updatedUserIds.contains(e.getDTO().getId()))
            .collect(Collectors.toList());
  }

}
