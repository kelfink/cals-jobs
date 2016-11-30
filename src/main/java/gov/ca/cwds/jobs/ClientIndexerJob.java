package gov.ca.cwds.jobs;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import gov.ca.cwds.dao.elasticsearch.ElasticsearchConfiguration;
import gov.ca.cwds.dao.elasticsearch.ElasticsearchDao;
import gov.ca.cwds.rest.api.persistence.cms.Client;
import gov.ca.cwds.rest.jdbi.cms.ClientDao;

/**
 * Job to load clients from CMS into ElasticSearch
 * 
 * @author CWDS API Team
 */
public class ClientIndexerJob implements Job {
  private static final Logger LOGGER = LogManager.getLogger(ClientIndexerJob.class);

  private static ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
  private static ObjectMapper MAPPER = new ObjectMapper();
  // private static DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static SessionFactory sessionFactory;
  private static int count = 1;

  private ClientDao clientDao;
  private ElasticsearchDao elasticsearchDao;
  // private Date lastJobRunTime;



  /**
   * * Constructor
   * 
   * @param clientDao
   * @param elasticsearchDao
   */
  public ClientIndexerJob(ClientDao clientDao, ElasticsearchDao elasticsearchDao) {
    super();
    this.clientDao = clientDao;
    this.elasticsearchDao = elasticsearchDao;
  }


  public static void main(String... args) throws Exception {
    if (args.length != 2) {
      throw new Error(
          "Usage: java gov.ca.cwds.jobs.ClientIndexLoader configFileLocation lastJobRunTime(yyyy-MM-dd HH:mm:ss)");
    }

    File file = new File(args[0]);
    ElasticsearchConfiguration configuration =
        YAML_MAPPER.readValue(file, ElasticsearchConfiguration.class);
    ElasticsearchDao elasticSearchDao = new ElasticsearchDao(configuration);

    // Date lastJobRunTime = DATEFORMAT.parse(args[1]);

    sessionFactory = new Configuration().configure().buildSessionFactory();
    sessionFactory.getCurrentSession().beginTransaction();
    ClientDao clientDao = new ClientDao(sessionFactory);
    ClientIndexerJob job = new ClientIndexerJob(clientDao, elasticSearchDao);
    try {
      job.run();
    } catch (JobsException e) {
      LOGGER.error("Unable to complete job", e);
    } finally {
      sessionFactory.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.ca.cwds.jobs.Job#run()
   */
  @Override
  public void run() {
    try {
      List<Client> results = clientDao.findAll();
      elasticsearchDao.start();

      for (Client client : results) {
        indexDocument(client);
      }
    } catch (IOException e) {
      throw new JobsException("Could not parse configuration file", e);
    } catch (Exception e) {
      throw new JobsException(e);
    }

  }

  private void indexDocument(Client client) throws Exception {
    String document = "";

    if (client != null) {
      document = MAPPER.writeValueAsString(client);
    }
    elasticsearchDao.index(document, Integer.toString(count++));

  }
}

