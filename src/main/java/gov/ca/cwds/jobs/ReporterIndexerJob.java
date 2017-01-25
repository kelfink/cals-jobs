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
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

import gov.ca.cwds.dao.elasticsearch.ElasticsearchConfiguration;
import gov.ca.cwds.dao.elasticsearch.ElasticsearchDao;
import gov.ca.cwds.data.cms.ReporterDao;
import gov.ca.cwds.data.persistence.cms.Reporter;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.inject.JobsGuiceInjector;
import gov.ca.cwds.rest.api.domain.es.Person;

/**
 * Job to load reporters from CMS into ElasticSearch
 * 
 * @author CWDS API Team
 */
public class ReporterIndexerJob extends JobBasedOnLastSuccessfulRunTime {

  private static final Logger LOGGER = LogManager.getLogger(ReporterIndexerJob.class);

  private static ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
  private static ObjectMapper MAPPER = new ObjectMapper();

  private ReporterDao reporterDao;
  private ElasticsearchDao elasticsearchDao;

  public ReporterIndexerJob(ReporterDao reporterDao, ElasticsearchDao elasticsearchDao,
      String lastJobRunTimeFilename) {
    super(lastJobRunTimeFilename);
    this.reporterDao = reporterDao;
    this.elasticsearchDao = elasticsearchDao;
  }

  public static void main(String... args) throws Exception {
    if (args.length != 2) {
      throw new JobsException(
          "Usage: java gov.ca.cwds.jobs.ReporterIndexerJob esconfigFileLocation lastJobRunTimeFilename");
    }

    final Injector injector = Guice.createInjector(new JobsGuiceInjector());
    SessionFactory sessionFactory =
        injector.getInstance(Key.get(SessionFactory.class, CmsSessionFactory.class));

    ReporterDao reporterDao = new ReporterDao(sessionFactory);

    File file = new File(args[0]);
    ElasticsearchConfiguration configuration =
        YAML_MAPPER.readValue(file, ElasticsearchConfiguration.class);
    ElasticsearchDao elasticsearchDao = new ElasticsearchDao(configuration);

    ReporterIndexerJob job = new ReporterIndexerJob(reporterDao, elasticsearchDao, args[1]);
    try {
      job.run();
    } catch (JobsException e) {
      LOGGER.error("Unable to complete job", e);
    } finally {
      sessionFactory.close();
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
      List<Reporter> results = reporterDao.findAllUpdatedAfter(lastSuccessfulRunTime);
      LOGGER.info(MessageFormat.format("Found {0} reporters to index", results.size()));
      Date currentTime = new Date();
      elasticsearchDao.start();
      for (Reporter reporter : results) {
        Person esPerson = new Person(reporter.getPrimaryKey(), reporter.getFirstName(),
            reporter.getLastName(), "", // Gender
            "", // DOB
            "", // SSN
            reporter.getClass().getName(), MAPPER.writeValueAsString(reporter));
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
        LOGGER.error(e);
      }
    }
  }

  private void indexDocument(Person person) throws JsonProcessingException {
    String document = MAPPER.writeValueAsString(person);
    elasticsearchDao.index(document, person.getId().toString());
  }

}
