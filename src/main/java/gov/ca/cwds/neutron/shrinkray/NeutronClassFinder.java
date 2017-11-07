package gov.ca.cwds.neutron.shrinkray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.neutron.jetpack.JobLogs;

public class NeutronClassFinder {

  private static final Logger LOGGER = LoggerFactory.getLogger(NeutronClassFinder.class);

  public static Class<?> classForName(String className) throws NeutronException {
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw JobLogs.checked(LOGGER, e, "CLASS NAME NOT FOUND!: {}", e.getMessage());
    }
  }

}
