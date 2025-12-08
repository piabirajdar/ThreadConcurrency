import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// ✅ Mini Cache With TTL (Java)
// 🔹 Requirements

// put(key, value, ttlSeconds)

// get(key) returns:

// value if not expired

// null if expired or not found

// We’ll store:

// the value, and

// the expiry timestamp (in ms)
   // Inner class to store a cache entry
public static class CacheEntry {
    Object value;
    long expiryTime;

    CacheEntry(Object value, long expiryTime) {
        this.value = value;
        this.expiryTime = expiryTime;
    }
}
public class TTLCache {

    // Store key -> CacheEntry(value, expiryTime)
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    // Insert value with TTL in seconds
    public void put(String key, Object value, long ttlSeconds) {
        long expiryTime = System.currentTimeMillis() + ttlSeconds * 1000;
        cache.put(key, new CacheEntry(value, expiryTime));
    }

    // Retrieve value if not expired
    public Object get(String key) {
        CacheEntry entry = cache.get(key);

        if (entry == null) return null;

        // Check expiry
        if (System.currentTimeMillis() > entry.expiryTime) {
            cache.remove(key);       // clean up
            return null;             // expired
        }

        return entry.value;
    }

    // Demo
    public static void main(String[] args) throws Exception {
        TTLCache cache = new TTLCache();

        cache.put("user1", "Priyanka", 2);  // expires in 2 seconds
        
        System.out.println(cache.get("user1")); // Priyanka
        Thread.sleep(3000);
        System.out.println(cache.get("user1")); // null (expired)
    }
}
