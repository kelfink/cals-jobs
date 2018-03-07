package gov.ca.cwds.jobs.cals.facility;

import org.junit.Test;

import java.time.LocalDateTime;

import static gov.ca.cwds.jobs.cals.facility.recordchange.LisRecordChange.lisTimestampFormatter;
import static org.junit.Assert.assertEquals;

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