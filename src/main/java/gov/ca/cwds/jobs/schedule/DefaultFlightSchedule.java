package gov.ca.cwds.jobs.schedule;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobKey;
import org.quartz.listeners.JobChainingJobListener;

import gov.ca.cwds.jobs.ChildCaseHistoryIndexerJob;
import gov.ca.cwds.jobs.ClientIndexerJob;
import gov.ca.cwds.jobs.CollateralIndividualIndexerJob;
import gov.ca.cwds.jobs.EducationProviderContactIndexerJob;
import gov.ca.cwds.jobs.IntakeScreeningJob;
import gov.ca.cwds.jobs.MSearchJob;
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

public enum DefaultFlightSchedule {

  //
  // Person document roots.
  //

  /**
   * Client. Essential document root.
   */
  CLIENT(ClientIndexerJob.class, "client", 1, 5, 20, 1000, null),

  REPORTER(ReporterIndexerJob.class, "reporter", 2, 10, 30, 950, null),

  COLLATERAL_INDIVIDUAL(CollateralIndividualIndexerJob.class, "collateral_individual", 3, 20, 30,
      90, null),

  SERVICE_PROVIDER(ServiceProviderIndexerJob.class, "service_provider", 4, 25, 120, 85, null),

  SUBSTITUTE_CARE_PROVIDER(SubstituteCareProviderIndexJob.class, "substitute_care_provider", 5, 30,
      120, 80, null),

  EDUCATION_PROVIDER(EducationProviderContactIndexerJob.class, "education_provider", 6, 42, 120, 75,
      null),

  OTHER_ADULT_IN_HOME(OtherAdultInPlacemtHomeIndexerJob.class, "other_adult", 7, 50, 120, 70, null),

  OTHER_CHILD_IN_HOME(OtherChildInPlacemtHomeIndexerJob.class, "other_child", 8, 55, 120, 65, null),

  //
  // JSON elements inside document.
  //

  /**
   * Client name aliases.
   */
  OTHER_CLIENT_NAME(OtherClientNameIndexerJob.class, "other_client_name", 20, 90, 45, 300, "akas"),

  /**
   * Child cases.
   */
  CHILD_CASE(ChildCaseHistoryIndexerJob.class, "child_case", 25, 70, 30, 550, "cases"),

  /**
   * Parent cases.
   */
  PARENT_CASE(ParentCaseHistoryIndexerJob.class, "parent_case", 30, 90, 30, 500, "cases"),

  /**
   * Relationships.
   */
  RELATIONSHIP(RelationshipIndexerJob.class, "relationship", 40, 90, 30, 600, "relationships"),

  /**
   * Referrals.
   */
  REFERRAL(ReferralHistoryIndexerJob.class, "referral", 50, 45, 30, 700, "referrals"),

  /**
   * Safety alerts.
   */
  SAFETY_ALERT(SafetyAlertIndexerJob.class, "safety_alert", 60, 90, 45, 350, "safety_alerts"),

  /**
   * Screenings.
   */
  INTAKE_SCREENING(IntakeScreeningJob.class, "intake_screening", 70, 90, 20, 800, "screenings"),

  /**
   * Validation.
   */
  VALIDATE_LAST_RUN(MSearchJob.class, "validate_last_run", 90, 90, 10, 100, null)

  ;

  private final Class<?> klazz;

  private final String shortName;

  private final int initialLoadOrder;

  private final int startDelaySeconds;

  private final int waitPeriodSeconds;

  private final int lastRunPriority;

  private final String jsonElement;

  private static final Map<String, DefaultFlightSchedule> mapName = new ConcurrentHashMap<>();

  static {
    for (DefaultFlightSchedule sched : DefaultFlightSchedule.values()) {
      mapName.put(sched.shortName, sched);
    }
  }

  private DefaultFlightSchedule(Class<?> klazz, String shortName, int initialLoadOrder,
      int startDelaySeconds, int periodSeconds, int lastRunPriority, String jsonElement) {
    this.klazz = klazz;
    this.shortName = shortName;
    this.initialLoadOrder = initialLoadOrder;
    this.startDelaySeconds = startDelaySeconds;
    this.waitPeriodSeconds = periodSeconds;
    this.lastRunPriority = lastRunPriority;
    this.jsonElement = jsonElement;
  }

  public static JobChainingJobListener buildFullLoadJobChainListener() {
    final JobChainingJobListener ret = new JobChainingJobListener("initial_load");

    final DefaultFlightSchedule[] arr =
        Arrays.copyOf(DefaultFlightSchedule.values(), DefaultFlightSchedule.values().length);
    Arrays.sort(arr, (o1, o2) -> Integer.compare(o1.initialLoadOrder, o2.initialLoadOrder));

    DefaultFlightSchedule sched;
    final int len = arr.length;
    for (int i = 0; i < len; i++) {
      sched = arr[i];
      ret.addJobChainLink(new JobKey(sched.shortName, NeutronSchedulerConstants.GRP_FULL_LOAD),
          i != (len - 1) ? new JobKey(arr[i + 1].shortName, NeutronSchedulerConstants.GRP_FULL_LOAD)
              : new JobKey("verify", NeutronSchedulerConstants.GRP_FULL_LOAD));
    }

    return ret;
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

  public static DefaultFlightSchedule lookupByJobName(String key) {
    return mapName.get(key);
  }

  public int getInitialLoadOrder() {
    return initialLoadOrder;
  }

}
