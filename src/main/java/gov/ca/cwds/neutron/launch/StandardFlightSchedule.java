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
import gov.ca.cwds.jobs.ParentCaseHistoryIndexerJob;
import gov.ca.cwds.jobs.ReferralHistoryIndexerJob;
import gov.ca.cwds.jobs.RelationshipIndexerJob;
import gov.ca.cwds.jobs.ReporterIndexerJob;
import gov.ca.cwds.jobs.SafetyAlertIndexerJob;
import gov.ca.cwds.jobs.ServiceProviderIndexerJob;
import gov.ca.cwds.jobs.SubstituteCareProviderIndexJob;
import gov.ca.cwds.neutron.enums.NeutronSchedulerConstants;

public enum StandardFlightSchedule {

  //
  // Person document roots.
  //

  /**
   * Client. Essential document root.
   */
  CLIENT(ClientIndexerJob.class, "client", 1, 5, 20, 1000, null, true, true),

  REPORTER(ReporterIndexerJob.class, "reporter", 2, 10, 30, 950, null, true, true),

  COLLATERAL_INDIVIDUAL(CollateralIndividualIndexerJob.class, "collateral_individual", 3, 20, 30,
      90, null, true, true),

  SERVICE_PROVIDER(ServiceProviderIndexerJob.class, "service_provider", 4, 25, 120, 85, null, true,
      true),

  SUBSTITUTE_CARE_PROVIDER(SubstituteCareProviderIndexJob.class, "substitute_care_provider", 5, 30,
      120, 80, null, true, true),

  EDUCATION_PROVIDER(EducationProviderContactIndexerJob.class, "education_provider", 6, 42, 120, 75,
      null, true, true),

  OTHER_ADULT_IN_HOME(OtherAdultInPlacemtHomeIndexerJob.class, "other_adult", 7, 50, 120, 70, null,
      true, true),

  OTHER_CHILD_IN_HOME(OtherChildInPlacemtHomeIndexerJob.class, "other_child", 8, 55, 120, 65, null,
      true, true),

  //
  // JSON elements inside document.
  //

  /**
   * Client name aliases.
   */
  OTHER_CLIENT_NAME(OtherClientNameIndexerJob.class, "other_client_name", 20, 90, 45, 300, "akas", true, true),

  /**
   * Child cases.
   */
  CHILD_CASE(ChildCaseHistoryIndexerJob.class, "child_case", 25, 70, 30, 550, "cases", true, true),

  /**
   * Parent cases.
   */
  PARENT_CASE(ParentCaseHistoryIndexerJob.class, "parent_case", 30, 90, 30, 500, "cases", true, true),

  /**
   * Relationships.
   */
  RELATIONSHIP(RelationshipIndexerJob.class, "relationship", 40, 90, 30, 600, "relationships", true, true),

  /**
   * Referrals.
   */
  REFERRAL(ReferralHistoryIndexerJob.class, "referral", 50, 45, 30, 700, "referrals", true, true),

  /**
   * Safety alerts.
   */
  SAFETY_ALERT(SafetyAlertIndexerJob.class, "safety_alert", 60, 90, 45, 350, "safety_alerts", true, true),

  /**
   * Screenings.
   */
  INTAKE_SCREENING(IntakeScreeningJob.class, "intake_screening", 70, 90, 20, 800, "screenings", true, true)

  // /**
  // * Validation.
  // */
  // , SANITY_CHECK(SanityCheckRocket.class, "sanity_check", 100, 90, 10, 1, null)

  ;

  private final Class<?> klazz;

  private final boolean runLastChange;

  private final boolean runInitialLoad;

  private final String shortName;

  private final int initialLoadOrder;

  private final int startDelaySeconds;

  private final int waitPeriodSeconds;

  private final int lastRunPriority;

  private final String jsonElement;

  private static final Map<String, StandardFlightSchedule> mapName = new ConcurrentHashMap<>();

  private static final Map<Class<?>, StandardFlightSchedule> mapClass = new ConcurrentHashMap<>();

  static {
    for (StandardFlightSchedule sched : StandardFlightSchedule.values()) {
      mapName.put(sched.shortName, sched);
      mapClass.put(sched.klazz, sched);
    }
  }

  private StandardFlightSchedule(Class<?> klazz, String rocketName, int initialLoadOrder,
      int startDelaySeconds, int periodSeconds, int lastRunPriority, String nestedElement,
      boolean runLastChange, boolean runInitialLoad) {
    this.klazz = klazz;
    this.shortName = rocketName;
    this.initialLoadOrder = initialLoadOrder;
    this.startDelaySeconds = startDelaySeconds;
    this.waitPeriodSeconds = periodSeconds;
    this.lastRunPriority = lastRunPriority;
    this.jsonElement = nestedElement;
    this.runLastChange = runLastChange;
    this.runInitialLoad = runInitialLoad;
  }

  /**
   * 
   * @return Quartz JobChainingJobListener
   */
  public static JobChainingJobListener buildInitialLoadJobChainListener() {
    final JobChainingJobListener ret = new JobChainingJobListener("initial_load");

    final StandardFlightSchedule[] arr =
        Arrays.copyOf(StandardFlightSchedule.values(), StandardFlightSchedule.values().length);
    Arrays.sort(arr, (o1, o2) -> Integer.compare(o1.initialLoadOrder, o2.initialLoadOrder));

    StandardFlightSchedule sched;
    final int len = arr.length;
    for (int i = 0; i < len; i++) {
      sched = arr[i];
      ret.addJobChainLink(new JobKey(sched.shortName, NeutronSchedulerConstants.GRP_FULL_LOAD),
          i != (len - 1) ? new JobKey(arr[i + 1].shortName, NeutronSchedulerConstants.GRP_FULL_LOAD)
              : new JobKey("verify", NeutronSchedulerConstants.GRP_FULL_LOAD));
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

  public String getShortName() {
    return shortName;
  }

  public boolean isNewDocument() {
    return StringUtils.isBlank(this.jsonElement);
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

  public String getJsonElement() {
    return jsonElement;
  }

  public static StandardFlightSchedule lookupByJobName(String key) {
    return mapName.get(key);
  }

  public static StandardFlightSchedule lookupByClass(Class<?> key) {
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
