package gov.ca.cwds.jobs.common.job.impl;

import gov.ca.cwds.jobs.common.job.ChangedEntitiesService;
import gov.ca.cwds.jobs.common.job.JobReader;
import gov.ca.cwds.jobs.common.job.timestamp.TimestampOperator;

import java.util.Iterator;

/**
 * Created by Alexander Serbin on 2/5/2018.
 */
public abstract class AbstractJobReader<T> implements JobReader<T> {

    private Iterator<T> entitiesIterator;
    private ChangedEntitiesService<T> changedEntitiesService;
    private TimestampOperator timestampOperator;

    public AbstractJobReader(ChangedEntitiesService<T> changedEntitiesService,
                             TimestampOperator timestampOperator) {
        this.changedEntitiesService = changedEntitiesService;
        this.timestampOperator = timestampOperator;
    }

    @Override
    public void init() {
        initEntitiesIterator();
    }

    public void initEntitiesIterator() {
        if (!timestampOperator.timeStampExists()) {
            entitiesIterator = changedEntitiesService.doInitialLoad().iterator();
        } else {
            entitiesIterator = changedEntitiesService.doIncrementalLoad(
                    timestampOperator.readTimestamp()).iterator();
        }
    }

    @Override
    public T read() {
        return entitiesIterator.hasNext() ? entitiesIterator.next() : null;
    }

}
