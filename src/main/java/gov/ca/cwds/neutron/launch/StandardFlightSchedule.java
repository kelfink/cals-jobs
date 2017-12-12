package gov.ca.cwds.neutron.launch;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobKey;
import org.quartz.listeners.JobChainingJobListener;

import gov.ca.cwds.jobs.ChildCaseHistoryIndexerJob;
import gov.ca.cwds.jobs.ClientIndexerJob;
import gov.ca.cwds.jobs.CollateralIndividualIndexerJob;
import gov.ca.cwds.jobs.EducationProviderContactIndexerJob;
import gov.ca.cwds.jobs.IntakeScreeningJob;
import gov.ca.cwds.jobs.OtherAdultInPlacemtHomeIndexerJob;
import gov.ca.cwds.jobs.OtherChildInPlacemtHomeIndexerJob;
import gov.ca.cwds.jobs.OtherClientNameIndexerJob;
import gov.ca.cwds.jobs.ReferralHistoryIndexerJob;
import gov.ca.cwds.jobs.RelationshipIndexerJob;
import gov.ca.cwds.jobs.ReporterIndexerJob;
import gov.ca.cwds.jobs.SafetyAlertIndexerJob;
import gov.ca.cwds.jobs.ServiceProviderIndexerJob;
import gov.ca.cwds.jobs.SubstituteCareProviderIndexJob;
import gov.ca.cwds.neutron.enums.NeutronSchedulerConstants;
import gov.ca.cwds.neutron.jetpack.ConditionalLogger;
import gov.ca.cwds.neutron.jetpack.JetPackLogger;
import gov.ca.cwds.neutron.rocket.ExitInitialLoadRocket;
import gov.ca.cwds.neutron.rocket.IndexResetRocket;

public enum StandardFlightSchedule {

  /**
   * If requested, drop and create Elasticsearch indexes.
   */
  RESET_INDEX(IndexResetRocket.class, // klazz
      "reset_index", // rocketName
      1, // initialLoadOrder
      200000000, // startDelaySeconds
      10000, // periodSeconds
      null, // lastRunPriority
      false, // nestedElement
      true // runInitialLoad
  ),

  /**
   * Client. Essential document root.
   */
  CLIENT(ClientIndexerJob.class, "client", 5, 20, 1000, null, true, true),

  /**
   * Document root. Reporter
   */
  REPORTER(ReporterIndexerJob.class, "reporter", 10, 30, 950, null, true, true),

  COLLATERAL_INDIVIDUAL(CollateralIndividualIndexerJob.class, "collateral_individual", 20, 30, 90,
      null, true, true),

  SERVICE_PROVIDER(ServiceProviderIndexerJob.class, "service_provider", 25, 120, 85, null, true,
      true),

  SUBSTITUTE_CARE_PROVIDER(SubstituteCareProviderIndexJob.class, "substitute_care_provider", 30, 25,
      80, null, true, true),

  EDUCATION_PROVIDER(EducationProviderContactIndexerJob.class, "education_provider", 42, 120, 75,
      null, true, true),

  OTHER_ADULT_IN_HOME(OtherAdultInPlacemtHomeIndexerJob.class, "other_adult", 50, 120, 70, null,
      true, true),

  OTHER_CHILD_IN_HOME(OtherChildInPlacemtHomeIndexerJob.class, "other_child", 55, 120, 65, null,
      true, true),

  //
  // JSON elements inside ES document.
  //

  /**
   * Client name aliases.
   */
  OTHER_CLIENT_NAME(OtherClientNameIndexerJob.class, "other_client_name", 90, 45, 300, "akas", true, true),

  /**
   * Combines child and parent case.
   */
  CASES(ChildCaseHistoryIndexerJob.class, "cases", 70, 30, 550, "cases", true, true),

  /**
   * Child cases.
   */
  // CHILD_CASE(ChildCaseHistoryIndexerJob.class, "child_case", 50, 70, 30, 550, "cases", true,
  // true),

  /**
   * Parent cases.
   */
  // PARENT_CASE(ParentCaseHistoryIndexerJob.class, "parent_case", 55, 80, 30, 575, "cases", true,
  // true),

  /**
   * Relationships.
   */
  RELATIONSHIP(RelationshipIndexerJob.class, "relationship", 90, 30, 600, "relationships", true, true),

  /**
   * Referrals.
   */
  REFERRAL(ReferralHistoryIndexerJob.class, "referral", 45, 30, 700, "referrals", true, true),

  /**
   * Safety alerts.
   */
  SAFETY_ALERT(SafetyAlertIndexerJob.class, "safety_alert", 90, 45, 800, "safety_alerts", true, true),

