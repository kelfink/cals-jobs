package gov.ca.cwds.jobs.cals.facility.inject;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import gov.ca.cwds.DataSourceName;
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
import gov.ca.cwds.jobs.cals.facility.FacilityJobConfiguration;
import gov.ca.cwds.jobs.common.util.SessionFactoryUtil;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * @author CWDS TPT-2
 */
public class NsDataAccessModule extends AbstractModule {

  public static final ImmutableList<Class<?>> nsEntityClasses = ImmutableList.<Class<?>>builder().add(
          AgeGroupType.class,
          LanguageType.class,
          GenderType.class,
          NameType.class,
          EducationLevelType.class,
          EthnicityType.class,
          RaceType.class,
          RelationshipToApplicantType.class,
          IncomeType.class,
          PhoneNumberType.class,
          AddressType.class,
          SiblingGroupType.class,
          StateType.class,
          ResidenceOwnershipType.class,
          ApplicantRelationshipType.class,
          LicenseType.class,
          MarriageTerminationReasonType.class,
          SchoolGradeType.class,
          RFA1aForm.class,
          RFA1aApplicant.class,
          RFA1aMinorChild.class,
          RFA1aOtherAdult.class,
          RFA1bForm.class,
          RFA1cForm.class,
          LIC198bForm.class).build();

  @Override
  protected void configure() {
    bind(SessionFactory.class).annotatedWith(CalsnsSessionFactory.class)
        .toProvider(NsSessionFactoryProvider.class).in(Singleton.class);
    bind(RFA1aFormsDao.class);
  }

  private static class NsSessionFactoryProvider implements Provider<SessionFactory> {

    @Inject
    private FacilityJobConfiguration facilityJobConfiguration;

    @Override
    public SessionFactory get() {
      return SessionFactoryUtil.buildSessionFactory(facilityJobConfiguration.getCalsnsDataSourceFactory(),
              DataSourceName.NS.name(), nsEntityClasses,
              (configuration) -> configuration.addPackage("gov.ca.cwds.cals.persistence.model.calsns.rfa")
      );
    }

  }

}
