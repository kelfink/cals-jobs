package gov.ca.cwds.jobs.common.job.impl;

import static org.junit.Assert.assertEquals;

import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.inject.JobTimeReport;
import java.util.Arrays;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by Alexander Serbin on 3/19/2018.
 */
@Ignore
public class JobTimeReportTest {

  @Test
  public void completionPercentTest() throws InterruptedException {
    ChangedEntityIdentifier id = new ChangedEntityIdentifier("1", RecordChangeOperation.I, null);
    List<ChangedEntityIdentifier> list = Arrays.asList(id, id, id);
    JobTimeReport jobTimeReport = new JobTimeReport(/*
        Arrays.asList(new JobBatch(list), new JobBatch(list))*/);
    assertEquals(50, jobTimeReport.getCompletionPercent(0), 0.0000001);
  }

}