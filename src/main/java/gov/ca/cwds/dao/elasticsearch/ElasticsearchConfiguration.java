package gov.ca.cwds.dao.elasticsearch;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;


public class ElasticsearchConfiguration {

  @NotNull
  @JsonProperty("elasticsearch.host")
  private String elasticsearchHost;

  @NotNull
  @JsonProperty("elasticsearch.cluster")
  private String elasticsearchCluster;

  @NotNull
  @JsonProperty("elasticsearch.node")
  private String elasticsearchNodeName;

  @NotNull
  @JsonProperty("elasticsearch.port")
  private int elasticsearchPort;

  @NotNull
  @JsonProperty("elasticsearch.indexName")
  private String indexName;

  @NotNull
  @JsonProperty("elasticsearch.indexType")
  private String indexType;

  /**
   * @return the elasticsearchHost
   */
  public String getElasticsearchHost() {
    return elasticsearchHost;
  }

  /**
   * @return the elasticsearchCluster
   */
  public String getElasticsearchCluster() {
    return elasticsearchCluster;
  }

  /**
   * @return the elasticsearchNodeName
   */
  public String getElasticsearchNodeName() {
    return elasticsearchNodeName;
  }

  /**
   * @return the elasticsearchPort
   */
  public int getElasticsearchPort() {
    return elasticsearchPort;
  }

  /**
   * @return the indexName
   */
  public String getIndexName() {
    return indexName;
  }

  /**
   * @return the indexType
   */
  public String getIndexType() {
    return indexType;
  }


}
