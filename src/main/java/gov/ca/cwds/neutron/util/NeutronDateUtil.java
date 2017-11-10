package gov.ca.cwds.neutron.util;

import java.util.Date;

public class NeutronDateUtil {

  private NeutronDateUtil() {
    // no-op
  }

  public static Date freshDate(Date incoming) {
    return incoming != null ? new Date(incoming.getTime()) : null;
  }

}
