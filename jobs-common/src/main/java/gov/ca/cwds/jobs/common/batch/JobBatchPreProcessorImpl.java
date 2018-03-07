package gov.ca.cwds.jobs.common.batch;

import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public class JobBatchPreProcessorImpl implements JobBatchPreProcessor {

    @Override
    public List<JobBatch> buildJobBatches(Stream<ChangedEntityIdentifier> identifiers) {
        return Collections.singletonList(new JobBatch(identifiers, LocalDateTime.now()));
    }

}
