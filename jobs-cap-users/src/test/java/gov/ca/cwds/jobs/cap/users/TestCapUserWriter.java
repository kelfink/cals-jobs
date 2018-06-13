package gov.ca.cwds.jobs.cap.users;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.jobs.common.job.BulkWriter;

import java.util.ArrayList;
import java.util.List;

public class TestCapUserWriter implements BulkWriter<User> {

  private static List<User> items = new ArrayList<>();

  @Override
  public void write(List<User> items) {
    TestCapUserWriter.items.addAll(items);
  }

  public static List<User> getItems() {
    return items;
  }

  @Override
  public void destroy() {

  }
}
