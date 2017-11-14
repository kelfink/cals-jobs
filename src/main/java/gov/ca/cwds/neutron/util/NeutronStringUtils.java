package gov.ca.cwds.neutron.util;

import java.io.File;

public final class NeutronStringUtils {

  private NeutronStringUtils() {
    // static methods only
  }

  public static String filePath(String path) {
    return path.substring(0, path.lastIndexOf(File.separatorChar));
  }

}
