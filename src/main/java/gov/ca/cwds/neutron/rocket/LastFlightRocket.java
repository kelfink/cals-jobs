package gov.ca.cwds.neutron.rocket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import gov.ca.cwds.jobs.component.Rocket;
import gov.ca.cwds.jobs.exception.JobsException;
import gov.ca.cwds.jobs.exception.NeutronException;
import gov.ca.cwds.neutron.atom.AtomRocketControl;
import gov.ca.cwds.neutron.atom.AtomShared;
import gov.ca.cwds.neutron.enums.NeutronDateTimeFormat;
import gov.ca.cwds.neutron.enums.NeutronIntegerDefaults;
import gov.ca.cwds.neutron.flight.FlightLog;
import gov.ca.cwds.neutron.flight.FlightPlan;
import gov.ca.cwds.neutron.jetpack.ConditionalLogger;
import gov.ca.cwds.neutron.jetpack.JetPackLogger;
import gov.ca.cwds.neutron.jetpack.JobLogs;

/**
 * Abstract base class for all Neutron rockets that rely on a last <strong>successful</strong> run
 * time file.
 * 
 * @author CWDS API Team
 */
public abstract class LastFlightRocket implements Rocket, AtomShared, AtomRocketControl {

  private static final long serialVersionUID = 1L;

  private static final ConditionalLogger LOGGER = new JetPackLogger(LastFlightRocket.class);

  /**
   * Command line options for this rocket.
   */
  protected FlightPlan flightPlan;

  private String lastRunTimeFilename;

  /**
   * Construct from last successful run date-time.
   * 
   * @param lastGoodRunTimeFilename location of last run time file
   * @param flightPlan job options
   */
  public LastFlightRocket(String lastGoodRunTimeFilename, final FlightPlan flightPlan) {
    this.lastRunTimeFilename = StringUtils.isBlank(lastGoodRunTimeFilename)
        ? flightPlan.getLastRunLoc() : lastGoodRunTimeFilename;
    this.flightPlan = flightPlan;
  }

  /**
   * HACK for dependency injection issue. (re-?) initialize the job.
   * 
   * @param lastGoodRunTimeFilename last run file location
   * @param flightPlan flight plan
   */
  public void init(String lastGoodRunTimeFilename, final FlightPlan flightPlan) {
    this.lastRunTimeFilename = StringUtils.isBlank(lastGoodRunTimeFilename)
        ? flightPlan.getLastRunLoc() : lastGoodRunTimeFilename;
    this.flightPlan = flightPlan;
  }

  @Override
  public final void run() {
    LOGGER.warn("run: lastRunTimeFilename: {}", lastRunTimeFilename);
    final FlightLog flightLog = getFlightLog();
    flightLog.start();

    try {
      final Date lastRunTime = determineLastSuccessfulRunTime();
      flightLog.setLastChangeSince(lastRunTime);
      writeLastSuccessfulRunTime(launch(lastRunTime));
    } catch (Exception e) {
      fail();
      LOGGER.error("FAIL JOB!", e);
    }

    try {
      finish(); // Close resources, notify listeners, or even close JVM in standalone mode.
    } catch (NeutronException e) {
      throw new JobsException("ABORT LANDING!", e);
    }

    // SLF4J does not yet support conditional invocation.
    if (LOGGER.isWarnEnabled()) {
      LOGGER.warn(flightLog.toString());
    }
  }

  /**
   * If last run time is provide in options then use it, otherwise use provided
   * lastSuccessfulRunTime.
   * 
   * <p>
   * NOTE: make the look-back period configurable.
   * </p>
   * 
   * @param lastSuccessfulRunTime last successful run
   * @param opts command line job options
   * @return appropriate date to detect changes
   */
  protected Date calcLastRunDate(final Date lastSuccessfulRunTime, final FlightPlan opts) {
    Date ret;
    final Date lastSuccessfulRunTimeOverride = opts.getOverrideLastRunTime();

    if (lastSuccessfulRunTimeOverride != null) {
      ret = lastSuccessfulRunTimeOverride;
    } else {
      final Calendar cal = Calendar.getInstance();
      cal.setTime(lastSuccessfulRunTime);
      cal.add(Calendar.MINUTE, NeutronIntegerDefaults.LOOKBACK_MINUTES.getValue());
      ret = cal.getTime();
    }

    return ret;
  }

