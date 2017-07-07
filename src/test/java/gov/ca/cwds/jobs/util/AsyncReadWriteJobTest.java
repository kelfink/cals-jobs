package gov.ca.cwds.jobs.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.jobs.exception.JobsException;

public class AsyncReadWriteJobTest {

  public static class Input implements PersistentObject {
    private Integer id;

    public Input(Integer id) {
      this.id = id;
    }

    @Override
    public Integer getPrimaryKey() {
      return id;
    }
  }

  @Mock
  JobReader<Object> reader;

  @Mock
  JobProcessor<Object, Object> processor;

  @Mock
  JobWriter<Object> writer;

  private List<Integer> input = new ArrayList<>();
  private List<String> output = new ArrayList<>();

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    reader = mock(JobReader.class);
    processor = mock(JobProcessor.class);
    writer = mock(JobWriter.class);

    input.add(0);
    input.add(1);
    input.add(2);
  }

  @After
  public void after() {
    input.clear();
    output.clear();
  }

  @Test
  public void genericTest() {
    AsyncReadWriteJob job = new AsyncReadWriteJob(() -> {
      if (!input.isEmpty()) {
        return input.remove(0);
      }
      return null;
    } , String::valueOf, output::addAll);
    job.run();

    Assert.assertEquals(3, output.size());
    for (int i = 0; i < output.size(); i++) {
      Assert.assertEquals(String.valueOf(i), output.get(i));
    }
  }

  @Test
  public void testReaderException() {
    AsyncReadWriteJob job = new AsyncReadWriteJob(() -> {
      if (input.size() == 3) {
        return input.remove(0);
      } else {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        throw new RuntimeException("failed on second!");
      }
    } , String::valueOf, output::addAll);

    job.run();
    Assert.assertEquals(1, output.size());
    for (int i = 0; i < output.size(); i++) {
      Assert.assertEquals(String.valueOf(i), output.get(i));
    }
  }

  @Test
  public void type() throws Exception {
    assertThat(AsyncReadWriteJob.class, notNullValue());
  }

  @Test
  public void instantiate_1() throws Exception {
    AsyncReadWriteJob target = new AsyncReadWriteJob(reader, processor, writer);
    assertThat(target, notNullValue());
  }

  @Test
  public void instantiate_2() throws Exception {
    AsyncReadWriteJob target = new AsyncReadWriteJob(reader, writer);
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
    doThrow(new JobsException("bozhe miy!")).when(reader).init();

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
    doThrow(new JobsException("bozhe miy!")).when(reader).destroy();

    try {
      target.destroy();
      fail("Expected exception was not thrown!");
    } catch (Exception e) {
    }

  }

}
