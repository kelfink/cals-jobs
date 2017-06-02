package gov.ca.cwds.dao.cms;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class BatchBucketTest {

  @Test
  public void type() throws Exception {
    assertThat(BatchBucket.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    BatchBucket target = new BatchBucket();
    assertThat(target, notNullValue());
  }

}
