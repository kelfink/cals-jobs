package gov.ca.cwds.generic.jobs.util;

import gov.ca.cwds.generic.jobs.Job;
import gov.ca.cwds.generic.jobs.exception.JobsException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author CWDS TPT-2
 */
@SuppressWarnings("unchecked")
public class AsyncReadWriteJob extends ProducerConsumer implements Job, JobComponent {

  /**
   * Default serialization.
   */
  private static final long serialVersionUID = 1L;

  private transient JobReader reader;
  private transient JobProcessor processor;
  private transient JobWriter writer;
  private transient List chunk = new LinkedList<>();

  private int chunkSize = 100;

  /**
   * @param reader reader
   * @param processor processor
   * @param writer writer
   * @param <I> output reader type
   * @param <O> output writer type
   */
  public <I, O> AsyncReadWriteJob(JobReader<I> reader, JobProcessor<I, O> processor,
      JobWriter<O> writer) {
    this.reader = reader;
    this.processor = processor;
    this.writer = writer;
  }

  /**
   * Input type = Output Type, no mapping required
   *
   * @param reader reader
   * @param writer writer
   * 
   * @param <I> output reader type
   */
  public <I> AsyncReadWriteJob(JobReader<I> reader, JobWriter<I> writer) {
    this.reader = reader;
    this.processor = item -> item;
    this.writer = writer;
  }

  /**
   * Set the chunk size.
   *
   * @param chunkSize chunk size
   */
  public void setChunkSize(int chunkSize) {
    if (chunkSize > 0) {
      this.chunkSize = chunkSize;
    }
  }

  @Override
  public Object produce() {
    try {
      return reader.read();
    } catch (Exception e) {
      throw new JobsException(e);
    }
  }

  @Override
  public void consume(Object o) {
    try {
      Object out = processor.process(o);
      chunk.add(out);
      if (chunk.size() == chunkSize) {
        flush();
      }
    } catch (Exception e) {
      chunk.clear();
      throw new JobsException("ERROR CONSUMING CHUNK!", e);
    }
  }

  private void flush() throws Exception { // NOSONAR
    writer.write(chunk);
    chunk.clear();
  }

  @Override
  public void run() {
    try {
      init();
      super.run();
      if (!chunk.isEmpty()) {
        flush();
      }
    } catch (Exception e) {
      throw new JobsException(e);
    } finally {
      try {
        destroy();
      } catch (Exception e) {
        throw new JobsException(e); // NOSONAR
      }
    }
  }

  @Override
  public void destroy() {
    writer.destroy();
  }

  @Override
  protected void producerInit() {
    reader.init();
  }

  @Override
  protected void consumerInit() {
    writer.init();
  }

  @Override
  protected void producerDestroy() {
    reader.destroy();
  }

}