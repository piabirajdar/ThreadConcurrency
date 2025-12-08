
// Design and implement a multi-threaded job scheduler that executes jobs only after all of their dependencies have completed

import java.util.*;
import java.util.concurrent.*;

public class JobScheduler {

    // dependency graph: dep -> list of jobs depending on it
    private final Map<String, List<String>> graph = new HashMap<>();

    // indegree: job -> number of unsatisfied dependencies
    private final Map<String, Integer> indegree = new HashMap<>();

    // job status: pending, ready, running, done, failed
    private final Map<String, String> status = new ConcurrentHashMap<>();

    // queue of ready jobs
    private final BlockingQueue<String> readyQueue = new LinkedBlockingQueue<>();

    // worker thread pool
    private final ExecutorService workers = Executors.newFixedThreadPool(4);

    /**
     * Adds a job and its dependency list.
     * Populate indegree, graph, and status.
     */
    public void addJob(String job, List<String> deps) {
        // TODO: initialize indegree
        // TODO: mark job as pending
        // TODO: update graph: each dependency -> job
    }

    /**
     * Start the scheduler:
     * 1. enqueue jobs with indegree 0
     * 2. start worker threads
     */
    public void start() {
        // TODO: enqueue all jobs with indegree 0 and update status → "ready"

        Runnable workerTask = () -> {
            while (true) {
                try {
                    // TODO: dequeue job from readyQueue
                    // TODO: run the job (simulate with sleep)
                    // TODO: update status before and after
                    // TODO: decrement dependencies of children; enqueue those with indegree 0
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        };

        // start workers
        for (int i = 0; i < 4; i++) {
            workers.submit(workerTask);
        }
    }

    /**
     * Simulate executing a job.
     * Should set status to running -> done or failed.
     */
    private void runJob(String job) {
        // TODO
    }

    /**
     * Return status of a job.
     */
    public String getStatus(String job) {
        return status.getOrDefault(job, "unknown");
    }

    /**
     * Shutdown the thread pool.
     */
    public void shutdown() {
        workers.shutdownNow();
    }

    public static void main(String[] args) throws Exception {
        JobScheduler s = new JobScheduler();

        // Example:
        // A has no deps
        // B depends on A
        // C depends on A
        // D depends on B and C

        // TODO: add jobs A, B, C, D with dependencies and start scheduler

        Thread.sleep(5000);
        s.shutdown();
    }
}
