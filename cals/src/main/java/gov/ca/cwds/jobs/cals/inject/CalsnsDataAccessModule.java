package gov.ca.cwds.jobs.cals.inject;

import gov.ca.cwds.cals.inject.CalsnsSessionFactory;
import gov.ca.cwds.cals.persistence.dao.calsns.RFA1aFormsDao;
import gov.ca.cwds.cals.persistence.model.calsns.dictionaries.AddressType;
import gov.ca.cwds.cals.persistence.model.calsns.dictionaries.AgeGroupType;
import gov.ca.cwds.cals.persistence.model.calsns.dictionaries.ApplicantRelationshipType;
import gov.ca.cwds.cals.persistence.model.calsns.dictionaries.EducationLevelType;
import gov.ca.cwds.cals.persistence.model.calsns.dictionaries.EthnicityType;
import gov.ca.cwds.cals.persistence.model.calsns.dictionaries.GenderType;
import gov.ca.cwds.cals.persistence.model.calsns.dictionaries.IncomeType;
import gov.ca.cwds.cals.persistence.model.calsns.dictionaries.LanguageType;
import gov.ca.cwds.cals.persistence.model.calsns.dictionaries.LicenseType;
import gov.ca.cwds.cals.persistence.model.calsns.dictionaries.MarriageTerminationReasonType;
import gov.ca.cwds.cals.persistence.model.calsns.dictionaries.NameType;
import gov.ca.cwds.cals.persistence.model.calsns.dictionaries.PhoneNumberType;
import gov.ca.cwds.cals.persistence.model.calsns.dictionaries.RaceType;
import gov.ca.cwds.cals.persistence.model.calsns.dictionaries.RelationshipToApplicantType;
import gov.ca.cwds.cals.persistence.model.calsns.dictionaries.ResidenceOwnershipType;
import gov.ca.cwds.cals.persistence.model.calsns.dictionaries.SchoolGradeType;
import gov.ca.cwds.cals.persistence.model.calsns.dictionaries.SiblingGroupType;
import gov.ca.cwds.cals.persistence.model.calsns.dictionaries.StateType;
import gov.ca.cwds.cals.persistence.model.calsns.rfa.LIC198bForm;
import gov.ca.cwds.cals.persistence.model.calsns.rfa.RFA1aApplicant;
import gov.ca.cwds.cals.persistence.model.calsns.rfa.RFA1aForm;
import gov.ca.cwds.cals.persistence.model.calsns.rfa.RFA1aMinorChild;
import gov.ca.cwds.cals.persistence.model.calsns.rfa.RFA1aOtherAdult;
import gov.ca.cwds.cals.persistence.model.calsns.rfa.RFA1bForm;
import gov.ca.cwds.cals.persistence.model.calsns.rfa.RFA1cForm;
import gov.ca.cwds.generic.jobs.inject.JobsDataAccessModule;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * @author CWDS TPT-2
 */
public class CalsnsDataAccessModule extends JobsDataAccessModule {

  public CalsnsDataAccessModule(DataSourceFactory dataSourceFactory, String dataSourceName) {
    super(dataSourceFactory, dataSourceName);
  }

  @Override
  protected void addEntityClasses(Configuration configuration) {
    configuration
            .addAnnotatedClass(AgeGroupType.class)
            .addAnnotatedClass(LanguageType.class)
            .addAnnotatedClass(GenderType.class)
            .addAnnotatedClass(NameType.class)
            .addAnnotatedClass(EducationLevelType.class)
            .addAnnotatedClass(EthnicityType.class)
            .addAnnotatedClass(RaceType.class)
            .addAnnotatedClass(RelationshipToApplicantType.class)
            .addAnnotatedClass(IncomeType.class)
            .addAnnotatedClass(PhoneNumberType.class)
            .addAnnotatedClass(AddressType.class)
            .addAnnotatedClass(SiblingGroupType.class)
            .addAnnotatedClass(StateType.class)
            .addAnnotatedClass(ResidenceOwnershipType.class)
            .addAnnotatedClass(ApplicantRelationshipType.class)
            .addAnnotatedClass(LicenseType.class)
            .addAnnotatedClass(MarriageTerminationReasonType.class)
            .addAnnotatedClass(SchoolGradeType.class)
            // for JsonType
            .addPackage("gov.ca.cwds.cals.persistence.model.calsns.rfa")
            //RFA
            .addAnnotatedClass(RFA1aForm.class)
            .addAnnotatedClass(RFA1aApplicant.class)
            .addAnnotatedClass(RFA1aMinorChild.class)
            .addAnnotatedClass(RFA1aOtherAdult.class)
            .addAnnotatedClass(RFA1bForm.class)
            .addAnnotatedClass(RFA1cForm.class)
            .addAnnotatedClass(LIC198bForm.class);
  }

  @Override
  protected void configure() {
    super.configure();
    bind(SessionFactory.class).annotatedWith(CalsnsSessionFactory.class)
        .toInstance(getSessionFactory());

    // schema: calsns
    bind(RFA1aFormsDao.class);
  }
}
