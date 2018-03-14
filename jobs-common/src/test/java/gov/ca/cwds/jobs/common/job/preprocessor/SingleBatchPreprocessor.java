package gov.ca.cwds.jobs.common.job.preprocessor;

import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.batch.JobBatchPreProcessor;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Alexander Serbin on 3/13/2018.
 */
public class SingleBatchPreprocessor implements JobBatchPreProcessor {

    @Override
    public List<JobBatch> buildJobBatches(Stream<ChangedEntityIdentifier> identifiers) {
        return Collections.singletonList(new JobBatch(identifiers.collect(Collectors.toList()), LocalDateTime.now()));
    }

}
