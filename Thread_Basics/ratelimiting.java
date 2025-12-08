
import java.util.*;
import java.util.concurrent.*;

class SlidingWindowRateLimiter {
    private final int maxRequests;
    private final long windowMillis;    
    private final ConcurrentHashMap<String, Deque<Long>> map = new ConcurrentHashMap<>();

    public SlidingWindowRateLimiter(int maxRequests, long windowMillis) {
        this.maxRequests = maxRequests;
        this.windowMillis = windowMillis;
    }

    public boolean allow(String userId) {
        long now = System.currentTimeMillis();
        Deque<Long> q = map.computeIfAbsent(userId, k -> new LinkedList<>());

        synchronized (q) {
            while (!q.isEmpty() && now - q.peekFirst() >= windowMillis) q.pollFirst();
            if (q.size() < maxRequests) { q.addLast(now); return true; }
            else return false;
        }
    }
}



1️⃣ Why use ConcurrentHashMap?
	• ConcurrentHashMap is a thread-safe version of HashMap.
	• Multiple threads can read and write the map simultaneously without corrupting it.
	• In our rate limiter:

ConcurrentHashMap<String, Deque<Long>> map = new ConcurrentHashMap<>();
	• Many threads might call allow(userId) at the same time for different users.
	• If we used a regular HashMap, concurrent writes could break the internal structure → data loss or exceptions.
	• ConcurrentHashMap handles thread-safe insertions and lookups efficiently.
✅ So: It protects the map itself, not the deque inside it.

synchronized(q) ensures:
	• Only one thread at a time can access that user’s deque
	• Other threads wait until the first finishes

