import java.util.concurrent.CountDownLatch;

1 means the latch starts with a count of 1.

Threads calling latch.await() will wait until someone calls latch.countDown() once.

After countDown() is called, count becomes 0 → all waiting threads are released.
public class FooLatch {

    private CountDownLatch latch1 = new CountDownLatch(1);
    private CountDownLatch latch2 = new CountDownLatch(1);

    public void first(Runnable printFirst) throws InterruptedException {
        printFirst.run();
        latch1.countDown(); // allow second()
    }

    public void second(Runnable printSecond) throws InterruptedException {
        latch1.await();     // wait for first()
        printSecond.run();
        latch2.countDown(); // allow third()
    }

    public void third(Runnable printThird) throws InterruptedException {
        latch2.await();     // wait for second()
        printThird.run();
    }
}
123

Semaphore version:

Semaphore(int permits)

The constructor parameter specifies how many permits are available initially.

acquire() → takes a permit. If no permit is available → thread blocks.

release() → adds a permit → unblocks waiting threads.

import java.util.concurrent.Semaphore;
class Foo {
 private Semaphore sem1;
 private Semaphore sem2;
    public Foo() {
       sem1 = new Semaphore(0);
       sem2 = new Semaphore(0);
    }

    public void first(Runnable printFirst) throws InterruptedException {
        
        // printFirst.run() outputs "first". Do not change or remove this line.
        printFirst.run();
        sem1.release();
    }

    public void second(Runnable printSecond) throws InterruptedException {
        sem1.acquire();
        // printSecond.run() outputs "second". Do not change or remove this line.
        printSecond.run();
        sem2.release();

    }

    public void third(Runnable printThird) throws InterruptedException {
        sem2.acquire();
        // printThird.run() outputs "third". Do not change or remove this line.
        printThird.run();
    }
}

synchronized version:

public class FooSync {

    private int stage = 1;

    public synchronized void first(Runnable printFirst) throws InterruptedException {
        printFirst.run();
        stage = 2;
        notifyAll(); // wake up waiting threads
    }

    public synchronized void second(Runnable printSecond) throws InterruptedException {
        while (stage != 2) wait(); // wait until first() done
        printSecond.run();
        stage = 3;
        notifyAll(); // wake up waiting threads
    }

    public synchronized void third(Runnable printThird) throws InterruptedException {
        while (stage != 3) wait(); // wait until second() done
        printThird.run();
    }
}
