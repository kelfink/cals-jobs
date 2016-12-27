package gov.ca.cwds.jobs;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

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
import gov.ca.cwds.jobs.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.inject.JobsGuiceInjector;
import gov.ca.cwds.rest.api.domain.DomainObject;
import gov.ca.cwds.rest.api.persistence.cms.OtherAdultInPlacemtHome;
import gov.ca.cwds.rest.jdbi.cms.OtherAdultInPlacemtHomeDao;

/**
 * Job to load other adults in placement home from CMS into ElasticSearch
 * 
 * @author CWDS API Team
 */
public class OtherAdultInPlacemtHomeIndexerJob extends JobBasedOnLastSuccessfulRunTime {

  private static final Logger LOGGER = LogManager.getLogger(PersonIndexerJob.class);

  private static ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
  private static ObjectMapper MAPPER = new ObjectMapper();

  private OtherAdultInPlacemtHomeDao otherAdultInPlacemtHomeDao;
  private ElasticsearchDao elasticsearchDao;


  public OtherAdultInPlacemtHomeIndexerJob(OtherAdultInPlacemtHomeDao otherAdultInPlacemtHomeDao,
      ElasticsearchDao elasticsearchDao, String lastJobRunTimeFilename) {
    super(lastJobRunTimeFilename);
    this.otherAdultInPlacemtHomeDao = otherAdultInPlacemtHomeDao;
    this.elasticsearchDao = elasticsearchDao;
  }

  public static void main(String... args) throws Exception {
    if (args.length != 2) {
      throw new Error(
          "Usage: java gov.ca.cwds.jobs.OtherAdultInPlacemtHomeIndexerJob esconfigFileLocation lastJobRunTimeFilename");
    }

    Injector injector = Guice.createInjector(new JobsGuiceInjector());
    SessionFactory sessionFactory =
        injector.getInstance(Key.get(SessionFactory.class, CmsSessionFactory.class));

    OtherAdultInPlacemtHomeDao otherAdultInPlacemtHomeDao =
        new OtherAdultInPlacemtHomeDao(sessionFactory);

    File file = new File(args[0]);
    ElasticsearchConfiguration configuration =
        YAML_MAPPER.readValue(file, ElasticsearchConfiguration.class);
    ElasticsearchDao elasticsearchDao = new ElasticsearchDao(configuration);

    OtherAdultInPlacemtHomeIndexerJob job = new OtherAdultInPlacemtHomeIndexerJob(
        otherAdultInPlacemtHomeDao, elasticsearchDao, args[1]);
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
      List<OtherAdultInPlacemtHome> results =
          otherAdultInPlacemtHomeDao.findAllUpdatedAfter(lastSuccessfulRunTime);
      LOGGER.info(MessageFormat.format("Found {0} people to index", results.size()));
      Date currentTime = new Date();

      elasticsearchDao.start();
      for (OtherAdultInPlacemtHome otherAdultInPlacemtHome : results) {
        String fullname = otherAdultInPlacemtHome.getName();
        StringTokenizer namestr = new StringTokenizer(fullname);
        String firstname = namestr.nextToken();
        String lastname = " ";
        while (namestr.hasMoreTokens()) {
          lastname = namestr.nextToken();
        }

        gov.ca.cwds.rest.api.elasticsearch.ns.Person esPerson =
            new gov.ca.cwds.rest.api.elasticsearch.ns.Person(
                otherAdultInPlacemtHome.getPrimaryKey(), firstname, lastname,
                otherAdultInPlacemtHome.getGenderCode(),
                DomainObject.cookDate(otherAdultInPlacemtHome.getBirthDate()), "", // SSN
                otherAdultInPlacemtHome.getClass().getName(),
                MAPPER.writeValueAsString(otherAdultInPlacemtHome));
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
        LOGGER.error(e.getMessage(), e);
        throw new JobsException(e);
      }
    }
  }

  private void indexDocument(gov.ca.cwds.rest.api.elasticsearch.ns.Person person) throws Exception {
    String document = MAPPER.writeValueAsString(person);
    elasticsearchDao.index(document, person.getId().toString());
  }
}
