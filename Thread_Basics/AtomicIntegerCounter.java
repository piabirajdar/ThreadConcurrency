
import java.util.concurrent.atomic.AtomicInteger;

class ThreadSafeCounter {
    private AtomicInteger count = new AtomicInteger(0);

    public void increment() {
        count.incrementAndGet(); // atomic increment
    }

    public int getCount() {
        return count.get();
    }
}

// Usage:
ThreadSafeCounter counter = new ThreadSafeCounter();
Runnable task = () -> {
    for(int i=0; i<1000; i++) counter.increment();
};
Thread t1 = new Thread(task);
Thread t2 = new Thread(task);
t1.start(); t2.start();
t1.join(); t2.join();
System.out.println(counter.getCount()); // 2000


OR

• synchronized method → locks the whole method
• synchronized block → locks only critical part, more efficient



class ThreadSafeCounter {
    private int count = 0;
    private final Object lock = new Object(); // explicit lock object

    public void increment() {
        synchronized (lock) { // only this block is locked
            count++;
        }
    }

    public int getCount() {
        synchronized (lock) { // lock only when reading
            return count;
        }
    }
}
OR
class ThreadSafeCounter {
    private int count = 0;

    public synchronized void increment() {
        count++; // only one thread can execute this at a time
    }

    public synchronized int getCount() {
        return count;
    }
}
// Usage will be same create threads, and object and call increment()


public class Main {
    public static void main(String[] args) throws InterruptedException {
        ThreadSafeCounter counter = new ThreadSafeCounter();

        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);


        t1.start();
        t2.start();

        t1.join();
        t2.join();


        ExecutorService  executor = Executors.newFixedThreadPool(4);
        Runnable task = () -> {
            for (int i=0; i < 10; i++){
                counter.increment();
            }
        }
        for (int i=0; i < 4; i++) {
            executor.submit(task);
        }
        executor.shutdown();
        System.out.println("Final count: " + counter.getCount());
    }
}
