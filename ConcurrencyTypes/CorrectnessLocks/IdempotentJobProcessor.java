import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class IdempotentJobSystem {

    enum Status {
        IN_PROGRESS,
        SUCCEEDED,
        FAILED
    }

    static class Job {
        final String jobId;
        final Runnable task;

        Job(String jobId, Runnable task) {
            this.jobId = jobId;
            this.task = task;
        }
    }

    static class JobState {
        Status status = Status.IN_PROGRESS;
        final ReentrantLock lock = new ReentrantLock();
    }

    private final ConcurrentHashMap<String, JobState> states = new ConcurrentHashMap<>();
    private final BlockingQueue<Job> queue;
    private final ExecutorService workers;

    public IdempotentJobSystem(int workerCount, int queueCapacity) {
        this.queue = new ArrayBlockingQueue<>(queueCapacity);
        this.workers = Executors.newFixedThreadPool(workerCount);
        startWorkers(workerCount);
    }

    // Producer API
    public boolean submit(String jobId, Runnable task) {
        return queue.offer(new Job(jobId, task)J); // backpressure via bounded queue
    }

    private void startWorkers(int workerCount) {
        Runnable task = () -> {
               try {
                while (true) {
                    Job job = queue.take(); // blocks
                    process(job);
                }
               } catch (InterruptedException e){
                Thread.currentThread.interrupt();
               }
            }
        }
        for (int i = 0; i < workerCount; i++) {
            workers.submit(task);
        }
    }

    private void process(Job job) {
        JobState state = states.computeIfAbsent(job.jobId, k -> new JobState());

        if (!state.lock.tryLock()) {
            return; // already running
        }

        try {
            if (state.status == Status.SUCCEEDED) {
                return; // idempotent no-op
            }

            state.status = Status.IN_PROGRESS;
            job.task.run();
            state.status = Status.SUCCEEDED;

        } catch (Exception e) {
            state.status = Status.FAILED;
            // Optional: re-enqueue for retry
            // queue.offer(job);
        } finally {
            state.lock.unlock();
        }
    }

    public void shutdown() {
        workers.shutdown();
    }
}


// Super short, simple rule 👇

// Use put() when the producer can wait
// → internal pipelines, batch jobs
// → “slow down until there’s space”

// Use offer() when the producer must not wait
// → APIs, user requests
// → “fail fast and let caller retry”

// One-liner to remember:

// put() = block for backpressure---hard backpresuure
// offer() = fail fast for backpressure