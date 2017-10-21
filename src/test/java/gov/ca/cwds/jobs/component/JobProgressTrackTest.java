package gov.ca.cwds.jobs.component;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

public class JobProgressTrackTest {
  JobProgressTrack target = new JobProgressTrack();

  @Before
  public void setup() throws Exception {
    target = new JobProgressTrack();
  }

  @Test
  public void type() throws Exception {
    assertThat(JobProgressTrack.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void getRecsSentToIndexQueue_Args__() throws Exception {
    AtomicInteger actual = target.getRecsSentToIndexQueue();
    AtomicInteger expected = new AtomicInteger(0);
    assertThat(actual.get(), is(equalTo(expected.get())));
  }

  @Test
  public void getRecsSentToBulkProcessor_Args__() throws Exception {
    AtomicInteger actual = target.getRecsSentToBulkProcessor();
    AtomicInteger expected = new AtomicInteger(0);
    assertThat(actual.get(), is(equalTo(expected.get())));
  }

  @Test
  public void getRecsBulkPrepared_Args__() throws Exception {
    AtomicInteger actual = target.getRecsBulkPrepared();
    AtomicInteger expected = new AtomicInteger(0);
    assertThat(actual.get(), is(equalTo(expected.get())));
  }

  @Test
  public void getRecsBulkDeleted_Args__() throws Exception {
    AtomicInteger actual = target.getRecsBulkDeleted();
    AtomicInteger expected = new AtomicInteger(0);
    assertThat(actual.get(), is(equalTo(expected.get())));
  }

  @Test
  public void getRecsBulkBefore_Args__() throws Exception {
    AtomicInteger actual = target.getRecsBulkBefore();
    AtomicInteger expected = new AtomicInteger(0);
    assertThat(actual.get(), is(equalTo(expected.get())));
  }

  @Test
  public void getRecsBulkAfter_Args__() throws Exception {
    AtomicInteger actual = target.getRecsBulkAfter();
    AtomicInteger expected = new AtomicInteger(0);
    assertThat(actual.get(), is(equalTo(expected.get())));
  }

  @Test
  public void getRecsBulkError_Args__() throws Exception {
    AtomicInteger actual = target.getRecsBulkError();
    AtomicInteger expected = new AtomicInteger(0);
    assertThat(actual.get(), is(equalTo(expected.get())));
  }

  @Test
  public void trackQueuedToIndex_Args__() throws Exception {
    int actual = target.trackQueuedToIndex();
    int expected = 1;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void trackNormalized_Args__() throws Exception {
    int actual = target.trackNormalized();
    int expected = 1;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void trackBulkDeleted_Args__() throws Exception {
    int actual = target.trackBulkDeleted();
    int expected = 1;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void trackBulkPrepared_Args__() throws Exception {
    int actual = target.trackBulkPrepared();
    int expected = 1;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void trackBulkError_Args__() throws Exception {
    int actual = target.trackBulkError();
    int expected = 1;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void trackRangeStart_Args__Pair() throws Exception {
    Pair<String, String> pair = Pair.of("1", "2");
    target.trackRangeStart(pair);
  }

  @Test
  public void trackRangeComplete_Args__Pair() throws Exception {
    Pair<String, String> pair = Pair.of("1", "2");
    target.trackRangeComplete(pair);
  }

  @Test
  public void start_Args__() throws Exception {
    target.start();
  }

  @Test
  public void fail_Args__() throws Exception {
    target.fail();
  }

  @Test
  public void done_Args__() throws Exception {
    target.done();
  }

  @Test
  public void hashCode_Args__() throws Exception {
    int actual = target.hashCode();
    assertThat(actual, not(equalTo(0)));
  }

  @Test
  public void equals_Args__Object() throws Exception {
    Object obj = null;
    boolean actual = target.equals(obj);
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getInitialLoadRangesStarted_Args__() throws Exception {
    List<Pair<String, String>> actual = target.getInitialLoadRangesStarted();
    List<Pair<String, String>> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getInitialLoadRangesCompleted_Args__() throws Exception {
    List<Pair<String, String>> actual = target.getInitialLoadRangesCompleted();
    List<Pair<String, String>> expected = new ArrayList<>();
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void toString_Args__() throws Exception {
    String actual = target.toString();
    assertThat(actual, is(notNullValue()));
  }

  @Test
  public void isInitialLoad_Args__() throws Exception {
    boolean actual = target.isInitialLoad();
    boolean expected = false;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setInitialLoad_Args__boolean() throws Exception {
    boolean initialLoad = false;
    target.setInitialLoad(initialLoad);
  }

  @Test
  public void getLastChangeSince_Args__() throws Exception {
    Date actual = target.getLastChangeSince();
    Date expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void setLastChangeSince_Args__Date() throws Exception {
    Date lastChangeSince = mock(Date.class);
    target.setLastChangeSince(lastChangeSince);
  }

}
