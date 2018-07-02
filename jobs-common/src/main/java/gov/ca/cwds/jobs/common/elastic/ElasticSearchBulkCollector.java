package gov.ca.cwds.jobs.common.elastic;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.BulkWriter;
import gov.ca.cwds.jobs.common.inject.ElasticSearchBulkSize;
import java.util.ArrayList;
import java.util.List;

/**
 * Must be thread-safe. Created by Alexander Serbin on 3/16/2018.
 */
public class ElasticSearchBulkCollector<E> {

  @Inject
  private BulkWriter<E> jobWriter;

  @Inject
  @ElasticSearchBulkSize
  private int bulkSize;

  private List<E> entities = new ArrayList<>(bulkSize);

  public synchronized void addEntity(E entity) {
    entities.add(entity);
    if (entities.size() == bulkSize) {
      flush();
    }
  }

  public synchronized void flush() {
    jobWriter.write(entities);
    resetEntities();
  }

  private void resetEntities() {
    entities = new ArrayList<>(bulkSize);
  }

  public void destroy() {
    jobWriter.destroy();
  }
}
