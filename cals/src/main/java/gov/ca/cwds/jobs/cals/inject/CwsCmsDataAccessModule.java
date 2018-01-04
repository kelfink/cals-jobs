package gov.ca.cwds.jobs.cals.inject;

import com.google.inject.AbstractModule;
import gov.ca.cwds.cals.persistence.dao.cms.RecordChangeCwsCmsDao;
import gov.ca.cwds.cals.persistence.model.RecordChange;
import gov.ca.cwds.data.legacy.cms.dao.ClientDao;
import gov.ca.cwds.data.legacy.cms.dao.CountiesDao;
import gov.ca.cwds.data.legacy.cms.dao.PlacementHomeDao;
import gov.ca.cwds.data.legacy.cms.entity.AddressPhoneticName;
import gov.ca.cwds.data.legacy.cms.entity.AddressPhoneticNamePK;
import gov.ca.cwds.data.legacy.cms.entity.BackgroundCheck;
import gov.ca.cwds.data.legacy.cms.entity.Client;
import gov.ca.cwds.data.legacy.cms.entity.ClientScpEthnicity;
import gov.ca.cwds.data.legacy.cms.entity.CountyLicenseCase;
import gov.ca.cwds.data.legacy.cms.entity.CountyOwnership;
import gov.ca.cwds.data.legacy.cms.entity.CountyOwnershipPK;
import gov.ca.cwds.data.legacy.cms.entity.EmergencyContactDetail;
import gov.ca.cwds.data.legacy.cms.entity.ExternalInterface;
import gov.ca.cwds.data.legacy.cms.entity.ExternalInterfacePK;
import gov.ca.cwds.data.legacy.cms.entity.LicensingVisit;
import gov.ca.cwds.data.legacy.cms.entity.OtherAdultsInPlacementHome;
import gov.ca.cwds.data.legacy.cms.entity.OtherChildrenInPlacementHome;
import gov.ca.cwds.data.legacy.cms.entity.OtherPeopleScpRelationship;
import gov.ca.cwds.data.legacy.cms.entity.OutOfHomePlacement;
import gov.ca.cwds.data.legacy.cms.entity.OutOfStateCheck;
import gov.ca.cwds.data.legacy.cms.entity.PhoneContactDetail;
import gov.ca.cwds.data.legacy.cms.entity.PlacementEpisode;
import gov.ca.cwds.data.legacy.cms.entity.PlacementHome;
import gov.ca.cwds.data.legacy.cms.entity.PlacementHomeInformation;
import gov.ca.cwds.data.legacy.cms.entity.PlacementHomeInformationPK;
import gov.ca.cwds.data.legacy.cms.entity.PlacementHomeNotes;
import gov.ca.cwds.data.legacy.cms.entity.PlacementHomeProfile;
import gov.ca.cwds.data.legacy.cms.entity.PlacementHomeProfilePK;
import gov.ca.cwds.data.legacy.cms.entity.PlacementHomeUc;
import gov.ca.cwds.data.legacy.cms.entity.StaffPerson;
import gov.ca.cwds.data.legacy.cms.entity.SubCareProviderPhoneticName;
import gov.ca.cwds.data.legacy.cms.entity.SubstituteCareProvider;
import gov.ca.cwds.data.legacy.cms.entity.SubstituteCareProviderUc;
import gov.ca.cwds.data.legacy.cms.entity.syscodes.County;
import gov.ca.cwds.data.legacy.cms.entity.syscodes.FacilityType;
import gov.ca.cwds.data.legacy.cms.entity.syscodes.LicenseStatus;
import gov.ca.cwds.data.legacy.cms.entity.syscodes.State;
import gov.ca.cwds.data.legacy.cms.entity.syscodes.VisitType;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.inject.CwsRsSessionFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * @author CWDS TPT-2
 */
public class CwsCmsDataAccessModule extends AbstractModule {
  private SessionFactory cmsSessionFactory;

  public CwsCmsDataAccessModule(String hibernateCfg) {
    this.cmsSessionFactory = new Configuration().configure(hibernateCfg)
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

        .buildSessionFactory();
  }

  @Override
  protected void configure() {
    bind(SessionFactory.class).annotatedWith(CmsSessionFactory.class).toInstance(cmsSessionFactory);
    bind(SessionFactory.class).annotatedWith(CwsRsSessionFactory.class).toInstance(cmsSessionFactory);

    // schema: cwscms
    bind(RecordChangeCwsCmsDao.class);
    bind(CountiesDao.class);
    bind(ClientDao.class);
    bind(PlacementHomeDao.class);
  }
}
