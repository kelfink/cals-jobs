package gov.ca.cwds.jobs.util;

import gov.ca.cwds.jobs.Job;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by dmitry.rudenko on 4/28/2017.
 */

@SuppressWarnings("unchecked")
public class AsyncReadWriteJob extends ProducerConsumer implements Job, JobComponent {
    private int chunkSize = 100;
    private ItemReader reader;
    private List chunk = new LinkedList<>();
    private ItemProcessor processor;
    private ItemWriter writer;

    public <I, O> AsyncReadWriteJob(ItemReader<I> reader, ItemProcessor<I, O> processor, ItemWriter<O> writer) {
        this.reader = reader;
        this.processor = processor;
        this.writer = writer;
    }

    /**
     * Input type = Output Type, no mapping required
     */
    public <I> AsyncReadWriteJob(ItemReader<I> reader, ItemWriter<I> writer) {
        this.reader = reader;
        this.processor = item -> item;
        this.writer = writer;
    }

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
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }

    private void flush() throws Exception {
        writer.write(chunk);
        chunk.clear();
    }

    @SuppressWarnings("ThrowFromFinallyBlock")
    @Override
    public void run() {
        try {
            init();
            super.run();
            if (!chunk.isEmpty()) {
                flush();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                destroy();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void init() throws Exception{
        reader.init();
        writer.init();
    }

    public void destroy() throws Exception{
        reader.destroy();
        writer.destroy();
    }
}
