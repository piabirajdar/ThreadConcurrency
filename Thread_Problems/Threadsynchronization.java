1️⃣ Synchronized Method 🔄
When you declare an entire method as synchronized, the lock 🔐 is acquired on the object instance (or on the Class object for static methods) before the method is executed and released after it finishes ✅.

📌 This is useful when the whole method represents a critical section where no concurrent execution is desired. It is straightforward and reduces the chance of forgetting to protect part of the code 🧠🧩.
public class CounterSyncMethod { 
    private int count = 0; 
    // The entire method is synchronized. 
    public synchronized void increment() { 
        System.out.println("Synchronized Method - Start increment: " + Thread.currentThread().getName()); 
        // Critical section: updating the shared counter 
        count++; 
        System.out.println("Synchronized Method - Counter value after increment: " + count); 
        System.out.println("Synchronized Method - End increment: " + Thread.currentThread().getName()); 
    } 

    public int getCount() { 
        return count; 
    } 

    // Main method to test the synchronized method 
    public static void main(String[] args) { 
        CounterSyncMethod counter = new CounterSyncMethod(); 
        int numberOfThreads = 5; 
        Thread[] threads = new Thread[numberOfThreads]; 
        // Create and start threads that call the synchronized increment method. 
        for (int i = 0; i < numberOfThreads; i++) { 
            threads[i] = new Thread(new Runnable() { 
                public void run() { 
                    counter.increment(); 
                } 
            }, "Thread-" + (i + 1)); 


            threads[i].start(); 
        } 
        // Wait for all threads to complete. 
        for (int i = 0; i < numberOfThreads; i++) { 
            try { 
                threads[i].join(); 
            } catch (InterruptedException e) { 
                e.printStackTrace(); 
            } 
        } 
        // Display the final counter value. 
        System.out.println("Final counter value: " + counter.getCount()); 
    } 
}


2️⃣ Synchronized Block 🧩
public class CounterSyncBlock { 
    private int count = 0; 
    // Explicit lock object for finer control. 
    private final Object lock = new Object(); 

    public void increment() { 
        // Non-critical part: runs without locking. 
        System.out.println("Non-Synchronized part (pre-processing): " + Thread.currentThread().getName()); 
        // Critical section: only this part is synchronized. 
        synchronized (lock) { 
            System.out.println("Synchronized Block - Start increment: " + Thread.currentThread().getName()); 
            count++; 
            System.out.println("Synchronized Block - Counter value after increment: " + count); 
            System.out.println("Synchronized Block - End increment: " + Thread.currentThread().getName()); 
        } 
        // Non-critical part: runs after the synchronized block. 
        System.out.println("Non-Synchronized part (post-processing): " + Thread.currentThread().getName()); 
    } 

    public int getCount() { 
        return count; 
    } 

    // Main method to test the synchronized block functionality. 
    public static void main(String[] args) { 
        CounterSyncBlock counter = new CounterSyncBlock(); 
        int numberOfThreads = 5; 
        Thread[] threads = new Thread[numberOfThreads]; 
        // Create and start threads that execute the increment method. 
        for (int i = 0; i < numberOfThreads; i++) { 
            threads[i] = new Thread(new Runnable() { 
                public void run() { 
                    counter.increment(); 
                } 
            }, "Thread-" + (i + 1)); 
            threads[i].start(); 
        } 
        // Wait for all threads to finish. 
        for (int i = 0; i < numberOfThreads; i++) { 
            try { 
                threads[i].join(); 
            } catch (InterruptedException e) { 
                e.printStackTrace(); 
            } 
        } 
        // Display the final value of the counter. 
        System.out.println("Final counter value: " + counter.getCount()); 
    } 
}

VOLATILE KEYWORD : for flga sto access values for all threads from cachec.

ATOMIC VARIABLES: instead of synchriznied block, just use atomic variable if you want to increement decrement those variables, using incrementAndGet()
and decrementAndGet(), and variable.get()

