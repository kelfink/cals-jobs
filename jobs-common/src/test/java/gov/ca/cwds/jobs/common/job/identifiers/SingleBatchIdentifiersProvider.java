package gov.ca.cwds.jobs.common.job.identifiers;

import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.job.TestChangedIdentifiersProvider;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Created by Alexander Serbin on 3/13/2018.
 */
public class SingleBatchIdentifiersProvider extends TestChangedIdentifiersProvider {

  public SingleBatchIdentifiersProvider() {
    super(Arrays.asList(
        new ChangedEntityIdentifier("testId",
            RecordChangeOperation.I,
            LocalDateTime.now())));
  }
}
