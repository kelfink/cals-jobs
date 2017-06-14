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
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getMinId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setMinId_Args__String() throws Exception {

    BatchBucket target = new BatchBucket();
    // given
    String minId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setMinId(minId);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getMaxId_Args__() throws Exception {

    BatchBucket target = new BatchBucket();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    String actual = target.getMaxId();
    // then
    // e.g. : verify(mocked).called();
    String expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setMaxId_Args__String() throws Exception {

    BatchBucket target = new BatchBucket();
    // given
    String maxId = null;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setMaxId(maxId);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getBucketCount_Args__() throws Exception {

    BatchBucket target = new BatchBucket();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    int actual = target.getBucketCount();
    // then
    // e.g. : verify(mocked).called();
    int expected = 0;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setBucketCount_Args__int() throws Exception {

    BatchBucket target = new BatchBucket();
    // given
    int bucketCount = 0;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setBucketCount(bucketCount);
    // then
    // e.g. : verify(mocked).called();
  }

  @Test
  public void getBucket_Args__() throws Exception {

    BatchBucket target = new BatchBucket();
    // given
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    int actual = target.getBucket();
    // then
    // e.g. : verify(mocked).called();
    int expected = 0;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setBucket_Args__int() throws Exception {

    BatchBucket target = new BatchBucket();
    // given
    int bucket = 0;
    // e.g. : given(mocked.called()).willReturn(1);
    // when
    target.setBucket(bucket);
    // then
    // e.g. : verify(mocked).called();
  }

}
