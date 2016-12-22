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
import gov.ca.cwds.rest.api.persistence.cms.OtherChildInPlacemtHome;
import gov.ca.cwds.rest.jdbi.cms.OtherChildInPlacemtHomeDao;

/**
 * Job to load other children in placement home from CMS into ElasticSearch
 * 
 * @author CWDS API Team
 */
public class OtherChildInPlacemtHomeIndexerJob extends JobBasedOnLastSuccessfulRunTime {

  private static final Logger LOGGER = LogManager.getLogger(PersonIndexerJob.class);

  private static ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
  private static ObjectMapper MAPPER = new ObjectMapper();

  private OtherChildInPlacemtHomeDao otherChildInPlacemtHomeDao;
  private ElasticsearchDao elasticsearchDao;


  public OtherChildInPlacemtHomeIndexerJob(OtherChildInPlacemtHomeDao otherChildInPlacemtHomeDao,
      ElasticsearchDao elasticsearchDao, String lastJobRunTimeFilename) {
    super(lastJobRunTimeFilename);
    this.otherChildInPlacemtHomeDao = otherChildInPlacemtHomeDao;
    this.elasticsearchDao = elasticsearchDao;
  }

  public static void main(String... args) throws Exception {
    if (args.length != 2) {
      throw new Error(
          "Usage: java gov.ca.cwds.jobs.OtherChildInPlacemtHomeIndexerJob esconfigFileLocation lastJobRunTimeFilename");
    }

    Injector injector = Guice.createInjector(new JobsGuiceInjector());
    SessionFactory sessionFactory =
        injector.getInstance(Key.get(SessionFactory.class, CmsSessionFactory.class));

    OtherChildInPlacemtHomeDao otherChildInPlacemtHomeDao =
        new OtherChildInPlacemtHomeDao(sessionFactory);

    File file = new File(args[0]);
    ElasticsearchConfiguration configuration =
        YAML_MAPPER.readValue(file, ElasticsearchConfiguration.class);
    ElasticsearchDao elasticsearchDao = new ElasticsearchDao(configuration);

    OtherChildInPlacemtHomeIndexerJob job = new OtherChildInPlacemtHomeIndexerJob(
        otherChildInPlacemtHomeDao, elasticsearchDao, args[1]);
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
      List<OtherChildInPlacemtHome> results =
          otherChildInPlacemtHomeDao.findAllUpdatedAfter(lastSuccessfulRunTime);
      LOGGER.info(MessageFormat.format("Found {0} people to index", results.size()));
      Date currentTime = new Date();


      elasticsearchDao.start();
      for (OtherChildInPlacemtHome otherChildInPlacemtHome : results) {
        String fullname = otherChildInPlacemtHome.getName();
        StringTokenizer namestr = new StringTokenizer(fullname);
        String firstname = namestr.nextToken();
        String lastname = " ";
        while (namestr.hasMoreTokens()) {
          lastname = namestr.nextToken();
        }

        gov.ca.cwds.rest.api.elasticsearch.ns.Person esPerson =
            new gov.ca.cwds.rest.api.elasticsearch.ns.Person(
                otherChildInPlacemtHome.getPrimaryKey(), firstname, lastname,
                otherChildInPlacemtHome.getGenderCode(),
                DomainObject.cookDate(otherChildInPlacemtHome.getBirthDate()), "", // SSN
                otherChildInPlacemtHome.getClass().getName(),
                MAPPER.writeValueAsString(otherChildInPlacemtHome));
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
