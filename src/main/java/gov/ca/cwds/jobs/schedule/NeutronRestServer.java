package gov.ca.cwds.jobs.schedule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.core.Application;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.ServletModule;
import com.squarespace.jersey2.guice.JerseyGuiceModule;
import com.squarespace.jersey2.guice.JerseyGuiceUtils;

public class NeutronRestServer extends Application {

  private static final Logger LOGGER = LoggerFactory.getLogger(NeutronRestServer.class);

  private static boolean isAppRunning = true;

  @Override
  public Set<Class<?>> getClasses() {
    Set<Class<?>> s = new HashSet<>();
    s.add(NeutronJobManagerResource.class);
    return s;
  }

  public void run() {
    final Server server = new Server();

    final ServerConnector connector = new ServerConnector(server);
    connector.setPort(9999);
    server.setConnectors(new Connector[] {connector});

    final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    server.setHandler(context);

    final ServletHolder jerseyServlet =
        context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
    jerseyServlet.setInitOrder(0);

    // Tells the Jersey Servlet which REST service/class to load.
    final Map<String, String> resources = new ConcurrentHashMap<>();
    resources.put(ServerProperties.PROVIDER_CLASSNAMES,
        NeutronJobManagerResource.class.getCanonicalName() + ";");

    jerseyServlet.setInitParameters(resources);
    jerseyServlet.setInitParameter(ServerProperties.PROVIDER_PACKAGES,
        gov.ca.cwds.jobs.json.GsonMessageBodyHandler.class.getPackage().getName());

    LOGGER.info("init params: {}", jerseyServlet.getInitParameters());

    // Jersey-guice fix.
    final List<Module> modules = new ArrayList<>();
    modules.add(new JerseyGuiceModule("__HK2_Generated_0"));
    modules.add(new ServletModule());
    modules.add(new AbstractModule() {

      @Override
      protected void configure() {
        // no-op, for now.
      }

    });

    final Injector injector = Guice.createInjector(modules);
    JerseyGuiceUtils.install(injector);

    // Start Jetty.
    try {
      server.start();
      server.join();
    } catch (Exception e) {
      LOGGER.error("HTTP SERVER ERROR! {}", e.getMessage(), e);
    } finally {
      server.destroy();
    }
  }

  public static boolean isAppRunning() {
    return isAppRunning;
  }

  public static void setAppRunning(boolean isAppRunning) {
    NeutronRestServer.isAppRunning = isAppRunning;
  }

  public static void main(String[] args) throws Exception {
    try {
      new NeutronRestServer().run();
    } catch (Exception e) {
      LOGGER.error("HTTP SERVER ERROR! {}", e.getMessage(), e);
    }
  }

}
