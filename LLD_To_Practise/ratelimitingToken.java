// lazyly implemented Token Bucket Rate Limiter in Java on request check and refill.



class Solution {
    int currentTokens;
    int capacity;
    int refillRatePerSec;
    long lastRefillTimestamp;
    private final ReentrantLock lock = new ReentrantLock();
    public Solution ( int cap, int refillrate) {
        currentTokens = cap;
        capacity = cap;
       refillRatePerSec = refillrate;
    }

    public void refill() {
        long now = System.currentTimeMillis();
        int elapsedSeconds = (int)(now - lastRefillTimestamp) / 1000;
        int additionalToken = elapsedSeconds * refillRatePerSec;

        currentTokens = Math.min(capacity, additionalToken + currentTokens);
        lastRefillTimestamp = now;
    }

    public boolean isAllowed() {
        lock.lock();
        try {
            refill();
            if (currentTokens >= 1) {
                currentTokens--;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

   public static void main(String[] args) throws Exception {
    Solution limiter = new Solution(5, 2); // capacity=5, refill=2/sec

    ExecutorService executor = Executors.newFixedThreadPool(3);

    Runnable task = () -> {
        for (int i = 0; i < 10; i++) {
            boolean allowed = limiter.isAllowed();
            System.out.println(Thread.currentThread().getName() +
                               " -> Request " + i + ": " + allowed);
        }
    };

    // Submit multiple parallel request generators
    executor.submit(task);
    executor.submit(task);
    executor.submit(task);

    executor.shutdown();
    executor.awaitTermination(1, TimeUnit.MINUTES);
}

}


// at fixed rate refill tehn use scheduled executor service to refill tokens
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TokenBucket {
    private final int capacity;
    private final int refillRatePerSec;
    private double tokens;

    public TokenBucket(int capacity, int refillRatePerSec) {
        this.capacity = capacity;
        this.refillRatePerSec = refillRatePerSec;
        this.tokens = capacity;

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::refill, 0, 1, TimeUnit.SECONDS); // initialDelay 0, period 1 sec
    }

    private synchronized void refill() {
        tokens = Math.min(capacity, tokens + refillRatePerSec);
    }

    public synchronized boolean allowRequest() {
        if (tokens >= 1) {
            tokens -= 1;
            return true;
        }
        return false;
    }
}
