package gov.ca.cwds.neutron.launch.listener;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;

/**
 * Report Elasticsearch bulk errors elegantly instead of relying on
 * {@link BulkResponse#toString()}..
 * 
 * <p>
 * <a href="http://jimmyneutron.wikia.com/wiki/Cindy_Vortex">Cindy</a> is Jimmy's girlfriend, and
 * she excels at pointing out his flaws and short-comings -- just like this class.
 * </p>
 * 
 * @author CWDS API Team
 */
public class CindyBulkResponse extends BulkResponse {

  public CindyBulkResponse(BulkItemResponse[] responses, long tookInMillis) {
    super(responses, tookInMillis);
  }

}
