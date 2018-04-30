package gov.ca.cwds.jobs.cals.facility;

import gov.ca.cwds.jobs.common.RecordChangeOperation;
import org.junit.Assert;
import org.junit.Test;

public class RecordChangeTest {

  @Test
  public void testIdGetSetString() {
    RecordChange recordChange = new RecordChange();
    Assert.assertEquals(null, recordChange.getId());
    recordChange.setId(null);
    Assert.assertEquals(null, recordChange.getId());
    String stringId = "StringId";
    recordChange.setId(stringId);
    Assert.assertEquals(stringId, recordChange.getId());
    Assert.assertEquals(stringId, recordChange.getPrimaryKey());
  }

  @Test
  public void getRecordChangeOperation() {
    RecordChange recordChange = new RecordChange();
    Assert.assertEquals(null, recordChange.getRecordChangeOperation());
    recordChange.setRecordChangeOperation(null);
    Assert.assertEquals(null, recordChange.getRecordChangeOperation());
    recordChange.setRecordChangeOperation(RecordChangeOperation.I);
    Assert.assertEquals(RecordChangeOperation.I, recordChange.getRecordChangeOperation());
  }

}