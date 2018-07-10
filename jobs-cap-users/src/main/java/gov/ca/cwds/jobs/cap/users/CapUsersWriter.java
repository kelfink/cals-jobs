package gov.ca.cwds.jobs.cap.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.elastic.ElasticSearchIndexerDao;
import gov.ca.cwds.jobs.common.elastic.ElasticWriter;

public class CapUsersWriter extends ElasticWriter<ChangedUserDTO> {

  @Inject
  public CapUsersWriter(ElasticSearchIndexerDao elasticsearchDao, ObjectMapper objectMapper) {
    super(elasticsearchDao, objectMapper);
  }
}
