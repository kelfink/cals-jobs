package gov.ca.cwds.jobs;

public enum NeutronDefaultJobSchedule {

  //
  // Person document roots.
  //

  CLIENT(ClientIndexerJob.class, true, "client", 0, 30, 1, null),

  REPORTER(ReporterIndexerJob.class, true, "reporter", 10, 30, 2, null),

  COLLATERAL_INDIVIDUAL(CollateralIndividualIndexerJob.class, true, "collateral_individual", 60, 30,
      3, null),

  SERVICE_PROVIDER(ServiceProviderIndexerJob.class, true, "service_provider", 10, 30, 4, null),

  SUBSTITUTE_CARE_PROVIDER(SubstituteCareProviderIndexJob.class, true, "substitute_care_provider",
      10, 30, 5, null),

  EDUCATION_PROVIDER(EducationProviderContactIndexerJob.class, true, "education_provider", 120, 120,
      6, null),

  OTHER_ADULT_IN_HOME(OtherAdultInPlacemtHomeIndexerJob.class, true, "other_adult", 60, 30, 7,
      null),

  OTHER_CHILD_IN_HOME(OtherChildInPlacemtHomeIndexerJob.class, true, "other_child", 10, 30, 8,
      null),

  //
  // JSON elements inside ES document.
  //

  /**
   * Client name aliases.
   */
  OTHER_CLIENT_NAME(OtherClientNameIndexerJob.class, false, "other_client_name", 120, 60, 25, "akas"),

  CHILD_CASE(ChildCaseHistoryIndexerJob.class, false, "child_case", 60, 30, 20, "cases"),

  PARENT_CASE(ParentCaseHistoryIndexerJob.class, false, "parent_case", 60, 30, 22, "cases"),

  REFERRAL(ReferralHistoryIndexerJob.class, false, "referral", 60, 30, 10, "referrals"),

  RELATIONSHIP(RelationshipIndexerJob.class, false, "relationship", 60, 30, 30, "relationships"),

  SAFETY_ALERT(SafetyAlertIndexerJob.class, false, "safety_alert", 120, 60, 40, "safety_alerts"),

  INTAKE_SCREENING(IntakeScreeningJob.class, false, "intake_screening", 90, 30, 18, "screenings")

  ;

  private final Class<?> klazz;

  private final String name;

  private final boolean newDocument;

  private final int startDelaySeconds;

  private final int periodSeconds;

  private final int loadOrder;

  private final String jsonElement;

  private NeutronDefaultJobSchedule(Class<?> klazz, boolean newDocument, String name,
      int startDelaySeconds, int periodSeconds, int loadOrder, String jsonElement) {
    this.klazz = klazz;
    this.newDocument = newDocument;
    this.name = name;
    this.startDelaySeconds = startDelaySeconds;
    this.periodSeconds = periodSeconds;
    this.loadOrder = loadOrder;
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

  public int getLoadOrder() {
    return loadOrder;
  }

  public String getJsonElement() {
    return jsonElement;
  }

}
