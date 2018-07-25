package gov.ca.cwds.jobs.cap.users.iterator;

import gov.ca.cwds.jobs.cap.users.dto.ChangedUserDto;

import java.util.List;

public interface CapUsersIterator {
  List<ChangedUserDto> getNextPortion();
}
