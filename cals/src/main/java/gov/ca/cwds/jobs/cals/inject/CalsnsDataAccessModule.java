package gov.ca.cwds.jobs.cals.inject;

import com.google.inject.AbstractModule;
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
import gov.ca.cwds.cals.persistence.model.calsns.rfa.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * @author CWDS TPT-2
 */
public class CalsnsDataAccessModule extends AbstractModule {

  private SessionFactory calsnsSessionFactory;

  public CalsnsDataAccessModule(String hibernateCfg) {
    this.calsnsSessionFactory = new Configuration().configure(hibernateCfg)
        // Dictionaries
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
        .addAnnotatedClass(LIC198bForm.class)
        .buildSessionFactory();
  }

  @Override
  protected void configure() {
    bind(SessionFactory.class).annotatedWith(CalsnsSessionFactory.class)
        .toInstance(calsnsSessionFactory);

    // schema: calsns
    bind(RFA1aFormsDao.class);
  }
}
