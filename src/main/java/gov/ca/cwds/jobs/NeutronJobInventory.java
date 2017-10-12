package gov.ca.cwds.jobs;

public enum NeutronJobInventory {

  CLIENT(ClientIndexerJob.class, true, "client", 0L, 30L, 1),

  REPORTER(ReporterIndexerJob.class, true, "reporter", 10L, 30L, 2),

  COLLATERAL_INDIVIDUAL(CollateralIndividualIndexerJob.class, true, "collateral_individual", 60L,
      30L, 3),

  SERVICE_PROVIDER(ServiceProviderIndexerJob.class, true, "service_provider", 10L, 30L, 4),

  SUBSTITUTE_CARE_PROVIDER(SubstituteCareProviderIndexJob.class, true, "substitute_care_provider",
      10L, 30L, 5),

  EDUCATION_PROVIDER(EducationProviderContactIndexerJob.class, true, "education_provider", 120L,
      120L, 6),

  OTHER_ADULT_IN_HOME(OtherAdultInPlacemtHomeIndexerJob.class, true, "other_adult", 60L, 30L, 7),

  OTHER_CHILD_IN_HOME(OtherChildInPlacemtHomeIndexerJob.class, true, "other_child", 10L, 30L, 8),

  /**
   * JSON element inside document.
   */
  OTHER_CLIENT_NAME(OtherClientNameIndexerJob.class, false, "other_client_name", 10L, 30L, 1),

  CHILD_CASE(ChildCaseHistoryIndexerJob.class, false, "child_case", 60L, 60L, 1),

  PARENT_CASE(ParentCaseHistoryIndexerJob.class, false, "parent_case", 60L, 60L, 1),

  REFERRAL(ReferralHistoryIndexerJob.class, false, "referral", 10L, 30L, 1),

  RELATIONSHIP(RelationshipIndexerJob.class, false, "relationship", 10L, 30L, 1),

  SAFETY_ALERT(SafetyAlertIndexerJob.class, false, "safety_alert", 10L, 30L, 1),

  INTAKE_SCREENING(IntakeScreeningJob.class, false, "intake_screening", 10L, 30L, 1),

  SYSTEM_CODES_LOADER(SystemCodesLoaderJob.class, false, "system_codes_loader", 10L, 30L, 5)

  ;

  private final Class<?> klazz;

  private final String name;

  private final boolean newDocument;

  private final long startDelaySeconds;

  private final long periodSeconds;

  private final int loadOrder;

  private NeutronJobInventory(Class<?> klazz, boolean newDocument, String name,
      long startDelaySeconds, long periodSeconds, int loadOrder) {
    this.klazz = klazz;
    this.newDocument = newDocument;
    this.name = name;
    this.startDelaySeconds = startDelaySeconds;
    this.periodSeconds = periodSeconds;
    this.loadOrder = loadOrder;
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

}
