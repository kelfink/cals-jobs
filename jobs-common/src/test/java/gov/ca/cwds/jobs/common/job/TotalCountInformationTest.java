package gov.ca.cwds.jobs.common.job;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class TotalCountInformationTest {

  public static final int TOTAL_TO_BE_DELETED = 10;
  public static final int TOTAL_TO_BE_INSERTED = 100;
  public static final int TOTAL_TO_BE_UPDATED = 1000;
  @Spy
  @InjectMocks
  private TotalCountInformation totalCountInformation; // "Class Under Test"

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void type() {
    assertThat(TotalCountInformation.class, notNullValue());
  }

  @Test
  public void instantiation() {
    assertThat(totalCountInformation, notNullValue());
  }

  @Test
  public void testTotalCountInformation() {
    totalCountInformation.setTotalToBeDeleted(TOTAL_TO_BE_DELETED);
    Assert.assertEquals(totalCountInformation.getTotalToBeDeleted(), TOTAL_TO_BE_DELETED);

    totalCountInformation.setTotalToBeInserted(TOTAL_TO_BE_INSERTED);
    Assert.assertEquals(totalCountInformation.getTotalToBeInserted(), TOTAL_TO_BE_INSERTED);

    totalCountInformation.setTotalToBeUpdated(TOTAL_TO_BE_UPDATED);
    Assert.assertEquals(totalCountInformation.getTotalToBeUpdated(), TOTAL_TO_BE_UPDATED);

    Assert.assertNotEquals(totalCountInformation.getTotalToBeDeleted(),
        totalCountInformation.getTotalToBeUpdated());
  }
}
