package gov.ca.cwds.jobs.cals.facility;

import gov.ca.cwds.jobs.common.batch.BatchProcessor;
import gov.ca.cwds.jobs.common.savepoint.SavePoint;

/**
 * Created by Alexander Serbin on 3/18/2018.
 */
public class FacilityBatchProcessor<S extends SavePoint> extends
    BatchProcessor<ChangedFacilityDto, S> {

}
