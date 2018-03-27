package gov.ca.cwds.jobs.common.elastic;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.inject.ElasticSearchBulkSize;
import gov.ca.cwds.jobs.common.job.BulkWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Must be thread-safe
 * Created by Alexander Serbin on 3/16/2018.
 */
public class ElasticSearchBulkCollector<T> {

    @Inject
    private BulkWriter<T> jobWriter;

    @Inject
    @ElasticSearchBulkSize
    private int bulkSize;

    private List<T> entities = new ArrayList<>(bulkSize);

    public synchronized void addEntity(T entity) {
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
