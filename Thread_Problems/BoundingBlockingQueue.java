import java.util.concurrent.Semaphore;
import java.util.LinkedList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Queue;

class BoundedBlockingQueue {
    private final Queue<Integer> queue;
    private final int capacity;

    public BoundedBlockingQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
    }

    // Enqueue (producer)
    public synchronized void enqueue(int element) throws InterruptedException {
        // Wait while the queue is full
        while (queue.size() == capacity) {
            wait();
        }
        // Add element
        queue.offer(element);
        // Notify waiting threads that an element is available
        notifyAll();
    }

    // Dequeue (consumer)
    public synchronized int dequeue() throws InterruptedException {
        // Wait while the queue is empty
        while (queue.isEmpty()) {
            wait();
        }
        int val = queue.poll();
        // Notify waiting threads that space is available
        notifyAll();
        return val;
    }

    // Get size
    public synchronized int size() {
        return queue.size();
    }
}



import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        BoundedBlockingQueue bbq = new BoundedBlockingQueue(3);

        ExecutorService executor = Executors.newFixedThreadPool(4);

        // Producer task
        Runnable producer = () -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    bbq.enqueue(i);
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        // Consumer task
        Runnable consumer = () -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    int val = bbq.dequeue();
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        executor.submit(producer);
        executor.submit(producer);
        executor.submit(consumer);
        executor.submit(consumer);

        executor.shutdown();
    }
}




// USING SEMAPHORE

class BoundedBlockingQueue {
    private final Queue<Integer> queue;
    private final int capacity;

    private final Semaphore notFull;   // available spaces
    private final Semaphore notEmpty;  // available elements
    private final Semaphore lock;      // mutual exclusion

    public BoundedBlockingQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
        this.notFull = new Semaphore(capacity); // initially all slots empty
        this.notEmpty = new Semaphore(0);       // no elements yet
        this.lock = new Semaphore(1);           // mutex
    }

    public void enqueue(int element) throws InterruptedException {
        notFull.acquire(); // wait if full
        lock.acquire();    // enter critical section
        queue.offer(element);
        lock.release();
        notEmpty.release(); // signal: at least one element available
    }

    public int dequeue() throws InterruptedException {
        notEmpty.acquire(); // wait if empty
        lock.acquire();
        int val = queue.poll();
        lock.release();
        notFull.release();  // signal: at least one space available
        return val;
    }

    public int size() {
        lock.acquireUninterruptibly();
        int s = queue.size();
        lock.release();
        return s;
    }
}
