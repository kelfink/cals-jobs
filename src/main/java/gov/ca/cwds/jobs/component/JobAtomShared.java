package gov.ca.cwds.jobs.component;

import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.std.ApiMarker;

public interface JobAtomShared extends ApiMarker {

  JobProgressTrack getTrack();

  ElasticsearchDao getEsDao();

}
