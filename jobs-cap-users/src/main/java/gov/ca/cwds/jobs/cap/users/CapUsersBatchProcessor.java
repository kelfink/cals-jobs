package gov.ca.cwds.jobs.cap.users;

import com.google.inject.Inject;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.elastic.ElasticSearchBulkCollector;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class CapUsersBatchProcessor {

  @Inject
  private CapUsersIterator capUsersJobBatchIterator;

  @Inject
  private ElasticSearchBulkCollector<ChangedUserDTO> elasticSearchBulkCollector;

  public void processBatches() {
    List<User> portion = capUsersJobBatchIterator.getNextPortion();
    while (!CollectionUtils.isEmpty(portion)) {
      loadEntities(portion);
      portion = capUsersJobBatchIterator.getNextPortion();
    }
    elasticSearchBulkCollector.flush();
  }

  private void loadEntities(List<User> userList) {
    userList.forEach(e -> elasticSearchBulkCollector.addEntity(new ChangedUserDTO(e, RecordChangeOperation.I)));
  }

  public void destroy() {
    elasticSearchBulkCollector.destroy();
  }

}
