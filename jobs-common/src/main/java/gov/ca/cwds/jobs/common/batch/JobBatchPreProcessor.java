package gov.ca.cwds.jobs.common.batch;

import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Alexander Serbin on 3/7/2018.
 */
@FunctionalInterface
public interface JobBatchPreProcessor {

    List<JobBatch> buildJobBatches(Stream<ChangedEntityIdentifier> identifiers);

}
