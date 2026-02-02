import java.util.concurrent.*;

public class LogProcessor {

    private static final int QUEUE_CAPACITY = 100;
    private static final int WORKERS = 3;

    private final BlockingQueue<String> queue =
            new LinkedBlockingQueue<>(QUEUE_CAPACITY);

    private final ExecutorService workers =
            Executors.newFixedThreadPool(WORKERS);

    // ----- Producer -----
    public void produce(String[] logs) throws InterruptedException {
        for (String log : logs) {
            queue.put(log); // blocks if queue is full (backpressure)
        }
    }

    // ----- Consumer -----
    private void startConsumers() {
        for (int i = 0; i < WORKERS; i++) {
            workers.submit(() -> {
                try {
                    while (true) {
                        String log = queue.take(); // blocks if empty
                        process(log);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }

    private void process(String log) {
        System.out.println("Processed: " + log);
    }

    // ----- Driver -----
    public void start() throws InterruptedException {
        startConsumers();

        // Simulate producers
        produce(new String[]{"A1", "A2", "A3"});
        produce(new String[]{"B1", "B2"});

        // Shutdown
        workers.shutdownNow();
        workers.awaitTermination(1, TimeUnit.MINUTES);
    }

    public static void main(String[] args) throws InterruptedException {
        new LogProcessor().start();
    }
}
