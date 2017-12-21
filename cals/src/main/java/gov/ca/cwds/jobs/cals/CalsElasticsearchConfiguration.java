package gov.ca.cwds.jobs.cals;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.api.ApiException;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;

class CalsElasticsearchConfiguration extends ElasticsearchConfiguration {

  private static final org.slf4j.Logger LOGGER = LoggerFactory
      .getLogger(CalsElasticsearchConfiguration.class);

  private String elasticsearchIndexSettings;

  private String elasticsearchDocumentMapping;

  String getElasticsearchIndexSettings() {
    return elasticsearchIndexSettings;
  }

  @NotNull
  @JsonProperty("elasticsearch.index.settings")
  void setElasticsearchIndexSettings(String elasticsearchIndexSettings) {
    try {
      LOGGER.info("loading Elasticsearch settings for index '{}' from: {}", getElasticsearchAlias(),
          elasticsearchIndexSettings);
      this.elasticsearchIndexSettings = FileUtils
          .readFileToString(new File(elasticsearchIndexSettings));
    } catch (IOException e) {
      throw new ApiException(e);
    }
  }

  String getElasticsearchDocumentMapping() {
    return elasticsearchDocumentMapping;
  }

  @NotNull
  @JsonProperty("elasticsearch.document.mapping")
  void setElasticsearchDocumentMapping(String elasticsearchDocumentMapping) {
    try {
      LOGGER.info("loading Elasticsearch mapping for document type '{}' from: {}",
          getElasticsearchDocType(), elasticsearchDocumentMapping);
      this.elasticsearchDocumentMapping = FileUtils
          .readFileToString(new File(elasticsearchDocumentMapping));
    } catch (IOException e) {
      throw new ApiException(e);
    }
  }
}
