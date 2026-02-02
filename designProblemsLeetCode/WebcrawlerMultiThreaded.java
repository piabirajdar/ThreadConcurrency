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

            // NO String url = queue.take(); 
            // workers blocks forever because it waits untill the new item becomes available
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


// OFFER:
// Never blocks producer/client, Returns true or false, Lets you reject immediately

// PUT: blocks producer
// Blocks producer until space is free, User waits → bad, good for internal jobs.


// TAKE():  blocks consumer.
// WAITS / SLEEPS unit next item becomes available.
// Workers are meant to run forever
// System lifecycle is controlled externally
// You don’t need the worker to decide when to stop

// POLL():
// queue.poll(100, TimeUnit.MILLISECONDS);
// time out and exit, workers dont run forever.  good for crawler, after crawling done exit the main thread.
// not good for job scheduling, GPU scedhuling because work keeps coming.. Externally worker close controlled