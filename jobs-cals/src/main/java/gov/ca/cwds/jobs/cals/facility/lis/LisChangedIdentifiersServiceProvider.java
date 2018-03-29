package gov.ca.cwds.jobs.cals.facility.lis;

import com.google.inject.Inject;
import com.google.inject.Injector;
import gov.ca.cwds.cals.inject.AbstractInjectProvider;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;

/**
 * @author CWDS TPT-2
 */

public class LisChangedIdentifiersServiceProvider extends
    AbstractInjectProvider<LisChangedIdentifiersService> {

  @Inject
  public LisChangedIdentifiersServiceProvider(Injector injector,
      UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory) {
    super(injector, unitOfWorkAwareProxyFactory);
  }

  @Override
  public Class<LisChangedIdentifiersService> getServiceClass() {
    return LisChangedIdentifiersService.class;
  }
}
