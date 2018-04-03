package gov.ca.cwds.jobs.common.job.identifiers;

import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.job.TestChangedIdentifiersProvider;
import java.util.Arrays;

/**
 * Created by Alexander Serbin on 3/7/2018.
 */
public class EmptyTimestampBatchIdentifiersProvider extends TestChangedIdentifiersProvider {

  public EmptyTimestampBatchIdentifiersProvider() {
    super(Arrays.asList(
        new ChangedEntityIdentifier("testId",
            RecordChangeOperation.I,
            null)));
  }
}
