package gov.ca.cwds.jobs;

import java.io.File;
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
   * @param line to process
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
   * 
   * <p>
   * <strong>Files must sorted prior to processing!</strong> To maintain sort order, this method
   * runs in a single thread.
   * </p>
   * 
   * @param fileName String to extract from
   */
  protected void runExtract(final String fileName) {
    final int i = nextThreadNum.getAndIncrement();
    final String cleanFileName =
        fileName.substring(fileName.lastIndexOf(File.separatorChar) + 1).replace(' ', '_');
    final String threadName = "extract_" + i + "_" + cleanFileName;
    Thread.currentThread().setName(threadName);
    LOGGER.warn("BEGIN: flat file extract: {}", threadName);

    final Path pathIn = Paths.get(fileName);
    try (Stream<String> lines = Files.lines(pathIn)) {
      lines.sequential().forEach(this::handOff);
    } catch (Exception e) {
      fatalError = true;
      JobLogUtils.raiseError(LOGGER, e, "BATCH ERROR! {}", e.getMessage());
    }

    LOGGER.warn("DONE: flat file extract " + i);
  }

  @Override
  protected void threadExtractJdbc() {
    Thread.currentThread().setName("extract");
    LOGGER.warn("BEGIN: flat file extract");

    List<String> filenames = new ArrayList<>();
    for (String s : this.altInputFilenames) {
      filenames.add(s);
    }

    try {
      filenames.parallelStream().forEach(this::runExtract);
    } catch (Exception e) { // NOSONAR
      LOGGER.warn("ERROR!: {}", e.getMessage(), e);
      fatalError = true;
      Thread.currentThread().interrupt();
      throw e;
    } finally {
      doneExtract = true;
    }

    LOGGER.warn("DONE: flat file Extract");
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
