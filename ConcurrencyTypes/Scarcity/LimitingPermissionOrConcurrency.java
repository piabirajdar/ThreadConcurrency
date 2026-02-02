import java.util.concurrent.Semaphore;
import java.io.File;
import java.nio.file.Files;

public class DownloadManager {
    private final Semaphore downloadSlots = new Semaphore(3);

    public void download(String url, File destination) throws InterruptedException {
        downloadSlots.acquire();
        try {
            byte[] data = httpClient.download(url);
            Files.write(destination.toPath(), data);
        } finally {
            downloadSlots.release();
        }
    }
}
