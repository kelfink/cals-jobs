package gov.ca.cwds.jobs.component;

import gov.ca.cwds.data.std.ApiMarker;

public interface JobShared extends ApiMarker {

  JobProgressTrack getTrack();

}
