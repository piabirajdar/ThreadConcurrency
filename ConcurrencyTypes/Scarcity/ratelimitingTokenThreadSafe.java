// lazyly implemented Token Bucket Rate Limiter in Java on request check and refill.

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class RateLimiter {

    // ---------- Token Bucket ----------
    static class TokenBucket {

        private double tokens;
        private final int capacity;
        private final int refillRatePerSec;
        private long lastRefillTime; // monotonic
        private final ReentrantLock lock = new ReentrantLock();

        TokenBucket(int capacity, int refillRatePerSec) {
            this.capacity = capacity;
            this.refillRatePerSec = refillRatePerSec;
            this.tokens = capacity;
            this.lastRefillTime = System.nanoTime(); // ✅ monotonic
        }

        RateLimitResult allow() {
            lock.lock();
            try {
                refill();

                if (tokens >= 1) {
                    tokens -= 1;
                    return new RateLimitResult(true, 0);
                }

                long retryAfterMs =
                        (long) Math.ceil((1 - tokens) * 1000 / refillRatePerSec);

                return new RateLimitResult(false, retryAfterMs);

            } finally {
                lock.unlock();
            }
        }

        private void refill() {
            long now = System.nanoTime();
            double secondsPassed =
                    (now - lastRefillTime) / 1_000_000_000.0;

            if (secondsPassed > 0) {
                tokens = Math.min(
                        capacity,
                        tokens + secondsPassed * refillRatePerSec
                );
                lastRefillTime = now;
            }
        }
    }

    // ---------- Per-Key Rate Limiter ----------
    private final ConcurrentHashMap<String, TokenBucket> buckets =
            new ConcurrentHashMap<>();

    private final int capacity;
    private final int refillRate;

    public RateLimiter(int capacity, int refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
    }

    public RateLimitResult allow(String key) {
        TokenBucket bucket =
                buckets.computeIfAbsent(
                        key,
                        k -> new TokenBucket(capacity, refillRate)
                );
        return bucket.allow();
    }

    // ---------- Result ----------
    static class RateLimitResult {
        final boolean allowed;
        final long retryAfterMs;

        RateLimitResult(boolean allowed, long retryAfterMs) {
            this.allowed = allowed;
            this.retryAfterMs = retryAfterMs;
        }
    }

    // ---------- Demo ----------
    public static void main(String[] args) throws Exception {
        RateLimiter limiter = new RateLimiter(5, 2); // 5 tokens, 2/sec

        Runnable task = () -> {
            for (int i = 0; i < 10; i++) {
                RateLimitResult r = limiter.allow("user1");
                System.out.println(Thread.currentThread().getName() +
                        " allowed=" + r.allowed +
                        " retryAfterMs=" + r.retryAfterMs);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {}
            }
        };

        new Thread(task).start();
        new Thread(task).start();
        new Thread(task).start();
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
