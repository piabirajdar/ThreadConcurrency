class WebCrawler {

    private final Set<String> visited = ConcurrentHashMap.newKeySet();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public List<String> crawl(String startUrl, HtmlParser htmlParser)
            throws InterruptedException {

        String host = getHostName(startUrl);

        BlockingQueue<String> queue = new LinkedBlockingQueue<>(1000);
        AtomicInteger activeTasks = new AtomicInteger(0);

        queue.put(startUrl);
        visited.add(startUrl);

        while (true) {
            String url = queue.poll(100, TimeUnit.MILLISECONDS);  // Wait most of the time, but occasionally wake up to see if we’re done.”

            // NO String url = queue.take(); // blocks forever
            // At the end of the crawl:

            // Queue becomes empty

            // No new URLs will ever arrive

            // Workers block forever

            // Main thread can’t know when to stop


            if (url == null) {
                if (activeTasks.get() == 0) break;
                continue;
            }

            activeTasks.incrementAndGet();

            executor.submit(() -> {
                try {
                    for (String nextUrl : htmlParser.getUrls(url)) {
                        if (getHostName(nextUrl).equals(host)
                                && visited.add(nextUrl)) {
                            queue.offer(nextUrl); // bounded backpressure
                        }
                    }
                } finally {
                    activeTasks.decrementAndGet();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        return new ArrayList<>(visited);
    }

    private String getHostName(String url) {
        return url.split("/")[2];
    }
}


// SIMILARLY

// What would go wrong with put()
// queue.put(nextUrl); // ❌


// Queue is bounded

// If it’s full, put() blocks

// Worker thread gets stuck

// If all workers block → deadlock

// No one left to consume from the queue

// 📌 Key idea:

// Workers must never block while producing new work.

// Why offer() is correct
// queue.offer(nextUrl); // ✅


// Never blocks

// If queue is full → returns false

// Worker keeps running and finishes

// System continues to make progress