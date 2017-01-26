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
import gov.ca.cwds.data.cms.EducationProviderContactDao;
import gov.ca.cwds.data.persistence.cms.EducationProviderContact;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.inject.JobsGuiceInjector;
import gov.ca.cwds.rest.api.domain.es.Person;

public class EducationProviderContactIndexerJob extends JobBasedOnLastSuccessfulRunTime {
  private static final Logger LOGGER = LogManager.getLogger(CollateralIndividualIndexerJob.class);

  private static ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
  private static ObjectMapper MAPPER = new ObjectMapper();

  private EducationProviderContactDao educationProviderContactDao;
  private ElasticsearchDao elasticsearchDao;

  public EducationProviderContactIndexerJob(EducationProviderContactDao educationProviderContactDao,
      ElasticsearchDao elasticsearchDao, String lastJobRunTimeFilename) {
    super(lastJobRunTimeFilename);
    this.educationProviderContactDao = educationProviderContactDao;
    this.elasticsearchDao = elasticsearchDao;
  }

  public static void main(String... args) throws Exception {
    if (args.length != 2) {
      throw new Error(
          "Usage: java gov.ca.cwds.jobs.EducationProviderContactJob esconfigFileLocation lastJobRunTimeFilename");
    }

    Injector injector = Guice.createInjector(new JobsGuiceInjector());
    SessionFactory sessionFactory =
        injector.getInstance(Key.get(SessionFactory.class, CmsSessionFactory.class));

    EducationProviderContactDao educationProviderContactDao =
        new EducationProviderContactDao(sessionFactory);

    File file = new File(args[0]);
    ElasticsearchConfiguration configuration =
        YAML_MAPPER.readValue(file, ElasticsearchConfiguration.class);
    ElasticsearchDao elasticsearchDao = new ElasticsearchDao(configuration);

    EducationProviderContactIndexerJob job = new EducationProviderContactIndexerJob(
        educationProviderContactDao, elasticsearchDao, args[1]);
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
      List<EducationProviderContact> results =
          educationProviderContactDao.findAllUpdatedAfter(lastSuccessfulRunTime);
      LOGGER.info(MessageFormat.format("Found {0} people to index", results.size()));
      Date currentTime = new Date();
      String nodate = new String();

      for (EducationProviderContact educationProviderContact : results) {
        Person esPerson = new Person(educationProviderContact.getPrimaryKey(),
            educationProviderContact.getFirstName(), educationProviderContact.getLastName(), "", // Gender
            nodate, // DOB
            "", // SSN
            educationProviderContact.getClass().getName(),
            MAPPER.writeValueAsString(educationProviderContact));
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
