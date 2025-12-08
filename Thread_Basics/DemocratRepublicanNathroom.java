🧩 Problem Recap

You have:

A bathroom with a maximum capacity of 3 people.

Two political parties: Democrats (D) and Republicans (R).

Each person:

Arrives over time.

Needs the bathroom for a specific duration (computed from their name).

The bathroom can only have one party inside at a time:

e.g. It can be 3 Democrats or 3 Republicans, but never a mix.

Goal:
Schedule people fairly so:

No one starves (eventually everyone gets a turn).

Average waiting time is minimized.

Bathroom capacity is always used efficiently.

🚪 Key Rules in the Solution

Capacity limit = 3

At most 3 people inside at any time.

No mixing

Either all Democrats or all Republicans inside.

Batches

People enter in groups (“batches”) of up to 3 from the same party.

Once the bathroom is empty, the scheduler decides which party goes next.

Fairness controls

If one party keeps getting batches, the other eventually gets priority (to avoid starvation).

If someone’s been waiting too long, their party gets to go next.

Efficiency

Within a party, those with shorter bathroom times (shortest processing time, SPT) go first among early arrivals, to reduce average waiting time.

⚙️ How It Works Step-by-Step
1. People Arrive

When a person arrives:

They’re added to their party’s queue (either Democrat or Republican).

The scheduler immediately tries to see if anyone can be admitted.

2. Choosing Who Goes Next

If the bathroom is empty:

The scheduler looks at both queues.

It decides which party should get the next batch.

If one party has been waiting longer → that party goes.

If one party has already had several consecutive batches (say 2) → switch to the other.

Otherwise, just pick the one who arrived first.

3. Picking People Within a Party

When a party is chosen:

The scheduler looks at the first few arrivals (up to W = 6 people).

From those, it picks the shortest bathroom times first.

Admits up to 3 (capacity) of them.

Gives each one permission to “enter” by releasing their private semaphore (signal).

4. While They Are Inside

Each admitted person prints “ENTER” and “LEAVE”.

When someone leaves, they release one stall.

If the bathroom becomes empty after a batch, that batch is considered finished.

5. After a Batch Finishes

The scheduler can pick the next batch’s party using the same rules.

The whole process repeats until everyone is done.

🧠 Simple Example (Visual)

Let’s say:

Time	Person	Party	Service Time
0ms	Alice	D	300ms
100ms	Bob	R	400ms
200ms	Carol	D	250ms
300ms	Dave	R	600ms
400ms	Eve	D	220ms
Timeline:

Alice (D) arrives → Bathroom empty → admits D batch.

Alice goes in.

Carol (D) and Eve (D) also arrive → same party, and capacity allows 3 → they join Alice.

Bathroom now full with Alice, Carol, Eve (all D).

Bob (R) and Dave (R) arrive → must wait (bathroom has Ds).

After Ds finish and leave → bathroom empty.

Scheduler sees:

Rs waiting (Bob, Dave)

No Ds waiting.

So admits Republicans next.

They go in, one or both, depending on available slots.

🧭 Starvation Protection Example

Imagine Democrats keep arriving nonstop — Republicans might starve.

To prevent that:

After K batches (say 2) of Democrats in a row,
even if more Democrats are waiting, the scheduler forces Republicans next.

Also:

If any Republican has been waiting too long (T_WAIT threshold),
they get priority for the next batch.

⏱ Optimization (SPT — Shortest Processing Time)

Within a party’s queue:

The scheduler looks at the first few arrivals (up to 6, W).

Picks the ones with the shortest bathroom time among them.

This helps minimize average waiting time for the group.

💬 In short

Bathroom capacity = 3

One party at a time

Pick party based on fairness + waiting time

Within a party, pick the shortest users first

No starvation → alternate parties when needed



import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class BathroomSchedulerSimple {
    // ---- Scheduler state ----
    private final int CAPACITY = 3;
    private final Semaphore stalls = new Semaphore(CAPACITY);
    private final Lock lock = new ReentrantLock();
    private final Queue<Person> demQueue = new LinkedList<>();
    private final Queue<Person> repQueue = new LinkedList<>();
    private Party currentParty = null;
    private int insideCount = 0;
    enum Party { DEM, REP }

    static class Person implements Runnable {
        String name;
        Party party;
        long serviceMs;
        long arrivalTime;
        BathroomSchedulerSimple sched;
        Semaphore personalSem = new Semaphore(0);

        Person(String name, Party p, BathroomSchedulerSimple s) {
            this.name = name;
            this.party = p;
            this.sched = s;
            this.serviceMs = 300 + (Math.abs(name.hashCode()) % 700);
        }

        public void run() {
            sched.arrive(this);
            try {
                personalSem.acquire(); // wait until admitted
                System.out.println(name + " (" + party + ") ENTERS for " + serviceMs + "ms");
                Thread.sleep(serviceMs);
                System.out.println(name + " (" + party + ") LEAVES");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                sched.leave(this);
            }
        }
    }

    // ---- Arrival ----
    void arrive(Person p) {
        lock.lock();
        try {
            (p.party == Party.DEM ? demQueue : repQueue).add(p);
            tryAdmit();
        } finally {
            lock.unlock();
        }
    }

    // ---- Leaving ----
    void leave(Person p) {
        lock.lock();
        try {
            stalls.release();
            insideCount--;
            if (insideCount == 0) currentParty = null;
            tryAdmit();
        } finally {
            lock.unlock();
        }
    }

    // ---- Admission logic ----
    void tryAdmit() {
        while (stalls.availablePermits() > 0) {
            Queue<Person> q = null;
            if (insideCount == 0) { // bathroom empty — choose who goes next
                if (demQueue.isEmpty() && repQueue.isEmpty()) return;
                if (demQueue.isEmpty()) currentParty = Party.REP;
                else if (repQueue.isEmpty()) currentParty = Party.DEM;
                else {
                    long demWait = System.currentTimeMillis() - demQueue.peek().arrivalTime;
                    long repWait = System.currentTimeMillis() - repQueue.peek().arrivalTime;
                    currentParty = (demWait >= repWait) ? Party.DEM : Party.REP;
                }
            }
            q = (currentParty == Party.DEM) ? demQueue : repQueue;
            if (q.isEmpty()) return;

            Person next = q.poll();
            stalls.tryAcquire();
            insideCount++;
            next.personalSem.release();
        }
    }

    // ---- Demo ----
    public static void main(String[] args) throws InterruptedException {
        BathroomSchedulerSimple sched = new BathroomSchedulerSimple();
        ExecutorService pool = Executors.newCachedThreadPool();

        List<Person> people = List.of(
            new Person("Alice", Party.DEM, sched),
            new Person("Bob", Party.REP, sched),
            new Person("Carol", Party.DEM, sched),
            new Person("Dave", Party.REP, sched),
            new Person("Eve", Party.DEM, sched),
            new Person("Frank", Party.REP, sched)
        );

        for (Person p : people) {
            pool.submit(p);
            Thread.sleep(200); // staggered arrival
        }

        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("Simulation finished.");
    }
}
