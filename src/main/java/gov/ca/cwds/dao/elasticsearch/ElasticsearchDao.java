package gov.ca.cwds.dao.elasticsearch;

import java.io.Closeable;
import java.io.IOException;
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
 * A client DAO for Elasticsearch.
 * 
 * @author CWDS API Team
 */
public class ElasticsearchDao implements Closeable {

  private static final Logger LOGGER = LogManager.getLogger(ElasticsearchDao.class);

  private String host;
  private int port;
  private String clusterName;
  private String indexName;
  private String indexType;
  private Client client;

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

  /**
   * Start the ES client.
   */
  public void start() {
    Settings settings = Settings.settingsBuilder().put("cluster.name", clusterName).build();
    try {
      this.client = TransportClient.builder().settings(settings).build()
          .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
    } catch (UnknownHostException e) {
      final String msg = "Caught UnknownHostException: " + e.getMessage();
      LOGGER.error(msg, e);
      throw new DaoException(msg, e);
    }
  }

  /**
   * Stop the ES client, if started.
   * 
   * @throws Exception on disconnect
   */
  public void stop() throws Exception {
    if (client != null) {
      this.client.close();
    }
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

  /**
   * Getter for configured host.
   * 
   * @return ES host
   */
  public String getHost() {
    return host;
  }

  /**
   * Getter for configured port.
   * 
   * @return ES port
   */
  public int getPort() {
    return port;
  }

  /**
   * Getter for configured cluster name.
   * 
   * @return ES cluster name
   */
  public String getClusterName() {
    return clusterName;
  }

  @Override
  public void close() throws IOException {
    try {
      stop();
    } catch (Exception e) {
      final String msg = "Error closing ElasticSearch DAO: " + e.getMessage();
      LOGGER.error(msg, e);
      throw new IOException(msg, e);
    }
  }
}
