import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class Bathroom {
    enum Party { DEM, REP }

    private static final int CAPACITY = 3;

    private final Semaphore stalls = new Semaphore(CAPACITY);
    private final Lock gateLock = new ReentrantLock();
    private final Condition canEnter = gateLock.newCondition();

    private final Queue<Person> demQueue = new LinkedList<>();
    private final Queue<Person> repQueue = new LinkedList<>();

    private Party currentParty = null;
    private int insideCount = 0;
    private long demOldestWait = Long.MAX_VALUE;
    private long repOldestWait = Long.MAX_VALUE;

    // person thread
    static class Person implements Runnable {
        final String name;
        final Party party;
        final Bathroom bathroom;

        Person(String n, Party p, Bathroom b) { name = n; party = p; bathroom = b; }

        long serviceMs() { return 200 + Math.abs(name.hashCode()) % 800; }

        public void run() {
            try {
                bathroom.arrive(this);
                bathroom.useBathroom(this);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // arrival: enqueue then wait for permission
    void arrive(Person p) throws InterruptedException {
        gateLock.lock();
        try {
            Queue<Person> q = (p.party == Party.DEM) ? demQueue : repQueue;
            q.offer(p);
            long now = System.currentTimeMillis();
            if (p.party == Party.DEM && demOldestWait == Long.MAX_VALUE) demOldestWait = now;
            if (p.party == Party.REP && repOldestWait == Long.MAX_VALUE) repOldestWait = now;
            while (!canEnterNow(p.party)) {
                canEnter.await();
            }
            q.remove(p);
            insideCount++;
            stalls.acquire(); // occupy stall
            if (currentParty == null) currentParty = p.party;
        } finally {
            gateLock.unlock();
        }
    }

    // decide if a person may enter right now
    private boolean canEnterNow(Party p) {
        if (currentParty == null) return pickNextParty() == p;
        if (currentParty != p) return false;
        return insideCount < CAPACITY;
    }

    // pick which party’s gate opens next
    private Party pickNextParty() {
        if (demQueue.isEmpty() && repQueue.isEmpty()) return null;
        long now = System.currentTimeMillis();
        long demWait = demQueue.isEmpty() ? 0 : now - demOldestWait;
        long repWait = repQueue.isEmpty() ? 0 : now - repOldestWait;

        if (demWait >= 1500) return Party.DEM;   // aging threshold
        if (repWait >= 1500) return Party.REP;

        if (!demQueue.isEmpty() && repQueue.isEmpty()) return Party.DEM;
        if (!repQueue.isEmpty() && demQueue.isEmpty()) return Party.REP;

        // fairness: alternate if both waiting
        return (currentParty == Party.DEM) ? Party.REP : Party.DEM;
    }

    // simulate usage
    void useBathroom(Person p) throws InterruptedException {
        System.out.println(p.name + " (" + p.party + ") ENTER");
        Thread.sleep(p.serviceMs());
        System.out.println(p.name + " (" + p.party + ") LEAVE");
        leave(p.party);
    }

    // leaving logic
    void leave(Party p) {
        gateLock.lock();
        try {
            stalls.release();
            insideCount--;
            if (insideCount == 0) {  // batch finished
                currentParty = null;
                long now = System.currentTimeMillis();
                if (p == Party.DEM) demOldestWait = Long.MAX_VALUE;
                else repOldestWait = Long.MAX_VALUE;
            }
            canEnter.signalAll(); // wake waiting threads
        } finally {
            gateLock.unlock();
        }
    }

    // --- demo ---
    public static void main(String[] args) throws InterruptedException {
        Bathroom b = new Bathroom();
        ExecutorService pool = Executors.newCachedThreadPool();

        List<Person> people = List.of(
                new Person("Alice", Party.DEM, b),
                new Person("Bob", Party.REP, b),
                new Person("Carol", Party.DEM, b),
                new Person("Dave", Party.REP, b),
                new Person("Eve", Party.DEM, b),
                new Person("Frank", Party.REP, b),
                new Person("Grace", Party.DEM, b),
                new Person("Heidi", Party.REP, b));

        long[] delays = {0, 100, 200, 300, 600, 800, 1200, 1600};
        for (int i = 0; i < people.size(); i++) {
            final Person p = people.get(i);
            final long d = delays[i];
            pool.submit(() -> {
                try { Thread.sleep(d); p.run(); } catch (InterruptedException ignored) {}
            });
        }

        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("Simulation finished.");
    }
}
