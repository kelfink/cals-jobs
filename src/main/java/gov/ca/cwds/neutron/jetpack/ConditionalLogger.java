package gov.ca.cwds.neutron.jetpack;

import java.util.function.Supplier;

import org.slf4j.Logger;

public interface ConditionalLogger extends Logger {

  void trace(String format, Supplier<Object>... args);

  void debug(String format, Supplier<Object>... args);

  void info(String format, Supplier<Object>... args);

  void warn(String format, Supplier<Object>... args);

  void error(String format, Supplier<Object>... args);

}
