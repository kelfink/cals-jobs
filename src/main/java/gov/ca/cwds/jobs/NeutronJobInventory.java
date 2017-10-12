package gov.ca.cwds.jobs;

public enum NeutronJobInventory {

  CLIENT(ClientIndexerJob.class, true, "client", 0L, 30L, 1, null),

  REPORTER(ReporterIndexerJob.class, true, "reporter", 10L, 30L, 2, null),

  COLLATERAL_INDIVIDUAL(CollateralIndividualIndexerJob.class, true, "collateral_individual", 60L,
      30L, 3, null),

  SERVICE_PROVIDER(ServiceProviderIndexerJob.class, true, "service_provider", 10L, 30L, 4, null),

  SUBSTITUTE_CARE_PROVIDER(SubstituteCareProviderIndexJob.class, true, "substitute_care_provider",
      10L, 30L, 5, null),

  EDUCATION_PROVIDER(EducationProviderContactIndexerJob.class, true, "education_provider", 120L,
      120L, 6, null),

  OTHER_ADULT_IN_HOME(OtherAdultInPlacemtHomeIndexerJob.class, true, "other_adult", 60L, 30L, 7,
      null),

  OTHER_CHILD_IN_HOME(OtherChildInPlacemtHomeIndexerJob.class, true, "other_child", 10L, 30L, 8,
      null),

  /**
   * JSON element inside document.
   */
  OTHER_CLIENT_NAME(OtherClientNameIndexerJob.class, false, "other_client_name", 120L, 60L, 25, "akas"),

  CHILD_CASE(ChildCaseHistoryIndexerJob.class, false, "child_case", 60L, 30L, 20, "cases"),

  PARENT_CASE(ParentCaseHistoryIndexerJob.class, false, "parent_case", 60L, 30L, 22, "cases"),

  REFERRAL(ReferralHistoryIndexerJob.class, false, "referral", 60L, 30L, 10, "referrals"),

  RELATIONSHIP(RelationshipIndexerJob.class, false, "relationship", 60L, 30L, 30, "relationships"),

  SAFETY_ALERT(SafetyAlertIndexerJob.class, false, "safety_alert", 120L, 60L, 40, "safety_alerts"),

  INTAKE_SCREENING(IntakeScreeningJob.class, false, "intake_screening", 90L, 30L, 18, "screenings")

  ;

  private final Class<?> klazz;

  private final String name;

  private final boolean newDocument;

  private final long startDelaySeconds;

  private final long periodSeconds;

  private final int loadOrder;

  private final String jsonElement;

  private NeutronJobInventory(Class<?> klazz, boolean newDocument, String name,
      long startDelaySeconds, long periodSeconds, int loadOrder, String jsonElement) {
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

  public long getStartDelaySeconds() {
    return startDelaySeconds;
  }

  public long getPeriodSeconds() {
    return periodSeconds;
  }

  public int getLoadOrder() {
    return loadOrder;
  }

  public String getJsonElement() {
    return jsonElement;
  }

}
