package gov.ca.cwds.jobs;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

import gov.ca.cwds.dao.elasticsearch.ElasticsearchConfiguration;
import gov.ca.cwds.dao.elasticsearch.ElasticsearchDao;
import gov.ca.cwds.data.cms.ClientDao;
import gov.ca.cwds.data.cms.SubstituteCareProviderDao;
import gov.ca.cwds.data.persistence.cms.SubstituteCareProvider;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.inject.JobsGuiceInjector;
import gov.ca.cwds.rest.api.domain.DomainChef;
import gov.ca.cwds.rest.api.domain.es.Person;

/**
 * Job to load substituteCareProvider from CMS into ElasticSearch
 * 
 * @author CWDS API Team
 */
public class SubstituteCareProviderIndexJob extends JobBasedOnLastSuccessfulRunTime {
  private static final Logger LOGGER = LogManager.getLogger(SubstituteCareProviderIndexJob.class);

  private static ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
  private static ObjectMapper MAPPER = new ObjectMapper();

  private SubstituteCareProviderDao substituteCareProviderDao;
  private ElasticsearchDao elasticsearchDao;

  public SubstituteCareProviderIndexJob(SubstituteCareProviderDao substituteCareProviderDao,
      ElasticsearchDao elasticsearchDao, String lastJobRunTimeFilename,
      @CmsSessionFactory SessionFactory sessionFactory) {
    super(lastJobRunTimeFilename);
    this.substituteCareProviderDao = substituteCareProviderDao;
    this.elasticsearchDao = elasticsearchDao;
  }

  public static void main(String... args) throws Exception {
    if (args.length != 2) {
      throw new Error(
          "Usage: java gov.ca.cwds.jobs.ClientIndexerJob esconfigFileLocation lastJobRunTimeFilename");
    }

    Injector injector = Guice.createInjector(new JobsGuiceInjector());
    SessionFactory sessionFactory =
        injector.getInstance(Key.get(SessionFactory.class, CmsSessionFactory.class));
    ClientDao clientDao = new ClientDao(sessionFactory);

    File file = new File(args[0]);
    ElasticsearchConfiguration configuration =
        YAML_MAPPER.readValue(file, ElasticsearchConfiguration.class);
    ElasticsearchDao elasticsearchDao = new ElasticsearchDao(configuration);

    ClientIndexerJob job = new ClientIndexerJob(clientDao, elasticsearchDao, args[1],
        injector.getInstance(ObjectMapper.class), sessionFactory);
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
   * @see gov.ca.cwds.jobs.JobBasedOnLastSuccessfulRunTime#_run(java.util.Date)
   */
  @Override
  public Date _run(Date lastSuccessfulRunTime) {

    try {
      elasticsearchDao.start();
      List<SubstituteCareProvider> results =
          substituteCareProviderDao.findAllUpdatedAfter(lastSuccessfulRunTime);
      LOGGER.info(MessageFormat.format("Found {0} people to index", results.size()));
      Date currentTime = new Date();
      for (SubstituteCareProvider substituteCareProvider : results) {
        Person esPerson = new Person(substituteCareProvider.getPrimaryKey(),
            substituteCareProvider.getFirstName(), substituteCareProvider.getLastName(),
            substituteCareProvider.getGenderIndicator(),
            DomainChef.cookDate(substituteCareProvider.getBirthDate()),
            substituteCareProvider.getSocialSecurityNumber(),
            substituteCareProvider.getClass().getName(),
            MAPPER.writeValueAsString(substituteCareProvider));
        indexDocument(esPerson);

      }
      LOGGER.info(MessageFormat.format("Indexed {0} people", results.size()));
      LOGGER.info(MessageFormat.format("Updating last succesful run time to {0}",
          DATE_FORMAT.format(currentTime)));
      return currentTime;
    } catch (IOException e) {
      throw new JobsException("Could not parse configuration file", e);
    } catch (Exception e) {
      throw new JobsException(e);
    } finally {
      try {
        elasticsearchDao.stop();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void indexDocument(Person person) throws Exception {
    String document = MAPPER.writeValueAsString(person);
    elasticsearchDao.index(document, person.getId().toString());
  }

}
