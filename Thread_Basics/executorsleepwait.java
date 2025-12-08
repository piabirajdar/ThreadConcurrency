mport java.util.concurrent.*; 
 
class Task implements Runnable { 
    private final int taskId; 
 
    public Task(int taskId) { 
        this.taskId = taskId; 
    } 
 
    @Override 
    public void run() { 
        System.out.println(Thread.currentThread().getName() + " - STARTING Task " + taskId);  
        try { 
            // Simulating different thread states 
            Thread.sleep(2000); // Simulates RUNNABLE -> TIMED_WAITING (Sleep) 
             
           synchronized (this) { 
                System.out.println(Thread.currentThread().getName() + " - WAITING on Task " + taskId); 
                // The thread is now RUNNING and enters a synchronized block. 
                this.wait(1000); // Simulates WAITING state for 1 second 
                // The thread leaves the RUNNING state and enters the WAITING state. 
            } 
                         // After wait() (either by timeout or notify), the thread becomes RUNNABLE again. 
            // When the scheduler picks it, it re-enters the RUNNING state. 
            System.out.println(Thread.currentThread().getName() + " - Task " + taskId + " COMPLETED"); 
        } catch (InterruptedException e) { 
            Thread.currentThread().interrupt(); 
            // If interrupted while RUNNING, it might transition to TERMINATED or handle the interrupt and continue. 
            // If interrupted while in TIMED_WAITING or WAITING, it will throw InterruptedException and become RUNNABLE. 
        } 
        // After the try-catch block, if the pool is still active, the thread will likely go back to the RUNNABLE state, 
        // waiting for a new task. If the pool is shutting down, it will eventually move to TERMINATED. 
    } 
} 
  
public class ThreadPoolLifecycleDemo { 
    public static void main(String[] args) { 
        // Step 1: Create a Thread Pool with 3 core threads 
        ExecutorService executor = Executors.newFixedThreadPool(3); 
        System.out.println("Thread Pool Created 🏊‍♂️"); 
        // Step 2: Submit 5 tasks to the pool 
        for (int i = 1; i <= 5; i++) { 
            executor.execute(new Task(i)); // Threads pick tasks and move to RUNNABLE 
        } 
        // Step 3: Initiate shutdown after all tasks are submitted 
        executor.shutdown(); 
        System.out.println("Thread Pool Shutdown Initiated 🚦"); 
 
        try { 
            // Wait for all threads to terminate 
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) { 
                executor.shutdownNow(); 
                System.out.println("Forcing Shutdown! 🚧"); 
               // If shutdownNow is called, threads currently in RUNNING state will be interrupted. 
            } 
        } catch (InterruptedException e) { 
            executor.shutdownNow(); 
        } 
        System.out.println("All Threads Terminated ✅"); 
         
// Once shutdown is complete, all threads that were processing tasks (RUNNING, BLOCKED, WAITING, TIMED_WAITING) 
// will have completed their work or been interrupted and will eventually reach the TERMINATED state 
    } 
}