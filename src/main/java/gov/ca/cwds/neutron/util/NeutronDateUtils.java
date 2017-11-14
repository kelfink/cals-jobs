package gov.ca.cwds.neutron.util;

import java.util.Date;

public class NeutronDateUtils {

  private NeutronDateUtils() {
    // no-op
  }

  public static Date freshDate(Date incoming) {
    return incoming != null ? new Date(incoming.getTime()) : null;
  }

}
