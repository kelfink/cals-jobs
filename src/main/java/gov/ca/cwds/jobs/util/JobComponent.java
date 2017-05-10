package gov.ca.cwds.jobs.util;

/**
 * Created by dmitry.rudenko on 5/1/2017.
 */
public interface JobComponent {

  default void init() throws Exception {}

  default void destroy() throws Exception {}

}
