package gov.ca.cwds.dao.cms;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
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

  @Test
  public void getMinId_Args__() throws Exception {
    BatchBucket target = new BatchBucket();
    String actual = target.getMinId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setMinId_Args__String() throws Exception {
    BatchBucket target = new BatchBucket();
    String minId = null;
    target.setMinId(minId);
  }

  @Test
  public void getMaxId_Args__() throws Exception {
    BatchBucket target = new BatchBucket();
    String actual = target.getMaxId();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setMaxId_Args__String() throws Exception {
    BatchBucket target = new BatchBucket();
    String maxId = null;
    target.setMaxId(maxId);
  }

  @Test
  public void getBucketCount_Args__() throws Exception {
    BatchBucket target = new BatchBucket();
    int actual = target.getBucketCount();
    int expected = 0;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setBucketCount_Args__int() throws Exception {
    BatchBucket target = new BatchBucket();
    int bucketCount = 0;
    target.setBucketCount(bucketCount);
  }

  @Test
  public void getBucket_Args__() throws Exception {
    BatchBucket target = new BatchBucket();
    int actual = target.getBucket();
    int expected = 0;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setBucket_Args__int() throws Exception {
    BatchBucket target = new BatchBucket();
    int bucket = 0;
    target.setBucket(bucket);
  }

}
