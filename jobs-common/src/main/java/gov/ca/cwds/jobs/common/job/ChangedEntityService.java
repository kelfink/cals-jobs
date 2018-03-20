package gov.ca.cwds.jobs.common.job;

import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;

/**
 * Created by Alexander Serbin on 2/5/2018.
 */
public interface ChangedEntityService<T> {

    T loadEntity(ChangedEntityIdentifier identifier);

}
