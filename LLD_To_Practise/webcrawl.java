import java.util.*;
import java.util.concurrent.*;

public class WebCrawler {

    private Set<String> visited = ConcurrentHashMap.newKeySet();
    private ExecutorService executor = Executors.newFixedThreadPool(5);
    private BlockingQueue<String> q = new LinkedBlockingQueue<>();

    public void webCrawl(Set<String> startUrls) {
        q.addAll(startUrls);
        visited.addAll(startUrls);

        while (!q.isEmpty()) {
            String url = q.poll();

            Runnable task = () -> {
                Set<String> next = htmlParser(url);
                for (String u : next) {
                    if (!visited.contains(u)) {
                        visited.add(u);
                        q.offer(u);
                    }
                }
            };

            executor.submit(task);
        }

        executor.shutdown();
    }

    public static void main(String[] args) {
        Set<String> urls = Set.of("https://a.com", "https://b.com");
        new WebCrawler().webCrawl(urls);
    }

    private Set<String> htmlParser(String url) {
        return Set.of(); // mock
    }
}
