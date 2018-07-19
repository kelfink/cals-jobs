package gov.ca.cwds.jobs.cap.users;

import gov.ca.cwds.idm.dto.User;

import java.util.Collections;
import java.util.List;

public class MockedIterator implements CapUsersIterator {
  private int i = 0;

  public static int NUMBER_OF_USERS = 20;

  public List<User> getNextPortion() {
    return i == NUMBER_OF_USERS ? Collections.emptyList() : createUserList();
  }

  private List<User> createUserList() {
    i++;
    return Collections.singletonList(createUser(i));
  }

  private User createUser(int i) {
    User result = new User();
    result.setId(String.valueOf(i));
    return result;
  }
}