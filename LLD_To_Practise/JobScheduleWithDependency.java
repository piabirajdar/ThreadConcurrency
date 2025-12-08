import java.util.*;
import java.util.concurrent.*;

create blockingQueue obj(for thread safe), indegree map, graph map, status map, worker thread pool.
1. addJob -> create indegree map and another map with <jobid, [dependent jobs]>
2. start -> 
    a. enqueue all jobs with indegree 0 and update status to "ready"
    b. start worker threads that:
        i. dequeue job from ready queue
        ii. run the job (simulate with sleep) ------------write normal run where update status before("running") and after(done) 
            and catch("failed")
        iii. for each dependent job, decrement indegree; if indegree is 0, enqueue it to queue.



public class JobScheduler {

    private final Map<String, List<String>> graph = new HashMap<>();
    private final Map<String, Integer> indegree = new HashMap<>();
    private final Map<String, String> status = new ConcurrentHashMap<>();

    private final ExecutorService workers = Executors.newFixedThreadPool(4);
    private final BlockingQueue<String> readyQueue = new LinkedBlockingQueue<>();

    // Add job + dependencies
    public void addJob(String job, List<String> deps) {
        indegree.put(job, deps.size());
        status.put(job, "pending");

        for (String d : deps) {
            graph.computeIfAbsent(d, k -> new ArrayList<>()).add(job);
        }
    }

    // Start scheduling
    public void start() {
        // enqueue all jobs with indegree 0
        for (String job : indegree.keySet()) {
            if (indegree.get(job) == 0) {
                readyQueue.offer(job);
            }
        }

       // Define worker task OUTSIDE the loop
        Runnable task = () -> {
            while (true) {
                try {
                    String job = readyQueue.take();
                    runJob(job);

                    // release dependents
                    for (String dep : graph.getOrDefault(job, List.of())) {
                        int updated = indegree.computeIfPresent(dep, (k, v) -> v - 1);
                        if (updated == 0) {
                            readyQueue.offer(dep);
                        }
                    }

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

    // simulate running the job (quiet, no prints)
    private void runJob(String job) {
        try {
            status.put(job, "running");
            Thread.sleep(1000);
            status.put(job, "done");
        } catch (Exception e) {
            status.put(job, "failed");
        }
    }

    public String getStatus(String job) {
        return status.getOrDefault(job, "unknown");
    }

    public void shutdown() {
        workers.shutdownNow();
    }

    public static void main(String[] args) throws Exception {
        JobScheduler s = new JobScheduler();

        // A has no deps
        // B depends on A
        // C depends on A
        // D depends on B and C
        s.addJob("A", List.of());
        s.addJob("B", List.of("A"));
        s.addJob("C", List.of("A"));
        s.addJob("D", List.of("B", "C"));

        s.start();

        Thread.sleep(6000);
        s.shutdown();
    }
}
