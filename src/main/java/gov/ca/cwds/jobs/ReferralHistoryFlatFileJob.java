package gov.ca.cwds.jobs;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
import gov.ca.cwds.jobs.exception.JobsException;
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

  private String[] altInputFilenames;

  private AtomicInteger nextThreadNum = new AtomicInteger(0);

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
    this.altInputFilenames = altInputFilename.split(",");
  }

  /**
   * Lambda convenience method.
   * 
   * @param r referral to hand to next queue
   */
  protected void handOff(String line) {
    try {
      if (fatalError) {
        throw new JobsException("Previous failure in other thread!");
      }

      final EsPersonReferral r = EsPersonReferral.parseLine(line);
      if (r != null) {
        queueTransform.putLast(r);
      }
    } catch (Exception e) {
      fatalError = true;
      JobLogUtils.raiseError(LOGGER, e, "Hand off failed! {}", e.getMessage());
    }
  }

  /**
   * Launch an extract thread for a single file. Maintain file order by client id and referral id.
   */
  protected void runExtractThread(String s) {
    final int i = nextThreadNum.getAndIncrement();
    Thread.currentThread().setName("extract_" + i);
    LOGGER.info("BEGIN: flat file extract " + i);

    final Path pathIn = Paths.get(s);
    try (Stream<String> lines = Files.lines(pathIn)) {
      lines.sequential().forEach(this::handOff);
    } catch (Exception e) {
      fatalError = true;
      JobLogUtils.raiseError(LOGGER, e, "BATCH ERROR! {}", e.getMessage());
    }

    LOGGER.info("DONE: flat file extract " + i);
  }

  private Thread startExtractThread(final String s) {
    Thread t = new Thread(() -> { // NOSONAR
      runExtractThread(s);
    });
    t.start();
    return t;
  }

  private void waitOnThread(Thread t) { // NOSONAR
    try {
      t.join();
    } catch (InterruptedException ie) { // NOSONAR
      LOGGER.warn("interrupted: {}", ie.getMessage(), ie);
      fatalError = true;
      Thread.currentThread().interrupt();
    }
  }

  @Override
  protected void threadExtractJdbc() {
    Thread.currentThread().setName("extract");
    LOGGER.info("BEGIN: flat file extract");

    List<String> filenames = new ArrayList<>();
    for (String s : this.altInputFilenames) {
      filenames.add(s);
    }

    try {
      filenames.stream().sequential().forEach(this::startExtractThread);
      // filenames.stream().sequential().map(this::runExtractThread).forEach(this::waitOnThread);
    } catch (Exception e) { // NOSONAR
      LOGGER.warn("ERROR!: {}", e.getMessage(), e);
      fatalError = true;
      Thread.currentThread().interrupt();
      throw e;
    } finally {
      doneExtract = true;
    }

    LOGGER.info("DONE: flat file Extract");
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
