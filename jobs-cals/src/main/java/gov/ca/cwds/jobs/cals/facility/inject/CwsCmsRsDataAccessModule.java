package gov.ca.cwds.jobs.cals.facility.inject;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import gov.ca.cwds.DataSourceName;
import gov.ca.cwds.data.legacy.cms.dao.ClientDao;
import gov.ca.cwds.data.legacy.cms.dao.CountiesDao;
import gov.ca.cwds.data.legacy.cms.dao.PlacementHomeDao;
import gov.ca.cwds.data.legacy.cms.entity.AddressPhoneticName;
import gov.ca.cwds.data.legacy.cms.entity.AddressPhoneticNamePK;
import gov.ca.cwds.data.legacy.cms.entity.BackgroundCheck;
import gov.ca.cwds.data.legacy.cms.entity.Client;
import gov.ca.cwds.data.legacy.cms.entity.ClientOtherEthnicity;
import gov.ca.cwds.data.legacy.cms.entity.CountyLicenseCase;
import gov.ca.cwds.data.legacy.cms.entity.CountyOwnership;
import gov.ca.cwds.data.legacy.cms.entity.CountyOwnershipPK;
import gov.ca.cwds.data.legacy.cms.entity.EmergencyContactDetail;
import gov.ca.cwds.data.legacy.cms.entity.ExternalInterface;
import gov.ca.cwds.data.legacy.cms.entity.ExternalInterfacePK;
import gov.ca.cwds.data.legacy.cms.entity.LicensingVisit;
import gov.ca.cwds.data.legacy.cms.entity.OtherAdultsInPlacementHome;
import gov.ca.cwds.data.legacy.cms.entity.OtherChildrenInPlacementHome;
import gov.ca.cwds.data.legacy.cms.entity.OtherEthnicity;
import gov.ca.cwds.data.legacy.cms.entity.OtherPeopleScpRelationship;
import gov.ca.cwds.data.legacy.cms.entity.OutOfHomePlacement;
import gov.ca.cwds.data.legacy.cms.entity.OutOfStateCheck;
import gov.ca.cwds.data.legacy.cms.entity.PhoneContactDetail;
import gov.ca.cwds.data.legacy.cms.entity.PlacementEpisode;
import gov.ca.cwds.data.legacy.cms.entity.PlacementFacilityTypeHistory;
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
import gov.ca.cwds.data.legacy.cms.entity.syscodes.NameType;
import gov.ca.cwds.data.legacy.cms.entity.syscodes.State;
import gov.ca.cwds.data.legacy.cms.entity.syscodes.VisitType;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.cals.facility.FacilityJobConfiguration;
import gov.ca.cwds.jobs.cals.facility.recordchange.CwsRecordChange;
import gov.ca.cwds.jobs.cals.facility.recordchange.RecordChangeCwsCmsDao;
import gov.ca.cwds.jobs.common.util.SessionFactoryUtil;
import org.hibernate.SessionFactory;

/**
 * @author CWDS TPT-2
 */
public class CwsCmsRsDataAccessModule extends AbstractModule {

  public static final ImmutableList<Class<?>> cwsrsEntityClasses = ImmutableList.<Class<?>>builder()
      .add(
          CwsRecordChange.class,
          Client.class,
          OutOfHomePlacement.class,
          PlacementEpisode.class,
          PlacementHome.class,
          CountyLicenseCase.class,
          LicensingVisit.class,
          StaffPerson.class,
          FacilityType.class,
          County.class,
          VisitType.class,
          State.class,
          LicenseStatus.class,
          PlacementFacilityTypeHistory.class,
          AddressPhoneticName.class,
          AddressPhoneticNamePK.class,
          BackgroundCheck.class,
          OtherEthnicity.class,
          ClientOtherEthnicity.class,
          CountyOwnership.class,
          CountyOwnershipPK.class,
          EmergencyContactDetail.class,
          ExternalInterface.class,
          ExternalInterfacePK.class,
          PlacementHomeProfile.class,
          PlacementHomeProfilePK.class,
          PlacementHomeInformation.class,
          PlacementHomeInformationPK.class,
          PlacementHomeNotes.class,
          OtherPeopleScpRelationship.class,
          OutOfStateCheck.class,
          OtherAdultsInPlacementHome.class,
          OtherChildrenInPlacementHome.class,
          PhoneContactDetail.class,
          PlacementHomeUc.class,
          SubstituteCareProvider.class,
          SubstituteCareProviderUc.class,
          SubCareProviderPhoneticName.class,
          NameType.class
      ).build();

  @Override
  protected void configure() {
    bind(SessionFactory.class).annotatedWith(CmsSessionFactory.class)
        .toProvider(CwsSessionFactoryProvider.class).in(Singleton.class);
    bind(RecordChangeCwsCmsDao.class);
    bind(CountiesDao.class);
    bind(ClientDao.class);
    bind(PlacementHomeDao.class);
  }

  private static class CwsSessionFactoryProvider implements Provider<SessionFactory> {

    @Inject
    private FacilityJobConfiguration facilityJobConfiguration;

    @Override
    public SessionFactory get() {
      return SessionFactoryUtil
          .buildSessionFactory(facilityJobConfiguration.getCmsDataSourceFactory(),
              DataSourceName.CWSRS.name(), cwsrsEntityClasses);
    }
  }
}
