package gov.ca.cwds.jobs.common.job;

import java.time.LocalDateTime;
import java.util.stream.Stream;

/**
 * Created by Alexander Serbin on 2/5/2018.
 */
public interface ChangedEntitiesService<T> {

    Stream<T> doInitialLoad();

    Stream<T> doIncrementalLoad(LocalDateTime dateAfter);

}
