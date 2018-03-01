package gov.ca.cwds.jobs.cals.facility;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;

/**
 * Created by Alexander Serbin on 2/27/2018.
 */
public class LisTimestampFormatterTest {

    @Test
    public void formatTimestampTest() {
        DateTimeFormatter lisTimestampFormatter = DateTimeFormatter.ofPattern("YYYYMMddHHmmss");
        LocalDateTime localDateTime = LocalDateTime.of(2018, 01, 01, 01, 00, 00);
        assertEquals("20180101010000",
                ChangedFacilityService.lisTimestampFormatter.format(localDateTime));
    }

}