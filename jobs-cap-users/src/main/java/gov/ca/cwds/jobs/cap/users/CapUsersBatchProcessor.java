package gov.ca.cwds.jobs.cap.users;

import com.google.inject.Inject;
import gov.ca.cwds.idm.dto.User;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class CapUsersBatchProcessor {

  @Inject
  private CapUsersJobBatchIterator capUsersJobBatchIterator;

  @Inject
  private BatchReadersPool batchReadersPool;

  public void processBatches() {
    List<User> portion = capUsersJobBatchIterator.getNextPortion();

    while (!CollectionUtils.isEmpty(portion)) {
      batchReadersPool.loadEntities(portion);
      portion = capUsersJobBatchIterator.getNextPortion();
    }
  }
}
