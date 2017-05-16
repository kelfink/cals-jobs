package gov.ca.cwds.jobs;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.ca.cwds.data.es.Elasticsearch5xDao;

import javax.validation.constraints.NotNull;

/**
 * Represents the configuration settings for {@link Elasticsearch5xDao}.
 *
 * @author CWDS API Team
 */
public class ElasticsearchConfiguration5x {

  @NotNull
  @JsonProperty("elasticsearch.host")
  private String host;

  @NotNull
  @JsonProperty("elasticsearch.port")
  private String port;

  @NotNull
  @JsonProperty("elasticsearch.cluster")
  private String cluster;

  @NotNull
  @JsonProperty("elasticsearch.alias")
  private String alias;

  @NotNull
  @JsonProperty("elasticsearch.doctype")
  private String docType;

  /**
   * @return the elasticsearch host
   */
  public String getElasticsearchHost() {
    return host;
  }

  /**
   * @return the elasticsearch port
   */
  public String getElasticsearchPort() {
    return port;
  }

  /**
   * @return the elasticsearch cluster
   */
  public String getElasticsearchCluster() {
    return cluster;
  }

  /**
   * @return the elasticsearch index alias
   */
  public String getElasticsearchAlias() {
    return alias;
  }


  /**
   * @return the elasticsearch document type
   */
  public String getElasticsearchDocType() {
    return docType;
  }
}
