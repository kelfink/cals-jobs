package gov.ca.cwds.neutron.vox.jmx;

import java.io.IOException;
import java.util.function.BiFunction;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.lang3.tuple.Triple;

import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.DefaultFlightSchedule;
import gov.ca.cwds.neutron.jetpack.ConditionalLogger;
import gov.ca.cwds.neutron.jetpack.JetPackLogger;
import gov.ca.cwds.neutron.jetpack.JobLogs;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class VoxJMXClient implements AutoCloseable {

  private static final ConditionalLogger LOGGER = new JetPackLogger(VoxJMXClient.class);

  private static final String DEFAULT_HOST = "localhost";
  private static final String DEFAULT_PORT = "1098";

  private static boolean testMode;

  private BiFunction<String, String, JMXConnector> makeConnector = (the_host, the_port) -> {
    try {
      return JMXConnectorFactory.connect(new JMXServiceURL(
          "service:jmx:rmi:///jndi/rmi://" + the_host + ":" + the_port + "/jmxrmi"));
    } catch (IOException e) {
      throw JobLogs.runtime(LOGGER, e, "FAILED TO CONNECT VIA JMX! {}", e.getMessage());
    }
  };

  private final String host;
  private final String port;

  private JMXConnector jmxConnector;
  private MBeanServerConnection mbeanServerConnection;

  public VoxJMXClient(final String host, final String port) {
    this.host = host;
    this.port = port;
  }

  public void connect() throws NeutronException {
    try {
      jmxConnector = makeConnector.apply(host, port);
      mbeanServerConnection = jmxConnector.getMBeanServerConnection();
      LOGGER.info("mbean count: {}", mbeanServerConnection.getMBeanCount());
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "OOPS! DIDN'T CONNECT!! {}", e.getMessage());
    }
  }

  @Override
  public void close() throws Exception {
    if (jmxConnector != null) {
      jmxConnector.close();
    }
  }

  public VoxLaunchPadMBean proxy(String rocketName) throws NeutronException {
    try {
      final ObjectName mbeanName = new ObjectName("Neutron:rocket=" + rocketName);
      final VoxLaunchPadMBean mbeanProxy = MBeanServerInvocationHandler
          .newProxyInstance(mbeanServerConnection, mbeanName, VoxLaunchPadMBean.class, true);
      LOGGER.info("status::{}", () -> mbeanProxy.status());
      return mbeanProxy;
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "FAILED TO GET MBEAN PROXY! {}", e.getMessage());
    }
  }

  public BiFunction<String, String, JMXConnector> getMakeConnector() {
    return makeConnector;
  }

  public void setMakeConnector(BiFunction<String, String, JMXConnector> makeConnector) {
    this.makeConnector = makeConnector;
  }

  public static Triple<String, String, String> parseCommandLine(final String[] args) {
    LOGGER.info("PARSE COMMAND LINE");
    final OptionParser parser = new OptionParser("h:p:r:");
    final OptionSet options = parser.parse(args);

    final String host = options.has("h") ? (String) options.valueOf("h") : DEFAULT_HOST;
    final String port = options.has("p") ? (String) options.valueOf("p") : DEFAULT_PORT;
    final String rocket = options.has("r") ? (String) options.valueOf("r")
        : DefaultFlightSchedule.CLIENT.getShortName();
    LOGGER.info("SETTINGS: host: {}, port: {}, rocket: {}", host, port, rocket);
    return Triple.of(host, port, rocket);
  }

  public JMXConnector getJmxConnector() {
    return jmxConnector;
  }

  public void setJmxConnector(JMXConnector jmxConnector) {
    this.jmxConnector = jmxConnector;
  }

  public MBeanServerConnection getMbeanServerConnection() {
    return mbeanServerConnection;
  }

  public void setMbeanServerConnection(MBeanServerConnection mbeanServerConnection) {
    this.mbeanServerConnection = mbeanServerConnection;
  }

  public String getHost() {
    return host;
  }

  public String getPort() {
    return port;
  }

  public static void launch(final Triple<String, String, String> triple) {
    final String host = triple.getLeft();
    final String port = triple.getMiddle();
    final String rocket = triple.getRight();

    try (VoxJMXClient client = new VoxJMXClient(host, port)) {
      LOGGER.info("CONNECT JMX...");
      client.connect();
      final VoxLaunchPadMBean mbeanProxy = client.proxy(rocket);
      LOGGER.info("status::{}", mbeanProxy::status);
    } catch (Exception e) {
      throw JobLogs.runtime(LOGGER, e, "JMX ERROR! host: {}, port: {}, rocket: {}", host, port,
          rocket);
    }
  }

  public static boolean isTestMode() {
    return testMode;
  }

  public static void setTestMode(boolean testMode) {
    VoxJMXClient.testMode = testMode;
  }

  public static void main(String[] args) {
    LOGGER.info("BEGIN");
    launch(parseCommandLine(args));
  }

}
