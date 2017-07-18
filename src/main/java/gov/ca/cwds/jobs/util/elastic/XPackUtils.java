package gov.ca.cwds.jobs.util.elastic;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;

/**
 * Created by dmitry.rudenko on 7/13/2017.
 */
public class XPackUtils {
  public static TransportClient secureClient(String user, String password, Settings.Builder settings) {
    if(user != null && password != null) {
      settings.put("xpack.security.user", user + ":" + password);
    }
    return new PreBuiltXPackTransportClient(settings.build());
  }
}
