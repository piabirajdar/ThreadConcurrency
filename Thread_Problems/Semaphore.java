A semaphore is a synchronization primitive that maintains a count of permits. 🧮 Threads can acquire these permits (decreasing the count) or release them (increasing the count). 
Conceptually, a semaphore has two primary operations:

🔹 acquire(): Obtains a permit, blocking if necessary until one becomes available 🛑

🔹 release(): Returns a permit to the semaphore ✅

control permits for number of available resources
Semaphore resourcePool = new Semaphore(5); // 5 permits for 5 resources


Semaphores can synchronize producer and consumer threads by using separate semaphores to track empty and filled slots in a buffe
Semaphore emptySlots = new Semaphore(bufferSize); // Track empty slots
Semaphore filledSlots = new Semaphore(0); // Track filled slots


Diff locks and Semaphores:
 A Lock allows only one thread to access a resource at a time (mutual exclusion), 
 while a Semaphore can allow a specified number of threads to access resources concurrently.
 A Lock is owned by a specific thread that must release it, 
 whereas Semaphore permits can be acquired and released by different thread


 public class SemaphoreVsLockExample { 
    private final Semaphore semaphore = new Semaphore(3); // Allows up to 3 threads concurrently 
    private final Lock lock = new ReentrantLock(); 
    // Using Semaphore 
    public void accessWithSemaphore() { 
        try { 
            semaphore.acquire(); // Acquire a permit; up to 3 threads can acquire concurrently 
            System.out.println(Thread.currentThread().getName() + " accessing resource with Semaphore"); 
            Thread.sleep(1000); // Simulate work 
        } catch (InterruptedException e) { 
            e.printStackTrace(); 
        } finally { 
            System.out.println(Thread.currentThread().getName() + " releasing Semaphore permit"); 
            semaphore.release(); // Release the permit 
        } 
    } 
    // Using Lock 
    public void accessWithLock() { 
        lock.lock(); // Acquire the lock (only one thread can hold it) 
        try { 
            System.out.println(Thread.currentThread().getName() + " accessing resource with Lock"); 
            Thread.sleep(1000); // Simulate work 
        } catch (InterruptedException e) { 
            e.printStackTrace(); 
        } finally { 
            System.out.println(Thread.currentThread().getName() + " unlocking Lock"); 
            lock.unlock(); // Release the lock 
        } 
    } 

    public static void main(String[] args) { 
        SemaphoreVsLockExample example = new SemaphoreVsLockExample(); 
        // Create and start threads with descriptive names 
        for (int i = 1; i <= 5; i++) { 
            Thread semaphoreThread = new Thread(example::accessWithSemaphore, "SemaphoreThread-" + i); 
            Thread lockThread = new Thread(example::accessWithLock, "LockThread-" + i); 
            semaphoreThread.start(); 
            lockThread.start(); 
        } 
    } 
}


BARRIER

public class SemaphoreBarrierExecutorDemo {
  // A reusable barrier implemented with semaphores
  static class SemaphoreBarrier {
    private final int parties;
    private int count;
    private final Semaphore mutex = new Semaphore(1);
    private final Semaphore barrier = new Semaphore(0);
    public SemaphoreBarrier(int parties) {
      this.parties = parties;
      this.count = parties;
    }

    public void await() throws InterruptedException {
      mutex.acquire();
      count--;
      if (count == 0) {
        // Last thread arrives: release all waiting threads
        barrier.release(parties - 1);
        // Reset barrier state for reuse
        count = parties;
        mutex.release();
      } else {
        // Release mutex so other threads can update the count
        mutex.release();
        // Wait until the last thread releases this thread
        barrier.acquire();
      }
    }
  }

  public static void main(String[] args) {
    final int numThreads = 5;
    final SemaphoreBarrier barrier = new SemaphoreBarrier(numThreads);
    // Create a fixed thread pool with custom thread names
    ExecutorService executor = Executors.newFixedThreadPool(numThreads, new ThreadFactory() {
      private int counter = 1;
      @Override
      public Thread newThread(Runnable r) {
        Thread t = new Thread(r, "Worker-" + counter);
        counter++;
        return t;
      }
    });
    // Submit tasks to the executor
    for (int i = 0; i < numThreads; i++) {
      executor.submit(() -> {
        try {
          // Phase 1: Some work before reaching the first barrier
          System.out.println(Thread.currentThread().getName() + " doing phase 1 work");
          Thread.sleep((long) (Math.random() * 1000)); // Simulate work
          System.out.println(
              Thread.currentThread().getName() + " arrived at barrier after phase 1");
          barrier.await(); // Wait until all threads reach here
          // Phase 2: This phase begins only after every thread has finished phase 1
          System.out.println(Thread.currentThread().getName() + " starting phase 2");
          Thread.sleep((long) (Math.random() * 1000)); // Simulate work
          System.out.println(Thread.currentThread().getName() + " finished phase 2");
          barrier.await(); // Synchronize end of phase 2
          // Phase 3: The final phase starts after all threads have completed phase 2
          System.out.println(Thread.currentThread().getName() + " starting phase 3");
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          System.out.println(Thread.currentThread().getName() + " was interrupted");
        }
      });
    }
    // Initiate an orderly shutdown
    executor.shutdown();
    try {
      if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
        System.out.println("Some tasks did not finish in time");
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      System.out.println("Main thread interrupted");
      executor.shutdownNow();
    }
    System.out.println("All tasks completed");
  }
}