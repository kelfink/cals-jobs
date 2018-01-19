package gov.ca.cwds.jobs.cals.inject;

import gov.ca.cwds.cals.persistence.dao.cms.RecordChangeCwsCmsDao;
import gov.ca.cwds.cals.persistence.model.RecordChange;
import gov.ca.cwds.data.legacy.cms.dao.ClientDao;
import gov.ca.cwds.data.legacy.cms.dao.CountiesDao;
import gov.ca.cwds.data.legacy.cms.dao.PlacementHomeDao;
import gov.ca.cwds.data.legacy.cms.entity.*;
import gov.ca.cwds.data.legacy.cms.entity.syscodes.*;
import gov.ca.cwds.generic.jobs.inject.JobsDataAccessModule;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.inject.CwsRsSessionFactory;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * @author CWDS TPT-2
 */
public class CwsCmsDataAccessModule extends JobsDataAccessModule {

  public CwsCmsDataAccessModule(DataSourceFactory dataSourceFactory, String dataSourceName) {
    super(dataSourceFactory, dataSourceName);
  }

  @Override
  protected void addEntityClasses(Configuration configuration) {
    configuration
            .addAnnotatedClass(RecordChange.class)
            .addAnnotatedClass(Client.class)
            .addAnnotatedClass(OutOfHomePlacement.class)
            .addAnnotatedClass(PlacementEpisode.class)
            .addAnnotatedClass(PlacementHome.class)
            .addAnnotatedClass(CountyLicenseCase.class)
            .addAnnotatedClass(LicensingVisit.class)
            .addAnnotatedClass(StaffPerson.class)
            .addAnnotatedClass(FacilityType.class)
            .addAnnotatedClass(County.class)
            .addAnnotatedClass(VisitType.class)
            .addAnnotatedClass(State.class)
            .addAnnotatedClass(LicenseStatus.class)

            .addAnnotatedClass(AddressPhoneticName.class)
            .addAnnotatedClass(AddressPhoneticNamePK.class)
            .addAnnotatedClass(BackgroundCheck.class)
            .addAnnotatedClass(ClientScpEthnicity.class)
            .addAnnotatedClass(CountyOwnership.class)
            .addAnnotatedClass(CountyOwnershipPK.class)
            .addAnnotatedClass(EmergencyContactDetail.class)
            .addAnnotatedClass(ExternalInterface.class)
            .addAnnotatedClass(ExternalInterfacePK.class)
            .addAnnotatedClass(PlacementHomeProfile.class)
            .addAnnotatedClass(PlacementHomeProfilePK.class)
            .addAnnotatedClass(PlacementHomeInformation.class)
            .addAnnotatedClass(PlacementHomeInformationPK.class)
            .addAnnotatedClass(PlacementHomeNotes.class)
            .addAnnotatedClass(OtherPeopleScpRelationship.class)
            .addAnnotatedClass(OutOfStateCheck.class)
            .addAnnotatedClass(OtherAdultsInPlacementHome.class)
            .addAnnotatedClass(OtherChildrenInPlacementHome.class)
            .addAnnotatedClass(PhoneContactDetail.class)
            .addAnnotatedClass(PlacementHomeUc.class)
            .addAnnotatedClass(SubstituteCareProvider.class)
            .addAnnotatedClass(SubstituteCareProviderUc.class)
            .addAnnotatedClass(SubCareProviderPhoneticName.class)
            .addAnnotatedClass(NameType.class);
  }

  @Override
  protected void configure() {
    super.configure();
    bind(SessionFactory.class).annotatedWith(CmsSessionFactory.class).toInstance(getSessionFactory());
    bind(SessionFactory.class).annotatedWith(CwsRsSessionFactory.class).toInstance(getSessionFactory());

    // schema: cwscms
    bind(RecordChangeCwsCmsDao.class);
    bind(CountiesDao.class);
    bind(ClientDao.class);
    bind(PlacementHomeDao.class);
  }
}
