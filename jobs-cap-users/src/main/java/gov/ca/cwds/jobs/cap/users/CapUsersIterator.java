package gov.ca.cwds.jobs.cap.users;

import gov.ca.cwds.idm.dto.User;

import java.util.List;

public interface CapUsersIterator {
  List<User> getNextPortion();
}
