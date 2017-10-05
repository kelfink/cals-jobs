package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import gov.ca.cwds.data.persistence.cms.EsPersonReferral;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonReferrals;

public class ReferralJobRangesTest extends PersonJobTester {

  ReferralJobRanges target = new ReferralJobRanges();

  public void checkPartitionRanges(String schema, boolean isZOS, int expectedCnt) throws Exception {
    System.setProperty("DB_CMS_SCHEMA", schema);
    BasePersonIndexerJob<ReplicatedPersonReferrals, EsPersonReferral> job =
        mock(BasePersonIndexerJob.class);

    when(job.isDB2OnZOS()).thenReturn(isZOS);
    when(job.getOpts()).thenReturn(opts);

    final List<Pair<String, String>> actual = target.getPartitionRanges(job);
    final int cntActual = actual.size();
    assertThat(cntActual, is(equalTo(expectedCnt)));

    // NOTE: consider checking key order and range for ASCII and EBCDIC.
    final Pair<String, String> p = actual.get(0);
  }

  @Test
  public void type() throws Exception {
    assertThat(ReferralJobRanges.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void getPartitionRanges_RSQ() throws Exception {
    when(opts.isRangeGiven()).thenReturn(true);
    when(opts.getStartBucket()).thenReturn(1L);
    when(opts.getEndBucket()).thenReturn(4L);
    checkPartitionRanges("CWSRSQ", true, 3562);
  }

  @Test
  public void getPartitionRanges_REP() throws Exception {
    when(opts.isRangeGiven()).thenReturn(true);
    when(opts.getStartBucket()).thenReturn(1L);
    when(opts.getEndBucket()).thenReturn(4L);
    checkPartitionRanges("CWSREP", true, 3562);
  }

  @Test
  public void getPartitionRanges_RS1() throws Exception {
    checkPartitionRanges("CWSRS1", true, 1);
  }

  @Test
  public void getPartitionRanges_RS1_Linux() throws Exception {
    checkPartitionRanges("CWSRS1", false, 1);
  }

  @Test
  public void getPartitionRanges_Args__BasePersonIndexerJob() throws Exception {
    BasePersonIndexerJob<ReplicatedPersonReferrals, EsPersonReferral> job =
        mock(BasePersonIndexerJob.class);

    when(job.getOpts()).thenReturn(opts);
    when(opts.isRangeGiven()).thenReturn(false);
    when(opts.getStartBucket()).thenReturn(1L);
    when(opts.getEndBucket()).thenReturn(1L);

    final List actual = target.getPartitionRanges(job);
    final List expected = new ArrayList<>();
    expected.add(Pair.of("0000000000", "ZZZZZZZZZZ"));
    assertThat(actual, is(equalTo(expected)));
  }

}
