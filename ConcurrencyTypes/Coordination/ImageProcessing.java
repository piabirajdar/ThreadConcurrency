import java.util.concurrent.*;

public class ImageProcessingService {

    private static final int QUEUE_CAPACITY = 50;
    private static final int WORKERS = 4;

    private final BlockingQueue<ImageJob> queue =
            new LinkedBlockingQueue<>(QUEUE_CAPACITY);

    private final ExecutorService workers =
            Executors.newFixedThreadPool(WORKERS);

    // ----- Job -----
    static class ImageJob {
        final String imageId;

        ImageJob(String imageId) {
            this.imageId = imageId;
        }
    }

    public ImageProcessingService() {
        startWorkers();
    }

    // ----- API (producer) -----
    // Reject fast if overloaded
    public boolean submit(ImageJob job) {
        return queue.offer(job); // never blocks
    }

    // ----- Workers (consumers) -----
    private void startWorkers() {
        for (int i = 0; i < WORKERS; i++) {
            workers.submit(() -> {
                try {
                    while (true) {
                        ImageJob job = queue.take(); // blocks when idle
                        process(job);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }

    private void process(ImageJob job) {
        System.out.println("Resizing image: " + job.imageId);
        // simulate resize
    }

    public void shutdown() {
        workers.shutdownNow();
    }

    // ----- Demo -----
    public static void main(String[] args) {
        ImageProcessingService service = new ImageProcessingService();

        for (int i = 0; i < 100; i++) {
            boolean accepted = service.submit(new ImageJob("img-" + i));
            if (!accepted) {
                System.out.println("Rejected img-" + i);
            }
        }

        service.shutdown();
    }
}
