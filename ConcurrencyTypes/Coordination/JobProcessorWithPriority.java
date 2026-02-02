import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

// 1. Submit jobs with priority

// 2. Fixed number of workers

// 3. Higher priority runs first

// 4. FIFO for same priority

// 5. Thread-safe
public class JobProcessor {

    static class Job {
        final long seq;
        final int priority;
        final long duration;

        Job(long seq, int priority, long duration ) {
            this.seq = seq;
            this.priority = priority;
            this.duration = duration
        }
    }

    private final AtomicLong seq = new AtomicLong();

    private final PriorityBlockingQueue<Job> queue =
        new PriorityBlockingQueue<>(11, (a, b) -> {
            // Higher priority first
            if (a.priority != b.priority) {
                return Integer.compare(b.priority, a.priority);
            }
            // FIFO for same priority
            return Long.compare(a.seq, b.seq);
        });

    private final ExecutorService workers;

    public JobProcessor(int workerCount) {
        this.workers = Executors.newFixedThreadPool(workerCount);
        startWorkers(workerCount);
    }

    public void submitJob(Runnable task, int priority) {
        queue.offer(new Job(seq.incrementAndGet(), priority, task));
    }

    private void startWorkers(int workerCount) {
       Runnable task = () -> {
            while (true) {
                Job job = queue.take(); // blocks

                try {
                    process(job);
                } catch (Exception e) {
                    System.out.print(e);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        for (int i = 0; i < workerCount; i++) {
            workers.submit(task);
        }
    }

    private void process(Job job) {
        try {
            Thread.sleep(job.duration);   // simulate work
        } catch (Exception e) {
            System.out.println("Error processing job: " + job.id);
        }
    }

    public void shutdown() {
        workers.shutdownNow();
    }

    public static void main(String[] args) throws Exception {

        JobProcessor processor = new JobProcessor();

        processor.submitJob("A", 1000);
        processor.submitJob("B", 500);
        processor.submitJob("C", 1200);
        processor.submitJob("D", 800);

        Thread.sleep(4000);
        processor.shutdown();
    }
}
// ⏱ Complexity

// Enqueue: O(log N)

// Dequeue: O(log N)

// Worker execution: O(1)