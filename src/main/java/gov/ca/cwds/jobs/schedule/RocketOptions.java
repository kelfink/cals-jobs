package gov.ca.cwds.jobs.schedule;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gov.ca.cwds.jobs.config.JobOptions;

@Singleton
public class RocketOptions implements AtomRocketOptions {

  private final JobOptions baseOpts;

  /**
   * Job options by job type.
   */
  private final Map<Class<?>, JobOptions> optionsRegistry = new ConcurrentHashMap<>();

  @Inject
  public RocketOptions(final JobOptions baseOpts) {
    this.baseOpts = baseOpts;
  }

  @Override
  public JobOptions getRocketOptions(Class<?> klazz, String jobName) {
    if (!optionsRegistry.containsKey(klazz)) {
      addRocketOptions(klazz, jobName, null);
    }

    return optionsRegistry.get(klazz);
  }

  @Override
  public void addRocketOptions(Class<?> klazz, String jobName, JobOptions inOpts) {
    if (!optionsRegistry.containsKey(klazz)) {
      final JobOptions opts = inOpts != null ? inOpts : new JobOptions(baseOpts);
      opts.setLastRunLoc(opts.getBaseDirectory() + File.separator + jobName + ".time");
      optionsRegistry.put(klazz, opts);
    }
  }

}
