package gov.ca.cwds.jobs;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;

import gov.ca.cwds.dao.elasticsearch.ElasticsearchConfiguration;
import gov.ca.cwds.dao.elasticsearch.ElasticsearchDao;
import gov.ca.cwds.jobs.inject.JobsGuiceInjector;
import gov.ca.cwds.rest.api.persistence.ns.Person;
import gov.ca.cwds.rest.jdbi.ns.PersonDao;

public class PersonIndexerJob implements Job {

  private static final Logger LOGGER = LogManager.getLogger(PersonIndexerJob.class);

  private static ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
  private static ObjectMapper MAPPER = new ObjectMapper();
  private static DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private PersonDao personDao;
  private ElasticsearchDao elasticsearchDao;
  private Date lastJobRunTime;

  public PersonIndexerJob(PersonDao personDao, ElasticsearchDao elasticsearchDao,
      Date lastJobRunTime) {
    super();
    this.personDao = personDao;
    this.elasticsearchDao = elasticsearchDao;
    this.lastJobRunTime = lastJobRunTime;
  }

  public static void main(String... args) throws Exception {
    if (args.length != 2) {
      throw new Error(
          "Usage: java gov.ca.cwds.jobs.PersonIndexerJob configFileLocation lastJobRunTime(yyyy-MM-dd HH:mm:ss)");
    }

    Injector injector = Guice.createInjector(new JobsGuiceInjector());
    SessionFactory sessionFactory = injector.getInstance(SessionFactory.class);
    PersonDao personDao = new PersonDao(sessionFactory);

    File file = new File(args[0]);
    ElasticsearchConfiguration configuration =
        YAML_MAPPER.readValue(file, ElasticsearchConfiguration.class);
    ElasticsearchDao elasticsearchDao = new ElasticsearchDao(configuration);

    Date lastJobRunTime = DATEFORMAT.parse(args[1]);

    PersonIndexerJob job = new PersonIndexerJob(personDao, elasticsearchDao, lastJobRunTime);
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
      List<Person> results = personDao.findAllUpdatedAfter(lastJobRunTime);
      elasticsearchDao.start();
      for (Person person : results) {
        indexDocument(person);
      }
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
    elasticsearchDao.index(document, person.getPrimaryKey().toString());
  }
}

