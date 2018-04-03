package gov.ca.cwds.jobs.common.identifier;

import gov.ca.cwds.jobs.common.batch.PageRequest;
import java.util.List;

/**
 * Created by Alexander Serbin on 3/30/2018.
 */
public interface ChangedIdentifiersProvider {

  List<ChangedEntityIdentifier> getNextPage(PageRequest pageRequest);

}
