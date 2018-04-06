package gov.ca.cwds.jobs.cals.facility;

import gov.ca.cwds.DataSourceName;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifiers;
import java.util.stream.Stream;

/**
 * Created by Alexander Serbin on 3/6/2018.
 */
public class ChangedFacilitiesIdentifiers extends ChangedEntityIdentifiers {

  private DataSourceName dataSourceName;

  public ChangedFacilitiesIdentifiers(DataSourceName dataSourceName) {
    this.dataSourceName = dataSourceName;
  }

  @Override
  public Stream<ChangedEntityIdentifier> newStream() {
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
