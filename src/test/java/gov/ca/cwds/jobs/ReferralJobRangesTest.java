package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.Test;

import gov.ca.cwds.data.persistence.cms.EsPersonReferral;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonReferrals;

public class ReferralJobRangesTest {

  @Test
  public void type() throws Exception {
    assertThat(ReferralJobRanges.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ReferralJobRanges target = new ReferralJobRanges();
    assertThat(target, notNullValue());
  }

  @Test
  public void getPartitionRanges_Args__BasePersonIndexerJob() throws Exception {
    ReferralJobRanges target = new ReferralJobRanges();
    BasePersonIndexerJob<ReplicatedPersonReferrals, EsPersonReferral> job =
        mock(BasePersonIndexerJob.class);
    List actual = target.getPartitionRanges(job);
    List expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

}
