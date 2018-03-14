package gov.ca.cwds.jobs.common;

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
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;

public abstract class BaseJobConfiguration extends ElasticsearchConfiguration {

  private static final org.slf4j.Logger LOGGER = LoggerFactory
      .getLogger(BaseJobConfiguration.class);

  private String documentMapping;
  private String indexSettings;

  private int batchSize;

  private DataSourceFactory calsnsDataSourceFactory;

  public DataSourceFactory getCalsnsDataSourceFactory() {
    return calsnsDataSourceFactory;
  }

  public void setCalsnsDataSourceFactory(DataSourceFactory calsnsDataSourceFactory) {
    this.calsnsDataSourceFactory = calsnsDataSourceFactory;
  }

  public String getDocumentMapping() {
    return documentMapping;
  }

  public void setDocumentMapping(String documentMappingFilename) {
    this.documentMapping = getElasticSearchDocumentMapping(documentMappingFilename);
  }

  public String getIndexSettings() {
    return indexSettings;
  }

  public void setIndexSettings(String indexSettingsFilename) {
    this.indexSettings = getElasticSearchIndexSettings(indexSettingsFilename);
  }

  public int getBatchSize() {
    return batchSize;
  }

  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }

  public static <T extends BaseJobConfiguration> T getJobsConfiguration(Class<T> clazz, String path) {
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

  private String getElasticSearchDocumentMapping(String documentMappingFilename) {
    return readElasticSettingsFile(documentMappingFilename,
            "Can't read elastic search document mapping");
  }

  private String getElasticSearchIndexSettings(String indexSettingsFilename) {
    return readElasticSettingsFile(indexSettingsFilename,
            "Can't read elastic search index settings");
  }

  private String readElasticSettingsFile(String filename, String errorMessage) {
      try {
          return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(filename), "UTF-8");
      } catch (IOException e) {
          throw new ApiException(errorMessage, e);
      }
  }

}
