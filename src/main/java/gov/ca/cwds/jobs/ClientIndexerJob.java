package gov.ca.cwds.jobs;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import gov.ca.cwds.dao.elasticsearch.ElasticsearchDao;
import gov.ca.cwds.data.cms.ClientDao;
import gov.ca.cwds.data.persistence.cms.Client;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.inject.JobsGuiceInjector;
import gov.ca.cwds.jobs.inject.LastRunFile;
import gov.ca.cwds.rest.api.domain.DomainChef;
import gov.ca.cwds.rest.api.domain.es.Person;

/**
 * Job to load clients from CMS into ElasticSearch
 * 
 * @author CWDS API Team
 */
public class ClientIndexerJob extends JobBasedOnLastSuccessfulRunTime {

  private static final Logger LOGGER = LogManager.getLogger(ClientIndexerJob.class);

  private final ObjectMapper mapper;
  private final ClientDao clientDao;
  private final ElasticsearchDao elasticsearchDao;
  private final SessionFactory sessionFactory;

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param clientDao Client DAO
   * @param elasticsearchDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last batch run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   */
  @Inject
  public ClientIndexerJob(final ClientDao clientDao, final ElasticsearchDao elasticsearchDao,
      @LastRunFile final String lastJobRunTimeFilename, final ObjectMapper mapper,
      @CmsSessionFactory SessionFactory sessionFactory) {
    super(lastJobRunTimeFilename);
    this.clientDao = clientDao;
    this.elasticsearchDao = elasticsearchDao;
    this.mapper = mapper;
    this.sessionFactory = sessionFactory;
  }

  public static void main(String... args) throws Exception {
    if (args.length != 2) {
      throw new JobsException("Usage: java " + ClientIndexerJob.class.getName()
          + "<ES config file> <last job runtime file>");
    }

    final Injector injector =
        Guice.createInjector(new JobsGuiceInjector(new File(args[0]), args[1]));
    final ClientIndexerJob job = injector.getInstance(ClientIndexerJob.class);

    // Let session factory and elasticsearch dao close themselves automatically.
    try (SessionFactory sessionFactory = job.sessionFactory;
        ElasticsearchDao esDao = job.elasticsearchDao) {
      job.run();
    } catch (JobsException e) {
      LOGGER.error("Unable to complete job", e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see gov.ca.cwds.jobs.JobBasedOnLastSuccessfulRunTime#_run(java.util.Date)
   */
  @Override
  public Date _run(Date lastSuccessfulRunTime) {
    try {
      final List<Client> results = clientDao.findAllUpdatedAfter(lastSuccessfulRunTime);
      LOGGER.info(MessageFormat.format("Found {0} people to index", results.size()));
      final Date startTime = new Date();
      elasticsearchDao.start();
      for (Client client : results) {
        final String json = mapper.writeValueAsString(client);
        LOGGER.debug("client: {}", json);
        final Person esPerson = new Person(client.getId().toString(), client.getCommonFirstName(),
            client.getCommonLastName(), client.getGenderCode(),
            DomainChef.cookDate(client.getBirthDate()), client.getSocialSecurityNumber(),
            client.getClass().getName(), json);
        indexDocument(esPerson);
      }
      LOGGER.info(MessageFormat.format("Indexed {0} people", results.size()));
      LOGGER.info(MessageFormat.format("Updating last succesful run time to {0}",
          jobDateFormat.format(startTime)));
      return startTime;
    } catch (IOException e) {
      throw new JobsException("Could not parse configuration file", e);
    } catch (Exception e) {
      throw new JobsException(e);
      // } finally {
      // try {
      // elasticsearchDao.stop();
      // } catch (Exception e) {
      // LOGGER.error(e);
      // }
    }
  }

  private void indexDocument(Person person) throws JsonProcessingException {
    final String document = mapper.writeValueAsString(person);
    elasticsearchDao.index(document, person.getId().toString());
  }
}

