package gov.ca.cwds.jobs.cap.users.service;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UsersPage;

import java.util.List;
import java.util.Set;

public interface IdmService {
  UsersPage getUserPage(String paginationToken);

  List<User> getUsersByRacfIds(Set<String> racfIds);

  void getCapChanges();
}
