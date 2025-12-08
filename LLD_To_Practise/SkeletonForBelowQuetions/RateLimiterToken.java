// lazyly implemented Token Bucket Rate Limiter in Java on request check and refill.



class Solution {
    int currentTokens;
    int capacity;
    int refillRatePerSec;
    long lastRefillTimestamp;
    private final ReentrantLock lock = new ReentrantLock();
    public Solution ( int cap, int refillrate) {
       
    }

    public void refill() {
       // TODO: refill tokens based on elapsed time since last refill
    }

    public boolean isAllowed() {
       // TODO: check and consume token if available
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
        // TODO: initialize capacity, refillRatePerSec, tokens and schedulerExecutor
    }

    private synchronized void refill() {
        // TODO: refill tokens based on refillRatePerSec
    }

    public synchronized boolean allowRequest() {
       // TODO: check and consume token if availabl
        
    }
}
