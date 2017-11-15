package gov.ca.cwds.neutron.vox.jmx;

import java.util.HashMap;
import java.util.Map;

import gov.ca.cwds.neutron.vox.jmx.cmd.VoxCommandFetchLogs;
import gov.ca.cwds.neutron.vox.jmx.cmd.VoxCommandFlightHistory;
import gov.ca.cwds.neutron.vox.jmx.cmd.VoxCommandLastRunStatus;
import gov.ca.cwds.neutron.vox.jmx.cmd.VoxCommandShutdown;

public enum VoxCommandType {

  STATUS(VoxCommandLastRunStatus.class, "status"),

  SHUTDOWN(VoxCommandShutdown.class, "shutdown"),

  HISTORY(VoxCommandFlightHistory.class, "history"),

  LOGS(VoxCommandFetchLogs.class, "logs"),

  DISABLE(VoxCommandLastRunStatus.class, "disable"),

  ENABLE(VoxCommandLastRunStatus.class, "enable"),

  PAUSE(VoxCommandLastRunStatus.class, "pause"),

  RESUME(VoxCommandLastRunStatus.class, "resume")

  ;

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
