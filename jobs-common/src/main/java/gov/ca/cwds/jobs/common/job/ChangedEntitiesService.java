package gov.ca.cwds.jobs.common.job;

import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;

import java.util.stream.Stream;

/**
 * Created by Alexander Serbin on 2/5/2018.
 */
public interface ChangedEntitiesService<T> {

    Stream<T> loadEntities(Stream<ChangedEntityIdentifier> identifiers);

}