  /**
   * Calculate last successful run date/time, per
   * {@link LastFlightRocket#calcLastRunDate(Date, FlightPlan)}.
   * 
   * @param lastSuccessfulRunTime last successful run
   * @return appropriate date to detect changes
   */
  public Date calcLastRunDate(final Date lastSuccessfulRunTime) {
    return calcLastRunDate(lastSuccessfulRunTime, getFlightPlan());
  }

  /**
   * Reads the last run file and returns the last run date.
   * 
   * @return last successful run date/time as a Java Date.
   * @throws NeutronException I/O or parse error
   */
  protected Date determineLastSuccessfulRunTime() throws NeutronException {
    Date ret = null;
    try (BufferedReader br = new BufferedReader(new FileReader(lastRunTimeFilename))) { // NOSONAR
      ret = new SimpleDateFormat(NeutronDateTimeFormat.LAST_RUN_DATE_FORMAT.getFormat())
          .parse(br.readLine().trim()); // NOSONAR
    } catch (IOException | ParseException e) {
      fail();
      throw JobLogs.checked(LOGGER, e, "ERROR FINDING LAST RUN TIME: {}", e.getMessage());
    }

    return ret;
  }

  /**
   * Write the time stamp <strong>IF</strong> the job succeeded.
   * 
   * @param datetime date and time to store
   * @throws NeutronException I/O or parse error
   */
  protected void writeLastSuccessfulRunTime(Date datetime) throws NeutronException {
    if (!isFailed()) {
      try (BufferedWriter w = new BufferedWriter(new FileWriter(lastRunTimeFilename))) { // NOSONAR
        w.write(NeutronDateTimeFormat.LAST_RUN_DATE_FORMAT.formatter().format(datetime));
      } catch (IOException e) {
        fail();
        throw JobLogs.checked(LOGGER, e, "ERROR WRITING TIMESTAMP FILE: {}", e.getMessage());
      }
    }
  }

  /**
   * Launch the rocket. Child classes must provide an implementation.
   * 
   * @param lastSuccessfulRunTime The last successful run
   * @return The time of the latest run if successful.
   * @throws NeutronException if job fails
   */
  public abstract Date launch(Date lastSuccessfulRunTime) throws NeutronException;

  /**
   * Marks the rocket as completed. Close resources, notify listeners, or even close JVM.
   * 
   * @throws NeutronException rocket landing failure
   */
  protected abstract void finish() throws NeutronException;

  /**
   * Getter for last run time.
   * 
   * @return last time the job ran successfully, in format
   *         {@link NeutronDateTimeFormat#LAST_RUN_DATE_FORMAT}
   */
  public String getLastJobRunTimeFilename() {
    return lastRunTimeFilename;
  }

  /**
   * Getter for this rocket's flight plan.
   * 
   * @return this rocket's flight plan
   */
  @Override
  public FlightPlan getFlightPlan() {
    return flightPlan;
  }

  /**
   * Setter for this rocket's flight plan.
   * 
   * @param flightPlan this rocket's flight plan
   */
  public void setFlightPlan(FlightPlan flightPlan) {
    this.flightPlan = flightPlan;
  }

  @Override
  public Logger getLogger() {
    return LOGGER;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void done() {
    this.getFlightLog().done();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void fail() {
    this.getFlightLog().fail();
    this.getFlightLog().done();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doneRetrieve() {
    getFlightLog().doneRetrieve();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doneTransform() {
    getFlightLog().doneTransform();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doneIndex() {
    getFlightLog().doneIndex();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isRunning() {
    return getFlightLog().isRunning();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isFailed() {
    return getFlightLog().isFailed();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isRetrieveDone() {
    return getFlightLog().isRetrieveDone();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isTransformDone() {
    return getFlightLog().isTransformDone();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isIndexDone() {
    return getFlightLog().isIndexDone();
  }

  public String getLastRunTimeFilename() {
    return lastRunTimeFilename;
  }

  public void setLastRunTimeFilename(String lastRunTimeFilename) {
    this.lastRunTimeFilename = lastRunTimeFilename;
  }

}
