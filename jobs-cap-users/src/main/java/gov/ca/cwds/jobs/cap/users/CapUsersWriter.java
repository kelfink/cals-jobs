package gov.ca.cwds.jobs.cap.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.jobs.common.ElasticSearchIndexerDao;
import gov.ca.cwds.jobs.common.elastic.ElasticWriter;

public class CapUsersWriter extends ElasticWriter<ChangedUserDTO> {
  /**
   * Constructor.
   *
   * @param elasticsearchDao ES DAO
   * @param objectMapper     Jackson object mapper
   */
  public CapUsersWriter(ElasticSearchIndexerDao elasticsearchDao, ObjectMapper objectMapper) {
    super(elasticsearchDao, objectMapper);
  }


}
