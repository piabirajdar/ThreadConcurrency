import java.util.LinkedList;
import java.util.Queue;

public class SlidingWindowRateLimiter {
    private final int maxRequests;         // max requests allowed
    private final long windowSizeInMillis; // window size in ms
    private final Queue<Long> requestTimestamps = new LinkedList<>();

    public SlidingWindowRateLimiter(int maxRequests, long windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis;
    }

    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();

        // Remove old timestamps outside the window
        while (!requestTimestamps.isEmpty() && (now - requestTimestamps.peek()) > windowSizeInMillis) {
            requestTimestamps.poll();
        }

        if (requestTimestamps.size() < maxRequests) {
            requestTimestamps.offer(now);
            return true; // allowed
        } else {
            return false; // rate limit exceeded
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter(3, 2000); // 3 requests per 2 seconds

        for (int i = 0; i < 10; i++) {
            System.out.println("Request " + i + ": " + limiter.allowRequest());
            Thread.sleep(500);
        }
    }
}