  /**
   * Screenings.
   */
  INTAKE_SCREENING(IntakeScreeningJob.class, "intake_screening", 90, 20, 900, "screenings", true, true),

  // /**
  // * Validation.
  // */
  // , SANITY_CHECK(SanityCheckRocket.class, "sanity_check", 100, 90, 10, 1, null)

  /**
   * Exit the initial load process.
   */
  EXIT_INITIAL_LOAD(ExitInitialLoadRocket.class, "exit_initial_load", 140, 2000000, 10000, null, false, true);

  private static final ConditionalLogger LOGGER = new JetPackLogger(StandardFlightSchedule.class);

  private final Class<?> klazz;

  private final boolean runLastChange;

  private final boolean runInitialLoad;

  private final String rocketName;

  private final int initialLoadOrder = ordinal();

  private final int startDelaySeconds;

  private final int waitPeriodSeconds;

  private final int lastRunPriority;

  private final String nestedElement;

  private static final Map<String, StandardFlightSchedule> mapName = new ConcurrentHashMap<>();

  private static final Map<Class<?>, StandardFlightSchedule> mapClass = new ConcurrentHashMap<>();

  static {
    for (StandardFlightSchedule sched : StandardFlightSchedule.values()) {
      mapName.put(sched.rocketName, sched);
      mapClass.put(sched.klazz, sched);
    }
  }

  private StandardFlightSchedule(Class<?> klazz, String rocketName, int startDelaySeconds,
      int periodSeconds, int lastRunPriority, String nestedElement, boolean runLastChange,
      boolean runInitialLoad) {
    this.klazz = klazz;
    this.rocketName = rocketName;
    this.startDelaySeconds = startDelaySeconds;
    this.waitPeriodSeconds = periodSeconds;
    this.lastRunPriority = lastRunPriority;
    this.nestedElement = nestedElement;
    this.runLastChange = runLastChange;
    this.runInitialLoad = runInitialLoad;
  }

  /**
   * A JobChainingJobListener executes Quartz jobs in sequence by blocking scheduled triggers.
   * Appropriate for initial load, not last change.
   * 
   * @return Quartz JobChainingJobListener
   */
  public static JobChainingJobListener buildInitialLoadJobChainListener() {
    final JobChainingJobListener ret =
        new JobChainingJobListener(NeutronSchedulerConstants.GRP_FULL_LOAD);

    final StandardFlightSchedule[] arr =
        Arrays.copyOf(StandardFlightSchedule.values(), StandardFlightSchedule.values().length);
    Arrays.sort(arr, (o1, o2) -> Integer.compare(o1.initialLoadOrder, o2.initialLoadOrder));

    StandardFlightSchedule sched;
    final int len = arr.length;

    for (int i = 0; i < len; i++) {
      sched = arr[i];
      LOGGER.info("intial load order: {}", sched.getRocketName());
      ret.addJobChainLink(new JobKey(sched.rocketName, NeutronSchedulerConstants.GRP_FULL_LOAD),
          i != (len - 1)
              ? new JobKey(arr[i + 1].rocketName, NeutronSchedulerConstants.GRP_FULL_LOAD)
              : new JobKey("exit_initial_load", NeutronSchedulerConstants.GRP_FULL_LOAD));
    }

    return ret;
  }

  public static List<StandardFlightSchedule> getInitialLoadRockets() {
    return Arrays.asList(values()).stream().sequential()
        .filter(StandardFlightSchedule::isRunInitialLoad).collect(Collectors.toList());
  }

  public static List<StandardFlightSchedule> getLastChangeRockets() {
    return Arrays.asList(values()).stream().sequential()
        .filter(StandardFlightSchedule::isRunLastChange).collect(Collectors.toList());
  }

  public Class<?> getRocketClass() {
    return klazz;
  }

  public String getRocketName() {
    return rocketName;
  }

  public boolean isNewDocument() {
    return StringUtils.isBlank(this.nestedElement);
  }

  public int getStartDelaySeconds() {
    return startDelaySeconds;
  }

  public int getWaitPeriodSeconds() {
    return waitPeriodSeconds;
  }

  public int getLastRunPriority() {
    return lastRunPriority;
  }

  public String getNestedElement() {
    return nestedElement;
  }

  public static StandardFlightSchedule lookupByRocketName(String key) {
    return mapName.get(key);
  }

  public static StandardFlightSchedule lookupByRocketClass(Class<?> key) {
    return mapClass.get(key);
  }

  public int getInitialLoadOrder() {
    return initialLoadOrder;
  }

  public Class<?> getKlazz() {
    return klazz;
  }

  public boolean isRunLastChange() {
    return runLastChange;
  }

  public boolean isRunInitialLoad() {
    return runInitialLoad;
  }

}
