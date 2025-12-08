import java.util.concurrent.Semaphore;

class FooBar {
    private int n;
    private Semaphore fooSem = new Semaphore(1); // foo starts first
    private Semaphore barSem = new Semaphore(0); // bar waits

    public FooBar(int n) {
        this.n = n;
    }

    public void foo(Runnable printFoo) throws InterruptedException {
        for (int i = 0; i < n; i++) {
            fooSem.acquire();       // wait for turn
            printFoo.run();         // print "foo"
            barSem.release();       // allow bar
        }
    }

    public void bar(Runnable printBar) throws InterruptedException {
        for (int i = 0; i < n; i++) {
            barSem.acquire();       // wait for foo
            printBar.run();         // print "bar"
            fooSem.release();       // allow next foo
        }
    }
}

class FooBarSync {
    private int n;
    private boolean fooTurn = true; // starts with foo

    public FooBarSync(int n) {
        this.n = n;
    }

    public synchronized void foo(Runnable printFoo) throws InterruptedException {
        for (int i = 0; i < n; i++) {
            while (!fooTurn) wait();
            printFoo.run();
            fooTurn = false;
            notifyAll();
        }
    }

    public synchronized void bar(Runnable printBar) throws InterruptedException {
        for (int i = 0; i < n; i++) {
            while (fooTurn) wait();
            printBar.run();
            fooTurn = true;
            notifyAll();
        }
    }
}
