Semaphore:
acquire() → takes a permit. If no permit is available → thread blocks.
release() → adds a permit → unblocks waiting threads.


CPU-intensive tasks → Executors.newFixedThreadPool(n) 
CPU-bound tasks (like image processing, video encoding, or complex calculations) spend most of their time using the CPU, rather than waiting for external resources. 
Too many threads can lead to excessive context switching, slowing down performance. 
A fixed number of threads (equal to the number of CPU cores) ensures that CPU resources are fully utilized without excessive overhead. 




THREAD POOL AND THREAD LIFECYCLE MANAGEMENT:
NEW/RUNNABLE/BLOCKED/WAITING/TERMINATED:
When a thread is created, it is in the NEW state.
When start() is called, it moves to the RUNNABLE state, where it is eligible to run.
If the thread needs to wait for a resource (like I/O), it moves to the BLOCKED or WAITING state.
Once the thread completes its task, it moves to the TERMINATED state.
Using a thread pool helps manage these states efficiently by reusing threads, reducing the overhead

Why newCachedThreadPool() is often better for I/O-bound tasks than newFixedThreadPool() ? : 
Short-lived tasks → Executors.newCachedThreadPool() 
I/O-bound tasks (like web scraping, database queries, file I/O) spend most of their time waiting. 
Threads are created dynamically as needed, avoiding delays due to waiting. 
If a thread is inactive, it is reused instead of creating a new one, reducing overhead. 
Automatic Thread Management:

CPU-intensive tasks → Executors.newFixedThreadPool(n) 
CPU-bound tasks (like image processing, video encoding, or complex calculations) spend most of their time using the CPU, rather than waiting for external resources. 

import java.util.concurrent.*;
THREAD Executors:
ExecutorService executorService = Executors.newFixedThreadPool(3);
   executorService.submit(new WorkerThread(i)); 
     executorService.shutdown();

3. What is the difference between shutdown() and shutdownNow()? 

Answer: shutdown() initiates a graceful shutdown, allowing queued tasks to complete but not accepting new tasks. shutdownNow() attempts to stop all executing tasks immediately and returns a list of tasks that were awaiting execution. 🛑 


Set appropriate queue sizes to balance memory usage and throughput. 📦 
 Example Scenario: 

Too large a queue → Delayed execution. 
Too small a queue → Frequent task rejections.

ExecutorService executor = new ThreadPoolExecutor( 
        4, 8, 30L, TimeUnit.SECONDS, 
        new LinkedBlockingQueue<>(10)); // Balanced queue size

2. How does a ThreadPoolExecutor queue size affect its behavior?

Answer: The queue stores tasks when all core threads are busy. 
A larger queue can handle more pending tasks but consumes more memory. 
If the queue reaches capacity, the pool creates additional threads up to maxPoolSize. 
If maxPoolSize is reached and the queue is full, the rejection policy is applied
  ThreadPoolExecutor executor = new ThreadPoolExecutor( 
            4,                          // Core pool size 
            4,                          // Maximum pool size 
            0, TimeUnit.MILLISECONDS,   // Keep-alive time 
            new LinkedBlockingQueue<>(),// Work queue (FIFO) 
            new ThreadPoolExecutor.CallerRunsPolicy()  // Rejection policy 
        ); 


THREAD EXECUTORS:

While raw threads and thread pools offer control over concurrent operations, 
Thread Executors provide structured, task-based concurrency with several key advantages:
1. Whats the difference between execute() and submit() methods?

Answer: execute() accepts only Runnable tasks and doesnt return any result. submit() accepts both Runnable and Callable tasks and returns a Future object that can be used to retrieve results or check completion status. 🔄

‍ExecutorService executor = Executors.newFixedThreadPool(2);
executor.execute(() -> System.out.println("Runnable executed"));
Future<Integer> future = executor.submit(() -> 42);
System.out.println("Callable result: " + future.get());
executor.shutdown();
4. What is the difference between scheduleAtFixedRate and scheduleWithFixedDelay methods ?

Answer: 

• scheduleAtFixedRate attempts to execute tasks at a consistent rate regardless of how long each task takes (tasks might overlap if execution takes longer than the period). 

• scheduleWithFixedDelay waits for the specified delay time after each task completes before starting the next execution. ⏱️


THREAD SYNCHRONIZATION:
1. synchronized keyword:
Used to lock an object for mutual exclusive access by threads.
When a thread enters a synchronized method/block, it acquires the lock for that object. Other threads trying to enter synchronized code on the same object will block until the lock is released.
2. VOLATILE keyword:
The volatile keyword in Java is used to indicate that a variable's value may be changed by different threads.
It ensures that reads and writes to that variable are directly from/to main memory, not cached in CPU caches.
This guarantees visibility of changes to variables across threads.
3. ATOMIC VARIABLES:
java.util.concurrent.atomic package provides classes like AtomicInteger, AtomicLong, AtomicReference, etc.
These classes support lock-free, thread-safe operations on single variables.
They use low-level atomic CPU instructions to perform operations like incrementing, updating, or comparing values atomically without using synchronized blocks.


THREAD COMMUNICATION:
wait()/notify()/notifyAll():
These methods must be called from within a synchronized context (a synchronized block or method) on the same object whose monitor the thread is waiting on. They work together with a shared condition (often a flag or another shared variable) that threads check in a loop to handle spurious wakeups. 🔄🔍
use a shared state variable/lock to track progress between threads.
‍

