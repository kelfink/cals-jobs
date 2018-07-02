package gov.ca.cwds.jobs.cals.facility;

import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifiers;
import gov.ca.cwds.jobs.common.savepoint.SavePoint;
import java.util.stream.Stream;

/**
 * Created by Alexander Serbin on 3/6/2018.
 */
public class ChangedFacilitiesIdentifiers<S extends SavePoint> extends ChangedEntityIdentifiers<S> {

  @Override
  public Stream<ChangedEntityIdentifier<S>> newStream() {
    //printRecordsCount();
    return super.newStream();
  }

  public void printRecordsCount() {
/*
    if (LOG.isInfoEnabled()) {
      String messageFormatString = "Found {} facilities from {} {} elastic search facility index";
      LOG.info(messageFormatString, toBeInserted.size(), dataSourceName.name(),
          "to be inserted to");
      LOG.info(messageFormatString, toBeUpdated.size(), dataSourceName.name(), "to be updated in");
      LOG.info(messageFormatString, toBeDeleted.size(), dataSourceName.name(),
          "to be deleted from");
    }
*/
  }

}
