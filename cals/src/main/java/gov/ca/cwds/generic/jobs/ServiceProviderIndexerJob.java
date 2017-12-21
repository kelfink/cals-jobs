package gov.ca.cwds.generic.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.generic.dao.cms.ReplicatedServiceProviderDao;
import gov.ca.cwds.generic.data.persistence.cms.rep.ReplicatedServiceProvider;
import gov.ca.cwds.generic.jobs.inject.JobRunner;
import gov.ca.cwds.generic.jobs.inject.LastRunFile;
import gov.ca.cwds.inject.CmsSessionFactory;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.SessionFactory;

/**
 * Job to load Service Provider from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class ServiceProviderIndexerJob
    extends BasePersonIndexerJob<ReplicatedServiceProvider, ReplicatedServiceProvider> {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param dao ServiceProvider DAO
   * @param esDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public ServiceProviderIndexerJob(final ReplicatedServiceProviderDao dao,
      final ElasticsearchDao esDao, @LastRunFile final String lastJobRunTimeFilename,
      final ObjectMapper mapper, @CmsSessionFactory SessionFactory sessionFactory) {
    super(dao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
  }

  /**
   * @deprecated soon to be removed.
   */
  @Override
  @Deprecated
  public String getLegacySourceTable() {
    return "SVC_PVRT";
  }

  @Override
  protected List<Pair<String, String>> getPartitionRanges() {
    List<Pair<String, String>> ret = new ArrayList<>();

    final boolean isMainframe = isDB2OnZOS();
    if (isMainframe && (getDBSchemaName().toUpperCase().endsWith("RSQ")
        || getDBSchemaName().toUpperCase().endsWith("REP"))) {
      ret.add(Pair.of("aaaaaaaaaa", "AI2pE99999"));
      ret.add(Pair.of("AI2pE99999", "BhUE999998"));
      ret.add(Pair.of("BhUE999998", "BQMUE99997"));
      ret.add(Pair.of("BQMUE99997", "CpE9999996"));
      ret.add(Pair.of("CpE9999996", "CYxpE99995"));
      ret.add(Pair.of("CYxpE99995", "DxpE999994"));
      ret.add(Pair.of("DxpE999994", "D6hUE99993"));
      ret.add(Pair.of("D6hUE99993", "EE99999992"));
      ret.add(Pair.of("EE99999992", "Fd2pE99991"));
      ret.add(Pair.of("Fd2pE99991", "FMUE999990"));
      ret.add(Pair.of("FMUE999990", "GlMUE9999Z"));
      ret.add(Pair.of("GlMUE9999Z", "GUE999999Y"));
      ret.add(Pair.of("GUE999999Y", "HtxpE9999X"));
      ret.add(Pair.of("HtxpE9999X", "H2pE99999W"));
      ret.add(Pair.of("H2pE99999W", "IBhUE9999V"));
      ret.add(Pair.of("IBhUE9999V", "I99999999U"));
      ret.add(Pair.of("I99999999U", "JI2pE9999T"));
      ret.add(Pair.of("JI2pE9999T", "KhUE99999S"));
      ret.add(Pair.of("KhUE99999S", "KQMUE9999R"));
      ret.add(Pair.of("KQMUE9999R", "LpE999999Q"));
      ret.add(Pair.of("LpE999999Q", "LYxpE9999P"));
      ret.add(Pair.of("LYxpE9999P", "MxpE99999O"));
      ret.add(Pair.of("MxpE99999O", "M6hUE9999N"));
      ret.add(Pair.of("M6hUE9999N", "NE9999999M"));
      ret.add(Pair.of("NE9999999M", "Od2pE9999L"));
      ret.add(Pair.of("Od2pE9999L", "OMUE99999K"));
      ret.add(Pair.of("OMUE99999K", "PlMUE9999J"));
      ret.add(Pair.of("PlMUE9999J", "PUE999999I"));
      ret.add(Pair.of("PUE999999I", "QtxpE9999H"));
      ret.add(Pair.of("QtxpE9999H", "Q2pE99999G"));
      ret.add(Pair.of("Q2pE99999G", "RBhUE9999F"));
      ret.add(Pair.of("RBhUE9999F", "R99999999E"));
      ret.add(Pair.of("R99999999E", "SI2pE9999D"));
      ret.add(Pair.of("SI2pE9999D", "ThUE99999C"));
      ret.add(Pair.of("ThUE99999C", "TQMUE9999B"));
      ret.add(Pair.of("TQMUE9999B", "UpE999999A"));
      ret.add(Pair.of("UpE999999A", "UYxpE9999z"));
      ret.add(Pair.of("UYxpE9999z", "VxpE99999y"));
      ret.add(Pair.of("VxpE99999y", "0BhUE9999p"));
      ret.add(Pair.of("0BhUE9999p", "099999999o"));
      ret.add(Pair.of("099999999o", "1I2pE9999n"));
      ret.add(Pair.of("1I2pE9999n", "2hUE99999m"));
      ret.add(Pair.of("2hUE99999m", "2QMUE9999l"));
      ret.add(Pair.of("2QMUE9999l", "3pE999999k"));
      ret.add(Pair.of("3pE999999k", "3YxpE9999j"));
      ret.add(Pair.of("3YxpE9999j", "4xpE99999i"));
      ret.add(Pair.of("4xpE99999i", "46hUE9999h"));
      ret.add(Pair.of("46hUE9999h", "5E9999999g"));
      ret.add(Pair.of("5E9999999g", "6d2pE9999f"));
      ret.add(Pair.of("6d2pE9999f", "6MUE99999e"));
      ret.add(Pair.of("6MUE99999e", "7lMUE9999d"));
      ret.add(Pair.of("7lMUE9999d", "7UE999999c"));
      ret.add(Pair.of("7UE999999c", "8txpE9999a"));
      ret.add(Pair.of("8txpE9999a", "82pE99999a"));
      ret.add(Pair.of("82pE99999a", "9BhUE99989"));
      ret.add(Pair.of("9BhUE99989", "9999999988"));

      ret = limitRange(ret); // command line range restriction
    } else if (isMainframe) {
      // ----------------------------
      // z/OS, small data set:
      // ORDER: a,z,A,Z,0,9
      // ----------------------------
      ret.add(Pair.of("aaaaaaaaaa", "9999999999"));
    } else {
      // ----------------------------
      // Linux or small data set:
      // ORDER: 0,9,a,A,z,Z
      // ----------------------------
      ret.add(Pair.of("0000000000", "ZZZZZZZZZZ"));
    }

    return ret;
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    JobRunner.runStandalone(ServiceProviderIndexerJob.class, args);
  }

}
