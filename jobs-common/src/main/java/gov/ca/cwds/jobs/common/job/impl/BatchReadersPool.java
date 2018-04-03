package gov.ca.cwds.jobs.common.job.impl;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.elastic.ElasticSearchBulkCollector;
import gov.ca.cwds.jobs.common.exception.JobsException;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.inject.ReaderThreadsCount;
import gov.ca.cwds.jobs.common.job.ChangedEntityService;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Alexander Serbin on 3/16/2018.
 */
public class BatchReadersPool<T> {

  @Inject
  @ReaderThreadsCount
  private int readersThreadsCount;

  @Inject
  private ChangedEntityService<T> changedEntitiesService;

  private ExecutorService executorService;

  private ElasticSearchBulkCollector<T> elasticSearchBulkCollector;

  public void init(ElasticSearchBulkCollector<T> elasticSearchBulkCollector) {
    this.elasticSearchBulkCollector = elasticSearchBulkCollector;
    this.executorService = Executors.newFixedThreadPool(readersThreadsCount);
  }

  public void loadEntities(List<ChangedEntityIdentifier> changedEntityIdentifiers) {
    List<Future> futures = changedEntityIdentifiers.parallelStream().
        map(identifier -> (Runnable) () -> elasticSearchBulkCollector.addEntity(
            changedEntitiesService.loadEntity(identifier))).
        map(executorService::submit).collect(Collectors.toList());
    for (Future future : futures) {
      try {
        future.get();
      } catch (InterruptedException | ExecutionException e) {
        throw new JobsException("Can't load entities", e);
      }
    }
  }

  public void destroy() {
    if (executorService != null) {
      executorService.shutdown();
      try {
        executorService.awaitTermination(1, TimeUnit.MINUTES);
      } catch (InterruptedException e) {
        throw new JobsException("Can't properly shutdown readers pool", e);
      }
    }
  }

}
