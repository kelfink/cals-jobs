package gov.ca.cwds.jobs.common.elastic;

import gov.ca.cwds.jobs.common.BaseJobConfiguration;
import gov.ca.cwds.rest.api.ApiException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ievgenii Drozd
 * @version 2/27/18
 */
public class ElasticUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticUtils.class);

  public static TransportClient createAndConfigureESClient(BaseJobConfiguration config) {
    TransportClient client = null;

    LOGGER.info("Create NEW ES client");
    try {
      Settings.Builder settings =
          Settings.builder().put("cluster.name", config.getElasticsearchCluster());
      client = XPackUtils.secureClient(config.getUser(), config.getPassword(), settings);

      for (InetSocketTransportAddress address : getValidatedESNodes(config)) {
        client.addTransportAddress(address);
      }
    } catch (RuntimeException | UnknownHostException e) {
      LOGGER.error("Error initializing Elasticsearch client: {}", e.getMessage(), e);
      if (client != null) {
        client.close();
      }
      throw new ApiException("Error initializing Elasticsearch client: " + e.getMessage(), e);
    }
    return client;
  }

  private static List<InetSocketTransportAddress> getValidatedESNodes(BaseJobConfiguration config)
      throws UnknownHostException {
    List<InetSocketTransportAddress> nodesList = new LinkedList<>();

    if (validateESNode(config.getElasticsearchHost(), config.getElasticsearchPort())) {
      LOGGER.info("Adding new ES Node host:[{}] port:[{}] to elasticsearch client", config.getElasticsearchHost(),
          config.getElasticsearchPort());
      nodesList
          .add(new InetSocketTransportAddress(InetAddress.getByName(config.getElasticsearchHost()),
              Integer.parseInt(config.getElasticsearchPort())));
    }

    return nodesList;
  }

  private static boolean validateESNode(String host, String node) {
    return true;
  }
}


