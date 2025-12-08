import java.util.*;
import java.util.concurrent.*;

public class Job {
    String id;
    int duration;

    Job(String id, int duration){
        this.id = id;
        this.duration = duration;
    }
}

public class JobProcessor {

    // Queue of Job objects
    private final BlockingQueue<Job> queue =
            new LinkedBlockingQueue<>();

    // Worker pool
    private final ExecutorService executor =
            Executors.newFixedThreadPool(3);

    // Submit a job to the queue
    public void submitJobToQueue(String id, int durationMs) {
        Job job = new Job(id, durationMs);
        queue.offer(job);
    }

    // Start worker threads
    public void start() {
        Runnable worker = () -> {
            while (true) {
                try {
                    Job job = queue.take();  // waits for next job
                    process(job);
                } catch (InterruptedException e) {
                    System.out.println("Worker shutting down...");
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        };

        // start 3 workers
        for (int i = 0; i < 3; i++) {
            executor.submit(worker);
        }
    }

    // Actual job processing
    private void process(Job job) {
        try {
            Thread.sleep(job.duration);   // simulate work
        } catch (Exception e) {
            System.out.println("Error processing job: " + job.id);
        }
    }

    public void shutdown() {
        executor.shutdownNow();
    }

    public static void main(String[] args) throws Exception {

        JobProcessor processor = new JobProcessor();
        processor.start();

        processor.submitJobToQueue("A", 1000);
        processor.submitJobToQueue("B", 500);
        processor.submitJobToQueue("C", 1200);
        processor.submitJobToQueue("D", 800);

        Thread.sleep(4000);
        processor.shutdown();
    }
}
