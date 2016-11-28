package gov.ca.cwds.jobs;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import gov.ca.cwds.rest.api.persistence.cms.CmsDocReferralClient;
import gov.ca.cwds.rest.jdbi.CrudsDao;
import gov.ca.cwds.rest.jdbi.cms.CmsDocReferralClientDao;
import gov.ca.cwds.rest.jdbi.cms.CmsDocumentDao;
import gov.ca.cwds.rest.services.cms.CmsDocReferralClientService;

/**
 * Job to load documents from CMS into ElasticSearch
 * 
 * @author CWDS API Team
 */
public class DocumentIndexerJob implements Job {
  private static final Logger LOGGER = LogManager.getLogger(DocumentIndexerJob.class);

  private static ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
  private static ObjectMapper MAPPER = new ObjectMapper();
  private static DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private DocumentMetadataDao documentMetadataDao;
  private ElasticsearchDao elasticsearchDao;
  private CmsDocReferralClientService cmsDocReferralClientService;
  private Date lastJobRunTime;


  /**
   * Constructor
   * 
   * @param documentMetadataDao The documentMetadataDao
   * @param elasticsearchDao The elasticsearchDao
   * @param cmsDocReferralClientService The cmsDocReferralClientService
   * @param lastJobRunTime The last time the job ran
   */
  public DocumentIndexerJob(DocumentMetadataDao documentMetadataDao,
      ElasticsearchDao elasticsearchDao, CmsDocReferralClientService cmsDocReferralClientService,
      Date lastJobRunTime) {
    super();
    this.documentMetadataDao = documentMetadataDao;
    this.elasticsearchDao = elasticsearchDao;
    this.cmsDocReferralClientService = cmsDocReferralClientService;
    this.lastJobRunTime = lastJobRunTime;
  }



  public static void main(String... args) throws Exception {
    if (args.length != 2) {
      throw new Error(
          "Usage: java gov.ca.cwds.jobs.DocumentIndexLoader configFileLocation lastJobRunTime(yyyy-MM-dd HH:mm:ss)");
    }
    Injector injector = Guice.createInjector(new JobsGuiceInjector());
    DocumentMetadataDao documentMetadataDao = injector.getInstance(DocumentMetadataDao.class);

    File file = new File(args[0]);
    ElasticsearchConfiguration configuration =
        YAML_MAPPER.readValue(file, ElasticsearchConfiguration.class);
    ElasticsearchDao elasticSearchDao = new ElasticsearchDao(configuration);

    Date lastJobRunTime = DATEFORMAT.parse(args[1]);

    // classes from the API project are not integrated with GUICE so construct by hand
    SessionFactory sessionFactory = injector.getInstance(SessionFactory.class);
    CrudsDao<CmsDocReferralClient> cmsDocDao = new CmsDocReferralClientDao(sessionFactory);
    CrudsDao<gov.ca.cwds.rest.api.persistence.cms.CmsDocument> docDao =
        new CmsDocumentDao(sessionFactory);
    CmsDocReferralClientService cmsDocReferralClientService = new CmsDocReferralClientService(
        (CmsDocReferralClientDao) cmsDocDao, (CmsDocumentDao) docDao);

    DocumentIndexerJob job = new DocumentIndexerJob(documentMetadataDao, elasticSearchDao,
        cmsDocReferralClientService, lastJobRunTime);
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
      List<DocumentMetadata> results =
          documentMetadataDao.findByLastJobRunTimeMinusOneMinute(lastJobRunTime);

      elasticsearchDao.start();
      for (DocumentMetadata documentMetadata : results) {
        indexDocument(documentMetadata);
      }
    } catch (IOException e) {
      throw new JobsException("Could not parse configuration file", e);
    } catch (Exception e) {
      throw new JobsException(e);
    }

    // TODO - handle this through Rundek?????
    // /** Write the last run timestamp to parm file */
    // DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // Date date = new Date();
    // String tsparm = dateFormat.format(date);
    //
    // try (BufferedWriter writedtparm = new BufferedWriter(new FileWriter("parmfile.txt"))) {
    // writedtparm.write(tsparm);
    // } catch (IOException e) {
    // LOGGER.error("Could not write the timestamp parameter file");
    // throw e;
    // } finally {
    // sessionFactory.close();
    // }
  }

  private void indexDocument(DocumentMetadata documentMetadata) throws Exception {
    String document = "";
    gov.ca.cwds.rest.api.domain.legacy.CmsDocReferralClient response =
        cmsDocReferralClientService.find(documentMetadata.getHandle());

    if (response != null) {
      document = MAPPER.writeValueAsString(response);
    }
    elasticsearchDao.index(document, documentMetadata.getHandle());
  }
}

