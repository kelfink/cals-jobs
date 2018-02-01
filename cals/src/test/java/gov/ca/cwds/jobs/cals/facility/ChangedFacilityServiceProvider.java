package gov.ca.cwds.jobs.cals.facility;

import com.google.inject.Inject;
import com.google.inject.Injector;
import gov.ca.cwds.cals.inject.AbstractInjectProvider;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;

/**
 * Created by Alexander Serbin on 1/30/2018.
 */
public class ChangedFacilityServiceProvider extends AbstractInjectProvider<ChangedFacilityService> {

    @Inject
    public ChangedFacilityServiceProvider(Injector injector,
                                   UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory) {
        super(injector, unitOfWorkAwareProxyFactory);
    }

    @Override
    public Class<ChangedFacilityService> getServiceClass() {
        return ChangedFacilityService.class;
    }
}

