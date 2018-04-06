package gov.ca.cwds.jobs.cals.facility;

import gov.ca.cwds.jobs.common.job.BulkWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander Serbin on 3/28/2018.
 */
public class TestWriter implements BulkWriter<ChangedFacilityDTO> {

  private static List<ChangedFacilityDTO> items = new ArrayList<>();

  @Override
  public void write(List<ChangedFacilityDTO> items) {
    TestWriter.items.addAll(items);
  }

  public static void reset() {
    TestWriter.items = new ArrayList<>();
  }

  public static List<ChangedFacilityDTO> getItems() {
    return items;
  }

}


