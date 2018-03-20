package gov.ca.cwds.jobs.common.job.preprocessor;

import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.batch.BatchPreProcessor;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Alexander Serbin on 3/7/2018.
 */
public class EmptyTimestampTestPreProcessor  implements BatchPreProcessor {

    @Override
    public List<JobBatch> buildJobBatches(Stream<ChangedEntityIdentifier> identifiers) {
        return Collections.singletonList(new JobBatch(identifiers.collect(Collectors.toList()), null));
    }
}
