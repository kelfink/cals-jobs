package gov.ca.cwds.jobs;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

import gov.ca.cwds.dao.elasticsearch.ElasticsearchConfiguration;
import gov.ca.cwds.dao.elasticsearch.ElasticsearchDao;
import gov.ca.cwds.jobs.inject.JobsGuiceInjector;
import gov.ca.cwds.jobs.inject.NsSessionFactory;
import gov.ca.cwds.rest.api.domain.DomainObject;
import gov.ca.cwds.rest.api.persistence.ns.Person;
import gov.ca.cwds.rest.jdbi.ns.PersonDao;

/**
 * Job to load person from NS into ElasticSearch
 * 
 * @author CWDS API Team
 */
public class PersonIndexerJob extends JobBasedOnLastSuccessfulRunTime {

  private static final Logger LOGGER = LogManager.getLogger(PersonIndexerJob.class);

  private static ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
  private static ObjectMapper MAPPER = new ObjectMapper();

  private PersonDao personDao;
  private ElasticsearchDao elasticsearchDao;

  public PersonIndexerJob(PersonDao personDao, ElasticsearchDao elasticsearchDao,
      String lastJobRunTimeFilename) {
    super(lastJobRunTimeFilename);
    this.personDao = personDao;
    this.elasticsearchDao = elasticsearchDao;
  }

  public static void main(String... args) throws Exception {
    if (args.length != 2) {
      throw new Error(
          "Usage: java gov.ca.cwds.jobs.PersonIndexerJob esconfigFileLocation lastJobRunTimeFilename");
    }

    Injector injector = Guice.createInjector(new JobsGuiceInjector());
    SessionFactory sessionFactory =
        injector.getInstance(Key.get(SessionFactory.class, NsSessionFactory.class));


    PersonDao personDao = new PersonDao(sessionFactory);

    File file = new File(args[0]);
    ElasticsearchConfiguration configuration =
        YAML_MAPPER.readValue(file, ElasticsearchConfiguration.class);
    ElasticsearchDao elasticsearchDao = new ElasticsearchDao(configuration);


    PersonIndexerJob job = new PersonIndexerJob(personDao, elasticsearchDao, args[1]);
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
      List<Person> results = personDao.findAllUpdatedAfter(lastSuccessfulRunTime);
      LOGGER.info(MessageFormat.format("Found {0} people to index", results.size()));
      Date currentTime = new Date();
      elasticsearchDao.start();
      for (Person person : results) {
        gov.ca.cwds.rest.api.elasticsearch.ns.Person esPerson =
            new gov.ca.cwds.rest.api.elasticsearch.ns.Person(person.getId().toString(),
                person.getFirstName(), person.getLastName(), person.getGender(),
                DomainObject.cookDate(person.getDateOfBirth()), person.getSsn(),
                person.getClass().getName(), MAPPER.writeValueAsString(person));

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

  private void indexDocument(gov.ca.cwds.rest.api.elasticsearch.ns.Person person) throws Exception {
    String document = MAPPER.writeValueAsString(person);
    elasticsearchDao.index(document, person.getId().toString());
  }
}

