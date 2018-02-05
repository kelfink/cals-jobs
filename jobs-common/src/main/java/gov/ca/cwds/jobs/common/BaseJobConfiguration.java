package gov.ca.cwds.jobs.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.ca.cwds.jobs.common.exception.JobsException;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.api.ApiException;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.FileConfigurationSourceProvider;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jackson.Jackson;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class BaseJobConfiguration extends ElasticsearchConfiguration {

  private static final org.slf4j.Logger LOGGER = LoggerFactory
      .getLogger(BaseJobConfiguration.class);

  private String elasticsearchIndexSettings;

  private String elasticsearchDocumentMapping;

  private DataSourceFactory calsnsDataSourceFactory;

  public DataSourceFactory getCalsnsDataSourceFactory() {
    return calsnsDataSourceFactory;
  }

  public void setCalsnsDataSourceFactory(DataSourceFactory calsnsDataSourceFactory) {
    this.calsnsDataSourceFactory = calsnsDataSourceFactory;
  }

  String getElasticsearchIndexSettings() {
    return elasticsearchIndexSettings;
  }

  @NotNull
  @JsonProperty("elasticsearch.index.settings")
  public void setElasticsearchIndexSettings(String elasticsearchIndexSettings) {
    try {
      LOGGER.info("loading Elasticsearch settings for index '{}' from: {}", getElasticsearchAlias(),
          elasticsearchIndexSettings);
      this.elasticsearchIndexSettings = FileUtils
          .readFileToString(new File(elasticsearchIndexSettings));
    } catch (IOException e) {
      throw new ApiException(e);
    }
  }

  public String getElasticsearchDocumentMapping() {
    return elasticsearchDocumentMapping;
  }

  @NotNull
  @JsonProperty("elasticsearch.document.mapping")
  public void setElasticsearchDocumentMapping(String elasticsearchDocumentMapping) {
    try {
      LOGGER.info("loading Elasticsearch mapping for document type '{}' from: {}",
          getElasticsearchDocType(), elasticsearchDocumentMapping);
      this.elasticsearchDocumentMapping = FileUtils
          .readFileToString(new File(elasticsearchDocumentMapping));
    } catch (IOException e) {
      throw new ApiException(e);
    }
  }

  public static <T extends BaseJobConfiguration> T getCalsJobsConfiguration(Class<T> clazz, String path) {
    final String pathToConfiguration = Paths.get(path).toAbsolutePath().toString();
    EnvironmentVariableSubstitutor environmentVariableSubstitutor = new EnvironmentVariableSubstitutor(false);
    ConfigurationSourceProvider configurationSourceProvider =
            new SubstitutingSourceProvider(new FileConfigurationSourceProvider(), environmentVariableSubstitutor);
    try {
      return new YamlConfigurationFactory<>(
              clazz,
              null,
              Jackson.newObjectMapper(),
              pathToConfiguration).build(configurationSourceProvider, pathToConfiguration);
    } catch (IOException | io.dropwizard.configuration.ConfigurationException e) {
      LOGGER.error("Error reading job configuration: {}", e.getMessage(), e);
      throw new JobsException("Error reading job configuration: " + e.getMessage(), e);
    }
  }

}
