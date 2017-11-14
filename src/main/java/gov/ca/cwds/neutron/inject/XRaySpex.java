package gov.ca.cwds.neutron.inject;

import java.lang.management.ManagementFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weakref.jmx.MBeanExporter;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gov.ca.cwds.jobs.schedule.LaunchCommand;
import gov.ca.cwds.neutron.atom.AtomLaunchDirector;
import gov.ca.cwds.neutron.atom.AtomLaunchPad;
import gov.ca.cwds.neutron.launch.LaunchCommandSettings;
import gov.ca.cwds.neutron.manage.rest.NeutronRestServer;

@Singleton
public class XRaySpex implements AtomCommandControlManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(XRaySpex.class);

  private final LaunchCommandSettings settings;
  private final AtomLaunchDirector launchScheduler;

  /**
   * REST administration. Started if enabled in #.
   */
  private NeutronRestServer restServer = new NeutronRestServer();

  @Inject
  public XRaySpex(final LaunchCommandSettings settings, final AtomLaunchDirector launchScheduler) {
    this.settings = settings;
    this.launchScheduler = launchScheduler;
  }

  @Override
  public void initCommandControl() {
    if (LaunchCommand.getSettings().isExposeJmx()) {
      exposeJMX();
    }

    if (LaunchCommand.getSettings().isExposeRest()) {
      exposeREST();
    }
  }

  protected void exposeREST() {
    // Jetty for REST administration.
    Thread jettyServer = new Thread(restServer::run);
    jettyServer.start();
  }

  protected void exposeJMX() {
    LOGGER.warn("\n>>>>>>> ENABLE JMX! <<<<<<<\n");
    final MBeanExporter exporter = new MBeanExporter(ManagementFactory.getPlatformMBeanServer());
    for (AtomLaunchPad pad : launchScheduler.getLaunchPads().values()) {
      exporter.export("Neutron:rocket=" + pad.getFlightSchedule().getShortName(), pad);
    }

    // Expose Command Center methods to JMX.
    exporter.export("Neutron:runner=Launch_Command", this);
    LOGGER.info("MBeans: {}", exporter.getExportedObjects());

    // Expose Guice bean attributes to JMX.
    // Manager.manage("Neutron_Guice", injector);
  }

  public LaunchCommandSettings getSettings() {
    return settings;
  }

}
