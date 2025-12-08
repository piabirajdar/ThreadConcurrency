import java.util.concurrent.*;

class ThreadPoolExample {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(3);

        for (int i = 1; i <= 5; i++) {
            int task = i;
            pool.submit(() -> System.out.println("Task " + task + " running on " + Thread.currentThread().getName()));
        }

        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.MINUTES);
    }
}

A thread pool is a collection of pre-created worker threads that can be reused to execute multiple tasks concurrently, instead of creating a new thread every time a task needs to run.
This creates a thread pool with 3 threads.
We are submitting 5 tasks to the pool.
Since the pool has only 3 threads, the first 3 tasks start immediately, and the remaining 2 wait in the task queue.
