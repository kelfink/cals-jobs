package gov.ca.cwds.neutron.rocket.referral;

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
import gov.ca.cwds.jobs.Goddard;
import gov.ca.cwds.neutron.rocket.BasePersonRocket;

public class ReferralJobRangesTest extends Goddard {

  ReferralJobRanges target = new ReferralJobRanges();

  public void checkPartitionRanges(String schema, boolean isZOS, int expectedCnt, boolean isLarge)
      throws Exception {
    System.setProperty("DB_CMS_SCHEMA", schema);
    BasePersonRocket<ReplicatedPersonReferrals, EsPersonReferral> job =
        mock(BasePersonRocket.class);

    when(job.isDB2OnZOS()).thenReturn(isZOS);
    when(job.isLargeDataSet()).thenReturn(isLarge);
    when(job.getFlightPlan()).thenReturn(flightPlan);

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
    when(flightPlan.isRangeGiven()).thenReturn(true);
    when(flightPlan.getStartBucket()).thenReturn(1L);
    when(flightPlan.getEndBucket()).thenReturn(4L);
    checkPartitionRanges("CWSRSQ", true, 3, true);
  }

  @Test
  public void getPartitionRanges_REP() throws Exception {
    when(flightPlan.isRangeGiven()).thenReturn(true);
    when(flightPlan.getStartBucket()).thenReturn(1L);
    when(flightPlan.getEndBucket()).thenReturn(4L);
    checkPartitionRanges("CWSREP", true, 3, true);
  }

  @Test
  public void getPartitionRanges_RS1() throws Exception {
    checkPartitionRanges("CWSRS1", true, 1, false);
  }

  @Test
  public void getPartitionRanges_RS1_Linux() throws Exception {
    checkPartitionRanges("CWSRS1", false, 1, false);
  }

  @Test
  public void getPartitionRanges__boom() throws Exception {
    when(flightPlan.isRangeGiven()).thenReturn(true);
    when(flightPlan.getStartBucket()).thenReturn(1L);
    when(flightPlan.getEndBucket()).thenReturn(4L);
    checkPartitionRanges("CWSRSQ", true, 3, true);
  }

  @Test
  public void getPartitionRanges_Args__BasePersonIndexerJob() throws Exception {
    BasePersonRocket<ReplicatedPersonReferrals, EsPersonReferral> rocket =
        mock(BasePersonRocket.class);

    when(rocket.getFlightPlan()).thenReturn(flightPlan);
    when(flightPlan.isRangeGiven()).thenReturn(false);
    when(flightPlan.getStartBucket()).thenReturn(1L);
    when(flightPlan.getEndBucket()).thenReturn(1L);

    final List actual = target.getPartitionRanges(rocket);
    final List expected = new ArrayList<>();
    expected.add(Pair.of("0000000000", "ZZZZZZZZZZ"));
    assertThat(actual, is(equalTo(expected)));
  }

}
