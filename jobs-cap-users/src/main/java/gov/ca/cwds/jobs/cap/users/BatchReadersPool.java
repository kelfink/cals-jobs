package gov.ca.cwds.jobs.cap.users;

import com.google.inject.Inject;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.jobs.common.elastic.ElasticSearchBulkCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BatchReadersPool {
  private static final Logger LOGGER = LoggerFactory.getLogger(BatchReadersPool.class);

  @Inject
  private ElasticSearchBulkCollector<User> elasticSearchBulkCollector;

  public void loadEntities(List<User> userList) {
    userList.forEach(elasticSearchBulkCollector::addEntity);
  }

}
