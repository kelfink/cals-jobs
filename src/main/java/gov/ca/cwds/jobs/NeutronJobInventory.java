package gov.ca.cwds.jobs;

public class NeutronJobInventory {

  public static final Class<?>[] inventory =
      {ChildCaseHistoryIndexerJob.class, ClientIndexerJob.class,
          CollateralIndividualIndexerJob.class, EducationProviderContactIndexerJob.class,
          IntakeScreeningJob.class, OtherAdultInPlacemtHomeIndexerJob.class,
          OtherChildInPlacemtHomeIndexerJob.class, OtherClientNameIndexerJob.class,
          ParentCaseHistoryIndexerJob.class, ReferralHistoryIndexerJob.class,
          RelationshipIndexerJob.class, ReporterIndexerJob.class, SafetyAlertIndexerJob.class,
          ServiceProviderIndexerJob.class, SubstituteCareProviderIndexJob.class};

}
