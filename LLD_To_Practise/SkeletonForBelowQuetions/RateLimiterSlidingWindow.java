import java.util.LinkedList;
import java.util.Queue;

public class SlidingWindowRateLimiter {
    private final int maxRequests;         // max requests allowed
    private final long windowSizeInMillis; // window size in ms
    private final Queue<Long> qrequestTimestamps = new LinkedList<>();

    public SlidingWindowRateLimiter(int maxRequests, long windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis;
    }

    public synchronized boolean allowRequest() {
        // TODO: implement allowRequest logic
    }

    public static void main(String[] args) throws InterruptedException {
        SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter(3, 2000); // 3 requests per 2 seconds

        for (int i = 0; i < 10; i++) {
            System.out.println("Request " + i + ": " + limiter.allowRequest());
            Thread.sleep(500);
        }
    }
}
