// Question: Write code that could lead to deadlock and explain.
// Answer:

class DeadlockDemo {
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();
public void method1() {
        synchronized(lock1) {
            try { Thread.sleep(100); } catch(Exception e) {}
            synchronized(lock2) { System.out.println("Method1 done"); }
        }
    }
public void method2() {
        synchronized(lock2) {
            try { Thread.sleep(100); } catch(Exception e) {}
            synchronized(lock1) { System.out.println("Method2 done"); }
        }
    }
}

Problem: Thread A holds lock1 and waits for lock2, Thread B holds lock2 and waits for lock1 → deadlock.

SOLVE above by acquirung the locks in the same order.
OR using timeouts for the locks.



import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

class DeadlockDemoFixed {

    private final ReentrantLock lock1 = new ReentrantLock();
    private final ReentrantLock lock2 = new ReentrantLock();

    // Method1 tries to acquire lock1 then lock2
    public void method1() throws InterruptedException {
        while (true) {
            if (lock1.tryLock()) {
                try {
                    if (lock2.tryLock()) {
                        try {
                            System.out.println("Method1 done");
                            break; // success
                        } finally {
                            lock2.unlock();
                        }
                    }
                } finally {
                    lock1.unlock();
                }
            }

            // Failed to acquire both, retry
            Thread.sleep(10);
        }
    }

    // Method2 ALSO tries to acquire lock1 then lock2 (same global order → no deadlock)
    public void method2() throws InterruptedException {
        while (true) {
            if (lock1.tryLock()) {
                try {
                    if (lock2.tryLock()) {
                        try {
                            System.out.println("Method2 done");
                            break;
                        } finally {
                            lock2.unlock();
                        }
                    }
                } finally {
                    lock1.unlock();
                }
            }

            Thread.sleep(10);
        }
    }
}

public class Main {
    public static void main(String[] args) {

        DeadlockDemoFixed demo = new DeadlockDemoFixed();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.submit(() -> {
            try {
                demo.method1();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        executor.submit(() -> {
            try {
                demo.method2();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        executor.shutdown();
    }
}
