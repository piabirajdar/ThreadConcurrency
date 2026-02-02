// 🧠 What this system guarantees (say this first)

// “Logging must never slow down the main request path.
// When disk IO is saturated, we drop logs and record metrics about the drops.”

// ✅ Key Design Choices

// Bounded queue → caps memory

// offer() on request path → never blocks

// Worker uses take() → blocks when idle

// Counters → track dropped logs


import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;

public class LogIngestionThrottler {

    private static final int QUEUE_CAPACITY = 1000;

    private final BlockingQueue<String> queue =
            new LinkedBlockingQueue<>(QUEUE_CAPACITY);

    private final ExecutorService diskWriter =
            Executors.newSingleThreadExecutor();

    // Metrics
    private final LongAdder acceptedLogs = new LongAdder();
    private final LongAdder droppedLogs = new LongAdder();

    public LogIngestionThrottler() {
        startDiskWriter();
    }

    // ----- Request path (NEVER block) -----
    public void log(String message) {
        boolean accepted = queue.offer(message); // fail fast

        if (accepted) {
            acceptedLogs.increment();
        } else {
            droppedLogs.increment(); // lossy backpressure
        }
    }

    // ----- Background disk writer -----
    private void startDiskWriter() {
        diskWriter.submit(() -> {
            try {
                while (true) {
                    String log = queue.take(); // block when idle
                    writeToDisk(log);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private void writeToDisk(String log) {
        // simulate slow disk IO
        System.out.println("Writing log: " + log);
        try {
            Thread.sleep(10);
        } catch (InterruptedException ignored) {}
    }

    // ----- Metrics API -----
    public long getAcceptedCount() {
        return acceptedLogs.sum();
    }

    public long getDroppedCount() {
        return droppedLogs.sum();
    }

    public void shutdown() {
        diskWriter.shutdownNow();
    }
}
