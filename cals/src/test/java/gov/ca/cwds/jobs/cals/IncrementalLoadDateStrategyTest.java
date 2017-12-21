package gov.ca.cwds.jobs.cals;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import gov.ca.cwds.jobs.cals.facility.FacilityIncrementalLoadDateStrategy;
import gov.ca.cwds.jobs.cals.facility.LISFacilityIncrementalLoadDateStrategy;
import gov.ca.cwds.jobs.cals.rfa.RFA1aFormIncrementalLoadDateStrategy;
import gov.ca.cwds.jobs.config.JobOptions;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

/**
 * @author CWDS TPT-2
 */
public class IncrementalLoadDateStrategyTest {

  private static final String TIME_FILES_DIR = "./";

  private static final RFA1aFormIncrementalLoadDateStrategy RFA1A_FORM_LOAD_DATE_STRATEGY = new RFA1aFormIncrementalLoadDateStrategy();

  private static final FacilityIncrementalLoadDateStrategy FACILITY_LOAD_DATE_STRATEGY = new FacilityIncrementalLoadDateStrategy();

  private static final LISFacilityIncrementalLoadDateStrategy LIS_FACILITY_LOAD_DATE_STRATEGY = new LISFacilityIncrementalLoadDateStrategy();

  private static void cleanUp() throws IOException {
    RFA1A_FORM_LOAD_DATE_STRATEGY.reset(TIME_FILES_DIR);
    FACILITY_LOAD_DATE_STRATEGY.reset(TIME_FILES_DIR);
    LIS_FACILITY_LOAD_DATE_STRATEGY.reset(TIME_FILES_DIR);
  }

  @BeforeClass
  public static void beforeClass() throws IOException {
    JobOptions jobOptions = BaseCalsIndexerJob
        .buildJobOptions(BaseCalsIndexerJob.class, new String[]{
            "-c", "config/config.yaml", "-l", TIME_FILES_DIR
        });
    Whitebox.setInternalState(RFA1A_FORM_LOAD_DATE_STRATEGY, "jobOptions", jobOptions);
    Whitebox.setInternalState(FACILITY_LOAD_DATE_STRATEGY, "jobOptions", jobOptions);
    Whitebox.setInternalState(LIS_FACILITY_LOAD_DATE_STRATEGY, "jobOptions", jobOptions);
    cleanUp();
  }

  @AfterClass
  public static void afterClass() throws IOException {
    cleanUp();
  }

  @Test
  public void testRFA1aFormIncrementalLoadDateStrategy() {
    LocalDateTime calculatedTime0 = toLocalDateTime(RFA1A_FORM_LOAD_DATE_STRATEGY.calculateDate());
    assertBefore(calculatedTime0, LocalDateTime.now().minusYears(99));

    LocalDateTime calculatedTime1 = toLocalDateTime(RFA1A_FORM_LOAD_DATE_STRATEGY.calculateDate());
    LocalDateTime now = LocalDateTime.now();
    assertBetween(calculatedTime1, now.minusMinutes(1), now);

    LocalDateTime calculatedTime2 = toLocalDateTime(RFA1A_FORM_LOAD_DATE_STRATEGY.calculateDate());
    assertAfter(calculatedTime2, calculatedTime1);
  }

  @Test
  public void testFacilityIncrementalLoadDateStrategy() {
    Date calculatedDate0 = FACILITY_LOAD_DATE_STRATEGY.calculateDate();
    assertThat(calculatedDate0, is(nullValue()));

    LocalDateTime calculatedTime1 = toLocalDateTime(FACILITY_LOAD_DATE_STRATEGY.calculateDate());
    LocalDateTime now = LocalDateTime.now();
    assertBetween(calculatedTime1, now.minusMinutes(1), now);

    LocalDateTime calculatedTime2 = toLocalDateTime(FACILITY_LOAD_DATE_STRATEGY.calculateDate());
    assertAfter(calculatedTime2, calculatedTime1);
  }

  @Test
  public void testLISFacilityIncrementalLoadDateStrategy() {
    LocalDate now = LocalDate.now();

    LocalDate calculatedDate0 = toLocalDate(LIS_FACILITY_LOAD_DATE_STRATEGY.calculateDate());
    assertBefore(calculatedDate0, now.minusYears(99));

    LocalDate calculatedDate1 = toLocalDate(LIS_FACILITY_LOAD_DATE_STRATEGY.calculateDate());
    assertBetween(calculatedDate1, now.minusDays(2), now);

    LocalDate calculatedDate2 = toLocalDate(LIS_FACILITY_LOAD_DATE_STRATEGY.calculateDate());
    assertThat(calculatedDate2, is(equalTo(calculatedDate1)));
  }

  private static LocalDateTime toLocalDateTime(final Date date) {
    return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
  }

  private static LocalDate toLocalDate(final Date date) {
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
  }

  /**
   * asserts if time tAfter is after time tBefore
   *
   * @param tAfter some moment in time
   * @param tBefore some moment in time
   */
  private static void assertAfter(LocalDateTime tAfter, LocalDateTime tBefore) {
    assertThat(tAfter.compareTo(tBefore), is(equalTo(1)));
  }

  /**
   * asserts if time tBefore is before time tAfter
   *
   * @param tBefore some moment in time
   * @param tAfter some moment in time
   */
  private static void assertBefore(LocalDateTime tBefore, LocalDateTime tAfter) {
    assertThat(tBefore.compareTo(tAfter), is(equalTo(-1)));
  }

  /**
   * asserts if time tBetween is between time tBefore and time tAfter
   *
   * @param tBetween some moment in time
   * @param tBefore some moment in time
   * @param tAfter some moment in time
   */
  private static void assertBetween(LocalDateTime tBetween, LocalDateTime tBefore,
      LocalDateTime tAfter) {
    assertBefore(tBefore, tBetween);
    assertAfter(tAfter, tBetween);
  }

  /**
   * asserts if day dAfter is after day dBefore
   *
   * @param dAfter some day
   * @param dBefore some day
   */
  private static void assertAfter(LocalDate dAfter, LocalDate dBefore) {
    assertTrue(dAfter.compareTo(dBefore) > 0);
  }

  /**
   * asserts if day dBefore is before day dAfter
   *
   * @param dBefore some day
   * @param dAfter some day
   */
  private static void assertBefore(LocalDate dBefore, LocalDate dAfter) {
    assertTrue(dBefore.compareTo(dAfter) < 0);
  }

  /**
   * asserts if day dBetween is between day dBefore and day dAfter
   *
   * @param dBetween some day
   * @param dBefore some day
   * @param dAfter some day
   */
  private static void assertBetween(LocalDate dBetween, LocalDate dBefore, LocalDate dAfter) {
    assertBefore(dBefore, dBetween);
    assertAfter(dAfter, dBetween);
  }

}
