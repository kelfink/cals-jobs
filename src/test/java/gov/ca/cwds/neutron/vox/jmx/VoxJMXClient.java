package gov.ca.cwds.neutron.vox.jmx;

import java.io.IOException;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.jobs.schedule.DefaultFlightSchedule;
import gov.ca.cwds.neutron.log.JobLogs;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class VoxJMXClient implements ApiMarker, AutoCloseable {

  private static final Logger LOGGER = LoggerFactory.getLogger(VoxJMXClient.class);

  private static final String DEFAULT_HOST = "localhost";
  private static final String DEFAULT_PORT = "1098";

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
      final JMXServiceURL url =
          new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi");
      jmxConnector = JMXConnectorFactory.connect(url);
      mbeanServerConnection = jmxConnector.getMBeanServerConnection();
      LOGGER.info("mbean count: {}", mbeanServerConnection.getMBeanCount());
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "JMX CONNECTION FAILED! {}", e.getMessage());
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
      final ObjectName mbeanName = new ObjectName("Neutron:rocket=client");
      final VoxLaunchPadMBean mbeanProxy = MBeanServerInvocationHandler
          .newProxyInstance(mbeanServerConnection, mbeanName, VoxLaunchPadMBean.class, true);
      LOGGER.info("status::{}", mbeanProxy.status());
      return mbeanProxy;
    } catch (Exception e) {
      throw JobLogs.checked(LOGGER, e, "JMX CONNECTION FAILED! {}", e.getMessage());
    }
  }

  public static void main(String[] args) throws IOException, MalformedObjectNameException {
    final OptionParser parser = new OptionParser("h:p:");
    final OptionSet options = parser.parse(args);

    final String host = options.has("h") ? (String) options.valueOf("h") : DEFAULT_HOST;
    final String port = options.has("p") ? (String) options.valueOf("p") : DEFAULT_PORT;

    try (VoxJMXClient client = new VoxJMXClient(host, port)) {
      client.connect();
      client.proxy(DefaultFlightSchedule.CLIENT.getShortName());
    } catch (Exception e) {
      LOGGER.error("FAILED TO CONNECT! host: {}, port: {}", host, port, e);
      Runtime.getRuntime().exit(-1); // NOSONAR
    }
  }

}
