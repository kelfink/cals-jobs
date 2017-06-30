package gov.ca.cwds.jobs.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import gov.ca.cwds.jobs.exception.JobsException;

public class AsyncReadWriteJobTest {

  @Mock
  JobReader<Object> reader;

  @Mock
  JobProcessor<Object, Object> processor;

  @Mock
  JobWriter<Object> writer;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    reader = mock(JobReader.class);
    processor = mock(JobProcessor.class);
    writer = mock(JobWriter.class);
  }

  @Test
  public void type() throws Exception {
    assertThat(AsyncReadWriteJob.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    AsyncReadWriteJob target = new AsyncReadWriteJob(reader, processor, writer);
    assertThat(target, notNullValue());
  }

  @Test
  public void setChunkSize_Args__int() throws Exception {
    AsyncReadWriteJob target = new AsyncReadWriteJob(reader, processor, writer);
    int chunkSize = 0;
    target.setChunkSize(chunkSize);
  }

  @Test
  public void produce_Args__() throws Exception {
    AsyncReadWriteJob target = new AsyncReadWriteJob(reader, processor, writer);
    Object actual = target.produce();
    Object expected = null;
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void consume_Args__Object() throws Exception {
    AsyncReadWriteJob target = new AsyncReadWriteJob(reader, processor, writer);
    Object o = null;
    target.consume(o);
  }

  @Test
  public void run_Args__() throws Exception {
    AsyncReadWriteJob target = new AsyncReadWriteJob(reader, processor, writer);
    target.run();
  }

  @Test
  public void init_Args__() throws Exception {
    AsyncReadWriteJob target = new AsyncReadWriteJob(reader, processor, writer);
    target.init();
  }

  @Test
  public void init_Args___T__Exception() throws Exception {
    AsyncReadWriteJob target = new AsyncReadWriteJob(reader, processor, writer);
    doThrow(new JobsException("боже мій!")).when(reader).init();

    try {
      target.init();
      fail("Expected exception was not thrown!");
    } catch (Exception e) {
    }
  }

  @Test
  public void destroy_Args__() throws Exception {
    AsyncReadWriteJob target = new AsyncReadWriteJob(reader, processor, writer);
    target.destroy();
  }

  @Test
  public void destroy_Args___T__Exception() throws Exception {
    AsyncReadWriteJob target = new AsyncReadWriteJob(reader, processor, writer);
    doThrow(new JobsException("ось лайно!")).when(reader).destroy();

    try {
      target.destroy();
      fail("Expected exception was not thrown!");
    } catch (Exception e) {
    }

  }

}
