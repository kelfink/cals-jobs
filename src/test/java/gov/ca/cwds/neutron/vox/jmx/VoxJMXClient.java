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

public class VoxJMXClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(VoxJMXClient.class);

  public static final String HOST = "localhost";
  public static final String PORT = "1097";

  public static void main(String[] args) throws IOException, MalformedObjectNameException {
    final JMXServiceURL url =
        new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + HOST + ":" + PORT + "/jmxrmi");
    final JMXConnector jmxConnector = JMXConnectorFactory.connect(url);
    final MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();

    // ObjectName should be same as your MBean name
    final ObjectName mbeanName = new ObjectName("Neutron:rocket");

    // Get MBean proxy instance that will be used to make calls to registered MBean.
    final VoxLaunchPadMBean mbeanProxy = MBeanServerInvocationHandler
        .newProxyInstance(mbeanServerConnection, mbeanName, VoxLaunchPadMBean.class, true);

    // let's make some calls to mbean through proxy and see the results.
    LOGGER.info("status::{}", mbeanProxy.status());

    // close the connection
    jmxConnector.close();
  }

}
