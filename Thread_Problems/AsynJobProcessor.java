import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class JobProcessor {

    // Stores jobId -> status
    private final Map<String, String> jobStatus = new ConcurrentHashMap<>();

    // Background worker pool
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    // Submit a new job and return its jobId
    public String submitJob(String audioId) {
        String jobId = UUID.randomUUID().toString();
        jobStatus.put(jobId, "pending");

        // Submit async task to process the job
        executor.submit(() -> processJob(jobId, audioId));

        return jobId;
    }

    // Get status of a job
    public String getStatus(String jobId) {
        return jobStatus.getOrDefault(jobId, "unknown");
    }

    // Simulate background processing
    private void processJob(String jobId, String audioId) {
        try {
            jobStatus.put(jobId, "processing");

            // Simulate work (e.g., transcription)
            Thread.sleep(2000); // 2 seconds

            // When done
            jobStatus.put(jobId, "done");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            jobStatus.put(jobId, "failed");
        }
    }

    // Graceful shutdown
    public void shutdown() {
        executor.shutdown();
    }

    // Demo
    public static void main(String[] args) throws Exception {
        JobProcessor processor = new JobProcessor();

        String jobId = processor.submitJob("audio_123");
        System.out.println("Submitted job: " + jobId);

        // Poll status
        for (int i = 0; i < 5; i++) {
            System.out.println("Status = " + processor.getStatus(jobId));
            Thread.sleep(1000);
        }

        processor.shutdown();
    }
}
