package gov.ca.cwds.jobs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author CWDS Elasticsearch Team
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobConfiguration extends ElasticsearchConfiguration5x {

  @JsonProperty("job.lis.reader.query")
  private String jobLisReaderQuery;

  public String getJobLisReaderQuery() {
    return jobLisReaderQuery;
  }
}
