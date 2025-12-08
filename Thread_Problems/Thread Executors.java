
THREAD EXECUTORS:

While raw threads and thread pools offer control over concurrent operations, 
Thread Executors provide structured, task-based concurrency with several key advantages:

• Separation of task submission from execution details
ExecutorService executor = Executors.newFixedThreadPool(3);
executor.submit(() -> {
    System.out.println("Task executed by: " + Thread.currentThread().getName());
});
 just submit a task without worrying about creating or starting a thread

 Built-in thread pooling and resource management 🏊‍♂️
 ExecutorService pool = Executors.newFixedThreadPool(5);
for (int i = 0; i < 10; i++) {
    pool.execute(() -> {
        System.out.println("Running: " + Thread.currentThread().getName());
    });
}
 executor reuses the same 5 threads to run 10 tasks, preventing overhead from creating a new thread for each task.


 Task queuing, scheduling, and execution policies ⏱️
 ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
scheduler.schedule(() -> {
    System.out.println("Executed after 3 seconds!");
}, 3, TimeUnit.SECONDS);
    schedule tasks to run after a delay or periodically without manual thread management.


Lifecycle control and graceful shutdown capabilities 🛑
ExecutorService executor = Executors.newCachedThreadPool();
executor.submit(() -> System.out.println("Working..."));
executor.shutdown(); // Initiates an orderly shutdown
 You can shut down the executor gracefully, allowing current tasks to complete without abruptly killing threads.

ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
scheduler.scheduleAtFixedRate(() -> {
    System.out.println("Running every 2 seconds");
}, 0, 2, TimeUnit.SECONDS);
