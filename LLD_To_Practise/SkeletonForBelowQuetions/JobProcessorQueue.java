// Question: Implement a Multi-Threaded Job Processor Using a Custom Queue

import java.util.concurrent.*;

// Simple job container
class Job {
    int id;
    int duration;

    Job(int id, int duration) {
        this.id = id;
        this.duration = duration;
    }
}

public class Solution {

    private final BlockingQueue<Job> queue =
            new LinkedBlockingQueue<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    // Producer: add job into the queue
    public void submitJobToQueue(int jobId, int durationMs) {
        // TODO: create job and offer to queue
    }

    // Start worker threads
    public void start() {

        Runnable worker = () -> {
            while (true) {
                try {
                    // TODO: take a job from queue
                    // TODO: call process(job)
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        };

        // submit 4 workers
        for (int i = 0; i < 4; i++) {
            executor.submit(worker);
        }
    }

    // Simulate job execution
    private void process(Job job) {
        try {
            // TODO: sleep based on job duration
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void shutdown() {
        executor.shutdownNow();
    }

    public static void main(String[] args) throws Exception {

        Solution s = new Solution();
       
        // Start the job processor and submit jobs

        Thread.sleep(4000);
        s.shutdown();
    }
}
