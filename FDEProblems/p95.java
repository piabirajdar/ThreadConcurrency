// ✅ What p95 ACTUALLY means

// 95% of the data is BELOW this number
// Imagine 100 kids, each with a test score.

// You line them up from smallest to biggest score.
// p95 means = X number:

// 👉 the score where 95 kids are to the LEFT of it
// But now kids shout decimal numbers:

// “73.2!”
// “73.6!”
// “73.51!”
// “73.49!”
// You try to make one box per number.That means:
// a box for 73.2
// a box for 73.21
// 😵 You’ll need millions of boxes.

// 0–1
// 1–2
// 2–3
// ...
// 73–74   ← ALL 73.x go here
// ...
// 99–100
// If p95 is:

// 73.2 vs 73.8
// ➡️ no human or system really cares.

// They care about:

// “Is GPU usage around 70% or around 95%?”
// “Bucket size decides how big each box is, so we don’t need infinite boxes.”
public class StreamingP95 {

    private final double bucketSize;   // e.g. 1.0%
    private final int numBuckets;
    private final long[] counts;
    private long total;

    public StreamingP95(double bucketSize) {
        this.bucketSize = bucketSize;
        this.numBuckets = (int) (100.0 / bucketSize) + 1;
        this.counts = new long[numBuckets];
        this.total = 0;
    }

    // Add one utilization sample
    public void add(double utilization) {
        if (Double.isNaN(utilization)) return;

        double u = Math.max(0.0, Math.min(100.0, utilization));  // because values can come in as 103.5 then they shud go in 100.0
        int idx = (int) (u / bucketSize);

        if (idx >= numBuckets) {
            idx = numBuckets - 1;
        }

        counts[idx]++;
        total++;
    }

    // Compute approximate p95
    public double p95() {
        if (total == 0) return 0.0;

        long threshold = (long) Math.ceil(0.95 * total); // how many samples I need to consider= 95% of total
        long running = 0;

        for (int i = 0; i < numBuckets; i++) {
            running += counts[i]; // keep counting untill you reach 95% of total samples to get the actual value(X) which is p95
            if (running >= threshold) {
                return i * bucketSize;
            }
        }

        return 100.0;
    }
}
