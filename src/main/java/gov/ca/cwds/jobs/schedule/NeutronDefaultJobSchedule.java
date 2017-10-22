package gov.ca.cwds.jobs.schedule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

public enum NeutronDefaultJobSchedule {

  //
  // Person document roots.
  //

  CLIENT(ClientIndexerJob.class, true, "client", 5, 20, 100, null),

  REPORTER(ReporterIndexerJob.class, true, "reporter", 10, 30, 95, null),

  COLLATERAL_INDIVIDUAL(CollateralIndividualIndexerJob.class, true, "collateral_individual", 20, 30,
      90, null),

  SERVICE_PROVIDER(ServiceProviderIndexerJob.class, true, "service_provider", 25, 120, 85, null),

  SUBSTITUTE_CARE_PROVIDER(SubstituteCareProviderIndexJob.class, true, "substitute_care_provider",
      30, 120, 80, null),

  EDUCATION_PROVIDER(EducationProviderContactIndexerJob.class, true, "education_provider", 42, 120,
      75, null),

  OTHER_ADULT_IN_HOME(OtherAdultInPlacemtHomeIndexerJob.class, true, "other_adult", 50, 120, 70,
      null),

  OTHER_CHILD_IN_HOME(OtherChildInPlacemtHomeIndexerJob.class, true, "other_child", 55, 120, 65,
      null),

  //
  // JSON elements inside document.
  //

  /**
   * Client name aliases.
   */
  OTHER_CLIENT_NAME(OtherClientNameIndexerJob.class, false, "other_client_name", 90, 45, 60, "akas"),

  CHILD_CASE(ChildCaseHistoryIndexerJob.class, false, "child_case", 70, 30, 55, "cases"),

  PARENT_CASE(ParentCaseHistoryIndexerJob.class, false, "parent_case", 90, 30, 50, "cases"),

  REFERRAL(ReferralHistoryIndexerJob.class, false, "referral", 90, 30, 45, "referrals"),

  RELATIONSHIP(RelationshipIndexerJob.class, false, "relationship", 90, 30, 40, "relationships"),

  SAFETY_ALERT(SafetyAlertIndexerJob.class, false, "safety_alert", 90, 45, 35, "safety_alerts"),

  INTAKE_SCREENING(IntakeScreeningJob.class, false, "intake_screening", 90, 20, 30, "screenings");

  private final Class<?> klazz;

  private final String name;

  private final boolean newDocument;

  private final int startDelaySeconds;

  private final int periodSeconds;

  private final int priority;

  private final String jsonElement;

  private static final Map<String, NeutronDefaultJobSchedule> mapName = new ConcurrentHashMap<>();

  static {
    for (NeutronDefaultJobSchedule sched : NeutronDefaultJobSchedule.values()) {
      mapName.put(sched.name, sched);
    }
  }

  private NeutronDefaultJobSchedule(Class<?> klazz, boolean newDocument, String name,
      int startDelaySeconds, int periodSeconds, int loadPriority, String jsonElement) {
    this.klazz = klazz;
    this.newDocument = newDocument;
    this.name = name;
    this.startDelaySeconds = startDelaySeconds;
    this.periodSeconds = periodSeconds;
    this.priority = loadPriority;
    this.jsonElement = jsonElement;
  }

  public Class<?> getKlazz() {
    return klazz;
  }

  public String getName() {
    return name;
  }

  public boolean isNewDocument() {
    return newDocument;
  }

  public int getStartDelaySeconds() {
    return startDelaySeconds;
  }

  public int getPeriodSeconds() {
    return periodSeconds;
  }

  public int getPriority() {
    return priority;
  }

  public String getJsonElement() {
    return jsonElement;
  }

  public static NeutronDefaultJobSchedule lookupByJobName(String key) {
    return mapName.get(key);
  }

}
