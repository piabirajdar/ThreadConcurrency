Caller thread
    |
    | schedule(task)
    v
PriorityBlockingQueue  <--- scheduler thread polls from here
    |
schedulerThread ---> scheduleTasks() runs continuously
    |
workerPool.submit(task.command) ---> worker threads execute Runnable


import java.util.concurrent.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        MyScheduledExecutorService scheduler = new MyScheduledExecutorService(3);

        scheduler.schedule(() -> System.out.println("One-shot task at " + System.currentTimeMillis()), 2, TimeUnit.SECONDS);

        scheduler.scheduleAtFixedRate(() -> System.out.println("Fixed-rate task at " + System.currentTimeMillis()), 1, 3, TimeUnit.SECONDS);

        scheduler.scheduleWithFixedDelay(() -> System.out.println("Fixed-delay task at " + System.currentTimeMillis()), 1, 4, TimeUnit.SECONDS);

        Thread.sleep(15000);
        scheduler.shutdown();
        System.out.println("Scheduler shutdown");
    }
}

This is a single background thread whose job is to monitor the queue of scheduled tasks.
It decides when a task is ready to execute based on its nextRunTime.
It does not run your task directly (well, in our simple first version we did, but in the improved version we delegate to a pool).
This is a pool of threads that actually executes the tasks (Runnable).

public class MyScheduledExecutorService {

    private final PriorityBlockingQueue<ScheduledTask> queue = new PriorityBlockingQueue<>();
    private final ExecutorService workerPool;
    private final Thread schedulerThread;
    private volatile boolean shutdown = false;

    public MyScheduledExecutorService(int poolSize) {
        workerPool = Executors.newFixedThreadPool(poolSize);

        schedulerThread = new Thread(this::scheduleTasks);
        schedulerThread.setDaemon(true);
        schedulerThread.start();
    }

    private void scheduleTasks() {
        try {
            while (!shutdown) {
                ScheduledTask task = queue.take();
                long now = System.currentTimeMillis();
                if (task.nextRunTime > now) {
                    queue.put(task);
                    Thread.sleep(task.nextRunTime - now);
                    continue;
                }

                // Submit to worker pool instead of creating a new thread
                workerPool.submit(task.command);

                // Reschedule if periodic
                if (task.period > 0) {
                    if (task.isFixedRate) {
                        task.nextRunTime += task.period;
                    } else {
                        task.nextRunTime = System.currentTimeMillis() + task.period;
                    }
                    queue.put(task);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void shutdown() {
        shutdown = true;
        schedulerThread.interrupt();
        workerPool.shutdown();
    }

    // One-shot schedule
    public void schedule(Runnable command, long delay, TimeUnit unit) {
        long time = System.currentTimeMillis() + unit.toMillis(delay);
        queue.put(new ScheduledTask(command, time));
    }

    // Fixed-rate schedule
    public void scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        long time = System.currentTimeMillis() + unit.toMillis(initialDelay);
        queue.put(new ScheduledTask(command, time, unit.toMillis(period), true));
    }

    // Fixed-delay schedule
    public void scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        long time = System.currentTimeMillis() + unit.toMillis(initialDelay);
        queue.put(new ScheduledTask(command, time, unit.toMillis(delay), false));
    }

    private static class ScheduledTask implements Comparable<ScheduledTask> {
        final Runnable command;
        long nextRunTime;
        long period;       // >0 for periodic tasks
        boolean isFixedRate;

        ScheduledTask(Runnable command, long nextRunTime) {
            this.command = command;
            this.nextRunTime = nextRunTime;
        }

        ScheduledTask(Runnable command, long nextRunTime, long period, boolean isFixedRate) {
            this.command = command;
            this.nextRunTime = nextRunTime;
            this.period = period;
            this.isFixedRate = isFixedRate;
        }

        @Override
        public int compareTo(ScheduledTask o) {
            return Long.compare(this.nextRunTime, o.nextRunTime);
        }
    }
}
