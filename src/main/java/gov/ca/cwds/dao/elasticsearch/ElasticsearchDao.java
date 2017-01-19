package gov.ca.cwds.dao.elasticsearch;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.WriteConsistencyLevel;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import gov.ca.cwds.dao.DaoException;

/**
 * A client for Elasticsearch
 * 
 * @author CWDS API Team
 *
 */
public class ElasticsearchDao {

  private static final Logger LOGGER = LogManager.getLogger(ElasticsearchDao.class);

  private Client client;
  private String host;
  private int port;
  private String clusterName;
  private String indexName;
  private String indexType;

  /**
   * Constructor
   * 
   * @param host The host
   * @param port The port
   * @param clusterName The clusterName
   * @param indexName The indexName
   * @param indexType The indexType
   */
  public ElasticsearchDao(String host, int port, String clusterName, String indexName,
      String indexType) {
    super();
    this.host = host;
    this.port = port;
    this.clusterName = clusterName;
    this.indexName = indexName;
    this.indexType = indexType;
  }

  /**
   * Constructor
   * 
   * @param configuration The configuration
   */
  public ElasticsearchDao(ElasticsearchConfiguration configuration) {
    this.host = configuration.getElasticsearchHost();
    this.clusterName = configuration.getElasticsearchCluster();
    this.port = configuration.getElasticsearchPort();
    this.indexName = configuration.getIndexName();
    this.indexType = configuration.getIndexType();
  }

  public void start() {
    Settings settings = Settings.settingsBuilder().put("cluster.name", clusterName).build();
    try {
      this.client = TransportClient.builder().settings(settings).build()
          .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
    } catch (UnknownHostException e) {
      throw new DaoException(e);
    }
  }

  public void stop() throws Exception {
    this.client.close();
  }

  /**
   * Create an ElasticSearch document with the given index and document type.
   * 
   * @param document JSON of document
   * @param id The id
   * 
   * @return true if indexed, false if updated
   */
  public boolean index(String document, String id) {
    LOGGER.info("ElasticSearchDao.createDocument(): " + document);

    IndexResponse response = client.prepareIndex(indexName, indexType, id)
        .setConsistencyLevel(WriteConsistencyLevel.DEFAULT).setSource(document).execute()
        .actionGet();

    boolean created = response.isCreated();
    if (created) {
      LOGGER.info("Created document:\nindex: " + response.getIndex() + "\ndoc type: "
          + response.getType() + "\nid: " + response.getId() + "\nversion: " + response.getVersion()
          + "\ncreated: " + response.isCreated());
      LOGGER.info("Created document --- index:{}, doc type:{},id:{},version:{},created:{}",
          response.getIndex(), response.getType(), response.getId(), response.getVersion(),
          response.isCreated());
    } else {
      LOGGER.warn("Document not created --- index:{}, doc type:{},id:{},version:{},created:{}",
          response.getIndex(), response.getType(), response.getId(), response.getVersion(),
          response.isCreated());
    }

    return created;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getClusterName() {
    return clusterName;
  }
}
