package gov.ca.cwds.jobs.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class ProducerConsumer<T> {
    private boolean producerDone;
    private Thread producer = new Thread(this::producer);
    private Thread consumer = new Thread(this::consumer);
    private BlockingQueue<T> queue = new LinkedBlockingQueue<>(5000);

    protected abstract T produce();

    protected abstract void consume(T var1);

    public void run() throws InterruptedException {
        this.producer.start();
        this.consumer.start();
        this.consumer.join();
    }

    private void consumer() {
        try {
            while (!producerDone) {
                T item = queue.poll(2L, TimeUnit.SECONDS);
                if(item != null) {
                    consume(item);
                }
            }
            while (!queue.isEmpty()) {
                consume(queue.take());
            }

        }catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            producer.interrupt();
        }

    }

    private void producer() {
        try {
            T o;
            try {
                while((o = produce()) != null) {
                    queue.put(o);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } finally {
            producerDone = true;
        }
    }
}
