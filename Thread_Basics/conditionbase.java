class SignalDemoSimple {
    private boolean ready = false;

    public synchronized void waitForSignal() throws InterruptedException {
        while (!ready) {
            wait(); // releases the intrinsic lock on 'this' and waits
        }
        System.out.println("Signal received");
    }

    public synchronized void sendSignal() {
        ready = true;
        notify(); // wakes one waiting thread
    }
}


import java.util.concurrent.locks.*;

class SignalDemo {
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private boolean ready = false;

    public void waitForSignal() throws InterruptedException {
        lock.lock();
        try {
            while (!ready) condition.await();
            System.out.println("Signal received");
        } finally { lock.unlock(); }
    }

    public void sendSignal() {
        lock.lock();
        try { ready = true; condition.signal(); }
        finally { lock.unlock(); }
    }
}

