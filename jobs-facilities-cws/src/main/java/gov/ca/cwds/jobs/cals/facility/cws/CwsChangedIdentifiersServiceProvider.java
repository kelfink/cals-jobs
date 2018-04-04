package gov.ca.cwds.jobs.cals.facility.cws;

import com.google.inject.Inject;
import com.google.inject.Injector;
import gov.ca.cwds.cals.inject.AbstractInjectProvider;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;

/**
 * @author CWDS TPT-2
 */

public class CwsChangedIdentifiersServiceProvider extends
    AbstractInjectProvider<CwsChangedIdentifiersService> {

  @Inject
  public CwsChangedIdentifiersServiceProvider(Injector injector,
      UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory) {
    super(injector, unitOfWorkAwareProxyFactory);
  }

  @Override
  public Class<CwsChangedIdentifiersService> getServiceClass() {
    return CwsChangedIdentifiersService.class;
  }

}
