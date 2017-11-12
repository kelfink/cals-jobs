package gov.ca.cwds.neutron.vox.jmx;

import java.util.HashMap;
import java.util.Map;

import gov.ca.cwds.neutron.vox.jmx.cmd.VoxCommandLastRunStatus;

public enum VoxCommandType {

  LAST_RUN_STATUS(VoxCommandLastRunStatus.class, "status"),

  SHUTDOWN_COMMAND_CENTER(VoxCommandLastRunStatus.class, "shutdown");

  private final Class<? extends VoxCommandAction> klass;
  private final String key;

  private static final Map<String, VoxCommandType> typeMap = new HashMap<>();

  static {
    for (VoxCommandType e : VoxCommandType.values()) {
      typeMap.put(e.key, e);
    }
  }

  private VoxCommandType(final Class<? extends VoxCommandAction> klass, String key) {
    this.klass = klass;
    this.key = key;
  }

  public static VoxCommandType lookup(String key) {
    return VoxCommandType.typeMap.get(key);
  }

  public Class<? extends VoxCommandAction> getKlass() {
    return klass;
  }

  public String getKey() {
    return key;
  }

}
