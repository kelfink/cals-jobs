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

import gov.ca.cwds.dao.DocumentMetadataDao;
import gov.ca.cwds.dao.elasticsearch.ElasticsearchConfiguration;
import gov.ca.cwds.dao.elasticsearch.ElasticsearchDao;
import gov.ca.cwds.data.model.cms.DocumentMetadata;
import gov.ca.cwds.jobs.inject.JobsGuiceInjector;
import gov.ca.cwds.rest.api.domain.cms.CmsDocReferralClient;
import gov.ca.cwds.rest.jdbi.CrudsDao;
import gov.ca.cwds.rest.jdbi.cms.CmsDocReferralClientDao;
import gov.ca.cwds.rest.jdbi.cms.CmsDocumentDao;
import gov.ca.cwds.rest.services.cms.CmsDocReferralClientService;

/**
 * Job to load documents from CMS into ElasticSearch
 * 
 * @author CWDS API Team
 */
public class DocumentIndexerJob extends JobBasedOnLastSuccessfulRunTime {
  private static final Logger LOGGER = LogManager.getLogger(DocumentIndexerJob.class);

  private static ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
  private static ObjectMapper MAPPER = new ObjectMapper();

  private DocumentMetadataDao documentMetadataDao;
  private ElasticsearchDao elasticsearchDao;
  private CmsDocReferralClientService cmsDocReferralClientService;


  /**
   * Constructor
   * 
   * @param documentMetadataDao The documentMetadataDao
   * @param elasticsearchDao The elasticsearchDao
   * @param cmsDocReferralClientService The cmsDocReferralClientService
   * @param lastJobRunTimeFilename The lastJobRunTimeFilename
   */
  public DocumentIndexerJob(DocumentMetadataDao documentMetadataDao,
      ElasticsearchDao elasticsearchDao, CmsDocReferralClientService cmsDocReferralClientService,
      String lastJobRunTimeFilename) {
    super(lastJobRunTimeFilename);
    this.documentMetadataDao = documentMetadataDao;
    this.elasticsearchDao = elasticsearchDao;
    this.cmsDocReferralClientService = cmsDocReferralClientService;
  }



  public static void main(String... args) throws Exception {
    if (args.length != 2) {
      throw new Error(
          "Usage: java gov.ca.cwds.jobs.DocumentIndexLoader esconfigFileLocation lastJobRunTimeFilename");
    }
    Injector injector = Guice.createInjector(new JobsGuiceInjector());
    DocumentMetadataDao documentMetadataDao = injector.getInstance(DocumentMetadataDao.class);

    File file = new File(args[0]);
    ElasticsearchConfiguration configuration =
        YAML_MAPPER.readValue(file, ElasticsearchConfiguration.class);
    ElasticsearchDao elasticSearchDao = new ElasticsearchDao(configuration);

    // classes from the API project are not integrated with GUICE so construct by hand
    SessionFactory sessionFactory = injector.getInstance(SessionFactory.class);
    CrudsDao<gov.ca.cwds.rest.api.persistence.cms.CmsDocReferralClient> cmsDocDao =
        new CmsDocReferralClientDao(sessionFactory);
    CrudsDao<gov.ca.cwds.rest.api.persistence.cms.CmsDocument> docDao =
        new CmsDocumentDao(sessionFactory);
    CmsDocReferralClientService cmsDocReferralClientService = new CmsDocReferralClientService(
        (CmsDocReferralClientDao) cmsDocDao, (CmsDocumentDao) docDao);

    DocumentIndexerJob job = new DocumentIndexerJob(documentMetadataDao, elasticSearchDao,
        cmsDocReferralClientService, args[1]);
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
      List<DocumentMetadata> results =
          documentMetadataDao.findByLastJobRunTimeMinusOneMinute(lastSuccessfulRunTime);
      LOGGER.info(MessageFormat.format("Found {0} documents to index", results.size()));
      Date currentTime = new Date();

      elasticsearchDao.start();
      for (DocumentMetadata documentMetadata : results) {
        indexDocument(documentMetadata);
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

  private void indexDocument(DocumentMetadata documentMetadata) throws Exception {
    String document = "";
    CmsDocReferralClient response = cmsDocReferralClientService.find(documentMetadata.getHandle());

    if (response != null) {
      document = MAPPER.writeValueAsString(response);
    }
    elasticsearchDao.index(document, documentMetadata.getHandle());
  }
}

