package gov.ca.cwds.jobs;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import gov.ca.cwds.dao.cms.ReplicatedPersonReferralsDao;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.cms.EsPersonReferral;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonReferrals;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.inject.AltInputFile;
import gov.ca.cwds.jobs.inject.LastRunFile;
import gov.ca.cwds.jobs.util.JobLogUtils;

/**
 * Job to load person referrals from CMS into ElasticSearch.
 * 
 * @author CWDS API Team
 */
public class ReferralHistoryFlatFileJob extends ReferralHistoryIndexerJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReferralHistoryFlatFileJob.class);

  private String altInputFilename;

  /**
   * Construct batch job instance with all required dependencies.
   * 
   * @param clientDao DAO for {@link ReplicatedPersonReferrals}
   * @param esDao ElasticSearch DAO
   * @param lastJobRunTimeFilename last run date in format yyyy-MM-dd HH:mm:ss
   * @param mapper Jackson ObjectMapper
   * @param sessionFactory Hibernate session factory
   * @param altInputFilename file to parse
   */
  @Inject
  public ReferralHistoryFlatFileJob(ReplicatedPersonReferralsDao clientDao, ElasticsearchDao esDao,
      @LastRunFile String lastJobRunTimeFilename, ObjectMapper mapper,
      @CmsSessionFactory SessionFactory sessionFactory, @AltInputFile String altInputFilename) {
    super(clientDao, esDao, lastJobRunTimeFilename, mapper, sessionFactory);
    this.altInputFilename = altInputFilename;
  }

  /**
   * Lambda convenience method.
   * 
   * @param r referral to hand to next queue
   */
  protected void handOff(final EsPersonReferral r) {
    try {
      if (r != null) {
        queueTransform.putLast(r);
      }
    } catch (Exception e) {
      fatalError = true;
      JobLogUtils.raiseError(LOGGER, e, "Hand off failed! {}", e.getMessage());
    }
  }

  @Override
  protected void threadExtractJdbc() {
    Thread.currentThread().setName("extract");
    LOGGER.info("BEGIN: flat file extract");

    Path pathIn = Paths.get(altInputFilename);
    try (Stream<String> lines = Files.lines(pathIn)) {

      // Maintain file order by client, referral.
      lines.sequential().map(EsPersonReferral::parseLine).forEach(this::handOff);

    } catch (Exception e) {
      fatalError = true;
      JobLogUtils.raiseError(LOGGER, e, "BATCH ERROR! {}", e.getMessage());
    } finally {
      doneExtract = true;
    }

    LOGGER.info("DONE: Stage #1: Extract");
  }

  /**
   * Batch job entry point.
   * 
   * @param args command line arguments
   */
  public static void main(String... args) {
    runMain(ReferralHistoryFlatFileJob.class, args);
  }
}
