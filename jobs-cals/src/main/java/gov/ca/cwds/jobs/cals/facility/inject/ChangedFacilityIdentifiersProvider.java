package gov.ca.cwds.jobs.cals.facility.inject;

import com.google.inject.Inject;
import com.google.inject.Injector;
import gov.ca.cwds.cals.inject.AbstractInjectProvider;
import gov.ca.cwds.jobs.cals.facility.ChangedFacilityIdentifiersServiceImpl;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;

/**
 * Created by Alexander Serbin on 3/6/2018.
 */
public class ChangedFacilityIdentifiersProvider extends
    AbstractInjectProvider<ChangedFacilityIdentifiersServiceImpl> {

  @Inject
  public ChangedFacilityIdentifiersProvider(Injector injector,
      UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory) {
    super(injector, unitOfWorkAwareProxyFactory);
  }

  @Override
  public Class<ChangedFacilityIdentifiersServiceImpl> getServiceClass() {
    return ChangedFacilityIdentifiersServiceImpl.class;
  }
}
