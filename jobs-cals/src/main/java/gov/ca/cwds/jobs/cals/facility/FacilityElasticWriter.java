package gov.ca.cwds.jobs.cals.facility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.ElasticSearchIndexerDao;
import gov.ca.cwds.jobs.common.elastic.ElasticWriter;

/**
 * Created by Alexander Serbin on 3/28/2018.
 */
public class FacilityElasticWriter extends ElasticWriter<ChangedFacilityDTO> {

  @Inject
  FacilityElasticWriter(ElasticSearchIndexerDao elasticsearchDao, ObjectMapper objectMapper) {
    super(elasticsearchDao, objectMapper);
  }
}
