package gov.ca.cwds.neutron.util;

import java.nio.file.Paths;

public final class NeutronStringUtils {

  private NeutronStringUtils() {
    // static methods only
  }

  public static String filePath(String path) {
    return Paths.get(path).normalize().getParent().toAbsolutePath().toString();
  }

}
