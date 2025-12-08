import java.util.*;
import java.util.concurrent.*;

class Job {
    String id;
    int duration;
    long runAt;   // NEW: timestamp when job should run

    Job(String id, int duration, long delayMs){
        this.id = id;
        this.duration = duration;
        this.runAt = System.currentTimeMillis() + delayMs;
    }
}

public class JobScheduler {

    // Queue for jobs that are ready to run
    private final BlockingQueue<Job> readyToExecuteQueue = new LinkedBlockingQueue<>();

    // Min-heap for scheduled jobs (by runAt)
    private final PriorityQueue<Job> pq =
            new PriorityQueue<>(Comparator.comparingLong(j -> j.runAt));

    // Worker pool: executes jobs
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    private final Object lock = new Object();
    private volatile boolean running = true;

    // Add a job with delay
    public void schedule(String id, int durationMs, long delayMs) {
        Job job = new Job(id, durationMs, delayMs);

        synchronized (lock) {
            p2.offer(job);
            lock.notifyAll();  // wake scheduler thread
        }
    }

    // Start scheduler and workers
    public void start() {
        startWorkerThreads();
        startSchedulerThread();
    }

    private void startWorkerThreads() {
        Runnable worker = () -> {
            while (running) {
                try {
                    Job job = readyToExecuteQueue.take();
                    process(job);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        for (int i = 0; i < 3; i++) executor.submit(worker);
    }

    private void startSchedulerThread() {
        Thread scheduler = new Thread(() -> {
            while (running) {
                Job nextJob;

                synchronized (lock) {
                    // No jobs → wait
                    while (pq.isEmpty()) {
                        try { lock.wait(); } catch (InterruptedException ignored) {}
                    }

                    nextJob = pq.peek();
                    long now = System.currentTimeMillis();
                    long waitTime = nextJob.runAt - now;

                    if (waitTime > 0) {
                        try { lock.wait(waitTime); } catch (InterruptedException ignored) {}
                        continue;
                    }

                    // Time to run → move to worker queue
                    pq.poll();
                }

                readyToExecuteQueue.offer(nextJob); // Worker threads will pick it up
            }
        });

        scheduler.setDaemon(true);
        scheduler.start();
    }

    private void process(Job job) {
        try {
            Thread.sleep(job.duration);   // simulate work
        } catch (Exception ignored) {}
    }

    public void shutdown() {
        running = false;
        executor.shutdownNow();
        synchronized (lock) { lock.notifyAll(); }
    }

    public static void main(String[] args) throws Exception {
        JobScheduler scheduler = new JobScheduler();
        scheduler.start();

        scheduler.schedule("A", 1000, 1000);  // run after 1s
        scheduler.schedule("B", 500, 3000);   // run after 3s
        scheduler.schedule("C", 1200, 2000);  // run after 2s
        scheduler.schedule("D", 800, 500);    // run after 0.5s

        Thread.sleep(6000);
        scheduler.shutdown();
    }
}
