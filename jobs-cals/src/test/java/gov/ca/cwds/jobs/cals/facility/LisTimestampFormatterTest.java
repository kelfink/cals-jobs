package gov.ca.cwds.jobs.cals.facility;

import static gov.ca.cwds.jobs.cals.facility.lis.LisRecordChange.lisTimestampFormatter;
import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import org.junit.Test;

/**
 * Created by Alexander Serbin on 2/27/2018.
 */
public class LisTimestampFormatterTest {

  @Test
  public void formatTimestampTest() {
    String timestamp = "20180101010000";
    LocalDateTime localDateTime = LocalDateTime.of(2018, 01, 01, 01, 00, 00);
    assertEquals(timestamp,
        lisTimestampFormatter.format(localDateTime));
    assertEquals(localDateTime, LocalDateTime.parse(timestamp, lisTimestampFormatter));
  }


}