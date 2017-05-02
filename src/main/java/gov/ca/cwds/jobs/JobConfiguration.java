package gov.ca.cwds.jobs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.ca.cwds.rest.ElasticsearchConfiguration;

/**
 * Created by dmitry.rudenko on 5/1/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobConfiguration extends ElasticsearchConfiguration {
    @JsonProperty("job.lis.reader.query")
    private String jobLisReaderQuery;

    public String getJobLisReaderQuery() {
        return jobLisReaderQuery;
    }
}
